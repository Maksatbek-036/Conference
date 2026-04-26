package com.example.conference.Adapters;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Conference;
import com.example.conference.Models.Message;
import com.example.conference.Models.Participant;
import com.example.conference.R;
import com.example.conference.VideoHub;

public class ConferenceViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView identifity;
    TextView date;
    Button startButton;

    public ConferenceViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.list_plans_name);
        identifity = itemView.findViewById(R.id.list_plans_identifity);
        date = itemView.findViewById(R.id.list_plans_time);
        startButton = itemView.findViewById(R.id.start_button);
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(itemView.getContext(), VideoHub.class);
            itemView.getContext().startActivity(intent);
        });
    }

    public void bind(Conference conference) {
        title.setText(conference.getTitles());
        identifity.setText(conference.getDescription());
        date.setText(conference.getDate());
    }


}