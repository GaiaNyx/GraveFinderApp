package com.example.gravefinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder>{

    private ArrayList<ImageMemo> images;
    private View.OnClickListener onItemClickListener;


    public ImageAdapter(ArrayList<ImageMemo> images) {
        this.images = images;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View imageView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_image_item,
                parent,false);
        return new ImageAdapter.ImageViewHolder(imageView);    }

    public void setOnItemClickListener(View.OnClickListener itemClickListener){
        onItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        if(images == null)
            return 0;
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivStorageImage);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageMemo image = images.get(position);
        //holder.ivImage.setImageResource();
        Glide.with(holder.ivImage.getContext() /* context */)
                .load(image.getStorageFilePath().toString())
                .into(holder.ivImage);
    }
}
