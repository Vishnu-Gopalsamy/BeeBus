package com.busbooking.app.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.busbooking.app.R;
import com.busbooking.app.adapters.CityAdapter;
import com.busbooking.app.api.ApiClient;
import com.busbooking.app.api.ApiService;
import com.busbooking.app.models.api.ApiResponse;
import com.busbooking.app.models.api.CityListData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {
    private AutoCompleteTextView etFromCity, etToCity;
    private EditText etDepartureDate;
    private Button btnSearchBuses;
    private ImageView ivSwap;
    private Calendar calendar;
    private SimpleDateFormat displayFormat;
    private SimpleDateFormat apiFormat;

    private CityAdapter fromCityAdapter;
    private CityAdapter toCityAdapter;
    private ApiService apiService;
    private List<String> allCities = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initViews();
        setupApiService();
        setupAutoComplete();
        setupClickListeners();
        handleIntentData();
        loadCities();
    }

    private void initViews() {
        etFromCity = findViewById(R.id.et_from_city);
        etToCity = findViewById(R.id.et_to_city);
        etDepartureDate = findViewById(R.id.et_departure_date);
        btnSearchBuses = findViewById(R.id.btn_search_buses);
        ivSwap = findViewById(R.id.iv_swap);

        calendar = Calendar.getInstance();
        displayFormat = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Set today's date as default
        etDepartureDate.setText(displayFormat.format(calendar.getTime()));
        etDepartureDate.setTag(apiFormat.format(calendar.getTime()));
    }

    private void setupApiService() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    private void setupAutoComplete() {
        // Initialize adapters with empty lists
        fromCityAdapter = new CityAdapter(this, new ArrayList<>());
        toCityAdapter = new CityAdapter(this, new ArrayList<>());

        etFromCity.setAdapter(fromCityAdapter);
        etToCity.setAdapter(toCityAdapter);

        // Set threshold to 1 character
        etFromCity.setThreshold(1);
        etToCity.setThreshold(1);

        // Show dropdown on focus
        etFromCity.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !allCities.isEmpty()) {
                etFromCity.showDropDown();
            }
        });

        etToCity.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !allCities.isEmpty()) {
                etToCity.showDropDown();
            }
        });

        // Handle item selection
        etFromCity.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCity = fromCityAdapter.getItem(position);
            if (selectedCity != null) {
                etFromCity.setText(selectedCity);
                etFromCity.setSelection(selectedCity.length());
                etToCity.requestFocus();
            }
        });

        etToCity.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCity = toCityAdapter.getItem(position);
            if (selectedCity != null) {
                etToCity.setText(selectedCity);
                etToCity.setSelection(selectedCity.length());
            }
        });
    }

    private void loadCities() {
        // Skip API call if no network — go straight to defaults
        if (!com.busbooking.app.utils.NetworkUtils.isNetworkAvailable(this)) {
            loadDefaultCities();
            return;
        }

        apiService.getAllCities().enqueue(new Callback<ApiResponse<CityListData>>() {
            @Override
            public void onResponse(Call<ApiResponse<CityListData>> call,
                                   Response<ApiResponse<CityListData>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    CityListData data = response.body().getData();
                    if (data != null && data.getCities() != null) {
                        allCities = data.getCities();
                        fromCityAdapter.updateCities(allCities);
                        toCityAdapter.updateCities(allCities);
                    }
                } else {
                    // Load default cities if API fails
                    loadDefaultCities();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CityListData>> call, Throwable t) {
                // Load default cities if API fails
                loadDefaultCities();
            }
        });
    }

    private void loadDefaultCities() {
        // Default cities as fallback
        allCities = new ArrayList<>();
        allCities.add("Chennai");
        allCities.add("Bangalore");
        allCities.add("Mumbai");
        allCities.add("Delhi");
        allCities.add("Hyderabad");
        allCities.add("Coimbatore");
        allCities.add("Madurai");
        allCities.add("Trichy");
        allCities.add("Salem");
        allCities.add("Erode");
        allCities.add("Tirunelveli");
        allCities.add("Puducherry");
        allCities.add("Vellore");
        allCities.add("Thanjavur");
        allCities.add("Kochi");
        allCities.add("Thiruvananthapuram");
        allCities.add("Mysore");
        allCities.add("Mangalore");
        allCities.add("Vizag");
        allCities.add("Vijayawada");

        fromCityAdapter.updateCities(allCities);
        toCityAdapter.updateCities(allCities);
    }

    private void setupClickListeners() {
        etDepartureDate.setOnClickListener(v -> showDatePicker());

        btnSearchBuses.setOnClickListener(v -> performSearch());

        // Swap cities
        ivSwap.setOnClickListener(v -> {
            String fromCity = etFromCity.getText().toString();
            String toCity = etToCity.getText().toString();
            etFromCity.setText(toCity);
            etToCity.setText(fromCity);
        });
    }

    private void handleIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            String fromCity = intent.getStringExtra("from_city");
            String toCity = intent.getStringExtra("to_city");

            if (fromCity != null) etFromCity.setText(fromCity);
            if (toCity != null) etToCity.setText(toCity);
        }
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    calendar.set(Calendar.YEAR, year);
                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    etDepartureDate.setText(displayFormat.format(calendar.getTime()));
                    etDepartureDate.setTag(apiFormat.format(calendar.getTime()));
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void performSearch() {
        String fromCity = etFromCity.getText().toString().trim();
        String toCity = etToCity.getText().toString().trim();
        String apiDate = (String) etDepartureDate.getTag();

        if (fromCity.isEmpty()) {
            etFromCity.setError("From city is required");
            return;
        }

        if (toCity.isEmpty()) {
            etToCity.setError("To city is required");
            return;
        }

        if (fromCity.equalsIgnoreCase(toCity)) {
            Toast.makeText(this, "Source and Destination cannot be the same", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, SearchResultsActivity.class);
        intent.putExtra("from_city", fromCity);
        intent.putExtra("to_city", toCity);
        intent.putExtra("departure_date", apiDate);
        startActivity(intent);
    }
}
