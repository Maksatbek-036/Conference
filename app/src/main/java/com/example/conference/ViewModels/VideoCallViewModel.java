package com.example.conference.ViewModels;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.conference.Repositories.VideoCallRepository;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;
import org.webrtc.*;

import io.reactivex.rxjava3.disposables.Disposable;

public class VideoCallViewModel extends AndroidViewModel {
    private static final String TAG = "VideoCallViewModel";
    private final VideoCallRepository repository;
    private HubConnection hubConnection;
    private String targetId;

    public MutableLiveData<VideoTrack> remoteVideoTrack = new MutableLiveData<>();

    public VideoCallViewModel(Application app) {
        super(app);

        PeerConnection.Observer observer = new PeerConnection.Observer() {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                if (targetId != null && hubConnection != null) {
                    hubConnection.send("SendIceCandidate", targetId, iceCandidate.sdp);
                }
            }
            @Override
            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
                if (rtpReceiver.track() instanceof VideoTrack) {
                    remoteVideoTrack.postValue((VideoTrack) rtpReceiver.track());
                }
            }
            // Остальные заглушки методов PeerConnection.Observer...
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
        // !!! ЗАМЕНИТЕ ЭТОТ URL НА РЕАЛЬНЫЙ IP ВАШЕГО СЕРВЕРА !!!
        String serverUrl = "https://10.0.2.2:5001/hubs/video"; // Пример для локального сервера (эмулятор)

        hubConnection = HubConnectionBuilder.create(serverUrl).build();

        hubConnection.on("UserJoined", userId -> {
            Log.d(TAG, "User joined: " + userId);
            this.targetId = userId;
            startCall();
        }, String.class);

        hubConnection.on("ReceiveSignal", (senderId, sdp) -> {
            this.targetId = senderId;
            SessionDescription.Type type = sdp.contains("offer") ?
                    SessionDescription.Type.OFFER : SessionDescription.Type.ANSWER;
            repository.setRemoteDescription(new SessionDescription(type, sdp));
            if (type == SessionDescription.Type.OFFER) answerCall();
        }, String.class, String.class);

        hubConnection.on("ReceiveIceCandidate", (senderId, sdp) -> {
            repository.addIceCandidate(new IceCandidate("0", 0, sdp));
        }, String.class, String.class);

        // ЗАПУСК В ФОНЕ (БЕЗ blockingAwait)
        hubConnection.start().subscribe(
                () -> Log.i(TAG, "SignalR Connected"),
                throwable -> Log.e(TAG, "SignalR Connection Error: ", throwable)
        );
    }

    public void startVideoCall() {
        // Инициализируем WebRTC локально
        repository.initWebRTC();
        repository.setupPeerConnection();

        // Отправляем сигнал на сервер, только если подключение активно
        if (hubConnection.getConnectionState() == com.microsoft.signalr.HubConnectionState.CONNECTED) {
            hubConnection.send("JoinCall", "ROOM_1");
        } else {
            Log.e(TAG, "Cannot join call: SignalR not connected");
        }
    }

    private void startCall() {
        repository.createOffer(new SdpObserverAdapter() {
            @Override public void onCreateSuccess(SessionDescription sdp) {
                repository.setLocalDescription(sdp); // Важно установить локальное описание
                hubConnection.send("SendSignal", targetId, sdp.description);
            }
        });
    }

    private void answerCall() {
        repository.createAnswer(new SdpObserverAdapter() {
            @Override public void onCreateSuccess(SessionDescription sdp) {
                repository.setLocalDescription(sdp); // И здесь тоже
                hubConnection.send("SendSignal", targetId, sdp.description);
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
        if (hubConnection != null) {
            hubConnection.stop();
        }
    }

    private static class SdpObserverAdapter implements SdpObserver {
        @Override public void onCreateSuccess(SessionDescription sdp) {}
        @Override public void onSetSuccess() {}
        @Override public void onCreateFailure(String s) { Log.e(TAG, "SDP Create Failure: " + s); }
        @Override public void onSetFailure(String s) { Log.e(TAG, "SDP Set Failure: " + s); }
    }
}