package ma.makar.tinkoffnews.core;

import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutorService;

import ma.makar.base.Assert;
import ma.makar.base.ThreadUtils;
import ma.makar.tinkoffnews.Constants;
import ma.makar.tinkoffnews.cache.Storage;
import ma.makar.tinkoffnews.core.loaders.LoaderCallback;
import ma.makar.tinkoffnews.core.loaders.NetworkLoader;
import ma.makar.tinkoffnews.core.loaders.TitleLoader;
import ma.makar.tinkoffnews.core.mappers.Mapper;
import ma.makar.tinkoffnews.models.News;
import ma.makar.tinkoffnews.models.Title;

public class NewsRepositoryImpl implements NewsRepository {

    private final NetworkLoader<String, LoaderCallback<String>> mNewsLoader;
    private final TitleLoader mTitleLoader;
    private final Storage<String, String> mNewsDiskCache;
    private final Mapper<List<Title>, String> mTitlesMapper;
    private final Mapper<News, String> mNewsMapper;
    private final ExecutorService mExecutor;

    public NewsRepositoryImpl(NetworkLoader<String, LoaderCallback<String>> newsLoader,
                              TitleLoader titleLoader,
                              Storage<String, String> newsDiskCache,
                              Mapper<List<Title>, String> titlesMapper,
                              Mapper<News, String> newsMapper,
                              ExecutorService executor) {
        mNewsLoader = newsLoader;
        mTitleLoader = titleLoader;
        mNewsDiskCache = newsDiskCache;
        mTitlesMapper = titlesMapper;
        mNewsMapper = newsMapper;
        mExecutor = executor;
    }

    /**
     * Проверяем DiskCache, если в DiskCache данных нет, то запускаем сетевой запрос.
     */
    @Override
    public void loadTitles(LoaderCallback<List<Title>> callback) {
        Assert.assertNotNull(callback);

        Thread titlesLoadThread = new TitlesLoadThread(callback);
        mExecutor.execute(titlesLoadThread);
    }

    /**
     * Загружаем List<Title> из сети
     * после чего сохраненяем в DiskCache.
     */
    @Override
    public void loadNewTitles(final LoaderCallback<List<Title>> callback) {
        Assert.assertNotNull(callback);

        mTitleLoader.loadInBackground(new LoaderCallback<String>() {
            @Override
            public void onLoaded(@Nullable String strTitles, @Nullable Exception e) {
                if (TextUtils.isEmpty(strTitles) || e != null) {
                    sendTitlesOnUiThread(null, callback, e);
                    return;
                }
                List<Title> titles = mTitlesMapper.dataToModel(strTitles);
                sendTitlesOnUiThread(titles, callback, null);
                mNewsDiskCache.save(Constants.TITLE_UNIQUE_NAME_FILE, strTitles);
            }
        });
    }

    /**
     * Проверяем DiskCache, если в DiskCache данных нет, то запускаем сетевой запрос.
     */
    @Override
    public void loadNews(String id, LoaderCallback<News> callback) {
        Assert.assertNotNull(callback);

        Thread newsLoadThread = new NewsLoadThread(id, callback);
        mExecutor.execute(newsLoadThread);
    }

    private void sendTitlesOnUiThread(@Nullable final List<Title> titles,
                                      final LoaderCallback<List<Title>> callback,
                                      @Nullable final Exception e) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onLoaded(titles, e);
            }
        });
    }

    private void sendNewsOnUiThread(@Nullable final News news,
                                    final LoaderCallback<News> callback,
                                    @Nullable final Exception e) {
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onLoaded(news, e);
            }
        });
    }

    private class TitlesLoadThread extends Thread {

        private static final String TAG = "TitlesLoadThread";
        private final WeakReference<LoaderCallback<List<Title>>> mCallback;

        private TitlesLoadThread(LoaderCallback<List<Title>> callback) {
            mCallback = new WeakReference<>(callback);
        }

        @Override
        public void run() {
            LoaderCallback<List<Title>> callback = mCallback.get();
            if (callback == null) {
                Log.w(TAG, "LoaderCallback is null Object");
                return;
            }
            String strTitles = mNewsDiskCache.get(Constants.TITLE_UNIQUE_NAME_FILE);
            if (!TextUtils.isEmpty(strTitles)) {
                List<Title> titles = mTitlesMapper.dataToModel(strTitles);
                sendTitlesOnUiThread(titles, callback, null);
                return;
            }

            loadNewTitles(callback);
        }
    }

    private class NewsLoadThread extends Thread {

        private static final String TAG = "NewsLoadThread";
        private final String mId;
        private final WeakReference<LoaderCallback<News>> mCallback;

        private NewsLoadThread(String id, LoaderCallback<News> callback) {
            mId = id;
            mCallback = new WeakReference<>(callback);
        }

        @Override
        public void run() {
            String strNews = mNewsDiskCache.get(mId);
            if (!TextUtils.isEmpty(strNews)) {
                News news = mNewsMapper.dataToModel(strNews);
                LoaderCallback<News> callback = mCallback.get();
                if (callback != null) {
                    sendNewsOnUiThread(news, callback, null);
                }
                return;
            }

            mNewsLoader.load(mId, new LoaderCallback<String>() {
                @Override
                public void onLoaded(@Nullable String strNews, Exception e) {
                    LoaderCallback<News> callback = mCallback.get();
                    if (callback == null) {
                        Log.w(TAG, "LoaderCallback is null Object");
                        return;
                    }

                    if (TextUtils.isEmpty(strNews) || e != null) {
                        sendNewsOnUiThread(null, callback, e);
                        return;
                    }
                    News news = mNewsMapper.dataToModel(strNews);
                    sendNewsOnUiThread(news, callback, null);
                    mNewsDiskCache.save(mId, strNews);
                }
            });
        }
    }
}
