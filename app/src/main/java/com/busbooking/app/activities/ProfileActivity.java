package com.busbooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.busbooking.app.R;
import com.busbooking.app.utils.SessionManager;
import com.busbooking.app.viewmodel.AuthViewModel;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvName, tvEmail, btnBookingHistory, btnLogout;
    private AuthViewModel authViewModel;
    private View progressBar;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = SessionManager.getInstance(this);
        initViews();
        initViewModel();
        setupClickListeners();
        observeViewModel();
        
        // Load actual profile data from API
        authViewModel.getProfile();
    }

    private void initViews() {
        tvName = findViewById(R.id.tv_profile_name);
        tvEmail = findViewById(R.id.tv_profile_email);
        btnBookingHistory = findViewById(R.id.btn_booking_history);
        btnLogout = findViewById(R.id.btn_logout);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }

    private void observeViewModel() {
        authViewModel.getUserData().observe(this, user -> {
            if (user != null) {
                tvName.setText(user.getName());
                tvEmail.setText(user.getEmail());
            }
        });

        authViewModel.getIsLoading().observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        authViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                
                // If session expired, clear local data and move to login
                if (error.contains("Session expired")) {
                    performLocalLogout();
                }
                
                authViewModel.clearError();
            }
        });
    }

    private void setupClickListeners() {
        btnBookingHistory.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, BookingHistoryActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            authViewModel.logout();
            performLocalLogout();
        });
    }

    private void performLocalLogout() {
        sessionManager.logout();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
