package com.example.conference.Adapters;

import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Conference;
import com.example.conference.Models.Participant;
import com.example.conference.R;
import com.example.conference.databinding.ParticipantItemBinding;

public class ParticipantViewHolder extends RecyclerView.ViewHolder {
ParticipantItemBinding binding;

    public ParticipantViewHolder(@NonNull View itemView) {
        super(itemView);
     binding=ParticipantItemBinding.bind(itemView);

    }

    public void bind(Participant participant) {
binding.participantName.setText(participant.getName());

if(participant.getAvatarUrl()==null){
   binding.avatarImage.setImageResource(R.drawable.iconstack_io____user_);
}
else{
    binding.avatarImage.setImageResource(R.drawable.iconstack_io____user_);
}
if(participant.isVideoEnabled()){
    binding.videoSurface.setVisibility(View.VISIBLE);
}
else{
    binding.videoSurface.setVisibility(View.GONE);
}
    }

}