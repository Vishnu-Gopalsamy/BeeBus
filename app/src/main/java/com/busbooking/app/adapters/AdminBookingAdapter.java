package com.busbooking.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.models.api.BookingData;
import java.util.ArrayList;
import java.util.List;

public class AdminBookingAdapter extends RecyclerView.Adapter<AdminBookingAdapter.BookingViewHolder> {
    private List<BookingData> bookings = new ArrayList<>();

    public void setBookings(List<BookingData> bookings) {
        this.bookings = bookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(bookings.get(position));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        private TextView tvBookingId, tvUserName, tvRoute, tvDate, tvAmount, tvStatus;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBookingId = itemView.findViewById(R.id.tv_admin_booking_id);
            tvUserName = itemView.findViewById(R.id.tv_admin_user_name);
            tvRoute = itemView.findViewById(R.id.tv_admin_route);
            tvDate = itemView.findViewById(R.id.tv_admin_date);
            tvAmount = itemView.findViewById(R.id.tv_admin_amount);
            tvStatus = itemView.findViewById(R.id.tv_admin_status);
        }

        public void bind(BookingData booking) {
            tvBookingId.setText("ID: " + (booking.getBookingId() != null ? booking.getBookingId() : booking.get_id()));

            if (booking.getUser() != null) {
                tvUserName.setText(booking.getUser().getName());
            } else {
                tvUserName.setText("User");
            }

            if (booking.getSchedule() != null && booking.getSchedule().getRoute() != null) {
                tvRoute.setText(booking.getSchedule().getRoute().getSource() + " → " +
                    booking.getSchedule().getRoute().getDestination());
            } else if (booking.getTicketDetails() != null) {
                tvRoute.setText(booking.getTicketDetails().getSource() + " → " +
                    booking.getTicketDetails().getDestination());
            } else {
                tvRoute.setText("Route");
            }

            tvDate.setText(booking.getCreatedAt() != null ? booking.getCreatedAt().substring(0, 10) : "");
            tvAmount.setText("₹" + (int) booking.getTotalAmount());
            tvStatus.setText(booking.getBookingStatus() != null ? booking.getBookingStatus() : "Pending");
        }
    }
}
