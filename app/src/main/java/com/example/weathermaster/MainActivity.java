package com.example.weathermaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements WeatherRequest.WeatherRequestListener {
    private RecyclerView forecastRecyclerView;
    private WeatherRequest weatherRequest;
    private Executor executor;
    private ForecastAdapter forecastAdapter;

    private String selectedCity; // Dodaj zmienną przechowującą wybrane miasto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forecastRecyclerView = findViewById(R.id.forecast_recycler_view);

        SSLUtils.disableCertificateValidation();

        weatherRequest = new WeatherRequest(this);
        executor = Executors.newSingleThreadExecutor();
        Button hourlyForecastButton = findViewById(R.id.hourly_forecast_button);
        Button sevenDaysForecastButton = findViewById(R.id.seven_days_forecast_button);
        Button mapButton = findViewById(R.id.map_button);

        hourlyForecastButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HourlyForecastActivity.class);
            intent.putExtra("miasto", selectedCity); // Dodaj wybrane miasto do Intentu
            startActivity(intent);
        });


        sevenDaysForecastButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SevenDaysForecastActivity.class);
            intent.putExtra("miasto", selectedCity); // Dodaj wybrane miasto do Intentu
            startActivity(intent);
        });

        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Map.class);
            intent.putExtra("miasto", selectedCity); // Dodaj wybrane miasto do Intentu
            startActivity(intent);
        });


        // Sprawdź, czy Intent zawiera wybrane miasto
        if (getIntent().hasExtra("miasto")) {
            selectedCity = getIntent().getStringExtra("miasto");
        } else {
            selectedCity = "London"; // Domyślne miasto, jeśli nie zostało wybrane żadne
        }

        executeWeatherRequest(selectedCity);
    }

    private void executeWeatherRequest(String city) {
        LocalDate startDate = LocalDate.now();
        String dailyForecastUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + city + "/"+startDate.toString()+"/"+startDate.toString()+"?unitGroup=metric&elements=datetime%2Ctemp%2Chumidity%2Cwindspeed%2Cwinddir%2Cpressure%2Cvisibility%2Cconditions%2Cdescription&include=days&key=5JFQTUSUAU529CC89JAB3XYS4&contentType=json";
        executor.execute(() -> weatherRequest.executeRequest(dailyForecastUrl));
    }

    @Override
    public void onRequestCompleted(List<WeatherForecast> forecastList) {
        runOnUiThread(() -> {
            forecastAdapter = new ForecastAdapter(forecastList, selectedCity);
            forecastRecyclerView.setAdapter(forecastAdapter);
        });
    }
}
