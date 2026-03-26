package com.busbooking.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.models.api.BusData;
import java.util.List;

public class AdminBusAdapter extends RecyclerView.Adapter<AdminBusAdapter.BusViewHolder> {
    private List<BusData> buses;
    private OnBusActionListener listener;

    public interface OnBusActionListener {
        void onEdit(BusData bus);
        void onDelete(BusData bus);
    }

    public AdminBusAdapter(List<BusData> buses, OnBusActionListener listener) {
        this.buses = buses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_bus, parent, false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        BusData bus = buses.get(position);
        holder.bind(bus, listener);
    }

    @Override
    public int getItemCount() {
        return buses.size();
    }

    static class BusViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBusName, tvBusNumber, tvOperator, tvSeats;
        private ImageButton btnEdit, btnDelete;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBusName = itemView.findViewById(R.id.tv_bus_name);
            tvBusNumber = itemView.findViewById(R.id.tv_bus_number);
            tvOperator = itemView.findViewById(R.id.tv_operator);
            tvSeats = itemView.findViewById(R.id.tv_seats);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(BusData bus, OnBusActionListener listener) {
            tvBusName.setText(bus.getBusName());
            tvBusNumber.setText(bus.getBusNumber());
            tvOperator.setText(bus.getOperatorName());
            tvSeats.setText(bus.getTotalSeats() + " Seats");

            btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(bus);
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(bus);
            });
        }
    }
}
