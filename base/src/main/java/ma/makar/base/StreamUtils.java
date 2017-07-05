package ma.makar.base;

import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {

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
    public static String get(File file) {
        FileInputStream input = null;
        if (!file.exists()) {
            return null;
        }
        try {
            input = new FileInputStream(file);
            String result = StreamUtils.readInputStream(input);
            return result;
        } catch (IOException ignored) {
            return null;
        } finally {
            CloseableUtils.close(input);
        }
    }

    @Nullable
    public static String readInputStream(InputStream inputStream) {
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
