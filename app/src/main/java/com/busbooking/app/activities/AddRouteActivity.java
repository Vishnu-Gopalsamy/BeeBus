package com.busbooking.app.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.busbooking.app.R;
import com.busbooking.app.viewmodel.AdminViewModel;

public class AddRouteActivity extends AppCompatActivity {
    private EditText etSource, etDestination, etDistance, etDuration;
    private Button btnAddRoute;
    private AdminViewModel adminViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_route);

        initViews();
        initViewModel();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        etSource = findViewById(R.id.et_source);
        etDestination = findViewById(R.id.et_destination);
        etDistance = findViewById(R.id.et_distance);
        etDuration = findViewById(R.id.et_duration);
        btnAddRoute = findViewById(R.id.btn_add_route);
    }

    private void initViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupClickListeners() {
        btnAddRoute.setOnClickListener(v -> addRoute());
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        adminViewModel.getIsLoading().observe(this, isLoading -> {
            btnAddRoute.setEnabled(!isLoading);
            btnAddRoute.setText(isLoading ? "Adding..." : "Add Route");
        });

        adminViewModel.getSuccessMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                adminViewModel.clearSuccess();
                finish();
            }
        });

        adminViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                adminViewModel.clearError();
            }
        });
    }

    private void addRoute() {
        String source = etSource.getText().toString().trim();
        String destination = etDestination.getText().toString().trim();
        String distanceStr = etDistance.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();

        if (source.isEmpty()) {
            etSource.setError("Source city is required");
            return;
        }
        if (destination.isEmpty()) {
            etDestination.setError("Destination city is required");
            return;
        }
        if (source.equalsIgnoreCase(destination)) {
            Toast.makeText(this, "Source and destination cannot be same", Toast.LENGTH_SHORT).show();
            return;
        }
        if (distanceStr.isEmpty()) {
            etDistance.setError("Distance is required");
            return;
        }
        if (durationStr.isEmpty()) {
            etDuration.setError("Duration is required");
            return;
        }

        double distance = Double.parseDouble(distanceStr);
        double duration = Double.parseDouble(durationStr);

        adminViewModel.addRoute(source, destination, distance, duration);
    }
}

