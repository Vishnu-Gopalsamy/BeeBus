package com.busbooking.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.models.api.RouteData;
import java.util.List;

public class AdminRouteAdapter extends RecyclerView.Adapter<AdminRouteAdapter.RouteViewHolder> {
    private List<RouteData> routes;
    private OnRouteActionListener listener;

    public interface OnRouteActionListener {
        void onDelete(RouteData route);
    }

    public AdminRouteAdapter(List<RouteData> routes, OnRouteActionListener listener) {
        this.routes = routes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        RouteData route = routes.get(position);
        holder.bind(route, listener);
    }

    @Override
    public int getItemCount() {
        return routes != null ? routes.size() : 0;
    }

    static class RouteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvRoute, tvDistance, tvDuration;
        private ImageButton btnDelete;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoute = itemView.findViewById(R.id.tv_route);
            tvDistance = itemView.findViewById(R.id.tv_distance);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }

        public void bind(RouteData route, OnRouteActionListener listener) {
            tvRoute.setText(route.getSource() + " → " + route.getDestination());
            tvDistance.setText(route.getDistance() + " km");
            tvDuration.setText(route.getDuration() + " hrs");

            btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(route);
            });
        }
    }
}
