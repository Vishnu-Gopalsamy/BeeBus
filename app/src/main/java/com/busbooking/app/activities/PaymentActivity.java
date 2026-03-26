package com.busbooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.busbooking.app.R;
import com.busbooking.app.models.Bus;
import com.busbooking.app.models.Passenger;
import com.busbooking.app.models.api.BookingData;
import com.busbooking.app.models.api.CreateBookingRequest;
import com.busbooking.app.models.api.PassengerData;
import com.busbooking.app.viewmodel.BookingViewModel;
import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity {
    private Bus bus;
    private List<Passenger> passengers;
    private double totalAmount;
    private String contactEmail, contactPhone;
    
    private TextView tvAmount;
    private Button btnPayNow;
    private RadioGroup rgPaymentMethods;
    private ProgressBar progressBar;
    
    private BookingViewModel bookingViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getIntentData();
        initViews();
        initViewModel();
        setupClickListeners();
        observeViewModel();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            bus = intent.getParcelableExtra("bus");
            passengers = intent.getParcelableArrayListExtra("passengers");
            totalAmount = intent.getDoubleExtra("total_amount", 0.0);
            contactEmail = intent.getStringExtra("contact_email");
            contactPhone = intent.getStringExtra("contact_phone");
        }
    }

    private void initViews() {
        tvAmount = findViewById(R.id.tv_payment_amount);
        btnPayNow = findViewById(R.id.btn_pay_now);
        rgPaymentMethods = findViewById(R.id.rg_payment_methods);
        progressBar = findViewById(R.id.progress_bar);

        tvAmount.setText("₹" + (int) totalAmount);
    }

    private void initViewModel() {
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
    }

    private void setupClickListeners() {
        btnPayNow.setOnClickListener(v -> {
            processPayment();
        });
    }

    private void observeViewModel() {
        bookingViewModel.getIsLoading().observe(this, isLoading -> {
            btnPayNow.setEnabled(!isLoading);
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        bookingViewModel.getBookingSuccess().observe(this, success -> {
            if (success) {
                navigateToConfirmation();
            }
        });

        bookingViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                bookingViewModel.clearError();
            }
        });
    }

    private void processPayment() {
        if (bus == null || passengers == null || passengers.isEmpty()) {
            Toast.makeText(this, "Booking data is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Critical: check network before attempting payment/booking
        if (!com.busbooking.app.utils.NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(this, "No internet connection. Please check your network before paying.", Toast.LENGTH_LONG).show();
            return;
        }

        List<PassengerData> passengerDataList = new ArrayList<>();
        List<Integer> seats = new ArrayList<>();

        for (Passenger p : passengers) {
            passengerDataList.add(new PassengerData(p.getName(), p.getAge(), p.getGender().toLowerCase()));
            try {
                // Strip non-numeric prefix (e.g., "S5" -> "5" for sleeper seats)
                String seatNum = p.getSeatNumber().replaceAll("[^0-9]", "");
                if (!seatNum.isEmpty()) {
                    seats.add(Integer.parseInt(seatNum));
                }
            } catch (NumberFormatException e) {
                // Skip if not a valid number
            }
        }

        if (bus.getId() == null || bus.getId().isEmpty()) {
            Toast.makeText(this, "Cannot process payment: invalid bus data", Toast.LENGTH_LONG).show();
            return;
        }

        CreateBookingRequest request = new CreateBookingRequest(
            bus.getId(),
            seats,
            passengerDataList,
            totalAmount
        );

        bookingViewModel.createBooking(request);
    }

    private void navigateToConfirmation() {
        Intent intent = new Intent(PaymentActivity.this, BookingConfirmationActivity.class);
        intent.putExtra("bus", bus);
        intent.putParcelableArrayListExtra("passengers", new ArrayList<>(passengers));
        intent.putExtra("total_amount", totalAmount);
        
        BookingData booking = bookingViewModel.getCurrentBooking().getValue();
        if (booking != null) {
            intent.putExtra("booking_id", booking.getBookingId());
            intent.putExtra("pnr", booking.getPnr());
        } else {
            intent.putExtra("booking_id", "B" + System.currentTimeMillis() / 1000);
        }
        
        startActivity(intent);
        finish();
    }
}
