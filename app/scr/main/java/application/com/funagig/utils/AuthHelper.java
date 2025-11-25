package application.com.funagig.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import application.com.funagig.R;
import application.com.funagig.models.User;
import application.com.funagig.repository.UserRepository;

/**
 * Utility class for managing authentication state and user sessions.
 * Provides secure handling of authentication states without storing credentials.
 */
public class AuthHelper {
    
    private static final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    
    /**
     * Get the current authenticated user
     * @return FirebaseUser if logged in, null otherwise
     */
    public static FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }
    
    /**
     * Check if user is currently authenticated
     * @return true if user is logged in, false otherwise
     */
    public static boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }
    
    /**
     * Get current user's email (securely from Firebase)
     * @return user email or null if not logged in
     */
    public static String getCurrentUserEmail() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getEmail() : null;
    }
    
    /**
     * Get current user's UID
     * @return user UID or null if not logged in
     */
    public static String getCurrentUserId() {
        FirebaseUser user = getCurrentUser();
        return user != null ? user.getUid() : null;
    }
    
    /**
     * Sign out the current user and clear any cached Google accounts so
     * the user must pick or re-enter their credentials next time.
     */
    public static void signOut(Context context) {
        mAuth.signOut();
        
        if (context == null) {
            return;
        }
        
        String webClientId = context.getString(R.string.default_web_client_id);
        GoogleSignInOptions.Builder builder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail();
        
        if (!TextUtils.isEmpty(webClientId) && !isPlaceholderClientId(webClientId)) {
            builder.requestIdToken(webClientId);
        }
        
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(context, builder.build());
        googleSignInClient.signOut();
        googleSignInClient.revokeAccess();
    }
    
    /**
     * Legacy helper retained for compatibility.
     */
    public static void signOut() {
        signOut(null);
    }
    
    private static boolean isPlaceholderClientId(String clientId) {
        return "947879979885-t1kei2b6ani2dsqjoambuu5s9lcmu7fo.apps.googleusercontent.com".equals(clientId);
    }
    
    /**
     * Check if an activity requires authentication and redirect if needed
     * @param context The activity context
     * @param loginActivityClass The login activity class to redirect to
     * @return true if user is authenticated, false if redirected to login
     */
    public static boolean requireAuthentication(Context context, Class<?> loginActivityClass) {
        if (!isUserLoggedIn()) {
            Intent intent = new Intent(context, loginActivityClass);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
            return false;
        }
        return true;
    }
    
    /**
     * Save or update user in local database after Google Sign-In
     * Creates a new user if they don't exist, or updates existing user
     * @param context The activity context
     * @param firebaseUser The authenticated Firebase user
     */
    public static void saveGoogleUserToDatabase(Context context, FirebaseUser firebaseUser) {
        if (firebaseUser == null) {
            return;
        }
        
        UserRepository userRepository = new UserRepository(context);
        
        // Check if user already exists in database
        User existingUser = userRepository.getUserByUidSync(firebaseUser.getUid());
        
        String displayName = firebaseUser.getDisplayName();
        if (displayName == null || displayName.isEmpty()) {
            // Use email username as fallback if display name is not available
            String email = firebaseUser.getEmail();
            if (email != null && email.contains("@")) {
                displayName = email.substring(0, email.indexOf("@"));
            } else {
                displayName = "User";
            }
        }
        
        String email = firebaseUser.getEmail();
        String photoUrl = firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : null;
        
        if (existingUser == null) {
            // Create new user
            User newUser = new User(
                firebaseUser.getUid(),
                displayName,
                email != null ? email : ""
            );
            newUser.setProfileImageUrl(photoUrl);
            newUser.setUpdatedAt(System.currentTimeMillis());
            userRepository.insertUser(newUser);
        } else {
            // Update existing user
            existingUser.setName(displayName);
            if (email != null) {
                existingUser.setEmail(email);
            }
            if (photoUrl != null) {
                existingUser.setProfileImageUrl(photoUrl);
            }
            existingUser.setUpdatedAt(System.currentTimeMillis());
            userRepository.updateUser(existingUser);
        }
    }
}

