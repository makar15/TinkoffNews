package ma.makar.tinkoffnews.multithreading;

import android.support.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

    private final String mName;

    public NamedThreadFactory(String name) {
        mName = name;
    }

    @Override
    public Thread newThread(@NonNull Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(mName);
        return thread;
    }
}
