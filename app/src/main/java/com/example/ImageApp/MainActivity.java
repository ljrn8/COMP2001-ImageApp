package com.example.ImageApp;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    SearchResponseViewModel sViewModel;
    ImageViewModel imageViewModel;
    ErrorViewModel errorViewModel;
    Button loadImage, toggle;
    ProgressBar progressBar;
    EditText searchKey;



    ScrollView scrollNormal;
    GridView gridView;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(SearchResponseViewModel.class);
        imageViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(ImageViewModel.class);
        errorViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(ErrorViewModel.class);



        scrollNormal = findViewById(R.id.scroll);
        gridView = findViewById(R.id.gridView);
        gridView.setVisibility(View.INVISIBLE);

        loadImage = findViewById(R.id.loadImage);
        progressBar = findViewById(R.id.progressBarId);
        searchKey = findViewById(R.id.inputSearch);

        progressBar.setVisibility(View.INVISIBLE);

        loadImage.setOnClickListener(view -> {
            String searchValues = searchKey.getText().toString();
            APISearchThread searchThread = new APISearchThread(searchValues,MainActivity.this,sViewModel);
            progressBar.setVisibility(View.VISIBLE);
            loadImage.setEnabled(false);
            searchThread.start();
        });

        sViewModel.response.observe(this, s -> {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(MainActivity.        // toggle
this, "Search Complete, Downloading Images",Toast.LENGTH_LONG).show();
            ImageRetrievalThread imageRetrievalThread = new ImageRetrievalThread(MainActivity.this, sViewModel, imageViewModel, errorViewModel);
            progressBar.setVisibility(View.VISIBLE);
            imageRetrievalThread.start();

        });

        linearLayout = findViewById(R.id.linear);

        // imported images
        imageViewModel.images.observe(this, bitmaps -> {
            progressBar.setVisibility(View.INVISIBLE);
            Log.i(t, bitmaps.toString());
            linearLayout.removeAllViews();
            imagesViews.clear();

            for (int i = 0; i < bitmaps.size(); i++) {
                ImageView imageView = new ImageView(this);
                imageView.setImageBitmap(imageViewModel.getImages().get(i));
                imagesViews.add(imageView);


                int finalI = i;
                imageView.setOnClickListener(v -> showPopup(imageViewModel.getImages().get(finalI)));

                fillLayouts();
                Log.i(t, "observer added image n " + i + "bitmap = " + imageView);
            }
            loadImage.setEnabled(true);
        });

        errorViewModel.errorCode.observe(this, integer -> {
            loadImage.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
        });

        toggle = findViewById(R.id.toggle);
        toggle.setOnClickListener(v -> toggleGrid());



    }


    private void uploadImage(Bitmap bitmap) {
        File imageFile = new File(this.getCacheDir(), "image.jpg"); // Create a temporary file
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // Save the bitmap to the file
            fos.flush();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // https://www.youtube.com/watch?v=CQ5qcJetYAI
        final String randomKey = UUID.randomUUID().toString();

        Uri imageUri = Uri.fromFile(imageFile);

        StorageReference imageRef = storageRef.child("images/" + randomKey);
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(this, "successfully uploaded image", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "failed to uploaded image, something went wrong", Toast.LENGTH_LONG).show();
                });
    }

    private FirebaseStorage storage;
    private StorageReference storageRef;
    public void showPopup(Bitmap bitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upload Image");
        builder.setMessage("upload this image to firebase?");
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        builder.setPositiveButton("Yes", (dialog, which) -> {
            uploadImage(bitmap);
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    List<ImageView> imagesViews = new ArrayList<>();

    public void fillLayouts() {
        String t = "layouts";

        // LL
        linearLayout.removeAllViews();
        imagesViews.forEach(image -> {
            Log.i(t, "added image linear layout -> " + image.toString());
            image.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            linearLayout.addView(image);
        });

        // GRID
        if (imageViewModel.getImages() != null) {
            ImageGridAdapter adapter = new ImageGridAdapter(this, R.layout.grid_item_layout, imageViewModel.getImages(), this); // TODO copy?
            gridView.setAdapter(adapter);
        }

    }

    boolean isGrid = false;
    public void toggleGrid() {
        if (!isGrid) {
            scrollNormal.setVisibility(View.INVISIBLE);
            gridView.setVisibility(View.VISIBLE);
            isGrid = true;

        } else {
            scrollNormal.setVisibility(View.VISIBLE);
            gridView.setVisibility(View.INVISIBLE);
            isGrid = false;
        }
        fillLayouts();
    }

    public final String t = "res";


}