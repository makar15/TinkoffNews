package ma.makar.tinkoffnews.cache;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.io.File;

import ma.makar.base.Assert;
import ma.makar.base.FileUtils;
import ma.makar.base.StreamUtils;

public class NewsDiskCache implements Storage<String, String> {

    private static final String NAME_CACHE_DIR = "tinkoffnews";
    private static final String FILE_TXT = ".txt";

    private final File mCacheDir;

    public NewsDiskCache(Context context) {
        Assert.assertNotNull(context);
        mCacheDir = FileUtils.getDiskCacheDir(context, NAME_CACHE_DIR);
    }

    @Override
    @WorkerThread
    public boolean save(String id, String value) {
        File file = getFile(id);
        return StreamUtils.save(value.getBytes(), file.getAbsolutePath());
    }

    @Override
    @Nullable
    @WorkerThread
    public String get(String id) {
        File file = getFile(id);
        return StreamUtils.get(file);
    }

    @Override
    @WorkerThread
    public void remove(String id) {
        getFile(id).delete();
    }

    @Override
    @WorkerThread
    public void clear() {
        File[] files = mCacheDir.listFiles();
        if (files != null) {
            for (File f : files) {
                f.delete();
            }
        }
    }

    private File getFile(String id) {
        String fileName = id + FILE_TXT;
        if (!mCacheDir.exists()) {
            mCacheDir.mkdir();
        }
        return new File(mCacheDir, fileName);
    }
}
