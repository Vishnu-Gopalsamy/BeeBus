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

public class AddBusActivity extends AppCompatActivity {
    private EditText etBusName, etBusNumber, etOperatorName, etTotalSeats;
    private EditText etSeaterSeats, etSleeperLowerSeats, etSleeperUpperSeats;
    private Spinner spinnerBusType, spinnerSeatLayout, spinnerBerthType;
    private CheckBox cbWifi, cbAc, cbCharging, cbBlanket, cbWater, cbSnacks;
    private Button btnAddBus;
    private AdminViewModel adminViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);

        initViews();
        initViewModel();
        setupSpinner();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        etBusName = findViewById(R.id.et_bus_name);
        etBusNumber = findViewById(R.id.et_bus_number);
        etOperatorName = findViewById(R.id.et_operator_name);
        etTotalSeats = findViewById(R.id.et_total_seats);
        spinnerBusType = findViewById(R.id.spinner_bus_type);
        spinnerSeatLayout = findViewById(R.id.spinner_seat_layout);
        spinnerBerthType = findViewById(R.id.spinner_berth_type);
        etSeaterSeats = findViewById(R.id.et_seater_seats);
        etSleeperLowerSeats = findViewById(R.id.et_sleeper_lower_seats);
        etSleeperUpperSeats = findViewById(R.id.et_sleeper_upper_seats);
        cbWifi = findViewById(R.id.cb_wifi);
        cbAc = findViewById(R.id.cb_ac);
        cbCharging = findViewById(R.id.cb_charging);
        cbBlanket = findViewById(R.id.cb_blanket);
        cbWater = findViewById(R.id.cb_water);
        cbSnacks = findViewById(R.id.cb_snacks);
        btnAddBus = findViewById(R.id.btn_add_bus);
    }

    private void initViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupSpinner() {
        String[] busTypes = {"AC Seater", "AC Sleeper", "Non-AC Seater", "Non-AC Sleeper", "Volvo AC", "Semi Sleeper"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, busTypes);
        spinnerBusType.setAdapter(adapter);

        String[] seatLayouts = {"Seater", "Sleeper", "Seater + Sleeper"};
        ArrayAdapter<String> seatLayoutAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, seatLayouts);
        spinnerSeatLayout.setAdapter(seatLayoutAdapter);

        String[] berthTypes = {"Single Berth", "Double Berth"};
        ArrayAdapter<String> berthAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, berthTypes);
        spinnerBerthType.setAdapter(berthAdapter);
    }

    private void setupClickListeners() {
        btnAddBus.setOnClickListener(v -> addBus());

        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        adminViewModel.getIsLoading().observe(this, isLoading -> {
            btnAddBus.setEnabled(!isLoading);
            btnAddBus.setText(isLoading ? "Adding..." : "Add Bus");
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

    private void addBus() {
        String busName = etBusName.getText().toString().trim();
        String busNumber = etBusNumber.getText().toString().trim();
        String operatorName = etOperatorName.getText().toString().trim();
        String totalSeatsStr = etTotalSeats.getText().toString().trim();
        String busType = spinnerBusType.getSelectedItem().toString();
        String seatLayout = spinnerSeatLayout.getSelectedItem().toString();
        String berthType = spinnerBerthType.getSelectedItem().toString();

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

        int seaterSeats = parseIntOrZero(etSeaterSeats.getText().toString().trim());
        int sleeperLowerSeats = parseIntOrZero(etSleeperLowerSeats.getText().toString().trim());
        int sleeperUpperSeats = parseIntOrZero(etSleeperUpperSeats.getText().toString().trim());

        // If total seats not consistent, adjust total to sum of provided seat counts when available
        int providedTotal = seaterSeats + sleeperLowerSeats + sleeperUpperSeats;
        if (providedTotal > 0 && providedTotal != totalSeats) {
            totalSeats = providedTotal;
            etTotalSeats.setText(String.valueOf(totalSeats));
        }

        adminViewModel.addBus(busName, busNumber, busType, operatorName, totalSeats, amenities,
                seatLayout, seaterSeats, sleeperLowerSeats, sleeperUpperSeats, berthType);
    }

    private int parseIntOrZero(String value) {
        try {
            return value.isEmpty() ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
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

