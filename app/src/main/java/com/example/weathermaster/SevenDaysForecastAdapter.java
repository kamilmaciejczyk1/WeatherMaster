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
        private String selectedCity;


    public SevenDaysForecastAdapter(List<WeatherForecast> sevenDaysForecastList) {
        this.sevenDaysForecastList = sevenDaysForecastList;
        this.selectedCity = selectedCity;

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

            // Ustalamy ikonę na podstawie warunków pogodowych
            String condition = forecast.getField(7);
            if (condition.contains("Rain, Partially cloudy")) {
                weatherIconImageView.setImageResource(R.drawable.rain_partially_cloudy); // obraz dla deszczowej pogody
            } else if (condition.contains("Partially cloudy")) {
                weatherIconImageView.setImageResource(R.drawable.partially_cloudy); // obraz dla śnieżnej pogody
            } else if (condition.contains("Clear")) {
                weatherIconImageView.setImageResource(R.drawable.sunny); // obraz dla słonecznej pogody
            } else if (condition.contains("Rain, Overcast")) {
                weatherIconImageView.setImageResource(R.drawable.rain_overcast); // obraz dla słonecznej pogody
            } else if (condition.contains("Overcast"))
            {
                weatherIconImageView.setImageResource(R.drawable.overcast); // obraz dla słonecznej pogody
            } else {
                weatherIconImageView.setImageResource(R.drawable.cloudy); // obraz dla chmurnej pogody
            }

            temperatureTextView.setText(forecast.getField(1)); // Używamy getField(1) dla temperatury
        }
    }
}
