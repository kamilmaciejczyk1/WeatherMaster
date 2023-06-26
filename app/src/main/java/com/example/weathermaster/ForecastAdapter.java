package com.example.weathermaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private static final int[] VIEW_IDS = {
            R.id.date_text,
            R.id.forecast_temperature_text,
            R.id.humidity_text,
            R.id.windspeed_text,
            R.id.pressure_text,
            R.id.visibility_text,
            R.id.winddirection_text,
            R.id.conditions_text,
            R.id.forecast_weather_description_text
    };

    private final List<WeatherForecast> forecastList;
    private String selectedCity;

    public ForecastAdapter(List<WeatherForecast> forecastList, String selectedCity) {
        this.forecastList = forecastList;
        this.selectedCity = selectedCity;
    }
    private String getUnitForField(int fieldIndex) {
        switch (fieldIndex) {
            case 1: // forecast_temperature_text
                return "";
            case 2: // humidity_text
                return "";
            case 3: // windspeed_text
                return "m/s";
            case 4: // pressure_text
                return "hPa";
            case 5: // visibility_text
                return "km";
            case 6: // winddirection_text
                return "°";
            // Dodaj pozostałe przypadki dla innych pól, jeśli są wymagane jednostki
            default:
                return "";
        }
    }


    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forecast_item, parent, false);

        return new ForecastViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ForecastViewHolder holder, int position) {
        WeatherForecast forecast = forecastList.get(position);

        for (int i = 0; i < VIEW_IDS.length; i++) {
            String field = forecast.getField(i);
            String unit = getUnitForField(i); // Pobierz jednostkę dla danego pola
            String text = field + " " + unit; // Dodaj jednostkę do tekstu
            holder.texts[i].setText(text);
        }

        String condition = forecast.getField(7);
        if (condition.contains("Rain, Partially cloudy")) {
            holder.weatherIcon.setImageResource(R.drawable.rain_partially_cloudy);
        } else if (condition.contains("Partially cloudy")) {
            holder.weatherIcon.setImageResource(R.drawable.partially_cloudy);
        } else if (condition.contains("Clear")) {
            holder.weatherIcon.setImageResource(R.drawable.sunny);
        } else {
            holder.weatherIcon.setImageResource(R.drawable.cloudy);
        }

        holder.cityTextView.setText(selectedCity); // Ustawienie nazwy miasta
    }


    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {
        public TextView[] texts;
        public ImageView weatherIcon;
        public TextView cityTextView;

        public ForecastViewHolder(View view) {
            super(view);
            texts = new TextView[VIEW_IDS.length];
            for (int i = 0; i < VIEW_IDS.length; i++) {
                texts[i] = view.findViewById(VIEW_IDS[i]);
            }
            weatherIcon = view.findViewById(R.id.weather_icon);
            cityTextView = view.findViewById(R.id.address_text); // Dodanie TextView dla miasta
        }
    }
}
