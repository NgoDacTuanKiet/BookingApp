package com.bookingapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.R;
import com.bookingapp.activities.ManageRoomsActivity;
import com.bookingapp.model.Hotel;
import com.bumptech.glide.Glide;

import java.util.List;

public class ManageRoomsHotelAdapter extends RecyclerView.Adapter<ManageRoomsHotelAdapter.HotelViewHolder> {

    private Context context;
    private List<Hotel> hotelList;

    public ManageRoomsHotelAdapter(Context context, List<Hotel> hotelList) {
        this.context = context;
        this.hotelList = hotelList;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_room, parent, false); // Reuse existing layout style or create new
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = hotelList.get(position);
        holder.tvName.setText(hotel.name);
        holder.tvAddress.setText(hotel.address);

        Glide.with(context)
                .load(hotel.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgHotel);

        // Hide edit/delete buttons if reusing item_manage_room layout
        View btnEdit = holder.itemView.findViewById(R.id.btnEdit);
        View btnDelete = holder.itemView.findViewById(R.id.btnDelete);
        if (btnEdit != null) btnEdit.setVisibility(View.GONE);
        if (btnDelete != null) btnDelete.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ManageRoomsActivity.class);
            intent.putExtra("HOTEL_ID", hotel.id);
            intent.putExtra("HOTEL_NAME", hotel.name);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return hotelList.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHotel;
        TextView tvName, tvAddress;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            imgHotel = itemView.findViewById(R.id.imgRoom); // Reusing ID from item_manage_room
            tvName = itemView.findViewById(R.id.tvRoomType); // Reusing ID from item_manage_room
            tvAddress = itemView.findViewById(R.id.tvRoomPrice); // Reusing ID from item_manage_room
        }
    }
}
