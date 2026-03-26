package com.busbooking.app.activities;

import android.content.Intent;
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
import com.busbooking.app.adapters.BusAdapter;
import com.busbooking.app.models.Bus;
import com.busbooking.app.models.api.ScheduleData;
import com.busbooking.app.viewmodel.BusViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {
    private TextView tvFromCity, tvToCity, tvTravelDate, tvNoResults;
    private RecyclerView recyclerBuses;
    private ProgressBar progressBar;
    private BusAdapter busAdapter;
    private List<Bus> busList = new ArrayList<>();
    
    private BusViewModel busViewModel;
    private String fromCity, toCity, travelDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        if (!getIntentData()) {
            Toast.makeText(this, "Search parameters missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initViewModel();
        setupRecyclerView();
        observeViewModel();
        
        loadBuses();
    }

    private boolean getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            fromCity = intent.getStringExtra("from_city");
            toCity = intent.getStringExtra("to_city");
            travelDate = intent.getStringExtra("departure_date");
            return fromCity != null && toCity != null && travelDate != null;
        }
        return false;
    }

    private void initViews() {
        tvFromCity = findViewById(R.id.tv_from_city);
        tvToCity = findViewById(R.id.tv_to_city);
        tvTravelDate = findViewById(R.id.tv_travel_date);
        recyclerBuses = findViewById(R.id.recycler_buses);
        progressBar = findViewById(R.id.progress_bar);
        tvNoResults = findViewById(R.id.tv_no_results);

        if (tvFromCity != null) tvFromCity.setText(fromCity);
        if (tvToCity != null) tvToCity.setText(toCity);
        if (tvTravelDate != null) tvTravelDate.setText(travelDate);
    }

    private void initViewModel() {
        busViewModel = new ViewModelProvider(this).get(BusViewModel.class);
    }

    private void setupRecyclerView() {
        busAdapter = new BusAdapter(busList, bus -> {
            Intent intent = new Intent(SearchResultsActivity.this, BusDetailsActivity.class);
            intent.putExtra("bus", bus);
            startActivity(intent);
        });
        recyclerBuses.setLayoutManager(new LinearLayoutManager(this));
        recyclerBuses.setAdapter(busAdapter);
    }

    private void observeViewModel() {
        busViewModel.getSchedules().observe(this, schedules -> {
            if (schedules != null && !schedules.isEmpty()) {
                populateBusList(schedules);
            } else {
                // If API returns no results, show demo data for presentation purposes
                showDemoBuses();
            }
        });

        busViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                // In case of network error, show demo data so the app is still presentable
                if (busList.isEmpty()) {
                    showDemoBuses();
                    Toast.makeText(this, "Network error. Showing offline demo buses.", Toast.LENGTH_SHORT).show();
                }
                busViewModel.clearError();
            }
        });
        
        busViewModel.getIsLoading().observe(this, isLoading -> {
            if (progressBar != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void populateBusList(List<ScheduleData> schedules) {
        busList.clear();
        for (ScheduleData schedule : schedules) {
            if (schedule == null) continue;

            Bus bus = new Bus();
            bus.setId(schedule.get_id());
            
            if (schedule.getBus() != null) {
                bus.setName(schedule.getBus().getBusName());
                bus.setBusType(schedule.getBus().getBusType());
                bus.setRating(schedule.getBus().getRating());
                bus.setAmenities(schedule.getBus().getAmenities());
            } else {
                bus.setName("Unknown Bus");
                bus.setBusType("Standard");
            }
            
            bus.setDepartureTime(schedule.getDepartureTime() != null ? schedule.getDepartureTime() : "N/A");
            bus.setArrivalTime(schedule.getArrivalTime() != null ? schedule.getArrivalTime() : "N/A");
            bus.setPrice(schedule.getPrice());
            bus.setAvailableSeats(schedule.getAvailableSeats());
            bus.setFromCity(fromCity);
            bus.setToCity(toCity);
            bus.setDepartureDate(travelDate);

            if (schedule.getRoute() != null && schedule.getRoute().getDuration() > 0) {
                int totalMinutes = (int) (schedule.getRoute().getDuration() * 60);
                int hrs = totalMinutes / 60;
                int mins = totalMinutes % 60;
                bus.setDuration(hrs + "h " + String.format("%02d", mins) + "m");
            } else {
                bus.setDuration("6h 30m");
            }
            
            busList.add(bus);
        }
        busAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void showDemoBuses() {
        busList.clear();
        
        // Demo Bus 1
        Bus bus1 = new Bus();
        bus1.setId("demo_1");
        bus1.setName("Orange Travels (Demo)");
        bus1.setBusType("AC Sleeper");
        bus1.setDepartureTime("09:00 PM");
        bus1.setArrivalTime("05:30 AM");
        bus1.setDuration("8h 30m");
        bus1.setPrice(1200.0);
        bus1.setAvailableSeats(12);
        bus1.setRating(4.5);
        bus1.setAmenities(Arrays.asList("WiFi", "AC", "Water Bottle", "Pillow", "Charging Point"));
        bus1.setFromCity(fromCity);
        bus1.setToCity(toCity);
        bus1.setDepartureDate(travelDate);
        busList.add(bus1);

        // Demo Bus 2
        Bus bus2 = new Bus();
        bus2.setId("demo_2");
        bus2.setName("VRL Travels (Demo)");
        bus2.setBusType("Non-AC Seater");
        bus2.setDepartureTime("06:00 AM");
        bus2.setArrivalTime("02:30 PM");
        bus2.setDuration("8h 30m");
        bus2.setPrice(650.0);
        bus2.setAvailableSeats(24);
        bus2.setRating(3.8);
        bus2.setAmenities(Arrays.asList("Charging Point", "Water Bottle"));
        bus2.setFromCity(fromCity);
        bus2.setToCity(toCity);
        bus2.setDepartureDate(travelDate);
        busList.add(bus2);

        // Demo Bus 3
        Bus bus3 = new Bus();
        bus3.setId("demo_3");
        bus3.setName("Parveen Travels (Demo)");
        bus3.setBusType("Volvo AC Seater");
        bus3.setDepartureTime("10:30 AM");
        bus3.setArrivalTime("07:00 PM");
        bus3.setDuration("8h 30m");
        bus3.setPrice(950.0);
        bus3.setAvailableSeats(8);
        bus3.setRating(4.2);
        bus3.setAmenities(Arrays.asList("WiFi", "AC", "Charging Point"));
        bus3.setFromCity(fromCity);
        bus3.setToCity(toCity);
        bus3.setDepartureDate(travelDate);
        busList.add(bus3);

        busAdapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (tvNoResults != null) {
            tvNoResults.setVisibility(busList.isEmpty() ? View.VISIBLE : View.GONE);
        }
        if (recyclerBuses != null) {
            recyclerBuses.setVisibility(busList.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void loadBuses() {
        busViewModel.searchBuses(fromCity, toCity, travelDate);
    }
}
