package ma.makar.tinkoffnews.ui.controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Collections;
import java.util.List;

import ma.makar.base.SystemUtils;
import ma.makar.tinkoffnews.R;
import ma.makar.tinkoffnews.adapters.RecyclerListAdapter;
import ma.makar.tinkoffnews.adapters.TitleAdapter;
import ma.makar.tinkoffnews.core.NewsRepository;
import ma.makar.tinkoffnews.core.loaders.LoaderCallback;
import ma.makar.tinkoffnews.models.Title;
import ma.makar.tinkoffnews.ui.FlowNavigator;
import ma.makar.tinkoffnews.utils.InformationShower;

public class TitlesViewController implements ViewController {

    private final Context mContext;
    private final NewsRepository mRepository;
    private final InformationShower mShower;
    private final FlowNavigator mNavigator;
    private final RecyclerListAdapter<?, Title> mTitleAdapter = new TitleAdapter();

    private final SwipeRefreshLayout.OnRefreshListener mRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!SystemUtils.isConnectingToInternet(mContext)) {
                        String msg = mContext.getString(R.string.please_connect_to_internet);
                        stopRefreshWithMessage(msg);
                        return;
                    }
                    mRepository.loadNewTitles(mTitleLoaderCallback);
                }
            };

    private final TitleAdapter.OnClickItemListener<Title> mClickTitleListener =
            new TitleAdapter.OnClickItemListener<Title>() {
                @Override
                public void onClick(Title title) {
                    mNavigator.openNews(title.id);
                }
            };

    private final LoaderCallback<List<Title>> mTitleLoaderCallback =
            new LoaderCallback<List<Title>>() {
                @Override
                public void onLoaded(@Nullable List<Title> titles, Exception e) {
                    if (e != null || titles == null) {
                        String msg = mContext.getString(R.string.network_request_error);
                        stopRefreshWithMessage(msg);
                        return;
                    }
                    Collections.sort(titles, Collections.reverseOrder());
                    mTitleAdapter.updateList(titles);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            };

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public TitlesViewController(Context context,
                                NewsRepository repository,
                                InformationShower shower,
                                FlowNavigator navigator) {
        mContext = context;
        mRepository = repository;
        mShower = shower;
        mNavigator = navigator;
    }

    @Override
    public void attach(View view, @Nullable Bundle args) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.lv_title);
        recyclerView.setLayoutManager(layoutManager);
        mTitleAdapter.setOnClickItemListener(mClickTitleListener);
        recyclerView.setAdapter(mTitleAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);

        mRepository.loadTitles(mTitleLoaderCallback);
    }

    private void stopRefreshWithMessage(String message) {
        mShower.showToast(message);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
