package com.bookingapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.R;
import com.bookingapp.model.Hotel;
import com.bumptech.glide.Glide;

import java.util.List;

public class VendorHotelAdapter extends RecyclerView.Adapter<VendorHotelAdapter.ViewHolder> {

    private List<Hotel> hotels;
    private OnHotelClickListener listener;

    public interface OnHotelClickListener {
        void onEditClick(Hotel hotel);
        void onDeleteClick(Hotel hotel);
    }

    public VendorHotelAdapter(List<Hotel> hotels, OnHotelClickListener listener) {
        this.hotels = hotels;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vendor_hotel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hotel hotel = hotels.get(position);
        holder.tvHotelName.setText(hotel.name);
        holder.tvHotelAddress.setText(hotel.address + ", " + hotel.city);
        holder.tvHotelPrice.setText("$" + hotel.price + " / night");

        Glide.with(holder.itemView.getContext())
                .load(hotel.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.ivHotelImage);

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(hotel));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(hotel));
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

    public void setHotels(List<Hotel> hotels) {
        this.hotels = hotels;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHotelImage;
        TextView tvHotelName, tvHotelAddress, tvHotelPrice;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivHotelImage = itemView.findViewById(R.id.ivHotelImage);
            tvHotelName = itemView.findViewById(R.id.tvHotelName);
            tvHotelAddress = itemView.findViewById(R.id.tvHotelAddress);
            tvHotelPrice = itemView.findViewById(R.id.tvHotelPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
