package com.example.weathermaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HourlyForecastActivity extends AppCompatActivity implements WeatherHourlyRequest.WeatherHourlyRequestListener {

    private HourlyForecastAdapter hourlyForecastAdapter;
    private RecyclerView hourlyForecastRecyclerView;
    private WeatherHourlyRequest weatherHourlyRequest;
    private Executor executor;
    private ForecastAdapter forecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hourly_forecast);
        SSLUtils.disableCertificateValidation();

        hourlyForecastRecyclerView = findViewById(R.id.hourly_forecast_recycler_view);
        weatherHourlyRequest = new WeatherHourlyRequest(this);
        executor = Executors.newSingleThreadExecutor();
        executeWeatherRequest("Gliwice");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();  // zakończ aktywność i wróć do poprzedniej
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void executeWeatherRequest(String city) {

        String hourlyForecastUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + city +"/2023-06-13/2023-06-13?unitGroup=metric&elements=datetime%2Ctemp%2Chumidity%2Cwindspeed%2Cwinddir%2Cpressure%2Cvisibility%2Cconditions%2Cdescription&include=hours&key=5JFQTUSUAU529CC89JAB3XYS4&contentType=json";
        executor.execute(() -> weatherHourlyRequest.executeRequest(hourlyForecastUrl));
    }


    @Override
    public void onHourlyRequestCompleted(List<WeatherForecast> forecastList) {
        runOnUiThread(() -> {
            hourlyForecastAdapter = new HourlyForecastAdapter(forecastList);
            hourlyForecastRecyclerView.setAdapter(hourlyForecastAdapter);
        });
    }
}