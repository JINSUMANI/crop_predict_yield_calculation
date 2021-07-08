package com.example.cropprediction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<HistoryRecyclerAdapter.MyViewHolder> {
    ArrayList<HistoryModel> historyModels;

    OnItemClick onItemClick;

    HistoryRecyclerAdapter(ArrayList<HistoryModel> historyModels,OnItemClick onItemClick){
        this.historyModels=historyModels;
        this.onItemClick=onItemClick;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,parent,false);
        MyViewHolder myViewHolder=new MyViewHolder(view,onItemClick);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.cropName.setText(historyModels.get(position).getCropPredicted());
        holder.yield.setText(historyModels.get(position).getYieldValue().substring(0,5));
        holder.date.setText(historyModels.get(position).getDate().split("T")[0]);


    }

    @Override
    public int getItemCount() {
        return historyModels.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView cropName,yield,date;
        ImageView imageView;
        OnItemClick onItemClick;
        public MyViewHolder(@NonNull View itemView,OnItemClick onItemClick) {
            super(itemView);
            cropName=itemView.findViewById(R.id.cropName);
            yield=itemView.findViewById(R.id.yield);
            date=itemView.findViewById(R.id.date);
            imageView=itemView.findViewById(R.id.image);
            this.onItemClick=onItemClick;
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View v) {
            onItemClick.onHistoryClicked(getAdapterPosition());

        }
    }
    public interface OnItemClick{
        void onHistoryClicked(int position);
    }
}
