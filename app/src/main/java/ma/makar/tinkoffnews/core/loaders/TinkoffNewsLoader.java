package ma.makar.tinkoffnews.core.loaders;

import android.support.annotation.WorkerThread;

import ma.makar.tinkoffnews.Constants;

public class TinkoffNewsLoader implements NetworkLoader<String, LoaderCallback<String>> {

    private final NetworkLoader<String, LoaderCallback<String>> mDataNetworkLoader;

    public TinkoffNewsLoader(NetworkLoader<String, LoaderCallback<String>> dataNetworkLoader) {
        mDataNetworkLoader = dataNetworkLoader;
    }

    @Override
    @WorkerThread
    public void load(String id, LoaderCallback<String> callback) {
        String url = Constants.NEWS_CONTENT_URL + id;
        mDataNetworkLoader.load(url, callback);
    }
}
