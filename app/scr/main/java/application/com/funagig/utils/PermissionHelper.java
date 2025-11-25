package application.com.funagig.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * Utility class for managing runtime permissions in the application.
 * Handles camera, storage, and location permissions with proper rationale messages.
 */
public class PermissionHelper {
    
    // Permission request codes
    public static final int CAMERA_PERMISSION_REQUEST_CODE = 2001;
    public static final int STORAGE_PERMISSION_REQUEST_CODE = 2002;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 2003;
    public static final int MULTIPLE_PERMISSIONS_REQUEST_CODE = 2004;
    
    /**
     * Check if camera permission is granted
     */
    public static boolean isCameraPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Check if storage permission is granted
     * For Android 13+, READ_MEDIA_IMAGES is used instead of READ_EXTERNAL_STORAGE
     */
    public static boolean isStoragePermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) 
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) 
                    == PackageManager.PERMISSION_GRANTED;
        }
    }
    
    /**
     * Check if location permission is granted
     */
    public static boolean isLocationPermissionGranted(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED ||
               ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED;
    }
    
    /**
     * Request camera permission
     */
    public static void requestCameraPermission(Activity activity) {
        if (!isCameraPermissionGranted(activity)) {
            ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.CAMERA},
                CAMERA_PERMISSION_REQUEST_CODE
            );
        }
    }
    
    /**
     * Request storage permission (handles Android version differences)
     */
    public static void requestStoragePermission(Activity activity) {
        if (!isStoragePermissionGranted(activity)) {
            String[] permissions;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES};
            } else {
                permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
            }
            ActivityCompat.requestPermissions(activity, permissions, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }
    
    /**
     * Request location permission
     */
    public static void requestLocationPermission(Activity activity) {
        if (!isLocationPermissionGranted(activity)) {
            ActivityCompat.requestPermissions(
                activity,
                new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                },
                LOCATION_PERMISSION_REQUEST_CODE
            );
        }
    }
    
    /**
     * Request multiple permissions at once
     */
    public static void requestMultiplePermissions(Activity activity, String[] permissions) {
        ActivityCompat.requestPermissions(activity, permissions, MULTIPLE_PERMISSIONS_REQUEST_CODE);
    }
    
    /**
     * Check if we should show rationale for permission
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity, String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }
    
    /**
     * Get user-friendly permission name for display
     */
    public static String getPermissionName(String permission) {
        if (permission.equals(Manifest.permission.CAMERA)) {
            return "Camera";
        } else if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE) || 
                   permission.equals(Manifest.permission.READ_MEDIA_IMAGES)) {
            return "Storage";
        } else if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) ||
                   permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return "Location";
        }
        return "Permission";
    }
    
    /**
     * Get rationale message for permission
     */
    public static String getPermissionRationale(String permission) {
        if (permission.equals(Manifest.permission.CAMERA)) {
            return "Camera permission is required to take profile photos.";
        } else if (permission.equals(Manifest.permission.READ_EXTERNAL_STORAGE) || 
                   permission.equals(Manifest.permission.READ_MEDIA_IMAGES)) {
            return "Storage permission is required to select photos from your gallery.";
        } else if (permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) ||
                   permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return "Location permission is required to show job opportunities near you.";
        }
        return "This permission is required for the app to function properly.";
    }
}

