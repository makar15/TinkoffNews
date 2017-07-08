package ma.makar.tinkoffnews.cache;

public interface Storage<K, V> {

    boolean save(K key, V value);

    V get(K key);
}
