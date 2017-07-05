package ma.makar.tinkoffnews.core;

import java.util.List;

import ma.makar.tinkoffnews.core.loaders.LoaderCallback;
import ma.makar.tinkoffnews.models.News;
import ma.makar.tinkoffnews.models.Title;

public interface NewsRepository {

    void loadTitles(LoaderCallback<List<Title>> callback);

    void loadNewTitles(LoaderCallback<List<Title>> callback);

    void loadNews(String id, LoaderCallback<News>  callback);
}
