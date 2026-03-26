package com.busbooking.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.adapters.AdminBookingAdapter;
import com.busbooking.app.viewmodel.AdminViewModel;

public class AdminBookingsActivity extends AppCompatActivity {
    private RecyclerView recyclerBookings;
    private TextView tvNoBookings;
    private AdminBookingAdapter adapter;
    private AdminViewModel adminViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_bookings);

        initViews();
        initViewModel();
        setupRecyclerView();
        observeViewModel();
        loadBookings();
    }

    private void initViews() {
        recyclerBookings = findViewById(R.id.recycler_admin_bookings);
        tvNoBookings = findViewById(R.id.tv_no_bookings);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void initViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new AdminBookingAdapter();
        recyclerBookings.setLayoutManager(new LinearLayoutManager(this));
        recyclerBookings.setAdapter(adapter);
    }

    private void observeViewModel() {
        adminViewModel.getAllBookings().observe(this, bookings -> {
            if (bookings != null && !bookings.isEmpty()) {
                adapter.setBookings(bookings);
                recyclerBookings.setVisibility(View.VISIBLE);
                tvNoBookings.setVisibility(View.GONE);
            } else {
                recyclerBookings.setVisibility(View.GONE);
                tvNoBookings.setVisibility(View.VISIBLE);
            }
        });

        adminViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                adminViewModel.clearError();
            }
        });
    }

    private void loadBookings() {
        adminViewModel.loadAllBookings();
    }
}

