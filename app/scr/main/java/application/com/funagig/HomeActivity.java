package application.com.funagig;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import application.com.funagig.activities.AvailableGigsActivity;
import application.com.funagig.activities.UserProfileActivity;
import application.com.funagig.utils.NotificationPermissionHelper;
import application.com.funagig.utils.AuthHelper;
import application.com.funagig.utils.ThemeUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity {

    private Button btnPostJob;
    private Button btnLogin;
    private Button btnRegister;
    private Button btnProfile;
    private Button btnFindGig;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();

        initializeViews();
        updateUIForAuthState(); // Update UI based on authentication state
        setClickListeners();
        requestNotificationPermission();
    }
    
    /**
     * Update UI elements based on authentication state
     * Show/hide login/register buttons and profile button accordingly
     */
    private void updateUIForAuthState() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        boolean isLoggedIn = (currentUser != null);
        
        // Show/hide buttons based on authentication state
        if (btnLogin != null) {
            btnLogin.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        }
        if (btnRegister != null) {
            btnRegister.setVisibility(isLoggedIn ? View.GONE : View.VISIBLE);
        }
        if (btnProfile != null) {
            btnProfile.setVisibility(isLoggedIn ? View.VISIBLE : View.GONE);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Update UI when returning to this activity (e.g., after logout)
        updateUIForAuthState();
    }

    private void initializeViews() {
        btnFindGig = findViewById(R.id.btn_find_gig);
        btnPostJob = findViewById(R.id.btn_PostJob);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnProfile = findViewById(R.id.btn_profile);
    }

    private void setClickListeners() {
        if (btnFindGig != null) {
            btnFindGig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openAvailableGigs();
                }
            });
        }
        btnPostJob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Posting jobs requires authentication
                if (AuthHelper.requireAuthentication(HomeActivity.this, MainActivity.class)) {
                    // Go directly to the gig creation form
                    startActivity(new Intent(getApplicationContext(), InsertJobPostActivity.class));
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check authentication before accessing profile
                if (AuthHelper.requireAuthentication(HomeActivity.this, MainActivity.class)) {
                    startActivity(new Intent(getApplicationContext(), UserProfileActivity.class));
                }
            }
        });
    }
    
    private void openAvailableGigs() {
        Intent intent = new Intent(getApplicationContext(), AvailableGigsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void requestNotificationPermission() {
        if (!NotificationPermissionHelper.isNotificationPermissionGranted(this)) {
            if (NotificationPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                Toast.makeText(this, "Notifications help you stay updated with new gigs and applications", 
                    Toast.LENGTH_LONG).show();
            }
            NotificationPermissionHelper.requestNotificationPermission(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == NotificationPermissionHelper.NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Notification permission denied. You can enable it in settings.", 
                    Toast.LENGTH_LONG).show();
            }
        }
    }
}