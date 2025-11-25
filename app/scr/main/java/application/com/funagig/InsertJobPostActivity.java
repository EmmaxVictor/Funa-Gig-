package application.com.funagig;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import application.com.funagig.models.Gig;
import application.com.funagig.repository.GigRepository;
import application.com.funagig.services.FirebaseGigSyncService;
import application.com.funagig.utils.AuthHelper;
import application.com.funagig.utils.ThemeUtils;

public class InsertJobPostActivity extends AppCompatActivity {

    private EditText txtJobTitle;
    private EditText txtJobDescription;
    private EditText txtJobSalary;
    private EditText txtJobLocation;
    private Spinner spinnerJobUrgency;
    private Button btnJobPost;
    private ChipGroup chipGroupSkills;
    private EditText inputSkill;
    private Button btnAddSkill;
    private TextView tvSchedule;
    private MaterialButtonToggleGroup togglePaymentType;
    private MaterialButtonToggleGroup toggleContactMethod;
    private EditText inputContactPhone;

    private GigRepository gigRepository;
    private FirebaseGigSyncService firebaseGigSyncService;
    private ProgressDialog progressDialog;

    private final List<String> skillList = new ArrayList<>();
    private Calendar selectedSchedule;
    private String selectedPaymentType = "fixed";
    private String selectedContactMethod = "In-App Chat";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeUtils.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_job_post);

        if (!AuthHelper.requireAuthentication(this, MainActivity.class)) {
            finish();
            return;
        }

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        txtJobTitle = findViewById(R.id.txt_job_title);
        txtJobDescription = findViewById(R.id.txt_job_description);
        txtJobSalary = findViewById(R.id.txt_job_salary);
        txtJobLocation = findViewById(R.id.txt_job_location);
        spinnerJobUrgency = findViewById(R.id.spinner_job_urgency);
        btnJobPost = findViewById(R.id.btn_job_post);
        chipGroupSkills = findViewById(R.id.chip_group_skills);
        inputSkill = findViewById(R.id.input_skill);
        btnAddSkill = findViewById(R.id.btn_add_skill);
        tvSchedule = findViewById(R.id.tv_schedule);
        togglePaymentType = findViewById(R.id.toggle_payment_type);
        toggleContactMethod = findViewById(R.id.toggle_contact_method);
        inputContactPhone = findViewById(R.id.input_contact_phone);

        gigRepository = new GigRepository(this);
        firebaseGigSyncService = new FirebaseGigSyncService(this);
        progressDialog = new ProgressDialog(this);

        ArrayAdapter<CharSequence> urgencyAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.urgency_options,
                android.R.layout.simple_spinner_item
        );
        urgencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJobUrgency.setAdapter(urgencyAdapter);

        togglePaymentType.check(R.id.btn_payment_fixed);
        toggleContactMethod.check(R.id.btn_contact_chat);
        inputContactPhone.setVisibility(View.GONE);
        tvSchedule.setOnClickListener(v -> openSchedulePicker());

        togglePaymentType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                selectedPaymentType = checkedId == R.id.btn_payment_hourly ? "hourly" : "fixed";
            }
        });

        toggleContactMethod.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_contact_phone) {
                    selectedContactMethod = "Phone";
                    inputContactPhone.setVisibility(View.VISIBLE);
                } else {
                    selectedContactMethod = "In-App Chat";
                    inputContactPhone.setVisibility(View.GONE);
                }
            }
        });

        btnAddSkill.setOnClickListener(v -> addSkillFromInput());
    }

    private void setupListeners() {
        btnJobPost.setOnClickListener(view -> postGig());
    }

    private void postGig() {
        String title = txtJobTitle.getText().toString().trim();
        String description = txtJobDescription.getText().toString().trim();
        String budget = txtJobSalary.getText().toString().trim();
        String location = txtJobLocation.getText().toString().trim();
        String urgency = spinnerJobUrgency.getSelectedItem() != null ?
                spinnerJobUrgency.getSelectedItem().toString() :
                getString(R.string.urgency_flexible);

        if (TextUtils.isEmpty(title)) {
            txtJobTitle.setError("Title is required");
            txtJobTitle.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(description)) {
            txtJobDescription.setError("Description is required");
            txtJobDescription.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(budget)) {
            txtJobSalary.setError("Budget is required");
            txtJobSalary.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(location)) {
            txtJobLocation.setError("Location is required");
            txtJobLocation.requestFocus();
            return;
        }

        if ("Phone".equals(selectedContactMethod) &&
                TextUtils.isEmpty(inputContactPhone.getText().toString().trim())) {
            inputContactPhone.setError("Phone number required for phone contact");
            inputContactPhone.requestFocus();
            return;
        }

        String userId = AuthHelper.getCurrentUserId();
        if (userId == null) {
            Toast.makeText(this, "Please log in to post a gig", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Posting gig...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Gig gig = new Gig(title, "General", description, location, budget);
        gig.setPostedBy(userId);
        gig.setRequiredSkills(buildSkillsString());
        gig.setBudgetType(selectedPaymentType);
        gig.setStatus("active");
        gig.setUrgencyLevel(urgency);
        gig.setScheduleDetails(tvSchedule.getText().toString());
        gig.setContactMethod(selectedContactMethod);
        gig.setContactPhone(inputContactPhone.getText().toString().trim());

        String gigId = firebaseGigSyncService.uploadGigToFirebase(gig);
        if (gigId != null) {
            gig.setGigId(gigId);
        }

        gigRepository.insertGig(gig);

        progressDialog.dismiss();
        Toast.makeText(this, "Gig posted successfully!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void addSkillFromInput() {
        String skill = inputSkill.getText().toString().trim();
        if (TextUtils.isEmpty(skill)) {
            inputSkill.setError("Enter a skill");
            return;
        }
        if (skillList.contains(skill)) {
            inputSkill.setText("");
            return;
        }
        skillList.add(skill);
        Chip chip = new Chip(this);
        chip.setText(skill);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            chipGroupSkills.removeView(chip);
            skillList.remove(skill);
        });
        chipGroupSkills.addView(chip);
        inputSkill.setText("");
    }

    private String buildSkillsString() {
        if (!skillList.isEmpty()) {
            return TextUtils.join(", ", skillList);
        }
        return "";
    }

    private void openSchedulePicker() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            this,
                            (timePicker, hourOfDay, minute) -> {
                                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                calendar.set(Calendar.MINUTE, minute);
                                selectedSchedule = calendar;
                                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d • h:mm a", Locale.getDefault());
                                tvSchedule.setText(sdf.format(calendar.getTime()));
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                    );
                    timePickerDialog.show();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
