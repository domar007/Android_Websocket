package com.example.beuth.taskql;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Stefan Völkel on 06.02.2016.
 */
public class Connection {
    private OkHttpClient client;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    Connection(){
        this.client = new OkHttpClient();
    }

    /**
     * Send post request with additional request parameters
     * @param url
     * @param json
     * @return
     * @throws IOException
     */
    public String doPostRequestWithAdditionalData(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Send post request with additional header
     * @param url
     * @param header
     * @return
     * @throws IOException
     */
    public String doPostRequestWithAdditionalHeader(String url, String header) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Cookie", header)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    /**
     * Send post request with additional request parameters and header
     * @param url
     * @param json
     * @param header
     * @return
     * @throws IOException
     */
    public String doPostRequestWithAdditionalDataAndHeader(String url, String json, String header) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("Cookie", header)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
