package application.com.funagig.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import com.google.android.material.tabs.TabLayout;

import application.com.funagig.R;
import application.com.funagig.models.Gig;
import application.com.funagig.models.User;
import application.com.funagig.repository.UserRepository;
import application.com.funagig.services.GigProcessingService;
import application.com.funagig.utils.AuthHelper;
import application.com.funagig.utils.ThemeUtils;

import androidx.annotation.NonNull;

public class ServiceDetailsActivity extends AppCompatActivity {

    private TextView tvServiceTitle;
    private TextView tvServiceDuration;
    private TextView tvServicePrice;
    private TextView tvServiceCategory;
    private TextView tvTabContent;
    private TextView tvPostedBy;
    private ImageView ivGigCover;
    private TabLayout tabGigDetails;
    private Button btnApplyForGig;
    private Button btnShareGig;
    private Button btnContactSeller;
    private Button btnViewApplicants;
    
    private Gig gig;
    private UserRepository userRepository;

    private BroadcastReceiver gigProcessingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String gigId = intent.getStringExtra(GigProcessingService.EXTRA_GIG_ID);
            String gigTitle = intent.getStringExtra(GigProcessingService.EXTRA_GIG_TITLE);

            if (GigProcessingService.ACTION_GIG_PROCESSED.equals(action)) {
                Toast.makeText(ServiceDetailsActivity.this, 
                    "Application submitted successfully for: " + gigTitle, 
                    Toast.LENGTH_LONG).show();
                btnApplyForGig.setEnabled(false);
                btnApplyForGig.setText("Applied");
            } else if (GigProcessingService.ACTION_GIG_FAILED.equals(action)) {
                String errorMessage = intent.getStringExtra(GigProcessingService.EXTRA_ERROR_MESSAGE);
                Toast.makeText(ServiceDetailsActivity.this, 
                    "Failed to submit application: " + errorMessage, 
                    Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_details);

        initializeViews();
        populateServiceData();
        setClickListeners();
        registerBroadcastReceiver();
    }

    private void initializeViews() {
        ivGigCover = findViewById(R.id.iv_gig_cover);
        tvServiceTitle = findViewById(R.id.tv_service_title);
        tvServicePrice = findViewById(R.id.tv_service_price);
        tvServiceCategory = findViewById(R.id.tv_service_category);
        tvServiceDuration = findViewById(R.id.tv_service_duration);
        tabGigDetails = findViewById(R.id.tab_gig_details);
        tvTabContent = findViewById(R.id.tv_tab_content);
        tvPostedBy = findViewById(R.id.tv_posted_by);
        btnApplyForGig = findViewById(R.id.btn_apply_for_gig);
        btnShareGig = findViewById(R.id.btn_share_gig);
        btnContactSeller = findViewById(R.id.btn_contact_seller);
        btnViewApplicants = findViewById(R.id.btn_view_applicants);
        
        userRepository = new UserRepository(this);
    }

