package com.example.beuth.taskql.helperClasses;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.example.beuth.tasql.R;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Connection class
 * @author Wael Gabsi, Stefan Völkel
 */
public class Connection {
    private OkHttpClient client;
    private ConnectivityManager cm;
    private Context context;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public Connection(Context context){
        this.context = context;
        this.client = getCustomTrustedClient(this.context);
    }

    /**
     * Create custom trusted OkHttpClient
     * @param context
     * @return
     */
    private OkHttpClient getCustomTrustedClient(Context context) {
        try {
            // Load CAs from an InputStream
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = context.getResources().openRawResource(R.raw.cax3);
            Certificate ca = null;
            try {
                ca = cf.generateCertificate(caInput);
            } catch (CertificateException e) {
                e.printStackTrace();
            } finally {
                caInput.close();
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            if (ca == null)
                return null;
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            // return OkhttpClient
            OkHttpClient client = new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory()).build();
            return client;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    /**
     * Checks if the network is available
     * @return
     */
    public Boolean isNetworkAvailable() {
        cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
