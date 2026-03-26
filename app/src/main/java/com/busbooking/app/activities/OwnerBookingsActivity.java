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
import com.busbooking.app.viewmodel.OwnerViewModel;

public class OwnerBookingsActivity extends AppCompatActivity {
    private RecyclerView recyclerBookings;
    private TextView tvNoBookings;
    private AdminBookingAdapter adapter;
    private OwnerViewModel ownerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_bookings);

        initViews();
        initViewModel();
        setupRecyclerView();
        observeViewModel();
        loadBookings();
    }

    private void initViews() {
        recyclerBookings = findViewById(R.id.recycler_owner_bookings);
        tvNoBookings = findViewById(R.id.tv_no_bookings);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void initViewModel() {
        ownerViewModel = new ViewModelProvider(this).get(OwnerViewModel.class);
    }

    private void setupRecyclerView() {
        // Reusing the AdminBookingAdapter since the layout & data structures are identical
        adapter = new AdminBookingAdapter();
        recyclerBookings.setLayoutManager(new LinearLayoutManager(this));
        recyclerBookings.setAdapter(adapter);
    }

    private void observeViewModel() {
        ownerViewModel.getOwnerBookings().observe(this, bookings -> {
            if (bookings != null && !bookings.isEmpty()) {
                adapter.setBookings(bookings);
                recyclerBookings.setVisibility(View.VISIBLE);
                tvNoBookings.setVisibility(View.GONE);
            } else {
                recyclerBookings.setVisibility(View.GONE);
                tvNoBookings.setVisibility(View.VISIBLE);
            }
        });

        ownerViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                ownerViewModel.clearError();
            }
        });
    }

    private void loadBookings() {
        ownerViewModel.loadOwnerBookings();
    }
}
