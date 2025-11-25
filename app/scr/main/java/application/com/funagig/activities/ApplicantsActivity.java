package application.com.funagig.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import application.com.funagig.R;
import application.com.funagig.adapters.ApplicantAdapter;
import application.com.funagig.models.Gig;
import application.com.funagig.models.JobApplication;
import application.com.funagig.repository.JobApplicationRepository;
import application.com.funagig.services.FirebaseGigSyncService;
import application.com.funagig.utils.AuthHelper;

import android.util.Log;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ApplicantsActivity extends AppCompatActivity {

    private TextView tvGigTitle;
    private TextView tvNoApplicants;
    private RecyclerView recyclerViewApplicants;
    private Button btnMarkAsTaken;
    
    private Gig gig;
    private ApplicantAdapter applicantAdapter;
    private JobApplicationRepository applicationRepository;
    private FirebaseGigSyncService firebaseGigSyncService;
    private DatabaseReference applicationsRef;
    private ValueEventListener applicationsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicants);

        // Get gig from intent
        gig = (Gig) getIntent().getSerializableExtra("gig");
        if (gig == null) {
            Toast.makeText(this, "Gig information not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Check if user is the owner of this gig
        String currentUserId = AuthHelper.getCurrentUserId();
        if (currentUserId == null || !currentUserId.equals(gig.getPostedBy())) {
            Toast.makeText(this, "You can only view applicants for your own gigs", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupRecyclerView();
        loadApplicants();
        setupListeners();
    }

    private void initializeViews() {
        tvGigTitle = findViewById(R.id.tv_gig_title);
        tvNoApplicants = findViewById(R.id.tv_no_applicants);
        recyclerViewApplicants = findViewById(R.id.recycler_view_applicants);
        btnMarkAsTaken = findViewById(R.id.btn_mark_as_taken);
        
        tvGigTitle.setText(gig.getTitle() != null ? gig.getTitle() : "Gig Applicants");
        
        applicationRepository = new JobApplicationRepository(this);
        firebaseGigSyncService = new FirebaseGigSyncService(this);
    }

    private void setupRecyclerView() {
        applicantAdapter = new ApplicantAdapter(this);
        recyclerViewApplicants.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewApplicants.setAdapter(applicantAdapter);
    }

    private void setupListeners() {
        btnMarkAsTaken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMarkAsTakenDialog();
            }
        });
    }

    private void loadApplicants() {
        if (gig.getGigId() == null || gig.getGigId().isEmpty()) {
            tvNoApplicants.setVisibility(View.VISIBLE);
            recyclerViewApplicants.setVisibility(View.GONE);
            return;
        }

        // Listen to Firebase for real-time updates
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        applicationsRef = database.getReference("applications").child(gig.getGigId());
        
        applicationsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<JobApplication> applications = new ArrayList<>();
                
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        JobApplication application = snapshot.getValue(JobApplication.class);
                        if (application != null) {
                            // Set the application ID from Firebase key
                            try {
                                application.setId(Integer.parseInt(snapshot.getKey()));
                            } catch (NumberFormatException e) {
                                // If key is not a number, use hash code
                                application.setId(snapshot.getKey().hashCode());
                            }
                            application.setGigIdString(gig.getGigId());
                            applications.add(application);
                        }
                    } catch (Exception e) {
                        Log.e("ApplicantsActivity", "Error parsing application: " + e.getMessage());
                    }
                }
                
                updateApplicantsList(applications);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ApplicantsActivity.this, 
                    "Error loading applicants: " + databaseError.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        };
        
        applicationsRef.addValueEventListener(applicationsListener);
    }

    private void updateApplicantsList(List<JobApplication> applications) {
        if (applications != null && !applications.isEmpty()) {
            applicantAdapter.setApplicantList(applications);
            recyclerViewApplicants.setVisibility(View.VISIBLE);
            tvNoApplicants.setVisibility(View.GONE);
        } else {
            applicantAdapter.setApplicantList(new ArrayList<>());
            recyclerViewApplicants.setVisibility(View.GONE);
            tvNoApplicants.setVisibility(View.VISIBLE);
        }
    }

    private void showMarkAsTakenDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Mark Gig as Taken")
            .setMessage("Are you sure you want to mark this gig as taken? It will be removed from the available gigs feed.")
            .setPositiveButton("Yes, Mark as Taken", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    markGigAsTaken();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    private void markGigAsTaken() {
        if (gig.getGigId() == null || gig.getGigId().isEmpty()) {
            Toast.makeText(this, "Gig ID not available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update gig status to "taken" in Firebase
        firebaseGigSyncService.updateGigStatus(gig.getGigId(), "taken");
        
        Toast.makeText(this, "Gig marked as taken. It will be removed from the feed.", Toast.LENGTH_SHORT).show();
        
        // Return to home
        Intent intent = new Intent(this, application.com.funagig.HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (applicationsRef != null && applicationsListener != null) {
            applicationsRef.removeEventListener(applicationsListener);
        }
    }
}

