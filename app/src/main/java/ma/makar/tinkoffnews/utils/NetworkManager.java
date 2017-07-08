package ma.makar.tinkoffnews.utils;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

import java.util.ArrayList;
import java.util.List;

import ma.makar.base.SystemUtils;

public class NetworkManager extends BroadcastReceiver {

    private final Context mContext;
    private final List<NetworkStateListener> mListeners;

    public NetworkManager(Context context) {
        mContext = context;
        mListeners = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null) {
            return;
        }

        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info != null && info.getState() == NetworkInfo.State.CONNECTED) {
            notifyAvailableStateToAll();
        }
    }

    public void addListener(NetworkStateListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(NetworkStateListener listener) {
        mListeners.remove(listener);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean isConnectingToInternet() {
        ConnectivityManager manager = (ConnectivityManager)
                mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        if (SystemUtils.supportLollipop()) {
            Network[] networks = manager.getAllNetworks();
            NetworkInfo info;
            for (Network network : networks) {
                info = manager.getNetworkInfo(network);
                if (info != null && info.isConnected()) {
                    return true;
                }
            }
            return false;
        }
        NetworkInfo[] networkInfo = manager.getAllNetworkInfo();
        if (networkInfo != null) {
            for (NetworkInfo info : networkInfo) {
                if (info != null && info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void notifyAvailableStateToAll() {
        for (NetworkStateListener listener : mListeners) {
            if (listener != null) {
                listener.networkAvailable();
            }
        }
    }
}
