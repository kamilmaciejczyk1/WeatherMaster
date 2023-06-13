package com.example.weathermaster;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        for (int i = 0; i < VIEW_IDS.length; i++) {
            holder.texts[i].setText(forecast.getField(i));
        }
    }

    @Override
    public int getItemCount() {
        return forecastList.size();
    }

    public static class ForecastViewHolder extends RecyclerView.ViewHolder {
        public TextView[] texts;

        public ForecastViewHolder(View view) {
            super(view);
            texts = new TextView[VIEW_IDS.length];
            for (int i = 0; i < VIEW_IDS.length; i++) {
                texts[i] = view.findViewById(VIEW_IDS[i]);
            }
        }
    }
}
