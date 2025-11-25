package application.com.funagig;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import application.com.funagig.utils.AuthHelper;

public class RegistrationActivity extends AppCompatActivity {

    private Button btnLogin;
    private Button btnGoogleSignUp;
    private EditText registerEmail;
    private EditText registerPassword;
    private EditText registerName;
    private Button btnEmailSignup;

    //Firebase Auth
    private FirebaseAuth mAuth;

    //Google Sign-In
    private GoogleSignInClient googleSignInClient;

    //Progress Display
    private ProgressDialog mDialog;
    
    // ActivityResultLauncher for Google Sign-In
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(this);
        
        // Configure Google Sign-In
        configureGoogleSignIn();
        
        // Register ActivityResultLauncher for Google Sign-In
        googleSignInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleGoogleSignInResult(task);
                } else {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show();
                }
            }
        );
        
        // find new fields
        registerEmail = findViewById(R.id.register_email);
        registerPassword = findViewById(R.id.register_password);
        registerName = findViewById(R.id.register_name);
        btnEmailSignup = findViewById(R.id.btn_email_signup);
        
        setupListeners();
    }
    
    /**
     * Configure Google Sign-In options
     */
    private void configureGoogleSignIn() {
        String webClientId = getString(R.string.default_web_client_id);
        
        // Check if Web Client ID is configured
        if (webClientId == null || webClientId.equals("YOUR_WEB_CLIENT_ID_HERE") || webClientId.isEmpty()) {
            // Web Client ID not configured - Google Sign-In will not work until it's set up
            return;
        }
        
        // Configure sign-in to request the user's ID, email address, and basic profile
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(webClientId)
                .requestEmail()
                .build();
        
        // Build a GoogleSignInClient with the options specified by gso
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupListeners() {
        btnGoogleSignUp = findViewById(R.id.btn_google_signup);
        btnLogin = findViewById(R.id.btn_login);
        btnEmailSignup = findViewById(R.id.btn_email_signup);
        
        btnGoogleSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleSignInClient == null) {
                    Toast.makeText(getApplicationContext(), 
                        "Google Sign-In is not configured. Please add your Web Client ID in strings.xml", 
                        Toast.LENGTH_LONG).show();
                    return;
                }
                signInWithGoogle();
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        btnEmailSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleEmailSignup();
            }
        });
    }
    
    /**
     * Initiate Google Sign-In flow
     */
    private void signInWithGoogle() {
        mDialog.setMessage("Signing up with Google...");
        mDialog.show();
        
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }
    
    /**
     * Handle Google Sign-In result and authenticate with Firebase
     */
    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                // Authenticate with Firebase using the Google account
                firebaseAuthWithGoogle(account.getIdToken());
            } else {
                mDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show();
            }
        } catch (ApiException e) {
            mDialog.dismiss();
            Toast.makeText(getApplicationContext(), getString(R.string.google_sign_in_failed) + ": " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Authenticate with Firebase using Google account credentials
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Save user to local database
                                AuthHelper.saveGoogleUserToDatabase(RegistrationActivity.this, user);
                                
                                Toast.makeText(getApplicationContext(), getString(R.string.google_sign_in_success), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                finish();
                            }
                        } else {
                            // If sign in fails, display a message to the user
                            Toast.makeText(getApplicationContext(), getString(R.string.google_sign_in_failed), Toast.LENGTH_SHORT).show();
                        }
                        mDialog.dismiss();
                    }
                });
    }

    private void handleEmailSignup() {
        String email = registerEmail.getText().toString().trim();
        String password = registerPassword.getText().toString().trim();
        String name = registerName.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            registerEmail.setError("Email is required");
            registerEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(password) || password.length() < 6) {
            registerPassword.setError("Password (min 6 chars) required");
            registerPassword.requestFocus();
            return;
        }
        mDialog.setMessage("Signing you up...");
        mDialog.show();

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (!TextUtils.isEmpty(name)) {
                                com.google.firebase.auth.UserProfileChangeRequest profileUpdate =
                                        new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                                .setDisplayName(name)
                                                .build();
                                user.updateProfile(profileUpdate);
                            }
                            // Save user to local db
                            AuthHelper.saveGoogleUserToDatabase(RegistrationActivity.this, user);
                            Toast.makeText(getApplicationContext(), "Registration successful!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                    mDialog.dismiss();
                }
            });
    }
}