package com.bookingapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bookingapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.slider.RangeSlider;

import java.util.List;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    public interface OnFilterAppliedListener {
        void onFilterApplied(double minPrice, double maxPrice, float minRating, boolean hasWifi, boolean hasPool, boolean hasFoodCourt, boolean hasPark);
        void onFilterCleared();
    }

    private OnFilterAppliedListener listener;
    private RangeSlider priceRangeSlider;
    private RatingBar ratingBar;
    private CheckBox cbWifi, cbPool, cbFoodCourt, cbPark;
    private Button btnApply, btnClear;

    public void setOnFilterAppliedListener(OnFilterAppliedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_filter_bottom_sheet, container, false);

        priceRangeSlider = view.findViewById(R.id.priceRangeSlider);
        ratingBar = view.findViewById(R.id.ratingBarFilter);
        cbWifi = view.findViewById(R.id.cbWifi);
        cbPool = view.findViewById(R.id.cbPool);
        cbFoodCourt = view.findViewById(R.id.cbFoodCourt);
        cbPark = view.findViewById(R.id.cbPark);
        btnApply = view.findViewById(R.id.btnApplyFilter);
        btnClear = view.findViewById(R.id.btnClearFilter);

        btnApply.setOnClickListener(v -> {
            if (listener != null) {
                List<Float> values = priceRangeSlider.getValues();
                double minPrice = values.get(0);
                double maxPrice = values.get(1);
                float minRating = ratingBar.getRating();
                boolean hasWifi = cbWifi.isChecked();
                boolean hasPool = cbPool.isChecked();
                boolean hasFoodCourt = cbFoodCourt.isChecked();
                boolean hasPark = cbPark.isChecked();

                listener.onFilterApplied(minPrice, maxPrice, minRating, hasWifi, hasPool, hasFoodCourt, hasPark);
            }
            dismiss();
        });

        btnClear.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFilterCleared();
            }
            dismiss();
        });

        return view;
    }
}
