package application.com.funagig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import application.com.funagig.utils.AuthHelper;

public class PostJobActivity extends AppCompatActivity {

    private FloatingActionButton fabBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        // Require authentication to post jobs
        if (!AuthHelper.requireAuthentication(this, MainActivity.class)) {
            finish();
            return;
        }

        fabBtn = findViewById(R.id.fab_add_post);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), InsertJobPostActivity.class));
            }
        });
    }
}