package com.busbooking.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.models.api.ScheduleData;
import java.util.List;

public class AdminScheduleAdapter extends RecyclerView.Adapter<AdminScheduleAdapter.ScheduleViewHolder> {
    private List<ScheduleData> scheduleList;
    private OnScheduleDeleteListener listener;

    public interface OnScheduleDeleteListener {
        void onDelete(ScheduleData schedule);
    }

    public AdminScheduleAdapter(List<ScheduleData> scheduleList, OnScheduleDeleteListener listener) {
        this.scheduleList = scheduleList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_schedule, parent, false);
        return new ScheduleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
        ScheduleData schedule = scheduleList.get(position);
        holder.bind(schedule);
    }

    @Override
    public int getItemCount() {
        return scheduleList != null ? scheduleList.size() : 0;
    }

    class ScheduleViewHolder extends RecyclerView.ViewHolder {
        TextView tvRoute, tvBusName, tvDate, tvTime, tvPrice, tvSeats;
        ImageButton btnDelete;

        ScheduleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tv_route);
            tvBusName = itemView.findViewById(R.id.tv_bus_name);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvSeats = itemView.findViewById(R.id.tv_seats);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        void bind(ScheduleData schedule) {
            tvRoute.setText(schedule.getSource() + " → " + schedule.getDestination());

            if (schedule.getBus() != null) {
                tvBusName.setText(schedule.getBus().getBusName());
            } else {
                tvBusName.setText("Unknown Bus");
            }

            tvDate.setText(schedule.getTravelDate() != null ? schedule.getTravelDate() : "N/A");
            tvTime.setText(schedule.getDepartureTime() != null ? schedule.getDepartureTime() : "N/A");
            tvPrice.setText("₹" + schedule.getPrice());
            tvSeats.setText(schedule.getAvailableSeats() + " seats");

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(schedule);
            });
        }
    }
}

