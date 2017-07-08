package ma.makar.tinkoffnews.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ma.makar.tinkoffnews.R;
import ma.makar.tinkoffnews.ui.controllers.ViewController;
import ma.makar.tinkoffnews.di.DependencyDistributor;
import ma.makar.tinkoffnews.di.DependencyResolver;

public class NewsDetailFragment extends Fragment {

    private ViewController mViewController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news_detail, null);

        DependencyResolver resolver = ((DependencyDistributor) getActivity()).getDependencyResolver();
        mViewController = resolver.getNewsViewController();
        mViewController.attachView(view, getArguments());
        return view;
    }

    @Override
    public void onDestroyView() {
        mViewController.detachView();
        super.onDestroyView();
    }
}
