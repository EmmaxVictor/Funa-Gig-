package application.com.funagig.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationPermissionHelper {
    
    public static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1001;
    
    /**
     * Check if notification permission is granted
     */
    public static boolean isNotificationPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) 
                    == PackageManager.PERMISSION_GRANTED;
        }
        // For Android 12 and below, notifications are enabled by default
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }
    
    /**
     * Request notification permission
     */
    public static void requestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isNotificationPermissionGranted(activity)) {
                ActivityCompat.requestPermissions(
                    activity,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    NOTIFICATION_PERMISSION_REQUEST_CODE
                );
            }
        }
    }
    
    /**
     * Check if we should show rationale for notification permission
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ActivityCompat.shouldShowRequestPermissionRationale(
                activity, 
                Manifest.permission.POST_NOTIFICATIONS
            );
        }
        return false;
    }
}
