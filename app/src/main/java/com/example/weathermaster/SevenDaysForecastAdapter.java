package com.example.weathermaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SevenDaysForecastAdapter extends RecyclerView.Adapter<SevenDaysForecastAdapter.SevenDaysForecastViewHolder> {
        private List<WeatherForecast> sevenDaysForecastList;

    public SevenDaysForecastAdapter(List<WeatherForecast> sevenDaysForecastList) {
        this.sevenDaysForecastList = sevenDaysForecastList;
    }


        @NonNull
        @Override
        public com.example.weathermaster.SevenDaysForecastAdapter.SevenDaysForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.seven_days_forecast_item, parent, false);
            return new com.example.weathermaster.SevenDaysForecastAdapter.SevenDaysForecastViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull com.example.weathermaster.SevenDaysForecastAdapter.SevenDaysForecastViewHolder holder, int position) {
            WeatherForecast forecast = sevenDaysForecastList.get(position + 1); // Pomiń pierwszy element
            holder.bind(forecast);
        }

        @Override
        public int getItemCount() {
            return sevenDaysForecastList.size() - 1; // Zmniejsz rozmiar o 1
        }

        public class SevenDaysForecastViewHolder extends RecyclerView.ViewHolder {
        private TextView dayTextView;
        private ImageView weatherIconImageView;
        private TextView temperatureTextView;

            public SevenDaysForecastViewHolder(@NonNull View itemView) {
                super(itemView);
                dayTextView = itemView.findViewById(R.id.hour_text);
                weatherIconImageView = itemView.findViewById(R.id.hourly_weather_icon);
                temperatureTextView = itemView.findViewById(R.id.hourly_temperature_text);
            }


        public void bind(WeatherForecast forecast) {
            dayTextView.setText(forecast.getField(0)); // Używamy getField(0) dla godziny
            // Użyjemy placeholdera dla ikony pogodowej, aby dostosować go do swojej implementacji
            weatherIconImageView.setImageResource(R.drawable.cloudy);
            temperatureTextView.setText(forecast.getField(1)); // Używamy getField(1) dla temperatury
        }
    }
}
