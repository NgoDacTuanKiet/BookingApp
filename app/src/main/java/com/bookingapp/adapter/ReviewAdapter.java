package com.bookingapp.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bookingapp.R;
import com.bookingapp.model.Review;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<Review> reviewList;
    private OnReviewLongClickListener longClickListener;
    private int currentUserId;

    public interface OnReviewLongClickListener {
        void onReviewLongClick(Review review);
    }

    public ReviewAdapter(Context context, List<Review> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getInt("userId", -1);
    }

    public void setOnReviewLongClickListener(OnReviewLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviewList.get(position);
        holder.tvUserName.setText(review.userName);
        holder.tvDate.setText(review.date);
        holder.rbRating.setRating(review.rating);
        holder.tvComment.setText(review.comment);

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null && review.userId == currentUserId) {
                longClickListener.onReviewLongClick(review);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return reviewList != null ? reviewList.size() : 0;
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvDate, tvComment;
        RatingBar rbRating;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvReviewUserName);
            tvDate = itemView.findViewById(R.id.tvReviewDate);
            tvComment = itemView.findViewById(R.id.tvReviewComment);
            rbRating = itemView.findViewById(R.id.rbReviewRating);
        }
    }
}
