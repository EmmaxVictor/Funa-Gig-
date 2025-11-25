package application.com.funagig.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Helper for persisting and applying the user's theme preference.
 */
public final class ThemeUtils {

    private static final String PREF_NAME = "funa_gig_theme";
    private static final String KEY_DARK_MODE = "dark_mode_enabled";

    private ThemeUtils() {
        // Utility
    }

    public static void applySavedTheme(Context context) {
        if (context == null) {
            return;
        }
        boolean enabled = isDarkModeEnabled(context);
        AppCompatDelegate.setDefaultNightMode(
                enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    public static boolean isDarkModeEnabled(Context context) {
        if (context == null) {
            return false;
        }
        return getPrefs(context).getBoolean(KEY_DARK_MODE, false); // default is light mode
    }

    public static void setDarkMode(Context context, boolean enabled) {
        if (context == null) {
            return;
        }
        getPrefs(context).edit().putBoolean(KEY_DARK_MODE, enabled).apply();
        AppCompatDelegate.setDefaultNightMode(
                enabled ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
    }

    private static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}

