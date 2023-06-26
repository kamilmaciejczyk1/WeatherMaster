package com.example.weathermaster;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class HourlyForecastAdapter extends RecyclerView.Adapter<HourlyForecastAdapter.HourlyForecastViewHolder> {
    private List<WeatherForecast> hourlyForecastList;
    private String selectedCity;


    public HourlyForecastAdapter(List<WeatherForecast> hourlyForecastList, String selectedCity) {
        this.hourlyForecastList = hourlyForecastList;
        this.selectedCity = selectedCity;

    }

    @NonNull
    @Override
    public HourlyForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hourly_forecast_item, parent, false);
        return new HourlyForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyForecastViewHolder holder, int position) {
        WeatherForecast forecast = hourlyForecastList.get(position + 1); // Pomiń pierwszy element
        holder.bind(forecast);
    }

    @Override
    public int getItemCount() {
        return hourlyForecastList.size() - 1; // Zmniejsz rozmiar o 1
    }

    public class HourlyForecastViewHolder extends RecyclerView.ViewHolder {
        private TextView hourTextView;
        private ImageView weatherIconImageView;
        private TextView temperatureTextView;

        public HourlyForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            hourTextView = itemView.findViewById(R.id.hour_text);
            weatherIconImageView = itemView.findViewById(R.id.hourly_weather_icon);
            temperatureTextView = itemView.findViewById(R.id.hourly_temperature_text);
        }

        public void bind(WeatherForecast forecast) {
            String hourText = forecast.getField(0); // Używamy getField(0) dla godziny
            hourTextView.setText(hourText);

            // Przekształcamy godzinę w postaci Stringa na int, aby móc ją porównać
            int hour = Integer.parseInt(hourText.split(":")[0]);

            // Sprawdzamy czy jest noc
            boolean isNight = hour >= 21 || hour < 5;

            // Ustalamy ikonę na podstawie warunków pogodowych
            String condition = forecast.getField(7);

            if (condition.contains("Rain, Partially cloudy")) {
                weatherIconImageView.setImageResource(
                        isNight ? R.drawable.night_rain_partially_cloudy : R.drawable.rain_partially_cloudy);
            } else if (condition.contains("Partially cloudy")) {
                weatherIconImageView.setImageResource(
                        isNight ? R.drawable.night_partially_cloudy : R.drawable.partially_cloudy);
            } else if (condition.contains("Clear")) {
                weatherIconImageView.setImageResource(
                        isNight ? R.drawable.night_sunny : R.drawable.sunny);
            } else if (condition.contains("Rain, Overcast")) {
                weatherIconImageView.setImageResource(
                        isNight ? R.drawable.night_rain_overcast : R.drawable.rain_overcast);
            } else if (condition.contains("Overcast")) {
                weatherIconImageView.setImageResource(
                        isNight ? R.drawable.night_overcast : R.drawable.overcast);
            } else {
                weatherIconImageView.setImageResource(
                        isNight ? R.drawable.cloudy : R.drawable.cloudy);
            }

            temperatureTextView.setText(forecast.getField(1)); // Używamy getField(1) dla temperatury
        }


    }
}

