package com.bookingapp.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bookingapp.R;
import com.bookingapp.activities.AddEditRoomActivity;
import com.bookingapp.dal.AppDatabase;
import com.bookingapp.model.Room;
import com.bumptech.glide.Glide;

import java.util.List;

public class ManageRoomAdapter extends RecyclerView.Adapter<ManageRoomAdapter.ManageRoomViewHolder> {

    private Context context;
    private List<Room> roomList;
    private OnRoomDeletedListener listener;

    public interface OnRoomDeletedListener {
        void onRoomDeleted();
    }

    public ManageRoomAdapter(Context context, List<Room> roomList, OnRoomDeletedListener listener) {
        this.context = context;
        this.roomList = roomList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ManageRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_manage_room, parent, false);
        return new ManageRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageRoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.tvRoomType.setText(room.roomType);
        holder.tvRoomPrice.setText("$" + room.price + " / đêm");

        Glide.with(context)
                .load(room.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imgRoom);

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddEditRoomActivity.class);
            intent.putExtra("ROOM_ID", room.id);
            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Xác nhận xóa")
                    .setMessage("Bạn có chắc chắn muốn xóa phòng này không?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        AppDatabase.getInstance(context).roomDao().delete(room);
                        Toast.makeText(context, "Đã xóa phòng", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onRoomDeleted();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class ManageRoomViewHolder extends RecyclerView.ViewHolder {
        ImageView imgRoom;
        TextView tvRoomType, tvRoomPrice;
        ImageButton btnEdit, btnDelete;

        public ManageRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            imgRoom = itemView.findViewById(R.id.imgRoom);
            tvRoomType = itemView.findViewById(R.id.tvRoomType);
            tvRoomPrice = itemView.findViewById(R.id.tvRoomPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
