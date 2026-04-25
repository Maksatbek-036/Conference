package com.example.conference.Repositories;

import android.content.Context;
import android.util.Log;
import org.webrtc.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VideoCallRepository {
    private static final String TAG = "WebRTC_Repo";
    private final Context context;
    private PeerConnectionFactory factory;
    private PeerConnection peerConnection;
    private final EglBase eglBase;
    private final PeerConnection.Observer observer;

    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack; // Добавлено аудио
    private SurfaceTextureHelper surfaceTextureHelper;

    public VideoCallRepository(Context context, PeerConnection.Observer observer) {
        this.context = context.getApplicationContext();
        this.observer = observer;
        this.eglBase = EglBase.create();
    }

    public void initWebRTC() {
        PeerConnectionFactory.InitializationOptions options = PeerConnectionFactory.InitializationOptions
                .builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions();
        PeerConnectionFactory.initialize(options);

        // Настройка кодеков (Hardware Acceleration)
        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(eglBase.getEglBaseContext(), true, true);
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(eglBase.getEglBaseContext());

        factory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();

        startCameraCapture();
        startAudioCapture(); // Запуск микрофона
    }

    private void startAudioCapture() {
        AudioSource audioSource = factory.createAudioSource(new MediaConstraints());
        localAudioTrack = factory.createAudioTrack("LOCAL_AUDIO", audioSource);
    }

    private void startCameraCapture() {
        CameraEnumerator enumerator = Camera2Enumerator.isSupported(context) ?
                new Camera2Enumerator(context) : new Camera1Enumerator(false);

        for (String deviceName : enumerator.getDeviceNames()) {
            if (enumerator.isFrontFacing(deviceName)) {
                videoCapturer = enumerator.createCapturer(deviceName, null);
                break;
            }
        }

        if (videoCapturer == null) return;

        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.getEglBaseContext());
        videoSource = factory.createVideoSource(videoCapturer.isScreencast());
        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());

        // Оптимальное разрешение для мобильной связи
        videoCapturer.startCapture(640, 480, 30);

        localVideoTrack = factory.createVideoTrack("LOCAL_VIDEO", videoSource);
    }

    public void setupPeerConnection() {
        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        // STUN сервера Google
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer());

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;

        peerConnection = factory.createPeerConnection(rtcConfig, observer);

        // Добавляем и видео, и аудио в соединение
        if (localVideoTrack != null) peerConnection.addTrack(localVideoTrack, Collections.singletonList("STREAM"));
        if (localAudioTrack != null) peerConnection.addTrack(localAudioTrack, Collections.singletonList("STREAM"));
    }

    public void createOffer(SdpObserver sdpObserver) {
        MediaConstraints constraints = new MediaConstraints();
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));

        peerConnection.createOffer(sdpObserver, constraints);
    }

    public void createAnswer(SdpObserver sdpObserver) {
        MediaConstraints constraints = new MediaConstraints();
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));

        peerConnection.createAnswer(sdpObserver, constraints);
    }

    public void setLocalDescription(SessionDescription sdp) {
        if (peerConnection != null) {
            peerConnection.setLocalDescription(new SimpleSdpObserver(), sdp);
        }
    }

    public void setRemoteDescription(SessionDescription sdp) {
        if (peerConnection != null) {
            peerConnection.setRemoteDescription(new SimpleSdpObserver(), sdp);
        }
    }

    public void addIceCandidate(IceCandidate candidate) {
        if (peerConnection != null) {
            peerConnection.addIceCandidate(candidate);
        }
    }

    public void setLocalVideoSink(VideoSink sink) {
        if (localVideoTrack != null) localVideoTrack.addSink(sink);
    }

    public EglBase.Context getEglContext() {
        return eglBase.getEglBaseContext();
    }

    public void dispose() {
        try {
            if (videoCapturer != null) {
                videoCapturer.stopCapture();
                videoCapturer.dispose();
            }
            if (surfaceTextureHelper != null) surfaceTextureHelper.dispose();
            if (peerConnection != null) peerConnection.dispose();
            if (factory != null) factory.dispose();
            eglBase.release();
        } catch (Exception e) {
            Log.e(TAG, "Error during dispose: " + e.getMessage());
        }
    }

    private static class SimpleSdpObserver implements SdpObserver {
        @Override public void onCreateSuccess(SessionDescription sdp) {}
        @Override public void onSetSuccess() {}
        @Override public void onCreateFailure(String s) { Log.e(TAG, "SDP Create Fail: " + s); }
        @Override public void onSetFailure(String s) { Log.e(TAG, "SDP Set Fail: " + s); }
    }
}