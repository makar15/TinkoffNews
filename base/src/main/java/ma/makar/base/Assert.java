package ma.makar.base;

import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Wrapper class for assertion. In debug it works as ordinary Assert
 */
public class Assert {

    private static final String TAG = "Assert";

    public static void fail(@Nullable String message) {
        Log.e(TAG, message);
        if (BuildConfig.DEBUG) {
            throw new AssertionError(message);
        }
    }

    public static void assertNotNull(@Nullable Object object) {
        assertNotNull(null, object);
    }

    private static void assertTrue(@Nullable String message, boolean condition) {
        if (!condition) {
            fail(message);
        }
    }

    private static void assertNotNull(@Nullable String message, @Nullable Object object) {
        assertTrue(message, object != null);
    }
}
