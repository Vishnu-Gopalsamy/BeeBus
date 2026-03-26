package com.busbooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.busbooking.app.R;
import com.busbooking.app.utils.SessionManager;
import com.busbooking.app.viewmodel.OwnerViewModel;
import com.google.android.material.card.MaterialCardView;

public class OwnerDashboardActivity extends AppCompatActivity {

    private TextView tvTotalBuses, tvTotalBookings, tvTotalSchedules;
    private MaterialCardView cvMyBuses, cvMyBookings, cvMySchedules;
    private Button btnLogout;
    private OwnerViewModel ownerViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_dashboard);

        sessionManager = SessionManager.getInstance(this);
        initViews();
        initViewModel();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        tvTotalBuses = findViewById(R.id.tv_total_buses);
        tvTotalBookings = findViewById(R.id.tv_total_bookings);
        tvTotalSchedules = findViewById(R.id.tv_total_schedules); // Added this line
        cvMyBuses = findViewById(R.id.cv_my_buses);
        cvMyBookings = findViewById(R.id.cv_my_bookings);
        cvMySchedules = findViewById(R.id.cv_my_schedules); // Added this line
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void initViewModel() {
        ownerViewModel = new ViewModelProvider(this).get(OwnerViewModel.class);
    }

    private void setupClickListeners() {
        cvMyBuses.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageBusesActivity.class));
        });

        cvMyBookings.setOnClickListener(v -> {
            startActivity(new Intent(this, OwnerBookingsActivity.class));
        });

        cvMySchedules.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageSchedulesActivity.class));
        });

        btnLogout.setOnClickListener(v -> {
            sessionManager.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void observeViewModel() {
        ownerViewModel.getBusList().observe(this, buses -> {
            if (buses != null) {
                tvTotalBuses.setText(String.valueOf(buses.size()));
            }
        });

        ownerViewModel.getOwnerBookings().observe(this, bookings -> {
            if (bookings != null) {
                tvTotalBookings.setText(String.valueOf(bookings.size()));
            }
        });

        ownerViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                ownerViewModel.clearError();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ownerViewModel.loadOwnerBuses();
        ownerViewModel.loadOwnerBookings();
    }
}
