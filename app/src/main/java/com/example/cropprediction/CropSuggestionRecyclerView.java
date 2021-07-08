package com.example.cropprediction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CropSuggestionRecyclerView extends RecyclerView.Adapter<CropSuggestionRecyclerView.MyViewHolder> {

    String[] suggestions;

    CropSuggestionRecyclerView(String[] suggestions){
        this.suggestions=suggestions;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.suggestion.setText(suggestions[position]);
    }
    @Override
    public int getItemCount() {
        return suggestions.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView suggestion;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            suggestion=itemView.findViewById(R.id.suggestions);
        }
    }
}
