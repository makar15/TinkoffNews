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
        return file != null && StreamUtils.save(value.getBytes(), file.getAbsolutePath());
    }

    @Override
    @Nullable
    @WorkerThread
    public String get(String id) {
        File file = getFile(id);
        return file == null ? null : StreamUtils.get(file);
    }

    @Nullable
    private File getFile(String id) {
        String fileName = id + FILE_TXT;
        boolean isDirectoryExists = mCacheDir.exists();
        if (!isDirectoryExists) {
            isDirectoryExists = mCacheDir.mkdir();
        }
        return isDirectoryExists ? new File(mCacheDir, fileName) : null;
    }
}
