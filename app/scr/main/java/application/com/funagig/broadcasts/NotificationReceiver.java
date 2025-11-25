package application.com.funagig.broadcasts;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    
    private static final String CHANNEL_ID = "FunaGig_Notifications";
    private static final int NOTIFICATION_ID = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null) {
            switch (intent.getAction()) {
                case "NEW_GIG_AVAILABLE":
                    showNewGigNotification(context, intent.getStringExtra("gig_title"));
                    break;
                case "GIG_APPLICATION_RECEIVED":
                    showApplicationNotification(context, intent.getStringExtra("applicant_name"));
                    break;
                case "PAYMENT_RECEIVED":
                    showPaymentNotification(context, intent.getDoubleExtra("amount", 0.0));
                    break;
            }
        }
    }

    private void showNewGigNotification(Context context, String gigTitle) {
        createNotificationChannel(context);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("New Gig Available!")
                .setContentText(gigTitle != null ? gigTitle : "A new gig matching your skills is available")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        // Check if notifications are enabled
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(NOTIFICATION_ID, builder.build());
        }
    }

    private void showApplicationNotification(Context context, String applicantName) {
        createNotificationChannel(context);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("New Application Received!")
                .setContentText(applicantName != null ? applicantName + " applied for your gig" : "Someone applied for your gig")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        // Check if notifications are enabled
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(NOTIFICATION_ID + 1, builder.build());
        }
    }

    private void showPaymentNotification(Context context, double amount) {
        createNotificationChannel(context);
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Payment Received!")
                .setContentText("You received $" + String.format("%.2f", amount) + " for your completed gig")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        // Check if notifications are enabled
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(NOTIFICATION_ID + 2, builder.build());
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FunaGig Notifications";
            String description = "Notifications for gigs, applications, and payments";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
