package ma.makar.tinkoffnews.di;

import android.support.v4.app.FragmentActivity;

import java.util.List;
import java.util.concurrent.ExecutorService;

import ma.makar.base.Assert;
import ma.makar.tinkoffnews.ui.FlowNavigator;
import ma.makar.tinkoffnews.ui.FragmentFlowNavigator;
import ma.makar.tinkoffnews.cache.NewsDiskCache;
import ma.makar.tinkoffnews.cache.Storage;
import ma.makar.tinkoffnews.ui.controllers.NewsViewController;
import ma.makar.tinkoffnews.ui.controllers.TitlesViewController;
import ma.makar.tinkoffnews.ui.controllers.ViewController;
import ma.makar.tinkoffnews.core.NewsRepository;
import ma.makar.tinkoffnews.core.NewsRepositoryImpl;
import ma.makar.tinkoffnews.core.helpers.TinkoffNewsUrlCreator;
import ma.makar.tinkoffnews.core.helpers.UrlCreator;
import ma.makar.tinkoffnews.core.loaders.DataNetworkLoader;
import ma.makar.tinkoffnews.core.loaders.LoaderCallback;
import ma.makar.tinkoffnews.core.loaders.NetworkLoader;
import ma.makar.tinkoffnews.core.loaders.TinkoffNewsLoader;
import ma.makar.tinkoffnews.core.loaders.TitleLoader;
import ma.makar.tinkoffnews.core.mappers.Mapper;
import ma.makar.tinkoffnews.core.mappers.NewsJsonMapper;
import ma.makar.tinkoffnews.core.mappers.TitlesJsonMapper;
import ma.makar.tinkoffnews.models.News;
import ma.makar.tinkoffnews.models.Title;
import ma.makar.tinkoffnews.multithreading.ExecutorServiceBuilder;
import ma.makar.tinkoffnews.multithreading.NamedThreadFactory;
import ma.makar.tinkoffnews.utils.InformationShower;

public class DependencyResolver {

    private static final String THREAD_FACTORY_NAME = "NewsThread";

    private final FlowNavigator mFlowNavigator;
    private final ViewController mTitlesViewController;
    private final ViewController mNewsViewController;

    public static DependencyResolver create(FragmentActivity activity) {
        return new DependencyResolver(activity);
    }

    private DependencyResolver(FragmentActivity activity) {
        Assert.assertNotNull(activity);

        ExecutorService executor = new ExecutorServiceBuilder()
                .setThreadFactory(new NamedThreadFactory(THREAD_FACTORY_NAME))
                .build();

        Storage<String, String> newsDiskCache = new NewsDiskCache(activity);
        Mapper<List<Title>, String> titlesMapper = new TitlesJsonMapper();
        Mapper<News, String> newsMapper = new NewsJsonMapper();
        NetworkLoader<String, LoaderCallback<String>> dataLoader = new DataNetworkLoader();
        UrlCreator<String> urlCreator = new TinkoffNewsUrlCreator();
        TitleLoader titleLoader = new TitleLoader(dataLoader, executor);
        NetworkLoader<String, LoaderCallback<String>> newsLoader =
                new TinkoffNewsLoader(urlCreator, dataLoader);

        InformationShower informationShower = new InformationShower(activity);
        NewsRepository newsRepository = new NewsRepositoryImpl(newsLoader, titleLoader,
                newsDiskCache, titlesMapper, newsMapper, executor);

        mFlowNavigator = new FragmentFlowNavigator(activity.getSupportFragmentManager());
        mTitlesViewController = new TitlesViewController(activity, newsRepository,
                informationShower, mFlowNavigator);
        mNewsViewController = new NewsViewController(activity, informationShower, newsRepository);
    }

    public FlowNavigator getFlowNavigator() {
        return mFlowNavigator;
    }

    public ViewController getTitlesViewController() {
        return mTitlesViewController;
    }

    public ViewController getNewsViewController() {
        return mNewsViewController;
    }
}
