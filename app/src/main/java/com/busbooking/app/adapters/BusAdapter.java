package com.busbooking.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.models.Bus;
import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {
    private List<Bus> buses;
    private OnBusClickListener listener;

    public interface OnBusClickListener {
        void onBusClick(Bus bus);
    }

    public BusAdapter(List<Bus> buses, OnBusClickListener listener) {
        this.buses = buses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bus, parent, false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        Bus bus = buses.get(position);
        holder.bind(bus, listener);
    }

    @Override
    public int getItemCount() {
        return buses.size();
    }

    static class BusViewHolder extends RecyclerView.ViewHolder {
        private CardView cardBus;
        private TextView tvBusName, tvBusType, tvDepartureTime, tvArrivalTime, tvDuration, tvPrice, tvAvailableSeats, tvRating;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBus = itemView.findViewById(R.id.card_bus);
            tvBusName = itemView.findViewById(R.id.tv_bus_name);
            tvBusType = itemView.findViewById(R.id.tv_bus_type);
            tvDepartureTime = itemView.findViewById(R.id.tv_departure_time);
            tvArrivalTime = itemView.findViewById(R.id.tv_arrival_time);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvAvailableSeats = itemView.findViewById(R.id.tv_available_seats);
            tvRating = itemView.findViewById(R.id.tv_rating);
        }

        public void bind(Bus bus, OnBusClickListener listener) {
            tvBusName.setText(bus.getName());
            tvBusType.setText(bus.getBusType());
            tvDepartureTime.setText(bus.getDepartureTime());
            tvArrivalTime.setText(bus.getArrivalTime());
            tvDuration.setText(bus.getDuration());
            tvPrice.setText("₹" + (int) bus.getPrice());
            tvAvailableSeats.setText(bus.getAvailableSeats() + " seats left");
            tvRating.setText(String.format("%.1f ★", bus.getRating()));

            cardBus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBusClick(bus);
                }
            });
        }
    }
}
