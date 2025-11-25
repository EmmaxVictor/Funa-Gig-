# Room Database Setup for FunaGig

This document describes the Room database implementation in the FunaGig app.

## Overview

Room Database has been successfully added to your app with the following components:

### Entities (Data Models)
- **User**: Stores user profile information
- **Gig**: Stores gig/job listings
- **JobApplication**: Stores job application records
- **JobPost**: Stores job posts from various sources (LinkedIn, BrighterMonday, etc.)

### DAOs (Data Access Objects)
- **UserDao**: Operations for user data
- **GigDao**: Operations for gig data
- **JobApplicationDao**: Operations for job application data
- **JobPostDao**: Operations for job post data

### Repositories
- **UserRepository**: Repository for user operations
- **GigRepository**: Repository for gig operations
- **JobApplicationRepository**: Repository for job application operations
- **JobPostRepository**: Repository for job post operations

### Core Components
- **FunaGigDatabase**: Main Room database class
- **Converters**: Type converters for Room
- **AppExecutors**: Executor service for database operations

## Dependencies Added

The following dependencies have been added to your project:
- `androidx.room:room-runtime:2.6.1`
- `androidx.room:room-compiler:2.6.1`
- `org.jetbrains.kotlin.kapt` (for annotation processing)

## How to Use

### 1. Initialize Database in Your Activity

```java
public class YourActivity extends AppCompatActivity {
    
    private DatabaseHelper dbHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your);
        
        // Initialize database helper
        dbHelper = new DatabaseHelper(this);
    }
}
```

### 2. Saving Data

```java
// Save a user profile
User user = new User("user123", "John Doe", "john@example.com");
user.setPhone("+1234567890");
user.setBio("Freelance Developer");
dbHelper.saveUserProfile(user);

// Save a gig
Gig gig = new Gig("Web Developer Needed", "Web Development", 
                   "Need a skilled web developer", "Remote", "500");
gig.setPostedBy("user123");
gigRepository.insertGig(gig);
```

### 3. Retrieving Data with LiveData

```java
// Observe LiveData for reactive UI updates
dbHelper.getActiveGigs().observe(this, gigs -> {
    if (gigs != null && !gigs.isEmpty()) {
        // Update your RecyclerView adapter
        adapter.submitList(gigs);
    }
});

// Get current user
dbHelper.getCurrentUser(currentUserId).observe(this, user -> {
    if (user != null) {
        // Update user info in UI
        tvUserName.setText(user.getName());
        tvUserEmail.setText(user.getEmail());
    }
});
```

### 4. Search Functionality

```java
// Search gigs
gigRepository.searchGigs("developer").observe(this, gigs -> {
    // Display search results
});

// Search job posts
jobPostRepository.searchJobPosts("software").observe(this, jobPosts -> {
    // Display job posts
});
```

### 5. Update and Delete Operations

```java
// Update user
User user = getCurrentUser();
user.setPhone("+9876543210");
user.setUpdatedAt(System.currentTimeMillis());
userRepository.updateUser(user);

// Delete application
JobApplication application = getApplication();
jobApplicationRepository.deleteApplication(application);
```

## Database Structure

### Users Table
- `id` (Primary Key, Auto-Generated)
- `uid` (Firebase User ID)
- `name`, `email`, `phone`, `bio`
- `profileImageUrl`, `skills`
- `rating`, `completedGigs`
- `createdAt`, `updatedAt`

### Gigs Table
- `id` (Primary Key, Auto-Generated)
- `gigId`
- `title`, `category`, `description`
- `location`, `budget`, `budgetType`
- `postedBy`, `postedDate`, `deadline`
- `status` (active, completed, cancelled)
- `requiredSkills`, `imageUrl`
- `applicantsCount`
- `createdAt`, `updatedAt`

### Job Applications Table
- `id` (Primary Key, Auto-Generated)
- `gigId` (Foreign Key)
- `applicantId` (Foreign Key)
- `applicantName`, `coverLetter`
- `proposedBudget`, `status`
- `appliedDate`, `reviewedDate`
- `reviewNotes`
- `createdAt`, `updatedAt`

### Job Posts Table
- `id` (Primary Key, Auto-Generated)
- `jobId`
- `jobTitle`, `companyName`, `location`
- `jobType`, `salary`, `description`
- `postedDate`, `applyUrl`, `companyLogo`
- `source` (LinkedIn, BrighterMonday, etc.)
- `isSaved`
- `createdAt`, `updatedAt`

## Next Steps

1. **Sync Gradle**: Click "Sync Now" in Android Studio to download Room dependencies
2. **Implement in Activities**: Start using the database in your existing activities
3. **Replace Mock Data**: Replace hardcoded data with database queries
4. **Add Migration Logic**: When you modify entities in the future, create migration scripts

## Important Notes

- The database uses `.allowMainThreadQueries()` for development. Remove this for production.
- For production, use `AppExecutors` for background operations
- The database uses `.fallbackToDestructiveMigration()` for development. Implement proper migrations for production.

## Example Integration

Here's how you could integrate this into your existing `UserProfileActivity`:

```java
public class UserProfileActivity extends AppCompatActivity {
    
    private DatabaseHelper dbHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        
        dbHelper = new DatabaseHelper(this);
        
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        // Observe user data
        dbHelper.getCurrentUser(currentUserId).observe(this, user -> {
            if (user != null) {
                tvUserName.setText(user.getName());
                tvUserEmail.setText(user.getEmail());
                tvUserRating.setText(String.valueOf(user.getRating()));
                tvCompletedGigs.setText(String.valueOf(user.getCompletedGigs()));
            }
        });
    }
    
    private void updateProfile() {
        // Get current user and update
        User currentUser = dbHelper.getCurrentUser(currentUserId).getValue();
        if (currentUser != null) {
            currentUser.setPhone(newPhone);
            currentUser.setBio(newBio);
            dbHelper.updateUserProfile(currentUser);
        }
    }
}
```

## Resources

- [Room Database Documentation](https://developer.android.com/training/data-storage/room)
- [LiveData Overview](https://developer.android.com/topic/libraries/architecture/livedata)
- [Room with Architecture Components](https://developer.android.com/jetpack/androidx/releases/room)

