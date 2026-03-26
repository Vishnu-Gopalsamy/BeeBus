package com.busbooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.adapters.PopularRoutesAdapter;
import com.busbooking.app.models.api.RouteData;
import com.busbooking.app.utils.SessionManager;
import com.busbooking.app.viewmodel.BusViewModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private View cardSearch;
    private Button btnSearch;
    private RecyclerView recyclerPopularRoutes;
    private PopularRoutesAdapter routesAdapter;
    private android.widget.ImageView btnProfile;
    private android.widget.ImageView btnAdmin;

    private BusViewModel busViewModel;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sessionManager = SessionManager.getInstance(this);
        
        initViews();
        initViewModel();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
        loadPopularRoutes();
        
        checkAdminAccess();
    }

    private void initViews() {
        cardSearch = findViewById(R.id.card_search);
        btnSearch = findViewById(R.id.btn_search);
        recyclerPopularRoutes = findViewById(R.id.recycler_popular_routes);
        btnProfile = findViewById(R.id.btn_profile);
        btnAdmin = findViewById(R.id.btn_admin);
    }

    private void initViewModel() {
        busViewModel = new ViewModelProvider(this).get(BusViewModel.class);
    }

    private void checkAdminAccess() {
        if (btnAdmin != null) {
            if ("admin".equalsIgnoreCase(sessionManager.getUserRole())) {
                btnAdmin.setVisibility(View.VISIBLE);
            } else {
                btnAdmin.setVisibility(View.GONE);
            }
        }
    }

    private void setupRecyclerView() {
        // Initialize with default routes
        List<String> defaultRoutes = Arrays.asList(
            "Chennai → Coimbatore",
            "Bangalore → Chennai",
            "Madurai → Trichy",
            "Mumbai → Pune",
            "Delhi → Agra",
            "Hyderabad → Bangalore"
        );

        routesAdapter = new PopularRoutesAdapter(defaultRoutes, route -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            String[] cities = route.split(" → ");
            if (cities.length == 2) {
                intent.putExtra("from_city", cities[0]);
                intent.putExtra("to_city", cities[1]);
            }
            startActivity(intent);
        });

        recyclerPopularRoutes.setLayoutManager(new LinearLayoutManager(this));
        recyclerPopularRoutes.setAdapter(routesAdapter);
    }

    private void observeViewModel() {
        // Observe popular routes from API
        busViewModel.getPopularRoutes().observe(this, routes -> {
            if (routes != null && !routes.isEmpty()) {
                List<String> routeStrings = new ArrayList<>();
                for (RouteData route : routes) {
                    String source = route.getSource();
                    String dest = route.getDestination();
                    if (source != null && dest != null && !source.equals("null") && !dest.equals("null")) {
                        routeStrings.add(source + " → " + dest);
                    }
                }

                if (!routeStrings.isEmpty()) {
                    routesAdapter = new PopularRoutesAdapter(routeStrings, routeString -> {
                        Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                        String[] cities = routeString.split(" → ");
                        if (cities.length == 2) {
                            intent.putExtra("from_city", cities[0]);
                            intent.putExtra("to_city", cities[1]);
                        }
                        startActivity(intent);
                    });
                    recyclerPopularRoutes.setAdapter(routesAdapter);
                }
            }
        });

        // Observe errors - show them to user
        busViewModel.getErrorMessage().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Toast.makeText(this, "Could not load routes: " + error, Toast.LENGTH_SHORT).show();
                busViewModel.clearError();
            }
        });
    }

    private void loadPopularRoutes() {
        busViewModel.loadPopularRoutes();
    }

    private void setupClickListeners() {
        cardSearch.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        });

        // Wire the Search Buses button inside the card
        if (btnSearch != null) {
            btnSearch.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(intent);
            });
        }

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        if (btnAdmin != null) {
            btnAdmin.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, AdminDashboardActivity.class);
                startActivity(intent);
            });
        }
    }
}
