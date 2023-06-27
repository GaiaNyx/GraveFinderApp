package com.example.gravefinder;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class GraveAdapter extends RecyclerView.Adapter<GraveAdapter.GraveViewHolder> {

    private ArrayList<Grave> graves;
    private View.OnClickListener onItemClickListener;

    public GraveAdapter(ArrayList<Grave> graves) {
        this.graves = graves;
    }

    @NonNull
    @Override
    public GraveAdapter.GraveViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View graveView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_grave_item,parent,false);
        return new GraveViewHolder(graveView);
    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener){
        onItemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull GraveAdapter.GraveViewHolder holder, int position) {
        Grave grave = graves.get(position);
        holder.tvGraveName.setText(grave.getGraveName());
        holder.tvBirth.setText(String.valueOf(grave.getBirthDate()));
        holder.tvDeath.setText(String.valueOf(grave.getDeathDate()));
    }

    @Override
    public int getItemCount() {
        if(graves == null){
            return 0;
        }
        else {
            return graves.size();
        }
    }

public class GraveViewHolder extends RecyclerView.ViewHolder{

        public TextView tvGraveName;
        public TextView tvBirth;
        public TextView tvDeath;

    public GraveViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGraveName = itemView.findViewById(R.id.tvGraveName);
            tvBirth = itemView.findViewById(R.id.tvBirthDate);
            tvDeath = itemView.findViewById(R.id.tvDeathDate);


            itemView.setTag(this);
            itemView.setOnClickListener(onItemClickListener);
        }
    }
}
