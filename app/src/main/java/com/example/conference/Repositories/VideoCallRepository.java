package com.example.conference.Repositories;

import android.content.Context;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import java.util.ArrayList;
import java.util.List;

public class VideoCallRepository {
    private PeerConnectionFactory factory;
    private PeerConnection peerConnection;
    private EglBase eglBase;
    private final Context context;
    private PeerConnection.Observer observer;

    // Исправлено: используем стандартный Context и инициализируем eglBase
    public VideoCallRepository(Context context, PeerConnection.Observer observer) {
        this.context = context;
        this.observer = observer;
        this.eglBase = EglBase.create(); // Обязательно создаем экземпляр
    }

    public void initWebRTC() {
        // 1. Глобальная инициализация
        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .setEnableInternalTracer(true)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);

        // 2. Настройка видео-кодеков
        VideoEncoderFactory encoderFactory = new DefaultVideoEncoderFactory(
                eglBase.getEglBaseContext(), true, true);
        VideoDecoderFactory decoderFactory = new DefaultVideoDecoderFactory(
                eglBase.getEglBaseContext());

        // 3. Создание фабрики соединений
        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        factory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(encoderFactory)
                .setVideoDecoderFactory(decoderFactory)
                .createPeerConnectionFactory();
    }

    /**
     * Вход в звонок с ICE серверами
     */
    public void joinCall(List<PeerConnection.IceServer> iceServers) {
        if (factory == null) {
            throw new IllegalStateException("Сначала вызовите initWebRTC()");
        }

        PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
        rtcConfig.sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN;

        // Создаем соединение с переданным observer
        peerConnection = factory.createPeerConnection(rtcConfig, observer);
    }

    /**
     * Перегруженный метод joinCall для простого теста (пустой список серверов)
     */
    public void joinCall() {
        List<PeerConnection.IceServer> defaultIceServers = new ArrayList<>();
        // Желательно добавить хотя бы один публичный STUN сервер Google
        defaultIceServers.add(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer());
        joinCall(defaultIceServers);
    }

    public EglBase.Context getEglContext() {
        return eglBase.getEglBaseContext();
    }

    public PeerConnection getPeerConnection() {
        return peerConnection;
    }
}