package com.busbooking.app.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.busbooking.app.R;
import com.busbooking.app.models.api.BusData;
import com.busbooking.app.models.api.RouteData;
import com.busbooking.app.utils.SessionManager;
import com.busbooking.app.viewmodel.AdminViewModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddScheduleActivity extends AppCompatActivity {
    private Spinner spinnerBus, spinnerRoute;
    private EditText etTravelDate, etDepartureTime, etArrivalTime, etPrice;
    private EditText etBoardingPoints, etDroppingPoints;
    private Button btnAddSchedule;
    private AdminViewModel adminViewModel;

    private List<BusData> busList = new ArrayList<>();
    private List<RouteData> routeList = new ArrayList<>();
    private Calendar calendar = Calendar.getInstance();
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

        initViews();
        initViewModel();
        setupClickListeners();
        observeViewModel();
        loadData();
    }

    private void initViews() {
        spinnerBus = findViewById(R.id.spinner_bus);
        spinnerRoute = findViewById(R.id.spinner_route);
        etTravelDate = findViewById(R.id.et_travel_date);
        etDepartureTime = findViewById(R.id.et_departure_time);
        etArrivalTime = findViewById(R.id.et_arrival_time);
        etPrice = findViewById(R.id.et_price);
        etBoardingPoints = findViewById(R.id.et_boarding_points);
        etDroppingPoints = findViewById(R.id.et_dropping_points);
        btnAddSchedule = findViewById(R.id.btn_add_schedule);
    }

    private void initViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupClickListeners() {
        etTravelDate.setOnClickListener(v -> showDatePicker());
        etDepartureTime.setOnClickListener(v -> showTimePicker(etDepartureTime));
        etArrivalTime.setOnClickListener(v -> showTimePicker(etArrivalTime));
        btnAddSchedule.setOnClickListener(v -> addSchedule());
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void observeViewModel() {
        adminViewModel.getBusList().observe(this, buses -> {
            if (buses != null && !buses.isEmpty()) {
                busList = buses;
                List<String> busNames = new ArrayList<>();
                for (BusData bus : buses) {
                    busNames.add(bus.getBusName() + " (" + bus.getBusType() + ")");
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, busNames);
                spinnerBus.setAdapter(adapter);
            }
        });

        adminViewModel.getRouteList().observe(this, routes -> {
            if (routes != null && !routes.isEmpty()) {
                routeList = routes;
                List<String> routeNames = new ArrayList<>();
                for (RouteData route : routes) {
                    routeNames.add(route.getSource() + " → " + route.getDestination());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, routeNames);
                spinnerRoute.setAdapter(adapter);
            }
        });

        adminViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                btnAddSchedule.setEnabled(!isLoading);
                btnAddSchedule.setText(isLoading ? "Adding..." : "Add Schedule");
            }
        });

        adminViewModel.getSuccessMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                adminViewModel.clearSuccess();
                finish();
            }
        });

        adminViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                adminViewModel.clearError();
            }
        });
    }

    private void loadData() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        if ("owner".equalsIgnoreCase(sessionManager.getUserRole())) {
            adminViewModel.loadOwnerBuses();
        } else {
            adminViewModel.loadAllBuses();
        }
        adminViewModel.loadAllRoutes();
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(this, (view, year, month, day) -> {
            calendar.set(year, month, day);
            etTravelDate.setText(dateFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void showTimePicker(EditText targetField) {
        TimePickerDialog dialog = new TimePickerDialog(this, (view, hour, minute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            targetField.setText(timeFormat.format(calendar.getTime()));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        dialog.show();
    }

    private void addSchedule() {
        if (busList.isEmpty()) {
            Toast.makeText(this, "No buses available. Add a bus first.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (routeList.isEmpty()) {
            Toast.makeText(this, "No routes available. Add a route first.", Toast.LENGTH_SHORT).show();
            return;
        }

        String travelDate = etTravelDate.getText().toString().trim();
        String departureTime = etDepartureTime.getText().toString().trim();
        String arrivalTime = etArrivalTime.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String boardingPoints = etBoardingPoints.getText().toString().trim();
        String droppingPoints = etDroppingPoints.getText().toString().trim();

        if (travelDate.isEmpty()) {
            Toast.makeText(this, "Select travel date", Toast.LENGTH_SHORT).show();
            return;
        }
        if (departureTime.isEmpty()) {
            Toast.makeText(this, "Select departure time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (arrivalTime.isEmpty()) {
            Toast.makeText(this, "Select arrival time", Toast.LENGTH_SHORT).show();
            return;
        }
        if (priceStr.isEmpty()) {
            etPrice.setError("Price is required");
            return;
        }

        int busIndex = spinnerBus.getSelectedItemPosition();
        int routeIndex = spinnerRoute.getSelectedItemPosition();

        if (busIndex < 0 || busIndex >= busList.size() || routeIndex < 0 || routeIndex >= routeList.size()) {
            return;
        }

        String busId = busList.get(busIndex).get_id();
        String routeId = routeList.get(routeIndex).get_id();
        double price = Double.parseDouble(priceStr);

        List<String> boardingList = new ArrayList<>();
        List<String> droppingList = new ArrayList<>();

        if (!boardingPoints.isEmpty()) {
            for (String point : boardingPoints.split(",")) {
                boardingList.add(point.trim());
            }
        }
        if (!droppingPoints.isEmpty()) {
            for (String point : droppingPoints.split(",")) {
                droppingList.add(point.trim());
            }
        }

        adminViewModel.addSchedule(busId, routeId, travelDate, departureTime, arrivalTime,
            price, boardingList, droppingList);
    }
}
