package com.example.conference.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.conference.Models.Participant;
import com.example.conference.R;
import com.example.conference.Repositories.VideoCallRepository;
import org.webrtc.EglBase;
import java.util.ArrayList;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantViewHolder> {

    private ArrayList<Participant> participants;
    private final EglBase.Context eglContext;
    private final VideoCallRepository repository;

    // Конструктор теперь принимает контекст WebRTC и репозиторий
    public ParticipantAdapter(ArrayList<Participant> participants,
                              EglBase.Context eglContext,
                              VideoCallRepository repository) {
        this.participants = participants;
        this.eglContext = eglContext;
        this.repository = repository;
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

        // Инициализируем SurfaceViewRenderer внутри холдера
        // videoView должен быть определен в вашем ParticipantViewHolder
        if (holder.videoView != null) {
            holder.videoView.init(eglContext, null);
            holder.videoView.setEnableHardwareScaler(true);

            // Если это первый элемент — подключаем локальную камеру
            if (position == 0) {
                holder.videoView.setMirror(true); // Зеркалим себя
                repository.setLocalVideoSink(holder.videoView);
            } else {
                holder.videoView.setMirror(false);
                // Здесь будет логика для удаленных треков (remoteVideoTrack)
            }
        }
    }

    @Override
    public void onViewRecycled(@NonNull ParticipantViewHolder holder) {
        super.onViewRecycled(holder);
        // Важно: освобождаем ресурсы при переиспользовании ячейки
        if (holder.videoView != null) {
            holder.videoView.release();
        }
    }

    @Override
    public int getItemCount() {
        return participants.size();
    }
}