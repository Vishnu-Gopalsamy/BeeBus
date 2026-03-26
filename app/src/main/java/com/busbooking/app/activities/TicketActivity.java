package com.busbooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.busbooking.app.R;
import com.busbooking.app.models.Bus;
import com.busbooking.app.models.Passenger;
import java.util.List;

public class TicketActivity extends AppCompatActivity {
    private Bus bus;
    private List<Passenger> passengers;
    private String bookingId;
    private String pnr;

    private TextView tvBusName, tvPnr, tvSource, tvDest, tvDepTime, tvArrTime, tvDate, tvSeats, tvPassengers;
    private Button btnDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        getIntentData();
        initViews();
        displayTicket();
        setupClickListeners();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            bus = intent.getParcelableExtra("bus");
            passengers = intent.getParcelableArrayListExtra("passengers");
            bookingId = intent.getStringExtra("booking_id");
            pnr = intent.getStringExtra("pnr");
            if (pnr == null || pnr.isEmpty()) {
                pnr = "BEE" + System.currentTimeMillis() / 10000;
            }
        }
    }

    private void initViews() {
        tvBusName = findViewById(R.id.tv_ticket_bus_name);
        tvPnr = findViewById(R.id.tv_ticket_pnr);
        tvSource = findViewById(R.id.tv_ticket_source);
        tvDest = findViewById(R.id.tv_ticket_dest);
        tvDepTime = findViewById(R.id.tv_ticket_dep_time);
        tvArrTime = findViewById(R.id.tv_ticket_arr_time);
        tvDate = findViewById(R.id.tv_ticket_date);
        tvSeats = findViewById(R.id.tv_ticket_seats);
        tvPassengers = findViewById(R.id.tv_ticket_passengers);
        btnDownload = findViewById(R.id.btn_download_ticket);
    }

    private void displayTicket() {
        if (bus != null) {
            tvBusName.setText(bus.getName());
            tvSource.setText(bus.getFromCity());
            tvDest.setText(bus.getToCity());
            tvDepTime.setText(bus.getDepartureTime());
            tvArrTime.setText(bus.getArrivalTime());
            tvDate.setText(bus.getDepartureDate());
        }
        
        tvPnr.setText("PNR: " + pnr);

        if (passengers != null) {
            StringBuilder seatsBuilder = new StringBuilder("Seats: ");
            StringBuilder namesBuilder = new StringBuilder();
            for (int i = 0; i < passengers.size(); i++) {
                if (i > 0) {
                    seatsBuilder.append(", ");
                    namesBuilder.append(", ");
                }
                seatsBuilder.append(passengers.get(i).getSeatNumber());
                namesBuilder.append(passengers.get(i).getName());
            }
            tvSeats.setText(seatsBuilder.toString());
            tvPassengers.setText(namesBuilder.toString());
        }
    }

    private void setupClickListeners() {
        btnDownload.setOnClickListener(v -> {
            Toast.makeText(this, "Ticket download feature is not available yet", Toast.LENGTH_SHORT).show();
        });
    }
}
