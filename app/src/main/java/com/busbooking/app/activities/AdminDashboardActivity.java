package com.busbooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.busbooking.app.R;
import com.busbooking.app.viewmodel.AdminViewModel;

public class AdminDashboardActivity extends AppCompatActivity {
    private LinearLayout btnManageBuses, btnManageRoutes, btnManageSchedules, btnViewBookings;
    private TextView tvTotalBuses, tvTotalRoutes, tvTotalBookings;
    private AdminViewModel adminViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        initViewModel();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        // Mapping UI elements from activity_admin_dashboard.xml
        // Using existing IDs from the layout file
        btnManageBuses = findViewById(R.id.btn_add_bus); // Label in UI is "Add New Bus" but we'll link to Manage
        btnManageRoutes = findViewById(R.id.btn_add_route);
        btnManageSchedules = findViewById(R.id.btn_add_schedule);
        btnViewBookings = findViewById(R.id.btn_view_bookings);
        
        tvTotalBuses = findViewById(R.id.tv_total_buses);
        tvTotalRoutes = findViewById(R.id.tv_total_routes);
        tvTotalBookings = findViewById(R.id.tv_total_bookings);
        
        findViewById(R.id.btn_back_home).setOnClickListener(v -> finish());
    }

    private void initViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupClickListeners() {
        btnManageBuses.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageBusesActivity.class));
        });

        btnManageRoutes.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageRoutesActivity.class));
        });

        btnManageSchedules.setOnClickListener(v -> {
            startActivity(new Intent(this, ManageSchedulesActivity.class));
        });

        btnViewBookings.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminBookingsActivity.class));
        });
    }

    private void observeViewModel() {
        adminViewModel.getDashboardStats().observe(this, stats -> {
            if (stats != null) {
                tvTotalBuses.setText(String.valueOf(stats.getTotalBuses()));
                tvTotalRoutes.setText(String.valueOf(stats.getTotalRoutes()));
                tvTotalBookings.setText(String.valueOf(stats.getTotalBookings()));
            }
        });

        adminViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                adminViewModel.clearError();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        adminViewModel.loadDashboardStats();
    }
}
