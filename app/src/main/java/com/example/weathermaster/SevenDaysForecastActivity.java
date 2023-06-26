package com.example.weathermaster;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SevenDaysForecastActivity extends AppCompatActivity implements WeatherRequest.WeatherRequestListener {
    private RecyclerView forecastRecyclerView;
    private WeatherRequest weatherRequest;
    private Executor executor;
    private SevenDaysForecastAdapter forecastAdapter;

    private String selectedCity; // Dodaj zmienną przechowującą wybrane miasto


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seven_days_forecast);
        if (getIntent().hasExtra("miasto")) {
            selectedCity = getIntent().getStringExtra("miasto");
        } else {
            selectedCity = "London"; // Domyślne miasto, jeśli nie zostało wybrane żadne
        }
        forecastRecyclerView = findViewById(R.id.forecast_recycler_view);

        SSLUtils.disableCertificateValidation();

        weatherRequest = new WeatherRequest(this);
        executor = Executors.newSingleThreadExecutor();
        executeWeatherRequest(selectedCity);
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
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(7);

        String dailyForecastUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + city + "/" + startDate.toString() + "/" + endDate.toString() + "?unitGroup=metric&elements=datetime%2Ctemp%2Chumidity%2Cwindspeed%2Cwinddir%2Cpressure%2Cvisibility%2Cconditions%2Cdescription&include=days&key=5JFQTUSUAU529CC89JAB3XYS4&contentType=json";

        executor.execute(() -> weatherRequest.executeRequest(dailyForecastUrl));

    }

    @Override
    public void onRequestCompleted(List<WeatherForecast> forecastList) {
        runOnUiThread(() -> {
            forecastAdapter = new SevenDaysForecastAdapter(forecastList);
            forecastRecyclerView.setAdapter(forecastAdapter);
        });
    }


}
