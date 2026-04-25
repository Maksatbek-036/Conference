package com.example.conference.ViewModels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.conference.Api.AuthApi;
import com.example.conference.Contracts.LoginUserRequest;
import com.example.conference.Contracts.RegisterUserRequest;
import com.example.conference.Models.User;
import com.example.conference.Repositories.AuthRepository;
import com.example.conference.Repositories.VideoCallRepository;
import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionBuilder;

import org.webrtc.DataChannel;
import org.webrtc.IceCandidate;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.RtpReceiver;
import org.webrtc.SdpObserver;
import org.webrtc.SessionDescription;
import org.webrtc.VideoTrack;

public class AuthViewModel extends ViewModel {
    public MutableLiveData<User> user = new MutableLiveData<>();
    private final AuthRepository repository;
    private String token;

    // Конструктор принимает AuthApi и создаёт репозиторий
    public AuthViewModel(AuthApi authApi) {
        this.repository = new AuthRepository();
    }

    public void register(String name, String email, String password) {
        repository.authenticateUser(RegisterUserRequest.Create(name, email, password));
    }

    public String login(String email, String password) {
        return repository.oauthLogin(LoginUserRequest.Create(email, password));
    }
}
