package ma.makar.base;

import android.os.Build;
import android.text.Html;

public class SystemUtils {

    public static boolean supportLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static String stripHtml(String html) {
        if (supportNougat()) {
            return String.valueOf(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            return String.valueOf(Html.fromHtml(html));
        }
    }

    private static boolean supportNougat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
    }
}
