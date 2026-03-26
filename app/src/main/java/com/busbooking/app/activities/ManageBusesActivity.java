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
import com.busbooking.app.adapters.AdminBusAdapter;
import com.busbooking.app.models.api.BusData;
import com.busbooking.app.utils.SessionManager;
import com.busbooking.app.viewmodel.AdminViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ManageBusesActivity extends AppCompatActivity {
    private RecyclerView recyclerBuses;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private AdminBusAdapter adapter;
    private AdminViewModel adminViewModel;
    private List<BusData> busList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_buses);

        initViews();
        initViewModel();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
        
        loadBuses();
    }

    private void initViews() {
        recyclerBuses = findViewById(R.id.recycler_buses);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);
        fabAdd = findViewById(R.id.fab_add);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void initViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new AdminBusAdapter(busList, new AdminBusAdapter.OnBusActionListener() {
            @Override
            public void onEdit(BusData bus) {
                Intent intent = new Intent(ManageBusesActivity.this, EditBusActivity.class);
                intent.putExtra("bus_id", bus.get_id());
                startActivity(intent);
            }

            @Override
            public void onDelete(BusData bus) {
                // In a real app, show a confirmation dialog
                Toast.makeText(ManageBusesActivity.this, "Delete feature coming soon", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerBuses.setLayoutManager(new LinearLayoutManager(this));
        recyclerBuses.setAdapter(adapter);
    }

    private void setupClickListeners() {
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddBusActivity.class));
        });
    }

    private void observeViewModel() {
        adminViewModel.getBusList().observe(this, buses -> {
            if (buses != null) {
                busList.clear();
                busList.addAll(buses);
                adapter.notifyDataSetChanged();
                
                tvEmpty.setVisibility(busList.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });

        adminViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        adminViewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                adminViewModel.clearError();
            }
        });
    }

    private void loadBuses() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        if ("owner".equalsIgnoreCase(sessionManager.getUserRole())) {
            adminViewModel.loadOwnerBuses();
        } else {
            adminViewModel.loadAllBuses();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBuses();
    }
}
