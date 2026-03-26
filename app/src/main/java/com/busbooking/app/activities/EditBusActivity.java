package com.busbooking.app.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.busbooking.app.R;
import com.busbooking.app.viewmodel.AdminViewModel;
import java.util.ArrayList;
import java.util.List;

public class EditBusActivity extends AppCompatActivity {
    private EditText etBusName, etBusNumber, etOperatorName, etTotalSeats;
    private Spinner spinnerBusType;
    private CheckBox cbWifi, cbAc, cbCharging, cbBlanket, cbWater, cbSnacks;
    private Button btnUpdateBus;
    private AdminViewModel adminViewModel;
    private String busId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bus);

        initViews();
        initViewModel();
        setupSpinner();
        loadBusData();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        etBusName = findViewById(R.id.et_bus_name);
        etBusNumber = findViewById(R.id.et_bus_number);
        etOperatorName = findViewById(R.id.et_operator_name);
        etTotalSeats = findViewById(R.id.et_total_seats);
        spinnerBusType = findViewById(R.id.spinner_bus_type);
        cbWifi = findViewById(R.id.cb_wifi);
        cbAc = findViewById(R.id.cb_ac);
        cbCharging = findViewById(R.id.cb_charging);
        cbBlanket = findViewById(R.id.cb_blanket);
        cbWater = findViewById(R.id.cb_water);
        cbSnacks = findViewById(R.id.cb_snacks);
        btnUpdateBus = findViewById(R.id.btn_update_bus);
    }

    private void initViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupSpinner() {
        String[] busTypes = {"AC Seater", "AC Sleeper", "Non-AC Seater", "Non-AC Sleeper", "Volvo AC", "Semi Sleeper"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, busTypes);
        spinnerBusType.setAdapter(adapter);
    }

    private void loadBusData() {
        busId = getIntent().getStringExtra("bus_id");
        String busName = getIntent().getStringExtra("bus_name");
        String busNumber = getIntent().getStringExtra("bus_number");
        String busType = getIntent().getStringExtra("bus_type");
        String operatorName = getIntent().getStringExtra("operator_name");
        int totalSeats = getIntent().getIntExtra("total_seats", 40);

        etBusName.setText(busName);
        etBusNumber.setText(busNumber);
        etOperatorName.setText(operatorName);
        etTotalSeats.setText(String.valueOf(totalSeats));

        // Set spinner selection
        String[] busTypes = {"AC Seater", "AC Sleeper", "Non-AC Seater", "Non-AC Sleeper", "Volvo AC", "Semi Sleeper"};
        for (int i = 0; i < busTypes.length; i++) {
            if (busTypes[i].equals(busType)) {
                spinnerBusType.setSelection(i);
                break;
            }
        }
    }

    private void setupClickListeners() {
        btnUpdateBus.setOnClickListener(v -> updateBus());
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        adminViewModel.getIsLoading().observe(this, isLoading -> {
            btnUpdateBus.setEnabled(!isLoading);
            btnUpdateBus.setText(isLoading ? "Updating..." : "Update Bus");
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

    private void updateBus() {
        String busName = etBusName.getText().toString().trim();
        String busNumber = etBusNumber.getText().toString().trim();
        String operatorName = etOperatorName.getText().toString().trim();
        String totalSeatsStr = etTotalSeats.getText().toString().trim();
        String busType = spinnerBusType.getSelectedItem().toString();

        if (busName.isEmpty()) {
            etBusName.setError("Bus name is required");
            return;
        }
        if (busNumber.isEmpty()) {
            etBusNumber.setError("Bus number is required");
            return;
        }
        if (operatorName.isEmpty()) {
            etOperatorName.setError("Operator name is required");
            return;
        }
        if (totalSeatsStr.isEmpty()) {
            etTotalSeats.setError("Total seats is required");
            return;
        }

        int totalSeats = Integer.parseInt(totalSeatsStr);
        List<String> amenities = getSelectedAmenities();

        // Basic seat config fallback based on type
        String seatLayout = busType.toLowerCase().contains("sleeper") ? "Sleeper" : "Seater";
        int seaterSeats = seatLayout.equals("Seater") ? totalSeats : 0;
        int sleeperLower = seatLayout.equals("Sleeper") ? totalSeats : 0;
        int sleeperUpper = 0;
        String berthType = "Single Berth";

        adminViewModel.updateBus(busId, busName, busNumber, busType, operatorName,
                totalSeats, amenities, seatLayout, seaterSeats, sleeperLower, sleeperUpper, berthType);
    }

    private List<String> getSelectedAmenities() {
        List<String> amenities = new ArrayList<>();
        if (cbWifi.isChecked()) amenities.add("WiFi");
        if (cbAc.isChecked()) amenities.add("AC");
        if (cbCharging.isChecked()) amenities.add("Charging Point");
        if (cbBlanket.isChecked()) amenities.add("Blanket");
        if (cbWater.isChecked()) amenities.add("Water Bottle");
        if (cbSnacks.isChecked()) amenities.add("Snacks");
        return amenities;
    }
}

