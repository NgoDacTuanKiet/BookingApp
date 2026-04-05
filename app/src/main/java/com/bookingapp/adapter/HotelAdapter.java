package com.bookingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bookingapp.R;
import com.bookingapp.model.Hotel;
import com.bumptech.glide.Glide;
import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private List<Hotel> hotels;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Hotel hotel);
    }

    public HotelAdapter(List<Hotel> hotels, OnItemClickListener listener) {
        this.hotels = hotels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hotel, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotels.get(position);
        holder.tvName.setText(hotel.name);
        holder.tvLocation.setText(hotel.city + ", " + hotel.address);
        
        Glide.with(holder.itemView.getContext())
                .load(hotel.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.ivHotel);
        
        holder.itemView.setOnClickListener(v -> listener.onItemClick(hotel));
    }

    @Override
    public int getItemCount() {
        return hotels != null ? hotels.size() : 0;
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHotel;
        TextView tvName, tvLocation;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHotel = itemView.findViewById(R.id.hotelImage);
            tvName = itemView.findViewById(R.id.hotelName);
            tvLocation = itemView.findViewById(R.id.hotelLocation);
        }
    }
}
