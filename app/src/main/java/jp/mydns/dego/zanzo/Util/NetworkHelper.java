package jp.mydns.dego.zanzo.Util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;

import jp.mydns.dego.zanzo.InstanceHolder;

public class NetworkHelper {

    // ---------------------------------------------------------------------------------------------
    // Constant Value
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = "NetworkHelper";

    // ---------------------------------------------------------------------------------------------
    // Private Fields
    // ---------------------------------------------------------------------------------------------
    private static boolean isConnected = false;

    private static final ConnectivityManager.NetworkCallback defaultCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            DebugLog.d(TAG, "onAvailable");
            NetworkHelper.checkConnection();
        }

        @Override
        public void onLost(Network network) {
            DebugLog.d(TAG, "onLost");
            NetworkHelper.checkConnection();
        }
    };

    // ---------------------------------------------------------------------------------------------
    // Public Method
    // ---------------------------------------------------------------------------------------------

    /**
     * registerNetworkCallback
     */
    public static void registerNetworkCallback() {
        DebugLog.d(TAG, "registerNetworkCallback");

        ConnectivityManager connectivityManager = NetworkHelper.getConnectivityManager();

        NetworkRequest request = new NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build();
        connectivityManager.registerNetworkCallback(request, NetworkHelper.defaultCallback);
    }

    /**
     * unregisterNetworkCallback
     */
    public static void unregisterNetworkCallback() {
        DebugLog.d(TAG, "unregisterNetworkCallback");

        ConnectivityManager connectivityManager = NetworkHelper.getConnectivityManager();
        connectivityManager.unregisterNetworkCallback(NetworkHelper.defaultCallback);
    }

    /**
     * networkCheck
     *
     * @return can access network
     */
    public static boolean networkCheck() {
        DebugLog.d(TAG, "networkCheck");

        return NetworkHelper.isConnected;
    }

    // ---------------------------------------------------------------------------------------------
    // Private Method
    // ---------------------------------------------------------------------------------------------

    /**
     * getConnectivityManager
     *
     * @return connectivity manager
     */
    private static ConnectivityManager getConnectivityManager() {
        Context context = InstanceHolder.getInstance().getApplicationContext();
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * checkConnection
     */
    private static void checkConnection() {
        DebugLog.d(TAG, "checkConnection");
        ConnectivityManager connectivityManager = NetworkHelper.getConnectivityManager();
        Network[] networks = connectivityManager.getAllNetworks();
        if (networks == null) {
            NetworkHelper.isConnected = false;
            return;
        }
        for (Network network : networks) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
            if (capabilities != null &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)) {
                NetworkHelper.isConnected = true;
                return;
            }
        }
        NetworkHelper.isConnected = false;
    }
}
