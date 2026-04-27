package com.example.conference.ViewModels;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.conference.Repositories.VideoCallRepository;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import com.microsoft.signalr.HubConnectionState;
import org.webrtc.*;

public class VideoCallViewModel extends AndroidViewModel {
    private static final String TAG = "VideoCallViewModel";
    private final VideoCallRepository repository;
    private HubConnection hubConnection;
    private String targetId;
    private String currentRoomId;
    private boolean pendingJoin = false;

    public MutableLiveData<VideoTrack> remoteVideoTrack = new MutableLiveData<>();

    public VideoCallViewModel(Application app) {
        super(app);

        PeerConnection.Observer observer = new PeerConnection.Observer() {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                // Отправляем ICE-кандидатов только если есть targetId и он не мы сами
                if (targetId != null && hubConnection != null && hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                    hubConnection.send("SendIceCandidate", targetId, iceCandidate.sdp);
                }
            }
            @Override
            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
                if (rtpReceiver.track() instanceof VideoTrack) {
                    remoteVideoTrack.postValue((VideoTrack) rtpReceiver.track());
                }
            }
            @Override public void onSignalingChange(PeerConnection.SignalingState s) {}
            @Override public void onIceConnectionChange(PeerConnection.IceConnectionState s) {}
            @Override public void onIceConnectionReceivingChange(boolean b) {}
            @Override public void onIceGatheringChange(PeerConnection.IceGatheringState s) {}
            @Override public void onIceCandidatesRemoved(IceCandidate[] i) {}
            @Override public void onAddStream(MediaStream m) {}
            @Override public void onRemoveStream(MediaStream m) {}
            @Override public void onDataChannel(DataChannel d) {}
            @Override public void onRenegotiationNeeded() {}
        };

        repository = new VideoCallRepository(app, observer);
        initSignalR();
    }

    private void initSignalR() {
        String serverUrl = "http://192.168.0.106:5000/hubs/video";

        hubConnection = HubConnectionBuilder.create(serverUrl).build();

        hubConnection.on("UserJoined", userId -> {
            // ИГНОРИРУЕМ САМИХ СЕБЯ
            String myConnectionId = hubConnection.getConnectionId();
            if (userId.equals(myConnectionId)) {
                Log.d(TAG, "Joined successfully. My ID: " + userId);
                return; 
            }

            Log.d(TAG, "Remote user joined: " + userId);
            this.targetId = userId;
            startCall();
        }, String.class);

        hubConnection.on("ReceiveSignal", (senderId, sdp) -> {
            // Игнорируем, если пришло от самих себя (на всякий случай)
            if (senderId.equals(hubConnection.getConnectionId())) return;

            Log.d(TAG, "Received signal from: " + senderId);
            this.targetId = senderId;
            SessionDescription.Type type = sdp.contains("offer") ?
                    SessionDescription.Type.OFFER : SessionDescription.Type.ANSWER;
            repository.setRemoteDescription(new SessionDescription(type, sdp));
            if (type == SessionDescription.Type.OFFER) answerCall();
        }, String.class, String.class);

        hubConnection.on("ReceiveIceCandidate", (senderId, sdp) -> {
            if (senderId.equals(hubConnection.getConnectionId())) return;
            repository.addIceCandidate(new IceCandidate("0", 0, sdp));
        }, String.class, String.class);

        hubConnection.onClosed(exception -> {
            Log.w(TAG, "SignalR Connection Closed");
        });

        hubConnection.start().subscribe(
                () -> {
                    Log.i(TAG, "SignalR Connected. ID: " + hubConnection.getConnectionId());
                    if (pendingJoin) joinRoom();
                },
                throwable -> Log.e(TAG, "SignalR Connection Error: ", throwable)
        );
    }

    public void startVideoCall(String roomId) {
        this.currentRoomId = roomId;
        repository.initWebRTC();
        repository.setupPeerConnection();

        if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
            joinRoom();
        } else {
            pendingJoin = true;
        }
    }

    private void joinRoom() {
        if (currentRoomId != null) {
            Log.i(TAG, "Joining room: " + currentRoomId);
            hubConnection.send("JoinCall", currentRoomId);
            pendingJoin = false;
        }
    }

    private void startCall() {
        repository.createOffer(new SdpObserverAdapter() {
            @Override public void onCreateSuccess(SessionDescription sdp) {
                repository.setLocalDescription(sdp);
                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED && targetId != null) {
                    hubConnection.send("SendSignal", targetId, sdp.description);
                }
            }
        });
    }

    private void answerCall() {
        repository.createAnswer(new SdpObserverAdapter() {
            @Override public void onCreateSuccess(SessionDescription sdp) {
                repository.setLocalDescription(sdp);
                if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED && targetId != null) {
                    hubConnection.send("SendSignal", targetId, sdp.description);
                }
            }
        });
    }

    public VideoCallRepository getRepository() { return repository; }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopVideoCall();
    }

    public void stopVideoCall() {
        repository.dispose();
        if (hubConnection != null) hubConnection.stop();
    }

    private static class SdpObserverAdapter implements SdpObserver {
        @Override public void onCreateSuccess(SessionDescription sdp) {}
        @Override public void onSetSuccess() {}
        @Override public void onCreateFailure(String s) { Log.e(TAG, "SDP Create Failure: " + s); }
        @Override public void onSetFailure(String s) { Log.e(TAG, "SDP Set Failure: " + s); }
    }
}
