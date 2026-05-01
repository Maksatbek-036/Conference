package com.example.conference.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.conference.Models.Participant;
import com.example.conference.R;
import com.example.conference.Repositories.VideoCallRepository;
import org.webrtc.EglBase;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantViewHolder> {

    private ArrayList<Participant> participants;
    private final EglBase.Context eglContext;
    private final VideoCallRepository repository;
    private OnItemClickListener clickListener;
    private Map<String, VideoTrack> remoteVideoTracks = new HashMap<>();

    public interface OnItemClickListener {
        void onItemClick();
    }

    public ParticipantAdapter(ArrayList<Participant> participants,
                              EglBase.Context eglContext,
                              VideoCallRepository repository) {
        this.participants = participants;
        this.eglContext = eglContext;
        this.repository = repository;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void addParticipant(Participant participant) {
        for (Participant p : participants) {
            if (p.getId().equals(participant.getId())) return;
        }
        participants.add(participant);
        notifyItemInserted(participants.size() - 1);
    }

    public void removeParticipant(String userId) {
        int index = -1;
        for (int i = 0; i < participants.size(); i++) {
            if (participants.get(i).getId().equals(userId)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            participants.remove(index);
            notifyItemRemoved(index);
        }
    }

    @NonNull
    @Override
    public ParticipantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParticipantViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participant_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ParticipantViewHolder holder, int position) {
        Participant participant = participants.get(position);
        holder.bind(participant);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick();
            }
        });

        if (holder.videoView != null) {
            try {
                holder.videoView.init(eglContext, null);
                holder.videoView.setEnableHardwareScaler(true);
            } catch (Exception e) {
                // Ignore if already initialized
            }

            if (position == 0) {
                holder.videoView.setMirror(true);
                repository.setLocalVideoSink(holder.videoView);
            } else {
                holder.videoView.setMirror(false);
                VideoTrack track = remoteVideoTracks.get(participant.getId());
                if (track != null) {
                    track.addSink(holder.videoView);
                }
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull ParticipantViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.videoView != null) {
            // Important: don't release here if you want to reuse the view properly in a grid
            // or ensure it's re-initialized in onBind
            holder.videoView.release();
        }
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public void setRemoteTracks(Map<String, VideoTrack> tracks) {
        this.remoteVideoTracks = new HashMap<>(tracks);
        notifyDataSetChanged();
    }
}
