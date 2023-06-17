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

    public HourlyForecastAdapter(List<WeatherForecast> hourlyForecastList) {
        this.hourlyForecastList = hourlyForecastList;
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
            hourTextView.setText(forecast.getField(0)); // Używamy getField(0) dla godziny
            // Użyjemy placeholdera dla ikony pogodowej, aby dostosować go do swojej implementacji
            weatherIconImageView.setImageResource(R.drawable.cloudy);
            temperatureTextView.setText(forecast.getField(1)); // Używamy getField(1) dla temperatury
        }
    }
}

