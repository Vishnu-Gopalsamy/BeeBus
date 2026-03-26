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
import com.busbooking.app.adapters.AdminScheduleAdapter;
import com.busbooking.app.models.api.ScheduleData;
import com.busbooking.app.utils.SessionManager;
import com.busbooking.app.viewmodel.AdminViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class ManageSchedulesActivity extends AppCompatActivity {
    private RecyclerView recyclerSchedules;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private AdminScheduleAdapter adapter;
    private AdminViewModel adminViewModel;
    private List<ScheduleData> scheduleList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_schedules);

        initViews();
        initViewModel();
        setupRecyclerView();
        setupClickListeners();
        observeViewModel();
        
        loadSchedules();
    }

    private void initViews() {
        recyclerSchedules = findViewById(R.id.recycler_schedules);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);
        fabAdd = findViewById(R.id.fab_add);
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
    }

    private void initViewModel() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new AdminScheduleAdapter(scheduleList, schedule -> {
            // Delete schedule
            adminViewModel.deleteSchedule(schedule.get_id());
        });
        recyclerSchedules.setLayoutManager(new LinearLayoutManager(this));
        recyclerSchedules.setAdapter(adapter);
    }

    private void setupClickListeners() {
        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(this, AddScheduleActivity.class));
        });
    }

    private void observeViewModel() {
        adminViewModel.getSchedules().observe(this, schedules -> {
            if (schedules != null) {
                scheduleList.clear();
                
                SessionManager sessionManager = SessionManager.getInstance(this);
                if ("owner".equalsIgnoreCase(sessionManager.getUserRole())) {
                    String ownerId = sessionManager.getUserId();
                    for (ScheduleData schedule : schedules) {
                        if (schedule.getBus() != null && ownerId.equals(schedule.getBus().getOwnerId())) {
                            scheduleList.add(schedule);
                        }
                    }
                } else {
                    scheduleList.addAll(schedules);
                }
                
                adapter.notifyDataSetChanged();
                tvEmpty.setVisibility(scheduleList.isEmpty() ? View.VISIBLE : View.GONE);
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

        adminViewModel.getSuccessMessage().observe(this, message -> {
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                adminViewModel.clearSuccess();
                loadSchedules();
            }
        });
    }

    private void loadSchedules() {
        adminViewModel.loadAllSchedules();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadSchedules();
    }
}
