package ma.makar.tinkoffnews.ui.controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ma.makar.base.Assert;
import ma.makar.tinkoffnews.R;
import ma.makar.tinkoffnews.core.NewsRepository;
import ma.makar.tinkoffnews.core.loaders.LoaderCallback;
import ma.makar.tinkoffnews.models.News;
import ma.makar.tinkoffnews.ui.FragmentFlowNavigator;
import ma.makar.tinkoffnews.utils.InfoShowManager;
import ma.makar.tinkoffnews.utils.NetworkManager;
import ma.makar.tinkoffnews.utils.NetworkStateListener;

public class NewsViewController implements ViewController {

    private static final String TAG = "NewsViewController";

    private final Context mContext;
    private final InfoShowManager mInfoShowManager;
    private final NewsRepository mRepository;
    private final NetworkManager mNetworkManager;

    private final LoaderCallback<News> mNewsLoaderCallback = new LoaderCallback<News>() {
        @Override
        public void onLoaded(News news, Exception e) {
            if (e != null || news == null) {
                String msg = !mNetworkManager.isConnectingToInternet() ?
                        mContext.getString(R.string.please_connect_to_internet) :
                        mContext.getString(R.string.network_request_error);

                mInfoShowManager.showToast(msg);
                return;
            }
            if (mContent == null) {
                Assert.fail("Field mContent is null Object, it means a leak");
                return;
            }
            mContent.setText(news.content);
        }
    };

    private final NetworkStateListener mNetworkStateListener = new NetworkStateListener() {
        @Override
        public void networkAvailable() {
            if (newsId != null) {
                mRepository.loadNews(newsId, mNewsLoaderCallback);
            }
        }
    };

    @Nullable private TextView mContent;
    @Nullable private String newsId;

    public NewsViewController(Context context,
                              InfoShowManager infoShowManager,
                              NewsRepository repository,
                              NetworkManager networkManager) {
        mContext = context;
        mInfoShowManager = infoShowManager;
        mRepository = repository;
        mNetworkManager = networkManager;
    }

    @Override
    public void attachView(View view, @Nullable Bundle args) {
        mContent = (TextView) view.findViewById(R.id.content);

        if (args == null || !args.containsKey(FragmentFlowNavigator.NEWS_ID_KEY)) {
            Log.e(TAG, "Data is not available or does not match the key");
            mInfoShowManager.showToast(mContext.getString(R.string.news_data_error));
            return;
        }

        newsId = args.getString(FragmentFlowNavigator.NEWS_ID_KEY);
        mNetworkManager.addListener(mNetworkStateListener);
        mRepository.loadNews(newsId, mNewsLoaderCallback);
    }

    @Override
    public void detachView() {
        mNetworkManager.removeListener(mNetworkStateListener);
        mContent = null;
        newsId = null;
    }
}
