package ma.makar.tinkoffnews.core.loaders;

public interface NetworkLoader<K, C> {

    void load(K key, C callback);
}
