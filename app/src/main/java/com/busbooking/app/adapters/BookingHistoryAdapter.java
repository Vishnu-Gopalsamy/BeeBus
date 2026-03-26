package com.busbooking.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.models.api.BookingData;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {
    private List<BookingData> bookings;

    public BookingHistoryAdapter(List<BookingData> bookings) {
        this.bookings = bookings;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingData booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings != null ? bookings.size() : 0;
    }

    public void setBookings(List<BookingData> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBusName, tvStatus, tvRoute, tvDate, tvAmount;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBusName = itemView.findViewById(R.id.tv_history_bus_name);
            tvStatus = itemView.findViewById(R.id.tv_history_status);
            tvRoute = itemView.findViewById(R.id.tv_history_route);
            tvDate = itemView.findViewById(R.id.tv_history_date);
            tvAmount = itemView.findViewById(R.id.tv_history_amount);
        }

        public void bind(BookingData booking) {
            if (booking.getTicketDetails() != null) {
                tvBusName.setText(booking.getTicketDetails().getBusName());
                tvRoute.setText(booking.getTicketDetails().getSource() + " to " + booking.getTicketDetails().getDestination());
                tvDate.setText(booking.getTicketDetails().getTravelDate());
            } else {
                tvBusName.setText("Bus Name");
                tvRoute.setText("Route not available");
                tvDate.setText(booking.getCreatedAt() != null ? booking.getCreatedAt().substring(0, 10) : "");
            }
            tvStatus.setText(booking.getBookingStatus());
            tvAmount.setText("₹" + (int) booking.getTotalAmount());
        }
    }
}
