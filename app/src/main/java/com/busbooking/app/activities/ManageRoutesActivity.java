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
import com.busbooking.app.adapters.AdminRouteAdapter;
import com.busbooking.app.models.api.RouteData;
import com.busbooking.app.viewmodel.AdminViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ManageRoutesActivity extends AppCompatActivity {
    private RecyclerView recyclerRoutes;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private AdminRouteAdapter adapter;
    private AdminViewModel adminViewModel;
    private List<RouteData> routeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_routes);

        initViews();
        initViewModel();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
        
        loadRoutes();
    }

    private void initViews() {
        recyclerRoutes = findViewById(R.id.recycler_routes);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);
        fabAdd = findViewById(R.id.fab_add);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void initViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new AdminRouteAdapter(routeList, new AdminRouteAdapter.OnRouteActionListener() {
            @Override
            public void onDelete(RouteData route) {
                // Feature coming soon
                Toast.makeText(ManageRoutesActivity.this, "Delete feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerRoutes.setLayoutManager(new LinearLayoutManager(this));
        recyclerRoutes.setAdapter(adapter);
    }

    private void setupClickListeners() {
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddRouteActivity.class));
        });
    }

    private void observeViewModel() {
        adminViewModel.getRouteList().observe(this, routes -> {
            if (routes != null) {
                routeList.clear();
                routeList.addAll(routes);
                adapter.notifyDataSetChanged();
                
                tvEmpty.setVisibility(routeList.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        adminViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        adminViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
                adminViewModel.clearError();
            }
        });
    }

    private void loadRoutes() {
        adminViewModel.loadAllRoutes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRoutes();
    }
}
