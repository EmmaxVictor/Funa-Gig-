package application.com.funagig.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

import application.com.funagig.R;
import application.com.funagig.utils.PermissionHelper;

public class EditProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA = 2001;
    private static final int REQUEST_GALLERY = 2002;

    private EditText etUserName;
    private EditText etUserEmail;
    private EditText etUserBio;
    private EditText etUserSkills;
    private Button btnSaveProfile;
    private Button btnCancel;
    private Button btnTakePhoto;
    private Button btnChoosePhoto;
    private ImageView ivProfilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initializeViews();
        populateCurrentData();
        setClickListeners();
    }

    private void initializeViews() {
        etUserName = findViewById(R.id.et_user_name);
        etUserEmail = findViewById(R.id.et_user_email);
        etUserBio = findViewById(R.id.et_user_bio);
        etUserSkills = findViewById(R.id.et_user_skills);
        btnSaveProfile = findViewById(R.id.btn_save_profile);
        btnCancel = findViewById(R.id.btn_cancel);
        btnTakePhoto = findViewById(R.id.btn_take_photo);
        btnChoosePhoto = findViewById(R.id.btn_choose_photo);
        ivProfilePhoto = findViewById(R.id.iv_profile_photo);
    }

    private void populateCurrentData() {
        Intent intent = getIntent();
        if (intent != null) {
            String currentName = intent.getStringExtra("user_name");
            String currentEmail = intent.getStringExtra("user_email");
            
            etUserName.setText(currentName != null ? currentName : "");
            etUserEmail.setText(currentEmail != null ? currentEmail : "");
            etUserBio.setText("Professional freelancer with 5+ years of experience");
            etUserSkills.setText("Graphic Design, Web Development, Digital Marketing");
        }
    }

    private void setClickListeners() {
        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfile();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelEdit();
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });

        btnChoosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
    }

    private void saveProfile() {
        String newName = etUserName.getText().toString().trim();
        String newEmail = etUserEmail.getText().toString().trim();
        String newBio = etUserBio.getText().toString().trim();
        String newSkills = etUserSkills.getText().toString().trim();

        if (newName.isEmpty()) {
            etUserName.setError("Name is required");
            return;
        }

        if (newEmail.isEmpty()) {
            etUserEmail.setError("Email is required");
            return;
        }

        // Return the updated data to the calling activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("new_name", newName);
        resultIntent.putExtra("new_email", newEmail);
        resultIntent.putExtra("new_bio", newBio);
        resultIntent.putExtra("new_skills", newSkills);
        
        setResult(RESULT_OK, resultIntent);
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void cancelEdit() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void openCamera() {
        if (!PermissionHelper.isCameraPermissionGranted(this)) {
            // Show rationale if needed
            if (PermissionHelper.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showPermissionRationaleDialog(
                    "Camera Permission Required",
                    PermissionHelper.getPermissionRationale(Manifest.permission.CAMERA),
                    () -> PermissionHelper.requestCameraPermission(EditProfileActivity.this)
                );
            } else {
                PermissionHelper.requestCameraPermission(this);
            }
            return;
        }
        
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_CAMERA);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    private void openGallery() {
        if (!PermissionHelper.isStoragePermissionGranted(this)) {
            // Show rationale if needed
            String permission = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU 
                ? Manifest.permission.READ_MEDIA_IMAGES 
                : Manifest.permission.READ_EXTERNAL_STORAGE;
                
            if (PermissionHelper.shouldShowRequestPermissionRationale(this, permission)) {
                showPermissionRationaleDialog(
                    "Storage Permission Required",
                    PermissionHelper.getPermissionRationale(permission),
                    () -> PermissionHelper.requestStoragePermission(EditProfileActivity.this)
                );
            } else {
                PermissionHelper.requestStoragePermission(this);
            }
            return;
        }
        
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        startActivityForResult(pickPhoto, REQUEST_GALLERY);
    }
    
    /**
     * Show a dialog explaining why permission is needed
     */
    private void showPermissionRationaleDialog(String title, String message, Runnable onGrant) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Grant", (dialog, which) -> {
                if (onGrant != null) {
                    onGrant.run();
                }
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (grantResults.length == 0) {
            return;
        }
        
        switch (requestCode) {
            case PermissionHelper.CAMERA_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
                    openCamera(); // Retry opening camera
                } else {
                    Toast.makeText(this, "Camera permission is required to take photos", 
                        Toast.LENGTH_LONG).show();
                }
                break;
                
            case PermissionHelper.STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
                    openGallery(); // Retry opening gallery
                } else {
                    Toast.makeText(this, "Storage permission is required to select photos", 
                        Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == REQUEST_CAMERA) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap photo = (Bitmap) extras.get("data");
                if (photo != null) {
                    ivProfilePhoto.setImageBitmap(photo);
                }
            }
        } else if (requestCode == REQUEST_GALLERY) {
            Uri imageUri = data.getData();
            if (imageUri != null) {
                try {
                    ivProfilePhoto.setImageURI(imageUri);
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
