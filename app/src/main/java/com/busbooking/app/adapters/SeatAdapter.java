package com.busbooking.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.models.Seat;
import java.util.List;

public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {
    private List<Seat> seats;
    private OnSeatClickListener listener;
    private boolean isSleeperBus = false;

    public interface OnSeatClickListener {
        void onSeatClick(Seat seat);
    }

    public SeatAdapter(List<Seat> seats, OnSeatClickListener listener) {
        this.seats = seats;
        this.listener = listener;
    }

    public void setSleeperBus(boolean isSleeperBus) {
        this.isSleeperBus = isSleeperBus;
        notifyDataSetChanged();
    }

    public boolean isSleeperBus() {
        return isSleeperBus;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isSleeperBus ? R.layout.item_seat_sleeper : R.layout.item_seat;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new SeatViewHolder(view, isSleeperBus);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seats.get(position);
        holder.bind(seat, listener, isSleeperBus);
    }

    @Override
    public int getItemCount() {
        return seats.size();
    }

    static class SeatViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSeat;
        private boolean isSleeper;

        public SeatViewHolder(@NonNull View itemView, boolean isSleeper) {
            super(itemView);
            this.isSleeper = isSleeper;
            tvSeat = itemView.findViewById(R.id.tv_seat);
        }

        public void bind(Seat seat, OnSeatClickListener listener, boolean isSleeperBus) {
            String seatLabel = isSleeperBus ? "S" + seat.getSeatNumber() : String.valueOf(seat.getSeatNumber());
            tvSeat.setText(seatLabel);

            // Set seat appearance based on status
            int backgroundRes;
            int textColor;

            switch (seat.getStatus()) {
                case BOOKED:
                    backgroundRes = isSleeperBus ? R.drawable.seat_sleeper_booked : R.drawable.seat_booked;
                    textColor = R.color.textWhite;
                    break;
                case SELECTED:
                    backgroundRes = isSleeperBus ? R.drawable.seat_sleeper_selected : R.drawable.seat_selected;
                    textColor = R.color.textWhite;
                    break;
                default: // AVAILABLE
                    if (seat.getSeatType() == Seat.SeatType.LADIES_ONLY) {
                        backgroundRes = isSleeperBus ? R.drawable.seat_sleeper_ladies : R.drawable.seat_ladies;
                        textColor = R.color.textPrimary;
                    } else {
                        backgroundRes = isSleeperBus ? R.drawable.seat_sleeper_available : R.drawable.seat_available;
                        textColor = R.color.textPrimary;
                    }
                    break;
            }

            tvSeat.setBackgroundResource(backgroundRes);
            tvSeat.setTextColor(itemView.getContext().getResources().getColor(textColor));

            // Disable click for booked seats
            itemView.setEnabled(seat.getStatus() != Seat.SeatStatus.BOOKED);
            itemView.setAlpha(seat.getStatus() == Seat.SeatStatus.BOOKED ? 0.6f : 1.0f);

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null && seat.getStatus() != Seat.SeatStatus.BOOKED) {
                    listener.onSeatClick(seat);
                }
            });
        }
    }
}
