package com.busbooking.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import java.util.List;

public class PopularRoutesAdapter extends RecyclerView.Adapter<PopularRoutesAdapter.RouteViewHolder> {
    private List<String> routes;
    private OnRouteClickListener listener;

    public interface OnRouteClickListener {
        void onRouteClick(String route);
    }

    public PopularRoutesAdapter(List<String> routes, OnRouteClickListener listener) {
        this.routes = routes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_popular_route, parent, false);
        return new RouteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        String route = routes.get(position);
        holder.bind(route, listener);
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    static class RouteViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout cardRoute;
        private TextView tvRoute;

        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoute = itemView.findViewById(R.id.card_route);
            tvRoute = itemView.findViewById(R.id.tv_route);
        }

        public void bind(String route, OnRouteClickListener listener) {
            tvRoute.setText(route);
            cardRoute.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRouteClick(route);
                }
            });
        }
    }
}
