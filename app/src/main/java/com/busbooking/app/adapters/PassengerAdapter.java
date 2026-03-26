package com.busbooking.app.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.busbooking.app.R;
import com.busbooking.app.models.Passenger;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class PassengerAdapter extends RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder> {
    private List<Passenger> passengers;

    public PassengerAdapter(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    @NonNull
    @Override
    public PassengerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_passenger, parent, false);
        return new PassengerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PassengerViewHolder holder, int position) {
        Passenger passenger = passengers.get(position);
        holder.bind(passenger, position + 1);
    }

    @Override
    public int getItemCount() {
        return passengers != null ? passengers.size() : 0;
    }

    static class PassengerViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSeatLabel;
        private TextInputEditText etName, etAge;
        private AutoCompleteTextView spinnerGender;

        public PassengerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSeatLabel = itemView.findViewById(R.id.tv_seat_label);
            etName = itemView.findViewById(R.id.et_passenger_name);
            etAge = itemView.findViewById(R.id.et_passenger_age);
            spinnerGender = itemView.findViewById(R.id.spinner_gender);
            
            // Setup gender spinner
            String[] genders = {"Male", "Female", "Other"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(itemView.getContext(),
                    android.R.layout.simple_dropdown_item_1line, genders);
            spinnerGender.setAdapter(adapter);
        }

        public void bind(Passenger passenger, int index) {
            tvSeatLabel.setText("Passenger " + index + " (Seat " + passenger.getSeatNumber() + ")");
            
            etName.setText(passenger.getName());
            etAge.setText(passenger.getAge() > 0 ? String.valueOf(passenger.getAge()) : "");
            
            // Set default gender if null
            if (passenger.getGender() == null) {
                passenger.setGender("Male");
            }
            spinnerGender.setText(passenger.getGender(), false);
            
            etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    passenger.setName(s.toString());
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });

            etAge.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        if (s.length() > 0) {
                            passenger.setAge(Integer.parseInt(s.toString()));
                        } else {
                            passenger.setAge(0);
                        }
                    } catch (NumberFormatException e) {
                        passenger.setAge(0);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });

            spinnerGender.setOnItemClickListener((parent, view, position, id) -> {
                String selectedGender = (String) parent.getItemAtPosition(position);
                passenger.setGender(selectedGender);
            });
        }
    }
}
