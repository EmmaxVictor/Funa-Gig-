package application.com.funagig.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import application.com.funagig.InsertJobPostActivity;
import application.com.funagig.MainActivity;
import application.com.funagig.R;
import application.com.funagig.adapters.GigAdapter;
import application.com.funagig.models.Gig;
import application.com.funagig.repository.GigRepository;
import application.com.funagig.services.FirebaseGigSyncService;
import application.com.funagig.utils.AuthHelper;
import application.com.funagig.utils.ThemeUtils;

public class AvailableGigsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmptyState;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText inputSearch;
    private ImageButton btnClearSearch;
    private MaterialButton btnFilterCategory;
    private MaterialButton btnFilterLocation;
    private MaterialButton btnFilterSkill;
    private Button btnSortToggle;
    private Button btnManualRefresh;
    private TextView tvSortValue;
    private FloatingActionButton fabMap;
    private ExtendedFloatingActionButton fabPostGig;

    private GigAdapter gigAdapter;
    private GigRepository gigRepository;
    private FirebaseGigSyncService firebaseGigSyncService;

    private final List<Gig> currentGigList = new ArrayList<>();
    private boolean gigsObserverRegistered = false;

    private String selectedCategory = "All";
    private String selectedLocation = "Anywhere";
    private String selectedSkill = "Any";
    private SortMode sortMode = SortMode.NEWEST;

    private enum SortMode {
        NEWEST,
        BUDGET_HIGH,
        TITLE_AZ
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_gigs);

        initializeViews();
        setupRecyclerView();
        setupFilters();
        setupRefreshControls();

        gigRepository = new GigRepository(this);
        firebaseGigSyncService = new FirebaseGigSyncService(this);

        loadActiveGigs();
        firebaseGigSyncService.startListening();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view_gigs);
        tvEmptyState = findViewById(R.id.tv_no_gigs);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        inputSearch = findViewById(R.id.input_search_gigs);
        btnClearSearch = findViewById(R.id.btn_clear_search);
        btnFilterCategory = findViewById(R.id.btn_filter_category);
        btnFilterLocation = findViewById(R.id.btn_filter_location);
        btnFilterSkill = findViewById(R.id.btn_filter_skill);
        btnSortToggle = findViewById(R.id.btn_sort_toggle);
        btnManualRefresh = findViewById(R.id.btn_manual_refresh);
        tvSortValue = findViewById(R.id.tv_sort_value);
        fabMap = findViewById(R.id.fab_map);
        fabPostGig = findViewById(R.id.fab_post_gig);

        ImageButton btnBack = findViewById(R.id.btn_back);
        ImageButton btnNotifications = findViewById(R.id.btn_notifications);

        btnBack.setOnClickListener(v -> onBackPressed());
        btnNotifications.setOnClickListener(v ->
                Toast.makeText(this, "Notifications coming soon", Toast.LENGTH_SHORT).show());
    }

    private void setupRecyclerView() {
        gigAdapter = new GigAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(gigAdapter);

        gigAdapter.setOnGigClickListener(gig -> {
            Intent intent = new Intent(this, ServiceDetailsActivity.class);
            intent.putExtra("gig", gig);
            startActivity(intent);
        });
    }

    private void setupFilters() {
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFiltersAndSort();
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnClearSearch.setOnClickListener(v -> inputSearch.setText(""));

        btnFilterCategory.setOnClickListener(v ->
                showFilterMenu(btnFilterCategory, R.array.gig_categories));
        btnFilterLocation.setOnClickListener(v ->
                showFilterMenu(btnFilterLocation, R.array.gig_locations));
        btnFilterSkill.setOnClickListener(v ->
                showFilterMenu(btnFilterSkill, R.array.gig_skill_levels));

        btnSortToggle.setOnClickListener(v -> {
            switch (sortMode) {
                case NEWEST:
                    sortMode = SortMode.BUDGET_HIGH;
                    tvSortValue.setText("Sort by: Highest Budget");
                    break;
                case BUDGET_HIGH:
                    sortMode = SortMode.TITLE_AZ;
                    tvSortValue.setText("Sort by: Alphabetical");
                    break;
                case TITLE_AZ:
                    sortMode = SortMode.NEWEST;
                    tvSortValue.setText("Sort by: Newest");
                    break;
            }
            applyFiltersAndSort();
        });

        fabMap.setOnClickListener(v ->
                Toast.makeText(this, "Map view coming soon", Toast.LENGTH_SHORT).show());

        fabPostGig.setOnClickListener(v -> {
            if (AuthHelper.requireAuthentication(this, MainActivity.class)) {
                startActivity(new Intent(this, InsertJobPostActivity.class));
            }
        });
    }

    private void setupRefreshControls() {
        swipeRefreshLayout.setOnRefreshListener(this::refreshGigs);
        btnManualRefresh.setOnClickListener(v -> {
            swipeRefreshLayout.setRefreshing(true);
            refreshGigs();
        });
    }

    private void showFilterMenu(MaterialButton anchor, int arrayRes) {
        PopupMenu popupMenu = new PopupMenu(this, anchor);
        String[] options = getResources().getStringArray(arrayRes);
        for (String option : options) {
            popupMenu.getMenu().add(option);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            String value = item.getTitle().toString();
            anchor.setText(value);
            anchor.setSelected(!"All".equalsIgnoreCase(value));

            if (anchor == btnFilterCategory) {
                selectedCategory = value;
            } else if (anchor == btnFilterLocation) {
                selectedLocation = value;
            } else if (anchor == btnFilterSkill) {
                selectedSkill = value;
            }
            applyFiltersAndSort();
            return true;
        });
        popupMenu.show();
    }

    private void refreshGigs() {
        firebaseGigSyncService.stopListening();
        firebaseGigSyncService.startListening();
        applyFiltersAndSort();
        swipeRefreshLayout.postDelayed(() -> swipeRefreshLayout.setRefreshing(false), 600);
    }

    private void loadActiveGigs() {
        if (gigsObserverRegistered) {
            applyFiltersAndSort();
            return;
        }
        gigsObserverRegistered = true;
        gigRepository.getActiveGigs().observe(this, gigs -> {
            currentGigList.clear();
            if (gigs != null) {
                currentGigList.addAll(gigs);
            }
            applyFiltersAndSort();
        });
    }

    private void applyFiltersAndSort() {
        List<Gig> filtered = new ArrayList<>();
        String term = inputSearch.getText().toString().toLowerCase().trim();

        for (Gig gig : currentGigList) {
            if (!matchesSearch(gig, term)) continue;
            if (!matchesCategory(gig)) continue;
            if (!matchesLocation(gig)) continue;
            if (!matchesSkill(gig)) continue;
            filtered.add(gig);
        }

        sortList(filtered);
        gigAdapter.setGigList(filtered);
        toggleEmptyState(filtered.isEmpty());
    }

    private boolean matchesSearch(Gig gig, String term) {
        if (term.isEmpty()) return true;
        String title = normalizeString(gig.getTitle());
        String description = normalizeString(gig.getDescription());
        return title.contains(term) || description.contains(term);
    }

    private boolean matchesCategory(Gig gig) {
        if ("All".equalsIgnoreCase(selectedCategory)) return true;
        return normalizeString(gig.getCategory()).contains(selectedCategory.toLowerCase());
    }

    private boolean matchesLocation(Gig gig) {
        if ("Anywhere".equalsIgnoreCase(selectedLocation)) return true;
        return normalizeString(gig.getLocation()).contains(selectedLocation.toLowerCase());
    }

    private boolean matchesSkill(Gig gig) {
        if ("Any".equalsIgnoreCase(selectedSkill)) return true;
        String skills = normalizeString(gig.getRequiredSkills());
        return skills.contains(selectedSkill.toLowerCase());
    }

    private void sortList(List<Gig> list) {
        switch (sortMode) {
            case BUDGET_HIGH:
                Collections.sort(list, (g1, g2) -> Double.compare(
                        parseBudget(g2.getBudget()), parseBudget(g1.getBudget())
                ));
                break;
            case TITLE_AZ:
                Collections.sort(list, Comparator.comparing(gig -> normalizeString(gig.getTitle())));
                break;
            case NEWEST:
            default:
                Collections.sort(list, (g1, g2) -> Long.compare(g2.getPostedDate(), g1.getPostedDate()));
                break;
        }
    }

    private void toggleEmptyState(boolean isEmpty) {
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        tvEmptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
    }

    private static String normalizeString(String value) {
        return value != null ? value.toLowerCase().trim() : "";
    }

    private static double parseBudget(String budget) {
        if (budget == null) {
            return 0;
        }
        String numeric = budget.replaceAll("[^0-9.]", "");
        if (numeric.isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(numeric);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (firebaseGigSyncService != null) {
            firebaseGigSyncService.stopListening();
        }
    }
}

