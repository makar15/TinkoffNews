package ma.makar.tinkoffnews.core.loaders;

import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ma.makar.base.CloseableUtils;
import ma.makar.base.StreamUtils;

public class DataNetworkLoader implements NetworkLoader<String, LoaderCallback<String>> {

    private static final String TAG = "DataNetworkLoader";
    private static final String GET_REQUEST = "GET";
    private static final int CONNECT_TIMEOUT = 5000;
    private static final int READ_TIMEOUT = 5000;

    @Override
    @WorkerThread
    public void load(String url, LoaderCallback<String> callback) {
        InputStream input = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod(GET_REQUEST);
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.connect();

            int responseCode = connection.getResponseCode();
            Log.d(TAG, "Response Code : " + responseCode);

            input = connection.getInputStream();
            String result = StreamUtils.readInputStream(input);
            callback.onLoaded(result, null);
        } catch (IOException e) {
            Log.e(TAG, "Error loading tinkoff news by next url :" + url, e);
            callback.onLoaded(null, e);
        } finally {
            CloseableUtils.close(input);
        }
    }
}
