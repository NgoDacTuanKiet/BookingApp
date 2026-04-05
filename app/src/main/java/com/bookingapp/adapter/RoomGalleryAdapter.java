package com.bookingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.R;
import com.bumptech.glide.Glide;

import java.util.List;

public class RoomGalleryAdapter extends RecyclerView.Adapter<RoomGalleryAdapter.GalleryViewHolder> {

    private Context context;
    private List<String> images;
    private OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(String imageUrl);
    }

    public RoomGalleryAdapter(Context context, List<String> images, OnImageClickListener listener) {
        this.context = context;
        this.images = images;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room_image, parent, false);
        return new GalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        String url = images.get(position);
        Glide.with(context).load(url).centerCrop().into(holder.imageView);
        holder.itemView.setOnClickListener(v -> listener.onImageClick(url));
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public GalleryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivGalleryItem);
        }
    }
}
