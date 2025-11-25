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

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button btnLogin;
    private Button btnRegister;
    private Button btnGoogleSignIn;

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
        setContentView(R.layout.activity_main);

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
        
        // Check if user is already logged in (session persistence)
        checkAuthenticationState();
        loginFunction();
    }
    
    /**
     * Configure Google Sign-In options
     */
    private void configureGoogleSignIn() {
        String webClientId = getString(R.string.default_web_client_id);
        
        // Check if Web Client ID is configured
        if (webClientId == null || webClientId.equals("947879979885-t1kei2b6ani2dsqjoambuu5s9lcmu7fo.apps.googleusercontent.com") || webClientId.isEmpty()) {
            // Web Client ID not configured - Google Sign-In will not work until it's set up
            // This is handled gracefully - the button will show an error when clicked
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
    
    /**
     * Check if user is already authenticated and redirect to HomeActivity
     * This implements session persistence - Firebase automatically maintains the session
     */
    private void checkAuthenticationState() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is already logged in, redirect to home
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            finish();
        }
    }

    private void loginFunction() {
        email = findViewById(R.id.login_email);
        password = findViewById(R.id.login_password);
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnGoogleSignIn = findViewById(R.id.btn_google_signin);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_LONG).show();
                String loginEmail = email.getText().toString().trim();
                String loginPassword = password.getText().toString().trim();

                if(TextUtils.isEmpty(loginEmail)){
                    email.setError("Email is a required field.");
                    return;
                }

                if(TextUtils.isEmpty(loginPassword)){
                    password.setError("Password is a required field.");
                    return;
                }
                mDialog.setMessage("Processing...");
                mDialog.show();

                mAuth.signInWithEmailAndPassword(loginEmail, loginPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish(); // Close the login activity so user can't go back to it
                            mDialog.dismiss();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Login Failed!", Toast.LENGTH_LONG).show();
                            mDialog.dismiss();
                        }
                    }
                });
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });
        
        btnGoogleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (googleSignInClient == null) {
                    Toast.makeText(getApplicationContext(), 
                        "Google Sign-In is not configured.",
                        Toast.LENGTH_LONG).show();
                    return;
                }
                signInWithGoogle();
            }
        });
    }
    
    /**
     * Initiate Google Sign-In flow
     */
    private void signInWithGoogle() {
        mDialog.setMessage("Signing in with Google...");
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
                                AuthHelper.saveGoogleUserToDatabase(MainActivity.this, user);
                                
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

    @Override
    public void onBackPressed() {
        // If user presses back on login screen, go to HomeActivity (public view)
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        finish();
    }
}