package com.example.conference.ViewModels;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.conference.Repositories.VideoCallRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import org.webrtc.*;
import java.util.HashMap;
import java.util.Map;

public class VideoCallViewModel extends AndroidViewModel {
    private static final String TAG = "VideoCallViewModel";
    private final VideoCallRepository repository;
    private HubConnection hubConnection;
    private String currentRoomId;
    private boolean pendingJoin = false;
    private final Gson gson = new Gson();

    // Map userId to their VideoTrack
    public MutableLiveData<Map<String, VideoTrack>> remoteTracks = new MutableLiveData<>(new HashMap<>());
    public MutableLiveData<Boolean> isVideoEnabled = new MutableLiveData<>(true);
    public MutableLiveData<Boolean> isAudioEnabled = new MutableLiveData<>(true);

    public VideoCallViewModel(Application app) {
        super(app);
        repository = new VideoCallRepository(app);
        
        repository.setMultiPeerObserver(new VideoCallRepository.MultiPeerObserver() {
            @Override
            public void onIceCandidate(String userId, IceCandidate candidate) {
                if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                    JsonObject json = new JsonObject();
                    json.addProperty("sdp", candidate.sdp);
                    json.addProperty("sdpMid", candidate.sdpMid);
                    json.addProperty("sdpMLineIndex", candidate.sdpMLineIndex);
                    hubConnection.send("SendIceCandidate", userId, json.toString());
                }
            }

            @Override
            public void onTrack(String userId, VideoTrack track) {
                Log.d(TAG, "Received remote track from: " + userId);
                Map<String, VideoTrack> currentMap = remoteTracks.getValue();
                if (currentMap != null) {
                    currentMap.put(userId, track);
                    remoteTracks.postValue(currentMap);
                }
            }

            @Override
            public void onConnectionChange(String userId, PeerConnection.IceConnectionState state) {
                Log.d(TAG, "Connection state for " + userId + ": " + state);
                if (state == PeerConnection.IceConnectionState.DISCONNECTED || 
                    state == PeerConnection.IceConnectionState.FAILED || 
                    state == PeerConnection.IceConnectionState.CLOSED) {
                    removeUser(userId);
                }
            }
        });

        initSignalR();
    }

    private void removeUser(String userId) {
        repository.removePeer(userId);
        Map<String, VideoTrack> currentMap = remoteTracks.getValue();
        if (currentMap != null && currentMap.containsKey(userId)) {
            currentMap.remove(userId);
            remoteTracks.postValue(currentMap);
        }
    }

    private void initSignalR() {
        String serverUrl = "http://192.168.0.106:5000/hubs/video";
        hubConnection = HubConnectionBuilder.create(serverUrl).build();

        hubConnection.on("UserJoined", userId -> {
            if (userId.equals(hubConnection.getConnectionId())) return;
            Log.d(TAG, "New user joined: " + userId);
            startCallWith(userId);
        }, String.class);

        hubConnection.on("UserLeft", userId -> {
            Log.d(TAG, "User left: " + userId);
            removeUser(userId);
        }, String.class);

        hubConnection.on("ReceiveSignal", (senderId, signalData) -> {
            Log.d(TAG, "Received signal from: " + senderId);
            try {
                JsonObject json = gson.fromJson(signalData, JsonObject.class);
                String typeStr = json.get("type").getAsString().toLowerCase();
                String sdp = json.get("description").getAsString();

                SessionDescription.Type type = typeStr.contains("offer") ? 
                        SessionDescription.Type.OFFER : SessionDescription.Type.ANSWER;
                
                repository.setRemoteDescription(senderId, new SessionDescription(type, sdp));

                if (type == SessionDescription.Type.OFFER) {
                    answerCallFrom(senderId);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing signal from " + senderId, e);
            }
        }, String.class, String.class);

        hubConnection.on("ReceiveIceCandidate", (senderId, candidateData) -> {
            try {
                JsonObject json = gson.fromJson(candidateData, JsonObject.class);
                String sdp = json.get("sdp").getAsString();
                String sdpMid = json.get("sdpMid").getAsString();
                int sdpMLineIndex = json.get("sdpMLineIndex").getAsInt();
                repository.addIceCandidate(senderId, new IceCandidate(sdpMid, sdpMLineIndex, sdp));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing candidate from " + senderId, e);
            }
        }, String.class, String.class);

        hubConnection.start().subscribe(
                () -> {
                    Log.i(TAG, "SignalR Connected");
                    if (pendingJoin) joinRoom();
                },
                throwable -> Log.e(TAG, "SignalR Error", throwable)
        );
    }

    public void startVideoCall(String roomId) {
        this.currentRoomId = roomId;
        repository.initWebRTC();
        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            joinRoom();
        } else {
            pendingJoin = true;
        }
    }

    public void toggleVideo() {
        boolean enabled = repository.toggleVideo();
        isVideoEnabled.postValue(enabled);
    }

    public void toggleAudio() {
        boolean enabled = repository.toggleAudio();
        isAudioEnabled.postValue(enabled);
    }

    public void switchCamera() {
        repository.switchCamera();
    }

    private void joinRoom() {
        if (currentRoomId != null) {
            hubConnection.send("JoinCall", currentRoomId);
            pendingJoin = false;
        }
    }

    private void startCallWith(String userId) {
        repository.createOffer(userId, new SdpObserver() {
            @Override public void onCreateSuccess(SessionDescription sdp) {
                repository.setLocalDescription(userId, sdp);
                JsonObject json = new JsonObject();
                json.addProperty("type", sdp.type.canonicalForm());
                json.addProperty("description", sdp.description);
                hubConnection.send("SendSignal", userId, json.toString());
            }
            @Override public void onSetSuccess() {}
            @Override public void onCreateFailure(String s) {}
            @Override public void onSetFailure(String s) {}
        });
    }

    private void answerCallFrom(String userId) {
        repository.createAnswer(userId, new SdpObserver() {
            @Override public void onCreateSuccess(SessionDescription sdp) {
                repository.setLocalDescription(userId, sdp);
                JsonObject json = new JsonObject();
                json.addProperty("type", sdp.type.canonicalForm());
                json.addProperty("description", sdp.description);
                hubConnection.send("SendSignal", userId, json.toString());
            }
            @Override public void onSetSuccess() {}
            @Override public void onCreateFailure(String s) {}
            @Override public void onSetFailure(String s) {}
        });
    }

    public VideoCallRepository getRepository() { return repository; }

    public void stopVideoCall() {
        // Run cleanup in a background thread to prevent UI freeze
        // WebRTC's stopCapture and SignalR's stop can be blocking/slow.
        new Thread(() -> {
            try {
                if (hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.CONNECTED && currentRoomId != null) {
                    hubConnection.send("LeaveCall", currentRoomId);
                }
                repository.dispose();
                if (hubConnection != null) {
                    hubConnection.stop().subscribe(
                            () -> Log.i(TAG, "SignalR stopped successfully"),
                            throwable -> Log.e(TAG, "Error stopping SignalR", throwable)
                    );
                }
            } catch (Exception e) {
                Log.e(TAG, "Error during stopVideoCall", e);
            }
        }).start();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopVideoCall();
    }
}
