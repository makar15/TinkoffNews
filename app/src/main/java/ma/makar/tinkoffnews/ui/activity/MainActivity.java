package ma.makar.tinkoffnews.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import ma.makar.tinkoffnews.ui.FlowNavigator;
import ma.makar.tinkoffnews.R;
import ma.makar.tinkoffnews.di.DependencyDistributor;
import ma.makar.tinkoffnews.di.DependencyResolver;

public class MainActivity extends AppCompatActivity implements DependencyDistributor {

    private DependencyResolver mResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResolver = DependencyResolver.create(this);
        FlowNavigator navigator = mResolver.getFlowNavigator();
        navigator.openTitleList();
    }

    @Override
    protected void onDestroy() {
        mResolver.destroy(this);
        super.onDestroy();
    }

    @Override
    public DependencyResolver getDependencyResolver() {
        return mResolver;
    }
}
