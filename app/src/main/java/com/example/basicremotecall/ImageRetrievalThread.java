package com.example.basicremotecall;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class ImageRetrievalThread extends Thread {

    private RemoteUtilities remoteUtilities;
    private SearchResponseViewModel sViewModel;
    private ImageViewModel imageViewModel;
    private ErrorViewModel errorViewModel;
    private Activity uiActivity;

    public ImageRetrievalThread(Activity uiActivity, SearchResponseViewModel viewModel, ImageViewModel imageViewModel, ErrorViewModel errorViewModel) {
        remoteUtilities = RemoteUtilities.getInstance(uiActivity);
        this.sViewModel = viewModel;
        this.imageViewModel = imageViewModel;
        this.errorViewModel = errorViewModel;
        this.uiActivity = uiActivity;
    }



    public void run() {
        List<String> endpoints = getEndpoint(sViewModel.getResponse());

        if (endpoints.size() == 0) {
            uiActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(uiActivity, "No image found", Toast.LENGTH_LONG).show();
                    errorViewModel.setErrorCode(errorViewModel.getErrorCode() + 1);
                }
            });

        } else {


            List<Bitmap> incomingBitmaps = new ArrayList<>();
            Log.i(t, "turning url -> bitmap");

            endpoints.forEach(endpoint -> {
                incomingBitmaps.add(getImageFromUrl(endpoint));
                Log.i(t, "encoded a bitmap");
            });


            try {
                Thread.sleep(3000);
            } catch (Exception e) {}
            imageViewModel.setImages(incomingBitmaps);
        }
    }
    public final String t = "res"; // log tag
    private List<String> getEndpoint(String data) {
        List<String> imageUrls = new ArrayList<>();
        try {
            JSONObject jBase = new JSONObject(data);

            Log.i(t, "json res >>" + jBase.toString());
            JSONArray jHits = jBase.getJSONArray("hits");

            for (int i = 0; i < jHits.length(); i++) {

                Log.i(t, "getting hit object n - " + i);
                JSONObject jHitsItem = jHits.getJSONObject(i);

                String imageUrl = jHitsItem.getString("webformatURL"); // or webformatURL previewURL
                imageUrls.add(imageUrl);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return imageUrls;
    }


    private Bitmap getImageFromUrl(String imageUrl) {
        Bitmap image = null;
        Uri.Builder url = Uri.parse(imageUrl).buildUpon();
        String urlString = url.build().toString();
        HttpURLConnection connection = remoteUtilities.openConnection(urlString);
        if (connection != null) {
            if (remoteUtilities.isConnectionOkay(connection)) {
                image = getBitmapFromConnection(connection);
                connection.disconnect();
            }
        }
        return image;
    }

    public Bitmap getBitmapFromConnection(HttpURLConnection conn) {
        Bitmap data = null;
        try {
            InputStream inputStream = conn.getInputStream();
            byte[] byteData = getByteArrayFromInputStream(inputStream);
            data = BitmapFactory.decodeByteArray(byteData, 0, byteData.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private byte[] getByteArrayFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

}
