package application.com.funagig.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class GigProcessingService extends IntentService {
    
    private static final String TAG = "GigProcessingService";
    public static final String ACTION_GIG_PROCESSED = "application.com.funagig.GIG_PROCESSED";
    public static final String ACTION_GIG_FAILED = "application.com.funagig.GIG_FAILED";
    public static final String EXTRA_GIG_ID = "gig_id";
    public static final String EXTRA_GIG_TITLE = "gig_title";
    public static final String EXTRA_ERROR_MESSAGE = "error_message";

    public GigProcessingService() {
        super("GigProcessingService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            String gigId = intent.getStringExtra(EXTRA_GIG_ID);
            String gigTitle = intent.getStringExtra(EXTRA_GIG_TITLE);

            Log.d(TAG, "Processing gig: " + gigTitle + " with ID: " + gigId);

            try {
                // Simulate processing time
                Thread.sleep(3000);

                // Simulate processing logic
                boolean success = processGig(gigId, gigTitle);

                if (success) {
                    // Send success broadcast
                    Intent successIntent = new Intent(ACTION_GIG_PROCESSED);
                    successIntent.putExtra(EXTRA_GIG_ID, gigId);
                    successIntent.putExtra(EXTRA_GIG_TITLE, gigTitle);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(successIntent);

                    Log.d(TAG, "Gig processed successfully: " + gigTitle);
                } else {
                    // Send failure broadcast
                    Intent failureIntent = new Intent(ACTION_GIG_FAILED);
                    failureIntent.putExtra(EXTRA_GIG_ID, gigId);
                    failureIntent.putExtra(EXTRA_GIG_TITLE, gigTitle);
                    failureIntent.putExtra(EXTRA_ERROR_MESSAGE, "Failed to process gig");
                    LocalBroadcastManager.getInstance(this).sendBroadcast(failureIntent);

                    Log.e(TAG, "Failed to process gig: " + gigTitle);
                }

            } catch (InterruptedException e) {
                Log.e(TAG, "Service interrupted", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                Log.e(TAG, "Error processing gig", e);
                
                // Send error broadcast
                Intent errorIntent = new Intent(ACTION_GIG_FAILED);
                errorIntent.putExtra(EXTRA_GIG_ID, gigId);
                errorIntent.putExtra(EXTRA_GIG_TITLE, gigTitle);
                errorIntent.putExtra(EXTRA_ERROR_MESSAGE, e.getMessage());
                LocalBroadcastManager.getInstance(this).sendBroadcast(errorIntent);
            }
        }
    }

    private boolean processGig(String gigId, String gigTitle) {
        // Simulate gig processing logic
        // In a real app, this would involve:
        // - Validating gig data
        // - Uploading to server
        // - Sending notifications
        // - Updating database
        
        // Simulate random success/failure for demo
        return Math.random() > 0.2; // 80% success rate
    }
}
