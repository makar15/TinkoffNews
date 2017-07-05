package ma.makar.tinkoffnews.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import ma.makar.tinkoffnews.R;
import ma.makar.tinkoffnews.ui.fragment.NewsDetailFragment;
import ma.makar.tinkoffnews.ui.fragment.TitleListFragment;

public class FragmentFlowNavigator implements FlowNavigator {

    private static final String ADD_FRAGMENT_TO_BACK_STACK_KEY = "key_back_stack";
    public static final String NEWS_ID_KEY = "news_id";

    private final FragmentManager mFragmentManager;

    public FragmentFlowNavigator(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    @Override
    public void openTitleList() {
        openFragment(new TitleListFragment(), false);
    }

    @Override
    public void openNews(String id) {
        Bundle bundle = new Bundle();
        bundle.putString(NEWS_ID_KEY, id);
        Fragment newFragment = new NewsDetailFragment();
        newFragment.setArguments(bundle);
        openFragment(newFragment, true);
    }

    private void openFragment(Fragment fragment, boolean saveInBackStack) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        if (saveInBackStack) {
            transaction.addToBackStack(ADD_FRAGMENT_TO_BACK_STACK_KEY);
        }
        transaction.commit();
    }
}
