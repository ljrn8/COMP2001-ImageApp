package com.example.basicremotecall;

import android.app.Activity;
import android.net.Uri;

import java.net.HttpURLConnection;

public class APISearchThread extends Thread {

    private String searchkey;
    private String baseUrl;
    private RemoteUtilities remoteUtilities;
    private SearchResponseViewModel viewModel;

    public APISearchThread(String searchKey, Activity uiActivity, SearchResponseViewModel viewModel) {
        this.searchkey = searchKey;
        baseUrl = "https://pixabay.com/api/"; // TODO
        remoteUtilities = RemoteUtilities.getInstance(uiActivity);
        this.viewModel = viewModel;
    }

    public void run() {
        String endpoint = getSearchEndpoint();
        HttpURLConnection connection = remoteUtilities.openConnection(endpoint);
        if (connection != null) {
            if (remoteUtilities.isConnectionOkay(connection)) {
                String response = remoteUtilities.getResponseString(connection);
                connection.disconnect();
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {

                }
                viewModel.setResponse(response);
            }
        }

    }

    private String getSearchEndpoint() {
        Uri.Builder url = Uri.parse(this.baseUrl).buildUpon();
//        url.appendQueryParameter("key", "23319229-94b52a4727158e1dc3fd5f2db");
        url.appendQueryParameter("key", "39887637-cdc93a1a94a73ff21549db346");
        url.appendQueryParameter("q", this.searchkey);
        url.appendQueryParameter("safesearch", "true");
        url.appendQueryParameter("per_page", "15"); // 15 images max
        return url.build().toString();
    }


}
