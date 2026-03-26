package com.busbooking.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.busbooking.app.R;
import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends ArrayAdapter<String> implements Filterable {
    private List<String> allCities;
    private List<String> filteredCities;
    private LayoutInflater inflater;
    private CityFilter cityFilter;

    public CityAdapter(@NonNull Context context, @NonNull List<String> cities) {
        super(context, R.layout.item_city_dropdown, cities);
        this.allCities = new ArrayList<>(cities);
        this.filteredCities = new ArrayList<>(cities);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return filteredCities.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        if (position >= 0 && position < filteredCities.size()) {
            return filteredCities.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.item_city_dropdown, parent, false);
        }

        TextView tvCityName = view.findViewById(R.id.tv_city_name);
        String city = getItem(position);
        if (city != null) {
            tvCityName.setText(city);
        }

        return view;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (cityFilter == null) {
            cityFilter = new CityFilter();
        }
        return cityFilter;
    }

    public void updateCities(List<String> newCities) {
        this.allCities = new ArrayList<>(newCities);
        this.filteredCities = new ArrayList<>(newCities);
        notifyDataSetChanged();
    }

    private class CityFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.values = new ArrayList<>(allCities);
                results.count = allCities.size();
            } else {
                String filterString = constraint.toString().toLowerCase().trim();
                List<String> filteredList = new ArrayList<>();

                // First, add cities that start with the query
                for (String city : allCities) {
                    if (city.toLowerCase().startsWith(filterString)) {
                        filteredList.add(city);
                    }
                }

                // Then, add cities that contain the query but don't start with it
                for (String city : allCities) {
                    if (city.toLowerCase().contains(filterString) &&
                        !city.toLowerCase().startsWith(filterString)) {
                        filteredList.add(city);
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredCities = (List<String>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return (String) resultValue;
        }
    }
}

