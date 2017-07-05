package ma.makar.tinkoffnews.ui.controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import ma.makar.base.SystemUtils;
import ma.makar.tinkoffnews.ui.FragmentFlowNavigator;
import ma.makar.tinkoffnews.R;
import ma.makar.tinkoffnews.core.NewsRepository;
import ma.makar.tinkoffnews.core.loaders.LoaderCallback;
import ma.makar.tinkoffnews.models.News;
import ma.makar.tinkoffnews.utils.InformationShower;

public class NewsViewController implements ViewController {

    private static final String TAG = "NewsViewController";

    private final Context mContext;
    private final InformationShower mShower;
    private final NewsRepository mRepository;

    private final LoaderCallback<News> mNewsLoaderCallback = new LoaderCallback<News>() {
        @Override
        public void onLoaded(News news, Exception e) {
            if (e != null || news == null) {
                mShower.showToast(mContext.getString(R.string.network_request_error));
                return;
            }
            mContent.setText(news.content);
        }
    };

    private TextView mContent;

    public NewsViewController(Context context,
                              InformationShower shower,
                              NewsRepository repository) {
        mContext = context;
        mShower = shower;
        mRepository = repository;
    }

    @Override
    public void attach(View view, @Nullable Bundle args) {
        if (!SystemUtils.isConnectingToInternet(mContext)) {
            mShower.showToast(mContext.getString(R.string.please_connect_to_internet));
        }

        mContent = (TextView) view.findViewById(R.id.content);

        if (args == null || !args.containsKey(FragmentFlowNavigator.NEWS_ID_KEY)) {
            Log.e(TAG, "Data is not available or does not match the key");
            mShower.showToast(mContext.getString(R.string.news_data_error));
            return;
        }

        String newsId = args.getString(FragmentFlowNavigator.NEWS_ID_KEY);
        mRepository.loadNews(newsId, mNewsLoaderCallback);
    }
}
