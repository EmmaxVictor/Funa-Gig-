package application.com.funagig.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import application.com.funagig.R;
import application.com.funagig.HomeActivity;
import application.com.funagig.MainActivity;
import application.com.funagig.utils.AuthHelper;
import application.com.funagig.utils.NotificationPermissionHelper;
import application.com.funagig.utils.ThemeUtils;

import com.google.firebase.auth.FirebaseUser;

public class UserProfileActivity extends AppCompatActivity {

    private TextView tvUserName;
    private TextView tvUserEmail;
    private TextView tvUserRating;
    private TextView tvCompletedGigs;
    private ImageButton btnEditProfile;
    private ImageButton btnBack;
    private Button btnLogout;
    private View rowPersonalDetails;
    private View rowContactInfo;
    private View rowMySkills;
    private View rowMyGigs;
    private View rowMyApplications;
    private View rowNotifications;
    private View rowPrivacy;
    private View rowHelp;
    private View rowTerms;
    private SwitchMaterial switchDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Check authentication - redirect to login if not authenticated
        if (!AuthHelper.requireAuthentication(this, MainActivity.class)) {
            finish();
            return;
        }

        initializeViews();
        populateUserData();
        setClickListeners();
    }

    private void initializeViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        tvUserRating = findViewById(R.id.tv_user_rating);
        tvCompletedGigs = findViewById(R.id.tv_completed_gigs);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnBack = findViewById(R.id.btn_back);
        rowPersonalDetails = findViewById(R.id.row_personal_details);
        rowContactInfo = findViewById(R.id.row_contact_information);
        rowMySkills = findViewById(R.id.row_my_skills);
        rowMyGigs = findViewById(R.id.row_my_gigs);
        rowMyApplications = findViewById(R.id.row_my_applications);
        rowNotifications = findViewById(R.id.row_notifications);
        rowPrivacy = findViewById(R.id.row_privacy);
        rowHelp = findViewById(R.id.row_help_center);
        rowTerms = findViewById(R.id.row_terms);
        switchDarkMode = findViewById(R.id.switch_dark_mode);
        btnLogout = findViewById(R.id.btn_logout);

        if (switchDarkMode != null) {
            switchDarkMode.setChecked(ThemeUtils.isDarkModeEnabled(this));
        }
    }

    private void populateUserData() {
        // Get user data from Firebase Auth (secure, no plain text storage)
        FirebaseUser currentUser = AuthHelper.getCurrentUser();
        if (currentUser != null) {
            String email = currentUser.getEmail();
            String displayName = currentUser.getDisplayName();
            
            // Use Firebase data if available, otherwise use defaults
            tvUserEmail.setText(email != null ? email : "No email");
            tvUserName.setText(displayName != null ? displayName : "User");
        } else {
            // Fallback if user data not available
            tvUserName.setText("User");
            tvUserEmail.setText("No email");
        }
        
        // These would come from your database in a real app
        tvUserRating.setText("4.8 ⭐");
        tvCompletedGigs.setText("23 Completed Gigs");
    }

    private void setClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        View.OnClickListener editListener = v -> editProfile();
        if (btnEditProfile != null) {
            btnEditProfile.setOnClickListener(editListener);
        }
        if (rowPersonalDetails != null) {
            rowPersonalDetails.setOnClickListener(editListener);
        }
        if (rowContactInfo != null) {
            rowContactInfo.setOnClickListener(editListener);
        }
        if (rowMySkills != null) {
            rowMySkills.setOnClickListener(editListener);
        }

        if (rowMyGigs != null) {
            rowMyGigs.setOnClickListener(v -> viewMyGigs());
        }
        if (rowMyApplications != null) {
            rowMyApplications.setOnClickListener(v -> viewMyApplications());
            }

        if (rowNotifications != null) {
            rowNotifications.setOnClickListener(v -> NotificationPermissionHelper.requestNotificationPermission(this));
        }
        if (rowPrivacy != null) {
            rowPrivacy.setOnClickListener(v -> openSettings());
            }
        if (rowHelp != null) {
            rowHelp.setOnClickListener(v -> openHelpCenter());
        }
        if (rowTerms != null) {
            rowTerms.setOnClickListener(v -> openTerms());
        }
        if (switchDarkMode != null) {
            switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
                ThemeUtils.setDarkMode(this, isChecked);
                recreate();
            });
        }
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> logout());
        }
    }

    private void editProfile() {
        // Explicit Intent to EditProfileActivity
        Intent intent = new Intent(this, EditProfileActivity.class);
        intent.putExtra("user_name", tvUserName.getText().toString());
        intent.putExtra("user_email", tvUserEmail.getText().toString());
        startActivityForResult(intent, 1001);
    }

    private void viewMyGigs() {
        String userId = AuthHelper.getCurrentUserId();
        Intent intent = new Intent(this, MyGigsActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    private void viewMyApplications() {
        String userId = AuthHelper.getCurrentUserId();
        Intent intent = new Intent(this, MyApplicationsActivity.class);
        intent.putExtra("user_id", userId);
        startActivity(intent);
    }

    private void openSettings() {
        // Implicit Intent for device settings
        Intent intent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Settings not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openHelpCenter() {
        Toast.makeText(this, "Opening help center...", Toast.LENGTH_SHORT).show();
    }

    private void openTerms() {
        Toast.makeText(this, "Opening terms of service...", Toast.LENGTH_SHORT).show();
    }

    private void logout() {
        // Securely sign out from Firebase (clears session, no credentials stored)
        AuthHelper.signOut(this);
        
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        
        // Navigate to home and clear activity stack
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            if (data != null) {
                String newName = data.getStringExtra("new_name");
                String newEmail = data.getStringExtra("new_email");
                
                if (newName != null) {
                    tvUserName.setText(newName);
                }
                if (newEmail != null) {
                    tvUserEmail.setText(newEmail);
                }
                
                Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
