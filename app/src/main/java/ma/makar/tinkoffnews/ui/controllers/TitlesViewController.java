package ma.makar.tinkoffnews.ui.controllers;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.Collections;
import java.util.List;

import ma.makar.base.Assert;
import ma.makar.tinkoffnews.R;
import ma.makar.tinkoffnews.adapters.RecyclerListAdapter;
import ma.makar.tinkoffnews.adapters.TitleAdapter;
import ma.makar.tinkoffnews.core.NewsRepository;
import ma.makar.tinkoffnews.core.loaders.LoaderCallback;
import ma.makar.tinkoffnews.models.Title;
import ma.makar.tinkoffnews.ui.FlowNavigator;
import ma.makar.tinkoffnews.utils.InfoShowManager;
import ma.makar.tinkoffnews.utils.NetworkManager;
import ma.makar.tinkoffnews.utils.NetworkStateListener;

public class TitlesViewController implements ViewController {

    private final Context mContext;
    private final NewsRepository mRepository;
    private final InfoShowManager mInfoShowManager;
    private final FlowNavigator mNavigator;
    private final NetworkManager mNetworkManager;
    private final RecyclerListAdapter<?, Title> mTitleAdapter = new TitleAdapter();

    private final SwipeRefreshLayout.OnRefreshListener mRefreshListener =
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (!mNetworkManager.isConnectingToInternet()) {
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
                        String msg = !mNetworkManager.isConnectingToInternet() ?
                                mContext.getString(R.string.please_connect_to_internet) :
                                mContext.getString(R.string.network_request_error);

                        stopRefreshWithMessage(msg);
                        return;
                    }
                    Collections.sort(titles, Collections.reverseOrder());
                    mTitleAdapter.updateList(titles);
                    if (mSwipeRefreshLayout != null) {
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
            };

    private final NetworkStateListener mNetworkStateListener = new NetworkStateListener() {
        @Override
        public void networkAvailable() {
            mRepository.loadTitles(mTitleLoaderCallback);
        }
    };

    @Nullable private SwipeRefreshLayout mSwipeRefreshLayout;

    public TitlesViewController(Context context,
                                NewsRepository repository,
                                InfoShowManager infoShowManager,
                                FlowNavigator navigator,
                                NetworkManager networkManager) {
        mContext = context;
        mRepository = repository;
        mInfoShowManager = infoShowManager;
        mNavigator = navigator;
        mNetworkManager = networkManager;
    }

    @Override
    public void attachView(View view, @Nullable Bundle args) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration decoration =
                new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.lv_title);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(decoration);
        mTitleAdapter.setOnClickItemListener(mClickTitleListener);
        recyclerView.setAdapter(mTitleAdapter);

        Assert.assertNotNull(mSwipeRefreshLayout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.green, R.color.blue);
            mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
        }

        mNetworkManager.addListener(mNetworkStateListener);
        mRepository.loadTitles(mTitleLoaderCallback);
    }

    @Override
    public void detachView() {
        mTitleAdapter.setOnClickItemListener(null);
        mNetworkManager.removeListener(mNetworkStateListener);

        Assert.assertNotNull(mSwipeRefreshLayout);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setOnRefreshListener(null);
            mSwipeRefreshLayout = null;
        }
    }

    private void stopRefreshWithMessage(String message) {
        mInfoShowManager.showToast(message);
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
