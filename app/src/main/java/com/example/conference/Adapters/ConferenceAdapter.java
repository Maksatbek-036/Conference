package com.example.conference.Adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.conference.Models.Conference;
import com.example.conference.R;

import java.util.ArrayList;
import java.util.List;

public class ConferenceAdapter extends RecyclerView.Adapter<ConferenceViewHolder>{
    private ArrayList<Conference> conferences;

    public ConferenceAdapter(ArrayList<Conference> conferences) {
        this.conferences = conferences != null ? conferences : new ArrayList<>();
    }

    @NonNull
    @Override
    public ConferenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConferenceViewHolder(LayoutInflater.
                from(parent.getContext())
                .inflate(R.layout.plan_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConferenceViewHolder holder, int position) {
        holder.bind(conferences.get(position));
    }

    @Override
    public int getItemCount() {
        return conferences != null ? conferences.size() : 0;
    }

    public void setConferences(List<Conference> conferences) {
        if (this.conferences == null) {
            this.conferences = new ArrayList<>();
        }
        this.conferences.clear();
        if (conferences != null) {
            this.conferences.addAll(conferences);
        }
        notifyDataSetChanged();
    }
}
