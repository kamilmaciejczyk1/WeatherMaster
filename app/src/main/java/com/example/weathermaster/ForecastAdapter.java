package com.example.weathermaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private final List<WeatherForecast> forecastList;

    public ForecastAdapter(List<WeatherForecast> forecastList) {
        this.forecastList = forecastList;
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
        holder.dateText.setText(forecast.getDate());
        holder.temperatureText.setText(forecast.getTemperature());
        holder.weatherDescriptionText.setText(forecast.getWeatherDescription());
        holder.humidityText.setText(forecast.getHumidity());
        holder.windspeedText.setText(forecast.getWindspeed());
        holder.pressureText.setText(forecast.getPressure());
        holder.visibilityText.setText(forecast.getVisibility());
        holder.winddirectionText.setText(forecast.getWinddirection());
        holder.conditionsText.setText(forecast.getConditions());
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {
        public TextView dateText;
        public TextView temperatureText;
        public TextView weatherDescriptionText;
        public TextView humidityText;
        public TextView windspeedText;
        public TextView pressureText;
        public TextView visibilityText;
        public TextView winddirectionText;
        public TextView conditionsText;

        public ForecastViewHolder(View view) {
            super(view);
            dateText = view.findViewById(R.id.date_text);
            temperatureText = view.findViewById(R.id.forecast_temperature_text);
            weatherDescriptionText = view.findViewById(R.id.forecast_weather_description_text);
            humidityText = view.findViewById(R.id.humidity_text);
            windspeedText = view.findViewById(R.id.windspeed_text);
            pressureText = view.findViewById(R.id.pressure_text);
            visibilityText = view.findViewById(R.id.visibility_text);
            winddirectionText = view.findViewById(R.id.winddirection_text);
            conditionsText = view.findViewById(R.id.conditions_text);
        }
    }
}
