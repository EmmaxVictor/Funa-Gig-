package application.com.funagig.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import application.com.funagig.InsertJobPostActivity;
import application.com.funagig.MainActivity;
import application.com.funagig.R;
import application.com.funagig.adapters.GigAdapter;
import application.com.funagig.models.Gig;
import application.com.funagig.repository.GigRepository;
import application.com.funagig.utils.AuthHelper;
import application.com.funagig.utils.ThemeUtils;

public class MyGigsActivity extends AppCompatActivity {

    private TextView tvGigCount;
    private RecyclerView recyclerViewMyGigs;
    private EditText inputSearch;
    private ImageButton btnClearSearch;
    private TabLayout tabGigStatus;
    private FloatingActionButton fabNewGig;
    private GigAdapter myGigsAdapter;
    private GigRepository gigRepository;

    private final List<Gig> allUserGigs = new ArrayList<>();
    private String currentStatusFilter = "applied";
    private String currentSearchTerm = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_gigs);

        if (!AuthHelper.requireAuthentication(this, MainActivity.class)) {
            finish();
            return;
        }

        initializeViews();
        setupRecyclerView();
        setupInteractions();

        gigRepository = new GigRepository(this);
        loadMyGigs();
    }

    private void initializeViews() {
        tvGigCount = findViewById(R.id.tv_gig_count);
        recyclerViewMyGigs = findViewById(R.id.recycler_view_my_gigs);
        inputSearch = findViewById(R.id.input_search_my_gigs);
        btnClearSearch = findViewById(R.id.btn_clear_my_gig_search);
        tabGigStatus = findViewById(R.id.tab_gig_status);
        fabNewGig = findViewById(R.id.fab_new_gig);
        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnFilterOptions = findViewById(R.id.btn_filter_options);

        btnBack.setOnClickListener(v -> finish());
        btnFilterOptions.setOnClickListener(v ->
                Toast.makeText(this, "Advanced filters coming soon", Toast.LENGTH_SHORT).show());
    }

    private void setupRecyclerView() {
        myGigsAdapter = new GigAdapter(this);
        recyclerViewMyGigs.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMyGigs.setAdapter(myGigsAdapter);
        myGigsAdapter.setOnGigClickListener(gig -> {
            Intent intent = new Intent(this, ServiceDetailsActivity.class);
            intent.putExtra("gig", gig);
            startActivity(intent);
        });
    }

    private void setupInteractions() {
        fabNewGig.setOnClickListener(v -> {
            if (AuthHelper.requireAuthentication(this, MainActivity.class)) {
                startActivity(new Intent(this, InsertJobPostActivity.class));
            }
        });

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentSearchTerm = s.toString().toLowerCase();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnClearSearch.setOnClickListener(v -> inputSearch.setText(""));

        tabGigStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentStatusFilter = tab.getText().toString().toLowerCase();
                applyFilters();
            }

            @Override public void onTabUnselected(TabLayout.Tab tab) { }
            @Override public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void loadMyGigs() {
        String userId = AuthHelper.getCurrentUserId();
        if (userId == null) {
            return;
        }
        gigRepository.getGigsByUser(userId).observe(this, gigs -> {
            allUserGigs.clear();
            if (gigs != null) {
                allUserGigs.addAll(gigs);
            }
            updateGigCountSummary();
            applyFilters();
        });
    }

    private void applyFilters() {
        List<Gig> filtered = new ArrayList<>();
        for (Gig gig : allUserGigs) {
            if (!matchesStatus(gig)) continue;
            if (!matchesSearch(gig)) continue;
            filtered.add(gig);
        }
        myGigsAdapter.setGigList(filtered);
    }

    private boolean matchesStatus(Gig gig) {
        if (currentStatusFilter.contains("applied")) {
            return !"completed".equalsIgnoreCase(gig.getStatus());
        } else if (currentStatusFilter.contains("progress")) {
            return "active".equalsIgnoreCase(gig.getStatus()) || "in_progress".equalsIgnoreCase(gig.getStatus());
        } else {
            return "completed".equalsIgnoreCase(gig.getStatus()) || "cancelled".equalsIgnoreCase(gig.getStatus());
        }
    }

    private boolean matchesSearch(Gig gig) {
        if (currentSearchTerm.isEmpty()) return true;
        String title = gig.getTitle() != null ? gig.getTitle().toLowerCase() : "";
        String description = gig.getDescription() != null ? gig.getDescription().toLowerCase() : "";
        return title.contains(currentSearchTerm) || description.contains(currentSearchTerm);
    }

    private void updateGigCountSummary() {
        int applied = 0;
        int inProgress = 0;
        int history = 0;
        for (Gig gig : allUserGigs) {
            String status = gig.getStatus() != null ? gig.getStatus().toLowerCase() : "";
            if ("completed".equals(status) || "cancelled".equals(status)) {
                history++;
            } else if ("active".equals(status) || "in_progress".equals(status)) {
                inProgress++;
            } else {
                applied++;
            }
        }
        tvGigCount.setText(String.format("Applied %d • In Progress %d • History %d", applied, inProgress, history));
    }
}
