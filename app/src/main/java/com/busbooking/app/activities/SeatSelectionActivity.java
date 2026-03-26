package com.busbooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.adapters.SeatAdapter;
import com.busbooking.app.models.Bus;
import com.busbooking.app.models.Seat;
import com.busbooking.app.models.api.SeatData;
import com.busbooking.app.viewmodel.BusViewModel;
import java.util.ArrayList;
import java.util.List;

public class SeatSelectionActivity extends AppCompatActivity {
    private Bus bus;
    private RecyclerView recyclerSeats;
    private SeatAdapter seatAdapter;
    private TextView tvSelectedSeats, tvTotalAmount, tvBusType;
    private TextView tabLowerDeck, tabUpperDeck;
    private LinearLayout layoutDeckTabs;
    private Button btnProceed;
    private ProgressBar progressBar;
    private List<Seat> allSeats = new ArrayList<>();
    private List<Seat> currentDeckSeats = new ArrayList<>();
    private List<Seat> selectedSeats = new ArrayList<>();
    private BusViewModel busViewModel;
    private boolean isSleeperBus = false;
    private boolean isLowerDeck = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        getBusFromIntent();
        initViews();
        initViewModel();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
        
        loadSeats();
    }

    private void getBusFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            bus = intent.getParcelableExtra("bus");
        }

        // Determine if it's a sleeper bus
        if (bus != null && bus.getBusType() != null) {
            String busType = bus.getBusType().toLowerCase();
            isSleeperBus = busType.contains("sleeper");
        }
    }

    private void initViews() {
        recyclerSeats = findViewById(R.id.recycler_seats);
        tvSelectedSeats = findViewById(R.id.tv_selected_seats);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnProceed = findViewById(R.id.btn_proceed);
        tvBusType = findViewById(R.id.tv_bus_type);
        layoutDeckTabs = findViewById(R.id.layout_deck_tabs);
        tabLowerDeck = findViewById(R.id.tab_lower_deck);
        tabUpperDeck = findViewById(R.id.tab_upper_deck);

        // Show bus type badge
        if (tvBusType != null && bus != null) {
            tvBusType.setText(bus.getBusType() != null ? bus.getBusType() : "Seater");
        }

        // Show deck tabs for sleeper buses
        if (layoutDeckTabs != null) {
            layoutDeckTabs.setVisibility(isSleeperBus ? View.VISIBLE : View.GONE);
        }

        // Set up back button
        android.widget.ImageView btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void initViewModel() {
        busViewModel = new ViewModelProvider(this).get(BusViewModel.class);
    }

    private void loadSeats() {
        // Try to load from API if we have a valid bus ID
        if (bus != null && bus.getId() != null && !bus.getId().isEmpty()) {
            busViewModel.loadSeats(bus.getId());
        } else {
            // No valid ID - generate default seats for display
            generateDefaultSeats();
        }
    }

    private void observeViewModel() {
        busViewModel.getSeats().observe(this, apiSeats -> {
            if (apiSeats != null && !apiSeats.isEmpty()) {
                mapApiSeats(apiSeats);
            } else if (allSeats.isEmpty()) {
                // API returned empty, generate default seats
                generateDefaultSeats();
            }
        });

        busViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, "Could not load seats: " + error, Toast.LENGTH_SHORT).show();
                busViewModel.clearError();
                if (allSeats.isEmpty()) {
                    generateDefaultSeats();
                }
            }
        });
    }

    private void mapApiSeats(List<SeatData> apiSeats) {
        allSeats.clear();
        int totalSeats = apiSeats.size();
        int midPoint = totalSeats / 2; // Dynamic split for deck assignment

        for (SeatData apiSeat : apiSeats) {
            Seat seat = new Seat();
            seat.setSeatNumber(apiSeat.getSeatNumber());
            seat.setSeatId(apiSeat.get_id());
            seat.setPrice(bus != null ? bus.getPrice() : 500.0);

            // Map status
            if ("booked".equalsIgnoreCase(apiSeat.getStatus())) {
                seat.setStatus(Seat.SeatStatus.BOOKED);
            } else if ("blocked".equalsIgnoreCase(apiSeat.getStatus())) {
                seat.setStatus(Seat.SeatStatus.BOOKED); // Treat blocked as booked for UI
            } else {
                seat.setStatus(Seat.SeatStatus.AVAILABLE);
            }

            // Map type
            if ("ladies".equalsIgnoreCase(apiSeat.getSeatType())) {
                seat.setSeatType(Seat.SeatType.LADIES_ONLY);
            } else {
                seat.setSeatType(Seat.SeatType.REGULAR);
            }

            // Assign deck dynamically based on total seats
            seat.setDeck(apiSeat.getSeatNumber() <= midPoint ? "lower" : "upper");

            allSeats.add(seat);
        }
        filterSeatsByDeck();
    }

    private void generateDefaultSeats() {
        allSeats.clear();
        int totalSeats = (bus != null && bus.getTotalSeats() > 0) ? bus.getTotalSeats() :
                          (isSleeperBus ? 30 : 40);

        for (int i = 1; i <= totalSeats; i++) {
            Seat seat = new Seat();
            seat.setSeatNumber(i);
            seat.setSeatId("seat_" + i);
            seat.setPrice(bus != null ? bus.getPrice() : 500.0);
            seat.setStatus(Seat.SeatStatus.AVAILABLE);
            seat.setSeatType(Seat.SeatType.REGULAR);

            // For sleeper: first half = lower deck, second half = upper deck
            if (isSleeperBus) {
                seat.setDeck(i <= totalSeats / 2 ? "lower" : "upper");
            } else {
                seat.setDeck("lower");
            }

            allSeats.add(seat);
        }
        filterSeatsByDeck();
    }

    private void filterSeatsByDeck() {
        currentDeckSeats.clear();
        for (Seat seat : allSeats) {
            if (isSleeperBus) {
                String deckFilter = isLowerDeck ? "lower" : "upper";
                if (deckFilter.equals(seat.getDeck())) {
                    currentDeckSeats.add(seat);
                }
            } else {
                currentDeckSeats.add(seat);
            }
        }
        seatAdapter.notifyDataSetChanged();
    }

    private void setupRecyclerView() {
        seatAdapter = new SeatAdapter(currentDeckSeats, seat -> {
            if (seat.getStatus() == Seat.SeatStatus.BOOKED) {
                Toast.makeText(SeatSelectionActivity.this, "Seat is already booked", Toast.LENGTH_SHORT).show();
                return;
            }

            if (seat.isSelected()) {
                seat.setSelected(false);
                seat.setStatus(Seat.SeatStatus.AVAILABLE);
                selectedSeats.remove(seat);
            } else {
                if (selectedSeats.size() >= 6) {
                    Toast.makeText(SeatSelectionActivity.this, "Maximum 6 seats can be selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                seat.setSelected(true);
                seat.setStatus(Seat.SeatStatus.SELECTED);
                selectedSeats.add(seat);
            }

            seatAdapter.notifyDataSetChanged();
            updateUI();
        });

        // Set sleeper mode
        seatAdapter.setSleeperBus(isSleeperBus);

        // Grid columns: 4 for seater (chair-style), 2 for sleeper (bed-style berths)
        int columns = isSleeperBus ? 2 : 4;
        recyclerSeats.setLayoutManager(new GridLayoutManager(this, columns));
        recyclerSeats.setAdapter(seatAdapter);
    }

    private void setupClickListeners() {
        // Deck tab clicks
        if (tabLowerDeck != null) {
            tabLowerDeck.setOnClickListener(v -> switchDeck(true));
        }
        if (tabUpperDeck != null) {
            tabUpperDeck.setOnClickListener(v -> switchDeck(false));
        }

        btnProceed.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(SeatSelectionActivity.this, "Please select at least one seat", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(SeatSelectionActivity.this, PassengerDetailsActivity.class);
            intent.putExtra("bus", bus);

            ArrayList<String> seatNumbers = new ArrayList<>();
            for (Seat seat : selectedSeats) {
                String seatLabel = isSleeperBus ? "S" + seat.getSeatNumber() : String.valueOf(seat.getSeatNumber());
                seatNumbers.add(seatLabel);
            }
            intent.putStringArrayListExtra("selected_seats", seatNumbers);
            intent.putExtra("total_amount", calculateTotalAmount());

            startActivity(intent);
        });
    }

    private void switchDeck(boolean lower) {
        isLowerDeck = lower;

        // Update tab appearance
        if (lower) {
            tabLowerDeck.setBackgroundResource(R.drawable.button_primary);
            tabLowerDeck.setTextColor(getResources().getColor(R.color.textWhite));
            tabUpperDeck.setBackgroundResource(R.drawable.button_secondary);
            tabUpperDeck.setTextColor(getResources().getColor(R.color.colorPrimary));
        } else {
            tabUpperDeck.setBackgroundResource(R.drawable.button_primary);
            tabUpperDeck.setTextColor(getResources().getColor(R.color.textWhite));
            tabLowerDeck.setBackgroundResource(R.drawable.button_secondary);
            tabLowerDeck.setTextColor(getResources().getColor(R.color.colorPrimary));
        }

        filterSeatsByDeck();
    }

    private void updateUI() {
        if (selectedSeats.isEmpty()) {
            tvSelectedSeats.setText("None");
            tvTotalAmount.setText("₹0");
            btnProceed.setEnabled(false);
        } else {
            StringBuilder seatNumbers = new StringBuilder();
            for (int i = 0; i < selectedSeats.size(); i++) {
                if (i > 0) seatNumbers.append(", ");
                if (isSleeperBus) {
                    seatNumbers.append("S").append(selectedSeats.get(i).getSeatNumber());
                } else {
                    seatNumbers.append(selectedSeats.get(i).getSeatNumber());
                }
            }
            tvSelectedSeats.setText(seatNumbers.toString());
            tvTotalAmount.setText("₹" + (int) calculateTotalAmount());
            btnProceed.setEnabled(true);
        }
    }

    private double calculateTotalAmount() {
        double total = 0;
        for (Seat seat : selectedSeats) {
            total += seat.getPrice();
        }
        return total;
    }
}
