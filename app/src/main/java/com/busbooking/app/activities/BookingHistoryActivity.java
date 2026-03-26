package com.busbooking.app.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.adapters.BookingHistoryAdapter;
import com.busbooking.app.models.api.BookingData;
import com.busbooking.app.viewmodel.BookingViewModel;
import java.util.ArrayList;
import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerHistory;
    private BookingHistoryAdapter historyAdapter;
    private List<BookingData> bookingList = new ArrayList<>();
    private BookingViewModel bookingViewModel;
    private TextView tvEmpty;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        initViews();
        initViewModel();
        setupRecyclerView();
        observeViewModel();
        
        loadBookingHistory();
    }

    private void initViews() {
        recyclerHistory = findViewById(R.id.recycler_booking_history);
        tvEmpty = findViewById(R.id.tv_empty);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void initViewModel() {
        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);
    }

    private void setupRecyclerView() {
        historyAdapter = new BookingHistoryAdapter(bookingList);
        recyclerHistory.setLayoutManager(new LinearLayoutManager(this));
        recyclerHistory.setAdapter(historyAdapter);
    }

    private void observeViewModel() {
        bookingViewModel.getBookingHistory().observe(this, bookings -> {
            if (bookings != null) {
                bookingList.clear();
                bookingList.addAll(bookings);
                historyAdapter.notifyDataSetChanged();
            }
            
            // Show/hide empty state
            boolean isEmpty = bookingList.isEmpty();
            if (tvEmpty != null) {
                tvEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
            }
            recyclerHistory.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        });

        bookingViewModel.getIsLoading().observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        bookingViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
                bookingViewModel.clearError();
                if (tvEmpty != null && bookingList.isEmpty()) {
                    tvEmpty.setText("Could not load booking history");
                    tvEmpty.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadBookingHistory() {
        bookingViewModel.loadUserBookings();
    }
}
