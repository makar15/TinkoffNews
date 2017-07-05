package ma.makar.tinkoffnews.core.loaders;

import android.support.annotation.Nullable;

public interface LoaderCallback<T> {

    void onLoaded(@Nullable T type, @Nullable Exception e);
}
