package ma.makar.tinkoffnews.core.helpers;

public interface UrlCreator<T> {

    String getUrl(T type);
}
