package com.example.conference.Adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Conference;
import com.example.conference.R;

public class ParticipantViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView identifity;
    TextView date;

    public ParticipantViewHolder(@NonNull View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.list_plans_name);
        identifity = itemView.findViewById(R.id.list_plans_identifity);
        date = itemView.findViewById(R.id.list_plans_time);
    }

    public void bind(Conference conference) {
        title.setText(conference.getTitles());
        identifity.setText(conference.getDescription());
        date.setText(conference.getDate());
    }

}