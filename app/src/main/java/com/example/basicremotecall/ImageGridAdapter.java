package com.example.basicremotecall;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ImageGridAdapter extends ArrayAdapter<ImageView> {

    public ImageGridAdapter(@NonNull Context context, List<ImageView> imageViews) {
        super(context, 0, imageViews);
    }

    // adapted from https://www.geeksforgeeks.org/gridview-in-android-with-example/
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ImageView imageView = getItem(position);

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_layout, parent, false);
        }

        assert listitemView != null;

        // redundant
        ImageView gridItemImageView = listitemView.findViewById(R.id.gridItemImageView);


        ViewGroup parentView = (ViewGroup) listitemView;
        parentView.removeAllViews();

        parentView.addView(imageView);
        return listitemView;
    }
}