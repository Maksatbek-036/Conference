package com.example.conference.Models;

public class Participant {
    private String id;
    private String name;
    private String avatarUrl;
    private boolean isMuted;
    private boolean isVideoEnabled;
   private long joinedAt;

    public Participant(String id, String name, String avatarUrl, boolean isMuted, boolean isVideoEnabled, long joinedAt) {
        this.id = id;
        this.name = name;
        this.avatarUrl = avatarUrl;
        this.isMuted = isMuted;
        this.isVideoEnabled = isVideoEnabled;
        this.joinedAt = joinedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public boolean isVideoEnabled() {
        return isVideoEnabled;
    }

    public void setVideoEnabled(boolean videoEnabled) {
        isVideoEnabled = videoEnabled;
    }

    public long getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(long joinedAt) {
        this.joinedAt = joinedAt;
    }
}