    private void populateServiceData() {
        Intent intent = getIntent();
        if (intent != null) {
            // Try to get Gig object first
            gig = (Gig) intent.getSerializableExtra("gig");
            
            if (gig != null) {
                // Use gig object
                tvServiceTitle.setText(gig.getTitle() != null ? gig.getTitle() : "Untitled Gig");
                tvServicePrice.setText(gig.getBudget() != null ? "UGX " + gig.getBudget() : "UGX 0");
                tvServiceCategory.setText(gig.getCategory() != null ? gig.getCategory() : "General");
                if (tvServiceDuration != null) {
                    String duration = gig.getUrgencyLevel() != null ? gig.getUrgencyLevel() : "Flexible timeline";
                    tvServiceDuration.setText(" • " + duration);
                }
                if (tvPostedBy != null) {
                    String postedBy = gig.getPostedBy() != null ? gig.getPostedBy() : "Unknown poster";
                    tvPostedBy.setText(postedBy);
                }
                if (ivGigCover != null) {
                    ivGigCover.setImageResource(R.drawable.bg_hero_placeholder);
                }
                setupTabs(gig);
                
                // Show/hide View Applicants button based on ownership
                String currentUserId = AuthHelper.getCurrentUserId();
                if (currentUserId != null && currentUserId.equals(gig.getPostedBy())) {
                    // User owns this gig - show View Applicants button
                    if (btnViewApplicants != null) {
                        btnViewApplicants.setVisibility(View.VISIBLE);
                    }
                    // Hide Apply button for owner
                    btnApplyForGig.setVisibility(View.GONE);
                } else {
                    // User doesn't own this gig - hide View Applicants button
                    if (btnViewApplicants != null) {
                        btnViewApplicants.setVisibility(View.GONE);
                    }
                }
            } else {
                // Fallback to old method for backward compatibility
                String title = intent.getStringExtra("service_title");
                String description = intent.getStringExtra("service_description");
                String price = intent.getStringExtra("service_price");
                String category = intent.getStringExtra("service_category");

                tvServiceTitle.setText(title != null ? title : "Sample Service");
                tvServicePrice.setText(price != null ? price : "$50");
                tvServiceCategory.setText(category != null ? category : "General");
                if (tvPostedBy != null) {
                    tvPostedBy.setText("Sample Poster");
                }
                setupTabs(null);

                if (btnViewApplicants != null) {
                    btnViewApplicants.setVisibility(View.GONE);
                }
                }
        }
    }
    private void setupTabs(Gig gig) {
        if (tabGigDetails == null || tvTabContent == null) {
            return;
        }
        final String detailsText = gig != null && gig.getDescription() != null
                ? gig.getDescription()
                : "Details coming soon.";
        final String requirementsText = gig != null && gig.getRequiredSkills() != null
                ? gig.getRequiredSkills()
                : "No special requirements listed.";
        final String locationText = gig != null && gig.getLocation() != null
                ? gig.getLocation()
                : "Location will be shared after booking.";

        tvTabContent.setText(detailsText);
        tabGigDetails.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        tvTabContent.setText(detailsText);
                        break;
                    case 1:
                        tvTabContent.setText(requirementsText);
                        break;
                    case 2:
                        tvTabContent.setText(locationText);
                        break;
                }
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
    }
                

    private void setClickListeners() {
        btnApplyForGig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyForGig();
            }
        });

        btnShareGig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareGig();
            }
        });

        btnContactSeller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactSeller();
            }
        });
        
        if (btnViewApplicants != null) {
            btnViewApplicants.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewApplicants();
                }
            });
        }
    }
    
    private void viewApplicants() {
        if (gig == null) {
            Toast.makeText(this, "Gig information not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Intent intent = new Intent(this, ApplicantsActivity.class);
        intent.putExtra("gig", gig);
        startActivity(intent);
    }

    private void applyForGig() {
        if (gig == null || gig.getGigId() == null || gig.getGigId().isEmpty()) {
            Toast.makeText(this, "Gig information not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check if user is logged in
        String currentUserId = AuthHelper.getCurrentUserId();
        if (currentUserId == null) {
            Toast.makeText(this, "Please log in to apply for gigs", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Get current user info
        com.google.firebase.auth.FirebaseUser firebaseUser = 
            com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            Toast.makeText(this, "User information not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create application
        application.com.funagig.models.JobApplication application = 
            new application.com.funagig.models.JobApplication(0, 0, firebaseUser.getDisplayName());
        application.setGigIdString(gig.getGigId());
        application.setApplicantUid(currentUserId);
        application.setApplicantName(firebaseUser.getDisplayName() != null ? firebaseUser.getDisplayName() : "User");
        application.setApplicantEmail(firebaseUser.getEmail());
        application.setStatus("pending");
        
        // Submit to Firebase
        application.com.funagig.services.FirebaseApplicationSyncService syncService = 
            new application.com.funagig.services.FirebaseApplicationSyncService(this);
        String applicationId = syncService.submitApplication(application);
        
        if (applicationId != null) {
            Toast.makeText(this, "Application submitted successfully!", Toast.LENGTH_SHORT).show();
            btnApplyForGig.setEnabled(false);
            btnApplyForGig.setText("Applied");
        } else {
            Toast.makeText(this, "Failed to submit application. Please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareGig() {
        // Implicit Intent for sharing
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this gig on FunaGig!");
        shareIntent.putExtra(Intent.EXTRA_TEXT, 
            "Service: " + tvServiceTitle.getText().toString() + "\n" +
            "Price: " + tvServicePrice.getText().toString() + "\n" +
            "Description: " + (tvTabContent != null ? tvTabContent.getText().toString() : ""));
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } else {
            Toast.makeText(this, "No sharing app available", Toast.LENGTH_SHORT).show();
        }
    }

    private void contactSeller() {
        if (gig == null || gig.getPostedBy() == null) {
            Toast.makeText(this, "Seller information not available", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // First try to get from local database
        userRepository.getUserByUid(gig.getPostedBy()).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User seller) {
                if (seller != null && (seller.getEmail() != null || seller.getPhone() != null)) {
                    showContactInfoDialog(seller);
                } else {
                    // Try to get from Firebase Realtime Database
                    fetchUserFromFirebase(gig.getPostedBy());
                }
            }
        });
    }
    
    private void fetchUserFromFirebase(String userId) {
        com.google.firebase.database.DatabaseReference userRef = 
            com.google.firebase.database.FirebaseDatabase.getInstance()
                .getReference("users").child(userId);
        
        userRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                User seller = dataSnapshot.getValue(User.class);
                if (seller != null && (seller.getEmail() != null || seller.getPhone() != null)) {
                    showContactInfoDialog(seller);
                } else {
                    // Fallback to Firebase Auth
                    com.google.firebase.auth.FirebaseUser firebaseUser = 
                        com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                    if (firebaseUser != null && firebaseUser.getUid().equals(userId)) {
                        User tempUser = new User();
                        tempUser.setEmail(firebaseUser.getEmail());
                        tempUser.setName(firebaseUser.getDisplayName());
                        tempUser.setPhone(firebaseUser.getPhoneNumber());
                        showContactInfoDialog(tempUser);
                    } else {
                        Toast.makeText(ServiceDetailsActivity.this, 
                            "Seller contact information not available", 
                            Toast.LENGTH_SHORT).show();
                    }
                }
            }
            
            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError databaseError) {
                // Fallback to Firebase Auth
                com.google.firebase.auth.FirebaseUser firebaseUser = 
                    com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null && firebaseUser.getUid().equals(userId)) {
                    User tempUser = new User();
                    tempUser.setEmail(firebaseUser.getEmail());
                    tempUser.setName(firebaseUser.getDisplayName());
                    tempUser.setPhone(firebaseUser.getPhoneNumber());
                    showContactInfoDialog(tempUser);
                } else {
                    Toast.makeText(ServiceDetailsActivity.this, 
                        "Seller contact information not available", 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    
    private void showContactInfoDialog(User seller) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Contact Information");
        
        StringBuilder contactInfo = new StringBuilder();
        if (seller.getEmail() != null && !seller.getEmail().isEmpty()) {
            contactInfo.append("📧 Email: ").append(seller.getEmail()).append("\n\n");
        }
        if (seller.getPhone() != null && !seller.getPhone().isEmpty()) {
            contactInfo.append("📞 Phone: ").append(seller.getPhone()).append("\n\n");
        }
        
        if (contactInfo.length() == 0) {
            contactInfo.append("Contact information not available");
        }
        
        builder.setMessage(contactInfo.toString());
        builder.setPositiveButton("OK", null);
        
        // Add email button if email is available
        if (seller.getEmail() != null && !seller.getEmail().isEmpty()) {
            builder.setNeutralButton("Send Email", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    sendEmailToSeller(seller.getEmail());
                }
            });
        }
        
        // Add call button if phone is available
        if (seller.getPhone() != null && !seller.getPhone().isEmpty()) {
            builder.setNegativeButton("Call", new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(android.content.DialogInterface dialog, int which) {
                    callSeller(seller.getPhone());
                }
            });
        }
        
        builder.show();
    }
    
    private void sendEmailToSeller(String email) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Inquiry about: " + tvServiceTitle.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Hi,\n\nI'm interested in your gig: " + tvServiceTitle.getText().toString());

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send email"));
        } else {
            Toast.makeText(this, "No email app available", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void callSeller(String phone) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(android.net.Uri.parse("tel:" + phone));
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        } else {
            Toast.makeText(this, "No phone app available", Toast.LENGTH_SHORT).show();
        }
    }

    private void registerBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(GigProcessingService.ACTION_GIG_PROCESSED);
        filter.addAction(GigProcessingService.ACTION_GIG_FAILED);
        LocalBroadcastManager.getInstance(this).registerReceiver(gigProcessingReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(gigProcessingReceiver);
    }
}
