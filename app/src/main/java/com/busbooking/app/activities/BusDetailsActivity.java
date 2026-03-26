package com.busbooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.busbooking.app.R;
import com.busbooking.app.models.Bus;

public class BusDetailsActivity extends AppCompatActivity {
    private Bus bus;
    private TextView tvBusName, tvBusType, tvDepartureTime, tvArrivalTime, tvDuration, tvPrice, tvRating;
    private TextView chipGroupAmenities;
    private Button btnSelectSeats;
    private android.widget.ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_details);

        getBusFromIntent();
        initViews();
        populateData();
        setupClickListeners();
    }

    private void getBusFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            bus = intent.getParcelableExtra("bus");
        }
    }

    private void initViews() {
        tvBusName = findViewById(R.id.tv_bus_name);
        tvBusType = findViewById(R.id.tv_bus_type);
        tvDepartureTime = findViewById(R.id.tv_departure_time);
        tvArrivalTime = findViewById(R.id.tv_arrival_time);
        tvDuration = findViewById(R.id.tv_duration);
        tvPrice = findViewById(R.id.tv_price);
        tvRating = findViewById(R.id.tv_rating);
        chipGroupAmenities = findViewById(R.id.chip_group_amenities);
        btnSelectSeats = findViewById(R.id.btn_select_seats);
        btnBack = findViewById(R.id.btn_back);
    }

    private void populateData() {
        if (bus == null) return;

        tvBusName.setText(bus.getName());
        tvBusType.setText(bus.getBusType());
        tvDepartureTime.setText(bus.getDepartureTime());
        tvArrivalTime.setText(bus.getArrivalTime());
        tvDuration.setText(bus.getDuration());
        tvPrice.setText("₹" + (int) bus.getPrice());
        tvRating.setText(String.format("%.1f ★", bus.getRating()));

        // Show amenities as text
        if (bus.getAmenities() != null && !bus.getAmenities().isEmpty()) {
            StringBuilder amenitiesText = new StringBuilder();
            for (int i = 0; i < bus.getAmenities().size(); i++) {
                if (i > 0) amenitiesText.append(", ");
                amenitiesText.append(bus.getAmenities().get(i));
            }
            chipGroupAmenities.setText(amenitiesText.toString());
        } else {
            chipGroupAmenities.setText("WiFi, AC, Charging Point, Water Bottle");
        }
    }

    private void setupClickListeners() {
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        btnSelectSeats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BusDetailsActivity.this, SeatSelectionActivity.class);
                intent.putExtra("bus", bus);
                startActivity(intent);
            }
        });
    }
}
