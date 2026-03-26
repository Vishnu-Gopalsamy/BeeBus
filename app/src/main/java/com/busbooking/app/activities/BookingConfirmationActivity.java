package com.busbooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.busbooking.app.R;
import com.busbooking.app.models.Bus;
import com.busbooking.app.models.Passenger;
import java.util.List;

public class BookingConfirmationActivity extends AppCompatActivity {
    private Bus bus;
    private List<Passenger> passengers;
    private double totalAmount;
    private String bookingId;
    private String pnr;

    private TextView tvBookingId, tvBusName, tvRoute, tvPassengers, tvSeats;
    private Button btnGoHome, btnViewTicket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        getIntentData();
        initViews();
        displayBookingDetails();
        setupClickListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            bus = intent.getParcelableExtra("bus");
            passengers = intent.getParcelableArrayListExtra("passengers");
            totalAmount = intent.getDoubleExtra("total_amount", 0.0);
            bookingId = intent.getStringExtra("booking_id");
            pnr = intent.getStringExtra("pnr");
        }
    }

    private void initViews() {
        tvBookingId = findViewById(R.id.tv_booking_id);
        tvBusName = findViewById(R.id.tv_conf_bus_name);
        tvRoute = findViewById(R.id.tv_conf_route);
        tvPassengers = findViewById(R.id.tv_conf_passengers);
        tvSeats = findViewById(R.id.tv_conf_seats);
        btnGoHome = findViewById(R.id.btn_go_home);
        btnViewTicket = findViewById(R.id.btn_view_ticket);
    }

    private void displayBookingDetails() {
        tvBookingId.setText("Booking ID: " + bookingId);
        if (bus != null) {
            tvBusName.setText(bus.getName());
            tvRoute.setText(bus.getFromCity() + " to " + bus.getToCity());
        }

        if (passengers != null) {
            tvPassengers.setText("Passengers: " + passengers.size());
            StringBuilder seats = new StringBuilder("Seats: ");
            for (int i = 0; i < passengers.size(); i++) {
                if (i > 0) seats.append(", ");
                seats.append(passengers.get(i).getSeatNumber());
            }
            tvSeats.setText(seats.toString());
        }
    }

    private void setupClickListeners() {
        btnViewTicket.setOnClickListener(v -> {
            Intent intent = new Intent(BookingConfirmationActivity.this, TicketActivity.class);
            intent.putExtra("bus", bus);
            intent.putParcelableArrayListExtra("passengers", new java.util.ArrayList<>(passengers));
            intent.putExtra("booking_id", bookingId);
            intent.putExtra("pnr", pnr);
            startActivity(intent);
        });

        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(BookingConfirmationActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
