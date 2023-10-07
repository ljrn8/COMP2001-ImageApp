package com.example.basicremotecall;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
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

    ScrollView scrollNormal, scrollGrid;
    GridLayout grid;
    LinearLayout linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(SearchResponseViewModel.class);
        imageViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(ImageViewModel.class);
        errorViewModel = new ViewModelProvider(this, (ViewModelProvider.Factory) new ViewModelProvider.NewInstanceFactory()).get(ErrorViewModel.class);



        grid = findViewById(R.id.grid);
        scrollGrid = findViewById(R.id.scrollGrid);

        // hide grid
        scrollGrid.setVisibility(View.INVISIBLE);

        scrollNormal = findViewById(R.id.scroll);

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
this, "Search Complete",Toast.LENGTH_LONG).show();
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
            for (int i = 0; i < bitmaps.size(); i++) {
                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                imageView.setImageBitmap(imageViewModel.getImages().get(i));
                linearLayout.addView(imageView);

                // grid TODO
                ImageView copyImageView = new ImageView(this); // Replace 'this' with your context
                copyImageView.setImageDrawable(imageView.getDrawable());
                copyImageView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                GridLayout.Spec rowSpec = GridLayout.spec(i / 2, 1); // row
                GridLayout.Spec colSpec = GridLayout.spec(i % 2, 1); // col
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(rowSpec, colSpec);
                // params.width = 0;
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                copyImageView.setLayoutParams(params);
                grid.addView(copyImageView);


                Log.i(t, "observer added image n " + i + "bitmap = " + imageView);
            }
        });
        errorViewModel.errorCode.observe(this, integer -> progressBar.setVisibility(View.INVISIBLE));

        toggle = findViewById(R.id.toggle);
        toggle.setOnClickListener(v -> {
            toggleGrid();
        });
    }

    public void imageLoadMessage(int n) {
        Toast.makeText(this, "loading " + n + " images ..", Toast.LENGTH_LONG)
                .show();
    }


    boolean isGrid = false;
    public void toggleGrid() {

        if (!isGrid) {
            scrollNormal.setVisibility(View.INVISIBLE);
            scrollGrid.setVisibility(View.VISIBLE);
            isGrid = true;
        } else {
            scrollNormal.setVisibility(View.VISIBLE);
            scrollGrid.setVisibility(View.INVISIBLE);
            isGrid = false;
        }
    }

    public final String t = "res";


}