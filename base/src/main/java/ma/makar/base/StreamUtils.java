package ma.makar.base;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {

    private static final String TAG = "StreamUtils";

    @WorkerThread
    public static boolean save(byte[] data, String path) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(data);
            return true;
        } catch (IOException ignored) {
            return false;
        } finally {
            CloseableUtils.close(out);
        }
    }

    @Nullable
    public static String get(@Nullable File file) {
        if (file == null) {
            Log.e(TAG, "File is null Object");
            return null;
        }
        FileInputStream input = null;
        if (!file.exists()) {
            return null;
        }
        try {
            input = new FileInputStream(file);
            return StreamUtils.readInputStream(input);
        } catch (IOException ignored) {
            return null;
        } finally {
            CloseableUtils.close(input);
        }
    }

    @Nullable
    public static String readInputStream(@Nullable InputStream inputStream) {
        if (inputStream == null) {
            Log.e(TAG, "InputStream is null Object");
            return null;
        }
        StringBuilder total = new StringBuilder();
        BufferedReader reader = null;
        String line;
        try {
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        } catch (IOException ignored) {
            return null;
        } finally {
            CloseableUtils.close(reader);
        }
    }
}
