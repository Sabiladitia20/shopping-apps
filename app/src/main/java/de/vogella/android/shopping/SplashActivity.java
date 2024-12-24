package de.vogella.android.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_1);

        // Next button on first screen
        Button btnNext = findViewById(R.id.btnNext);
        btnNext.setOnClickListener(v -> {
            setContentView(R.layout.activity_splash_screen_2);

            // Get Started button on second screen
            Button btnGetStarted = findViewById(R.id.btnGetStarted);
            btnGetStarted.setOnClickListener(getStartedView -> {
                // Navigate to MainActivity when Get Started is clicked
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Close splash activity
            });
        });
    }
}