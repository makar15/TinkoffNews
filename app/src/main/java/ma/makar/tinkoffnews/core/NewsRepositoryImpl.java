package ma.makar.tinkoffnews.core;

import android.support.annotation.Nullable;
import android.text.TextUtils;

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
    public void loadNewTitles(LoaderCallback<List<Title>> callback) {
        Assert.assertNotNull(callback);

        WeakReference<LoaderCallback<List<Title>>> weakCallback = new WeakReference<>(callback);
        loadNewTitles(weakCallback);
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

    private void loadNewTitles(final WeakReference<LoaderCallback<List<Title>>> weakCallback) {
        Assert.assertNotNull(weakCallback);

        mTitleLoader.loadInBackground(new LoaderCallback<String>() {
            @Override
            public void onLoaded(@Nullable String strTitles, @Nullable Exception e) {
                if (TextUtils.isEmpty(strTitles) || e != null) {
                    LoaderCallback<List<Title>> callback = weakCallback.get();
                    if (callback != null) {
                        sendTitlesOnUiThread(null, callback, e);
                    }
                    return;
                }

                List<Title> titles = mTitlesMapper.dataToModel(strTitles);
                LoaderCallback<List<Title>> callback = weakCallback.get();
                if (callback != null) {
                    sendTitlesOnUiThread(titles, callback, null);
                }
                mNewsDiskCache.save(Constants.TITLE_UNIQUE_NAME_FILE, strTitles);
            }
        });
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

        private final WeakReference<LoaderCallback<List<Title>>> mCallback;

        private TitlesLoadThread(LoaderCallback<List<Title>> callback) {
            mCallback = new WeakReference<>(callback);
        }

        @Override
        public void run() {
            String strTitles = mNewsDiskCache.get(Constants.TITLE_UNIQUE_NAME_FILE);
            if (!TextUtils.isEmpty(strTitles)) {
                List<Title> titles = mTitlesMapper.dataToModel(strTitles);
                LoaderCallback<List<Title>> callback = mCallback.get();
                if (callback != null) {
                    sendTitlesOnUiThread(titles, callback, null);
                }
                return;
            }
            LoaderCallback<List<Title>> callback = mCallback.get();
            if (callback != null) {
                loadNewTitles(callback);
            }
        }
    }

    private class NewsLoadThread extends Thread {

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
                    if (TextUtils.isEmpty(strNews) || e != null) {
                        LoaderCallback<News> callback = mCallback.get();
                        if (callback != null) {
                            sendNewsOnUiThread(null, callback, e);
                        }
                        return;
                    }
                    News news = mNewsMapper.dataToModel(strNews);
                    LoaderCallback<News> callback = mCallback.get();
                    if (callback != null) {
                        sendNewsOnUiThread(news, callback, null);
                    }
                    mNewsDiskCache.save(mId, strNews);
                }
            });
        }
    }
}
