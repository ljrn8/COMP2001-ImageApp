package com.example.basicremotecall;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

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

    // from android docs
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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

//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            options.inSampleSize = 2;
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
//            // BitmapFactory.decodeResource(uiActivity.getResources(), R.mipmap.hqimage, options);

            List<Bitmap> incomingBitmaps = new ArrayList<>();
//            uiActivity.imageLoadMessage(incomingBitmaps.size());
            endpoints.forEach(endpoint -> incomingBitmaps.add(getImageFromUrl(endpoint)));

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
                String imageUrl = jHitsItem.getString("largeImageURL");
                imageUrls.add(imageUrl);
            }

//            if (jHits.length() > 0) {
//                // for (JSONObject jHitsItem: jHits.getJSONArray())
//                JSONObject jHitsItem = jHits.getJSONObject(0);
//                imageUrl = jHitsItem.getString("largeImageURL");
//            }

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
