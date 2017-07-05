package ma.makar.tinkoffnews.core.loaders;

import android.support.annotation.WorkerThread;

import ma.makar.tinkoffnews.core.helpers.UrlCreator;

public class TinkoffNewsLoader implements NetworkLoader<String, LoaderCallback<String>> {

    private final UrlCreator<String> mNewsUrlCreator;
    private final NetworkLoader<String, LoaderCallback<String>> mDataNetworkLoader;

    public TinkoffNewsLoader(UrlCreator<String> urlCreator,
                             NetworkLoader<String, LoaderCallback<String>> dataNetworkLoader) {
        mNewsUrlCreator = urlCreator;
        mDataNetworkLoader = dataNetworkLoader;
    }

    @Override
    @WorkerThread
    public void load(String id, LoaderCallback<String> callback) {
        String url = mNewsUrlCreator.getUrl(id);
        mDataNetworkLoader.load(url, callback);
    }
}
