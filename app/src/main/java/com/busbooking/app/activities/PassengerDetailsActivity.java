package com.busbooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.adapters.PassengerAdapter;
import com.busbooking.app.models.Bus;
import com.busbooking.app.models.Passenger;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class PassengerDetailsActivity extends AppCompatActivity {
    private Bus bus;
    private List<String> selectedSeatNumbers;
    private double totalAmount;
    private RecyclerView recyclerPassengers;
    private PassengerAdapter passengerAdapter;
    private List<Passenger> passengers;
    private Button btnProceedToPayment;
    private TextView tvTotalAmount;
    private TextInputEditText etEmail, etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_details);

        getIntentData();
        initViews();
        setupPassengers();
        setupRecyclerView();
        setupClickListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            bus = intent.getParcelableExtra("bus");
            selectedSeatNumbers = intent.getStringArrayListExtra("selected_seats");
            totalAmount = intent.getDoubleExtra("total_amount", 0.0);
        }
    }

    private void initViews() {
        recyclerPassengers = findViewById(R.id.recycler_passengers);
        btnProceedToPayment = findViewById(R.id.btn_proceed_to_payment);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        etEmail = findViewById(R.id.et_contact_email);
        etPhone = findViewById(R.id.et_contact_phone);

        tvTotalAmount.setText("₹" + (int) totalAmount);
    }

    private void setupPassengers() {
        passengers = new ArrayList<>();
        if (selectedSeatNumbers != null) {
            for (String seatNum : selectedSeatNumbers) {
                Passenger p = new Passenger();
                p.setSeatNumber(seatNum);
                passengers.add(p);
            }
        }
    }

    private void setupRecyclerView() {
        passengerAdapter = new PassengerAdapter(passengers);
        recyclerPassengers.setLayoutManager(new LinearLayoutManager(this));
        recyclerPassengers.setAdapter(passengerAdapter);
    }

    private void setupClickListeners() {
        btnProceedToPayment.setOnClickListener(v -> {
            if (validateInputs()) {
                Intent intent = new Intent(PassengerDetailsActivity.this, PaymentActivity.class);
                intent.putExtra("bus", bus);
                intent.putParcelableArrayListExtra("passengers", new ArrayList<>(passengers));
                intent.putExtra("total_amount", totalAmount);
                intent.putExtra("contact_email", etEmail.getText().toString().trim());
                intent.putExtra("contact_phone", etPhone.getText().toString().trim());
                startActivity(intent);
            }
        });
    }

    private boolean validateInputs() {
        for (Passenger p : passengers) {
            if (p.getName() == null || p.getName().trim().isEmpty()) {
                Toast.makeText(this, "Please enter all passenger names", Toast.LENGTH_SHORT).show();
                return false;
            }
            if (p.getAge() <= 0) {
                Toast.makeText(this, "Please enter valid age for all passengers", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (etEmail.getText().toString().trim().isEmpty()) {
            etEmail.setError("Email is required");
            return false;
        }

        if (etPhone.getText().toString().trim().isEmpty()) {
            etPhone.setError("Phone number is required");
            return false;
        }

        return true;
    }
}
