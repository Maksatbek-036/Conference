package com.example.conference.Adapters;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Participant;
import com.example.conference.R;
import com.example.conference.databinding.ParticipantItemBinding;
import com.example.conference.databinding.ParticipantItemFragBinding;

public class ParticipantFragViewHolder extends RecyclerView.ViewHolder {
ParticipantItemFragBinding binding;

    public ParticipantFragViewHolder(@NonNull View itemView) {
        super(itemView);
     binding=ParticipantItemFragBinding.bind(itemView);

    }

    public void bind(Participant participant) {
binding.participantName.setText(participant.getName());
if(participant.getAvatarUrl()==null){
    binding.avatarUrl.setImageResource(R.drawable.iconstack_io____user_);
}else{
    binding.avatarUrl.setImageResource(R.drawable.iconstack_io____user_);
}

if(participant.isMuted()){
    binding.mic.setImageResource(R.drawable.iconstack_io____mic_off_);
}

}
}


