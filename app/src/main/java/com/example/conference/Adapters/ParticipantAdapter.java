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
    private VideoTrack remoteVideoTrack;

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
        participants.add(participant);
        notifyItemInserted(participants.size() - 1);
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

            if (position == 0) {
                holder.videoView.setMirror(true); // Зеркалим себя
                repository.setLocalVideoSink(holder.videoView);
            } else {
                holder.videoView.setMirror(false);
                // Если это удаленный участник и у нас есть трек — подключаем его
                if (remoteVideoTrack != null) {
                    remoteVideoTrack.addSink(holder.videoView);
                }
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull ParticipantViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.videoView != null) {
            // Перед очисткой отписываемся от треков, если это был удаленный участник
            if (holder.getBindingAdapterPosition() != 0 && remoteVideoTrack != null) {
                remoteVideoTrack.removeSink(holder.videoView);
            }
            holder.videoView.release();
        }
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }

    public void setRemoteTrack(VideoTrack videoTrack) {
        this.remoteVideoTrack = videoTrack;
        // Обновляем только удаленных участников (начиная с индекса 1)
        if (participants.size() > 1) {
            for (int i = 1; i < participants.size(); i++) {
                notifyItemChanged(i);
            }
        }
    }
}
