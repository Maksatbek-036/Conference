package com.example.conference.Repositories;

import android.content.Context;
import android.util.Log;
import org.webrtc.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoCallRepository {
    private static final String TAG = "WebRTC_Repo";
    private final Context context;
    private PeerConnectionFactory factory;
    private final EglBase eglBase;
    
    private final Map<String, PeerConnection> peerConnections = new HashMap<>();
    
    private VideoCapturer videoCapturer;
    private VideoSource videoSource;
    private VideoTrack localVideoTrack;
    private AudioTrack localAudioTrack;
    private SurfaceTextureHelper surfaceTextureHelper;

    public interface MultiPeerObserver {
        void onIceCandidate(String userId, IceCandidate candidate);
        void onTrack(String userId, VideoTrack track);
        void onConnectionChange(String userId, PeerConnection.IceConnectionState state);
    }

    private MultiPeerObserver multiPeerObserver;

    public VideoCallRepository(Context context) {
        this.context = context.getApplicationContext();
        this.eglBase = EglBase.create();
    }

    public void setMultiPeerObserver(MultiPeerObserver observer) {
        this.multiPeerObserver = observer;
    }

    public void initWebRTC() {
        PeerConnectionFactory.InitializationOptions options = PeerConnectionFactory.InitializationOptions
                .builder(context)
                .setEnableInternalTracer(true)
                .createInitializationOptions();
        PeerConnectionFactory.initialize(options);

        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(eglBase.getEglBaseContext(), true, true);
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(eglBase.getEglBaseContext());

        factory = PeerConnectionFactory.builder()
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();

        startCameraCapture();
        startAudioCapture();
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
        videoCapturer.startCapture(640, 480, 30);

        localVideoTrack = factory.createVideoTrack("LOCAL_VIDEO", videoSource);
    }

    public boolean toggleVideo() {
        if (localVideoTrack != null) {
            boolean newState = !localVideoTrack.enabled();
            localVideoTrack.setEnabled(newState);
            return newState;
        }
        return false;
    }

    public boolean toggleAudio() {
        if (localAudioTrack != null) {
            boolean newState = !localAudioTrack.enabled();
            localAudioTrack.setEnabled(newState);
            return newState;
        }
        return false;
    }

    public PeerConnection getOrCreatePeerConnection(String userId) {
        if (peerConnections.containsKey(userId)) {
            return peerConnections.get(userId);
        }

        List<PeerConnection.IceServer> iceServers = new ArrayList<>();
        iceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
        iceServers.add(PeerConnection.IceServer.builder("stun:stun1.l.google.com:19302").createIceServer());

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;

        PeerConnection pc = factory.createPeerConnection(rtcConfig, new PeerConnection.Observer() {
            @Override
            public void onIceCandidate(IceCandidate iceCandidate) {
                if (multiPeerObserver != null) multiPeerObserver.onIceCandidate(userId, iceCandidate);
            }

            @Override
            public void onTrack(RtpTransceiver transceiver) {
                MediaStreamTrack track = transceiver.getReceiver().track();
                if (track instanceof VideoTrack && multiPeerObserver != null) {
                    Log.d(TAG, "onTrack (transceiver) from " + userId);
                    multiPeerObserver.onTrack(userId, (VideoTrack) track);
                }
            }

            @Override
            public void onAddTrack(RtpReceiver rtpReceiver, MediaStream[] mediaStreams) {
                MediaStreamTrack track = rtpReceiver.track();
                if (track instanceof VideoTrack && multiPeerObserver != null) {
                    Log.d(TAG, "onAddTrack from " + userId);
                    multiPeerObserver.onTrack(userId, (VideoTrack) track);
                }
            }

            @Override public void onSignalingChange(PeerConnection.SignalingState signalingState) {}
            @Override
            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
                Log.d(TAG, "ICE State for " + userId + ": " + iceConnectionState);
                if (multiPeerObserver != null) multiPeerObserver.onConnectionChange(userId, iceConnectionState);
            }
            @Override public void onIceConnectionReceivingChange(boolean b) {}
            @Override public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {}
            @Override public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {}
            @Override public void onAddStream(MediaStream mediaStream) {}
            @Override public void onRemoveStream(MediaStream mediaStream) {}
            @Override public void onDataChannel(DataChannel dataChannel) {}
            @Override public void onRenegotiationNeeded() {}
        });

        if (localVideoTrack != null) pc.addTrack(localVideoTrack, Collections.singletonList("STREAM"));
        if (localAudioTrack != null) pc.addTrack(localAudioTrack, Collections.singletonList("STREAM"));

        peerConnections.put(userId, pc);
        return pc;
    }

    public void createOffer(String userId, SdpObserver sdpObserver) {
        PeerConnection pc = getOrCreatePeerConnection(userId);
        MediaConstraints constraints = new MediaConstraints();
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pc.createOffer(sdpObserver, constraints);
    }

    public void createAnswer(String userId, SdpObserver sdpObserver) {
        PeerConnection pc = getOrCreatePeerConnection(userId);
        MediaConstraints constraints = new MediaConstraints();
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        constraints.mandatory.add(new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        pc.createAnswer(sdpObserver, constraints);
    }

    public void setLocalDescription(String userId, SessionDescription sdp) {
        PeerConnection pc = peerConnections.get(userId);
        if (pc != null) pc.setLocalDescription(new SimpleSdpObserver(), sdp);
    }

    public void setRemoteDescription(String userId, SessionDescription sdp) {
        PeerConnection pc = getOrCreatePeerConnection(userId);
        pc.setRemoteDescription(new SimpleSdpObserver(), sdp);
    }

    public void addIceCandidate(String userId, IceCandidate candidate) {
        PeerConnection pc = peerConnections.get(userId);
        if (pc != null) pc.addIceCandidate(candidate);
    }

    public void setLocalVideoSink(VideoSink sink) {
        if (localVideoTrack != null) localVideoTrack.addSink(sink);
    }

    public EglBase.Context getEglContext() {
        return eglBase.getEglBaseContext();
    }

    public void removePeer(String userId) {
        PeerConnection pc = peerConnections.remove(userId);
        if (pc != null) {
            pc.dispose();
        }
    }

    public void dispose() {
        try {
            if (videoCapturer != null) {
                videoCapturer.stopCapture();
                videoCapturer.dispose();
            }
            if (surfaceTextureHelper != null) surfaceTextureHelper.dispose();
            for (PeerConnection pc : peerConnections.values()) {
                pc.dispose();
            }
            peerConnections.clear();
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
