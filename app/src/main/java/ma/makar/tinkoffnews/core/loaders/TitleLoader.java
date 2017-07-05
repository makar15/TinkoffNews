package ma.makar.tinkoffnews.core.loaders;

import android.support.annotation.WorkerThread;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;

import ma.makar.tinkoffnews.Constants;

public class TitleLoader {

    private final NetworkLoader<String, LoaderCallback<String>> mDataNetworkLoader;
    private final ExecutorService mExecutor;

    public TitleLoader(NetworkLoader<String, LoaderCallback<String>> dataNetworkLoader,
                       ExecutorService executor) {
        mDataNetworkLoader = dataNetworkLoader;
        mExecutor = executor;
    }

    @WorkerThread
    public void loadInBackground(final LoaderCallback<String> callback) {
        Runnable titleLoadingTask = new TitleLoadingTask(callback);
        mExecutor.execute(titleLoadingTask);
    }

    private class TitleLoadingTask implements Runnable {

        private final WeakReference<LoaderCallback<String>> mCallback;

        TitleLoadingTask(LoaderCallback<String> callback) {
            mCallback = new WeakReference<>(callback);
        }

        @Override
        public void run() {
            LoaderCallback<String> callback = mCallback.get();
            if (callback != null) {
                mDataNetworkLoader.load(Constants.TITLE_URL, callback);
            }
        }
    }
}
