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

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantViewHolder> {

    private ArrayList<Participant> participants;
    private final EglBase.Context eglContext;
    private final VideoCallRepository repository;
    private OnItemClickListener clickListener;

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

        // Инициализируем SurfaceViewRenderer внутри холдера
        if (holder.videoView != null) {
            holder.videoView.init(eglContext, null);
            holder.videoView.setEnableHardwareScaler(true);

            // Если это первый элемент — подключаем локальную камеру
            if (position == 0) {
                holder.videoView.setMirror(true); // Зеркалим себя
                repository.setLocalVideoSink(holder.videoView);
            } else {
                holder.videoView.setMirror(false);
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull ParticipantViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.videoView != null) {
            holder.videoView.release();
        }
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public void setRemoteTrack(VideoTrack videoTrack) {
        if (videoTrack == null) return;
        for (int i = 1; i < participants.size(); i++) {
            notifyItemChanged(i, videoTrack);
        }
    }
}
