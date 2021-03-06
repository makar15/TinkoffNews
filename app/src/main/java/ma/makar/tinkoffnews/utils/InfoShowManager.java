package ma.makar.tinkoffnews.utils;

import android.content.Context;
import android.widget.Toast;

public class InfoShowManager {

    private final Context mContext;

    public InfoShowManager(Context context) {
        mContext = context;
    }

    public void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
