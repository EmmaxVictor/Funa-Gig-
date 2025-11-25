package application.com.funagig.utils;

import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import application.com.funagig.database.FunaGigDatabase;
import application.com.funagig.database.UserDao;
import application.com.funagig.models.User;
import application.com.funagig.repository.GigRepository;
import application.com.funagig.repository.JobApplicationRepository;
import application.com.funagig.repository.JobPostRepository;
import application.com.funagig.repository.UserRepository;
import java.util.List;

/**
 * Helper class to demonstrate usage of Room Database
 * This shows how to initialize and use the database in your activities
 */
public class DatabaseHelper {
    
    private Context context;
    private UserRepository userRepository;
    private GigRepository gigRepository;
    private JobApplicationRepository jobApplicationRepository;
    private JobPostRepository jobPostRepository;
    
    public DatabaseHelper(Context context) {
        this.context = context;
        this.userRepository = new UserRepository(context);
        this.gigRepository = new GigRepository(context);
        this.jobApplicationRepository = new JobApplicationRepository(context);
        this.jobPostRepository = new JobPostRepository(context);
    }
    
    // Example: Save user profile
    public void saveUserProfile(User user) {
        userRepository.insertUser(user);
        Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show();
    }
    
    // Example: Get current user
    public LiveData<User> getCurrentUser(String uid) {
        return userRepository.getUserByUid(uid);
    }
    
    // Example: Update user profile
    public void updateUserProfile(User user) {
        user.setUpdatedAt(System.currentTimeMillis());
        userRepository.updateUser(user);
    }
    
    // Example: Get all gigs
    public LiveData<List<application.com.funagig.models.Gig>> getAllGigs() {
        return gigRepository.getAllGigs();
    }
    
    // Example: Get active gigs
    public LiveData<List<application.com.funagig.models.Gig>> getActiveGigs() {
        return gigRepository.getActiveGigs();
    }
    
    /**
     * Example usage in an Activity:
     * 
     * // Initialize database helper
     * DatabaseHelper dbHelper = new DatabaseHelper(this);
     * 
     * // Observe LiveData in Activity
     * dbHelper.getActiveGigs().observe(this, gigs -> {
     *     if (gigs != null) {
     *         // Update UI with gigs
     *         adapter.submitList(gigs);
     *     }
     * });
     * 
     * // Insert a new user
     * User newUser = new User("user123", "John Doe", "john@example.com");
     * newUser.setPhone("+1234567890");
     * newUser.setBio("Developer");
     * dbHelper.saveUserProfile(newUser);
     */
}

