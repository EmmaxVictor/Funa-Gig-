package application.com.funagig.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import application.com.funagig.R;
import application.com.funagig.MainActivity;
import application.com.funagig.utils.AuthHelper;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import application.com.funagig.adapters.ApplicantAdapter;
import application.com.funagig.models.JobApplication;
import application.com.funagig.repository.JobApplicationRepository;
import com.google.firebase.database.*;
import java.util.List;

public class MyApplicationsActivity extends AppCompatActivity {

    private TextView tvApplicationCount;
    private Button btnViewPendingApplications;
    private Button btnViewAcceptedApplications;
    private Button btnViewRejectedApplications;
    private Button btnBackToProfile;
    private Button btnRefreshApplications;

    private RecyclerView recyclerViewMyApplications;
    private ApplicantAdapter applicationsAdapter;
    private JobApplicationRepository jobApplicationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_applications);

        // Require authentication to view user's applications
        if (!AuthHelper.requireAuthentication(this, MainActivity.class)) {
            finish();
            return;
        }

        initializeViews();
        populateApplicationData();
        setClickListeners();

        recyclerViewMyApplications = findViewById(R.id.recycler_view_my_applications);
        recyclerViewMyApplications.setLayoutManager(new LinearLayoutManager(this));
        applicationsAdapter = new ApplicantAdapter(this);
        recyclerViewMyApplications.setAdapter(applicationsAdapter);
        jobApplicationRepository = new JobApplicationRepository(this);
        loadMyApplications();
        setupSwipeToDelete();
    }

    private void initializeViews() {
        tvApplicationCount = findViewById(R.id.tv_application_count);
        btnViewPendingApplications = findViewById(R.id.btn_view_pending_applications);
        btnViewAcceptedApplications = findViewById(R.id.btn_view_accepted_applications);
        btnViewRejectedApplications = findViewById(R.id.btn_view_rejected_applications);
        btnBackToProfile = findViewById(R.id.btn_back_to_profile);
        btnRefreshApplications = findViewById(R.id.btn_refresh_applications);
    }

    private void populateApplicationData() {
        Intent intent = getIntent();
        String userId = intent.getStringExtra("user_id");
        
        // Simulate application data
        tvApplicationCount.setText("You have 3 pending, 8 accepted, and 2 rejected applications");
    }

    private void setClickListeners() {
        btnViewPendingApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPendingApplications();
            }
        });

        btnViewAcceptedApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewAcceptedApplications();
            }
        });

        btnViewRejectedApplications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewRejectedApplications();
            }
        });

        btnBackToProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackToProfile();
            }
        });
    }

    private void viewPendingApplications() {
        Toast.makeText(this, "Showing pending applications...", Toast.LENGTH_SHORT).show();
        // In a real app, this would show a list of pending applications
    }

    private void viewAcceptedApplications() {
        Toast.makeText(this, "Showing accepted applications...", Toast.LENGTH_SHORT).show();
        // In a real app, this would show a list of accepted applications
    }

    private void viewRejectedApplications() {
        Toast.makeText(this, "Showing rejected applications...", Toast.LENGTH_SHORT).show();
        // In a real app, this would show a list of rejected applications
    }

    private void goBackToProfile() {
        finish();
    }

    private void loadMyApplications() {
        String userId = AuthHelper.getCurrentUserId();
        if (userId != null) {
            jobApplicationRepository.getApplicationsByApplicant(Integer.parseInt(userId)).observe(this, apps -> applicationsAdapter.setApplicantList(apps));
        }
    }

    private void setupSwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                JobApplication app = applicationsAdapter.getApplicationAt(position);
                jobApplicationRepository.deleteApplication(app);
            }
        };
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewMyApplications);
    }

    @Override
    protected void onStart() {
        super.onStart();
        findViewById(R.id.btn_refresh_applications).setOnClickListener(v -> {
            syncApplicationsWithFirebase();
        });
    }

    private void syncApplicationsWithFirebase() {
        String userId = AuthHelper.getCurrentUserId();
        if (userId == null) return;
        jobApplicationRepository.getApplicationsByApplicant(Integer.parseInt(userId)).observe(this, applications -> {
            if (applications == null) return;
            for (JobApplication application : applications) {
                if (application.getGigIdString() == null) continue;
                DatabaseReference appRef = FirebaseDatabase.getInstance()
                    .getReference("applications")
                    .child(application.getGigIdString())
                    .child(String.valueOf(application.getId()));
                appRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String newStatus = snapshot.child("status").getValue(String.class);
                            if (newStatus != null && !newStatus.equals(application.getStatus())) {
                                application.setStatus(newStatus);
                                jobApplicationRepository.updateApplication(application);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError error) {}
                });
            }
        });
        loadMyApplications();
    }
}
