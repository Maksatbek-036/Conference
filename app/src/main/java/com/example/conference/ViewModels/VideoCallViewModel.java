package com.example.conference.ViewModels;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.conference.Repositories.VideoCallRepository;
import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;

public class VideoCallViewModel extends AndroidViewModel {

    private final VideoCallRepository repository;

    public VideoCallViewModel(@NonNull Application application) {
        super(application);

        // Инициализируем Observer для прослушивания событий WebRTC
        PeerConnection.Observer peerConnectionObserver = new PeerConnection.Observer() {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                // Здесь мы отправляем кандидата через ваш Signaling сервер (Firebase/WebSocket)
            }

            @Override
            public void onAddStream(MediaStream mediaStream) {
                // Здесь получаем видео-поток собеседника
            }

            @Override
            public void onSignalingChange(PeerConnection.SignalingState signalingState) {}
            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {}
            @Override
            public void onIceConnectionReceivingChange(boolean b) {}
            @Override
            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {}
            @Override
            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {}
            @Override
            public void onRemoveStream(MediaStream mediaStream) {}
            @Override
            public void onDataChannel(DataChannel dataChannel) {}
            @Override
            public void onRenegotiationNeeded() {}
            @Override
            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {}
        };

        // Создаем экземпляр репозитория
        repository = new VideoCallRepository(application.getApplicationContext(), peerConnectionObserver);
    }

    public void startVideoCall() {
        repository.initWebRTC();
        repository.joinCall(); // Использует дефолтные STUN-серверы
    }

    public void stopVideoCall() {
        // Логика завершения звонка
        if (repository.getPeerConnection() != null) {
            repository.getPeerConnection().close();
        }
    }
}