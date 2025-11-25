package application.com.funagig.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            
            if (networkInfo != null && networkInfo.isConnected()) {
                // Network is connected
                Toast.makeText(context, "Internet connection restored", Toast.LENGTH_SHORT).show();
            } else {
                // Network is disconnected
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
