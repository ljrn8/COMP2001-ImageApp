package com.example.basicremotecall;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SearchResponseViewModel sViewModel;
    ImageViewModel imageViewModel;
    ErrorViewModel errorViewModel;
    Button loadImage, toggle;
    ProgressBar progressBar;
    EditText searchKey;

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu, menu);
//        return true;
//    }

    @Override // TODO menu items response here
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_item1) {
            // Handle item 1 click
            return true;
        } else if (id == R.id.action_item2) {
            // Handle item 2 click
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

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


//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        loadImage = findViewById(R.id.loadImage);
        progressBar = findViewById(R.id.progressBarId);
        searchKey = findViewById(R.id.inputSearch);

        progressBar.setVisibility(View.INVISIBLE);

        loadImage.setOnClickListener(view -> {
            String searchValues = searchKey.getText().toString();
            APISearchThread searchThread = new APISearchThread(searchValues,MainActivity.this,sViewModel);
            progressBar.setVisibility(View.VISIBLE);
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
                // TODO select listeneres here

                fillLayouts();

                Log.i(t, "observer added image n " + i + "bitmap = " + imageView);
            }
        });
        errorViewModel.errorCode.observe(this, integer -> progressBar.setVisibility(View.INVISIBLE));

        toggle = findViewById(R.id.toggle);
        toggle.setOnClickListener(v -> toggleGrid());

        // TODO DEL default testing images
        for (int i = 0; i < 25; i++) {
            ImageView im = new ImageView(this);
            im.setImageResource(R.drawable.ic_launcher_foreground);
            imagesViews.add(im);
            fillLayouts();
        }
    }


    public Bitmap resizeImageByWidth(Bitmap originalImage, int targetWidth) {
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        float scaleFactor = ((float) targetWidth) / width;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);

        return Bitmap.createBitmap(
                originalImage, 0, 0, width, height, matrix, false);
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
        ImageGridAdapter adapter = new ImageGridAdapter(this, imagesViews); // TODO copy?
        gridView.setAdapter(adapter);


//        int screenWidth = getResources().getDisplayMetrics().widthPixels;
//        for (int i = 0; i < imagesViews.size(); i++) {
//            ImageView oldImageView = imagesViews.get(i);
//
//            ImageView imageView = new ImageView(this);
//            imageView.setImageDrawable(oldImageView.getDrawable());
//
//            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
//            params.width = screenWidth / 2;
//            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
//
//            imageView.setLayoutParams(params);
//
//            GridLayout.Spec rowSpec = GridLayout.spec(i / 2); // row
//            GridLayout.Spec colSpec = GridLayout.spec(i % 2); // col
//            GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams(rowSpec, colSpec);
//            imageView.setLayoutParams(gridParams);
//            Log.i(t, "added image to grid layout -> [" + (i / 2) + "][" + (i % 2) + "]" + " " + imageView.toString());
//            grid.addView(imageView);
//        }
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