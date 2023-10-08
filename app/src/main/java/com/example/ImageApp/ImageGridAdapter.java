package com.example.ImageApp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ImageGridAdapter extends ArrayAdapter<Bitmap> {

    MainActivity activity;
    public ImageGridAdapter(@NonNull Context context, int resource, @NonNull List<Bitmap> objects, MainActivity activity) {
        super(context, resource, objects);
        this.activity = activity;
    }

    // adapted from https://www.geeksforgeeks.org/gridview-in-android-with-example/
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Bitmap bitmap = getItem(position);

        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_layout, parent, false);
        }

        assert listitemView != null;

        ImageView gridItemImageView = listitemView.findViewById(R.id.gridItemImageView);
        gridItemImageView.setImageBitmap(bitmap);

        gridItemImageView.setOnClickListener(v -> activity.showPopup(bitmap));
        return listitemView;
    }
}