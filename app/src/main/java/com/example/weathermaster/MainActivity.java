package com.example.weathermaster;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private TextView temperatureText;
    private TextView weatherDescriptionText;
    private TextView windSpeedText;
    private TextView location;
    private RecyclerView forecastRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       // temperatureText = findViewById(R.id.temperature);
        //weatherDescriptionText = findViewById(R.id.weather_description);
        //windSpeedText = findViewById(R.id.wind_speed);
        //location = findViewById(R.id.location);

        forecastRecyclerView = findViewById(R.id.forecast_recycler_view);

        setWeatherData();
    }

    @SuppressLint("SetTextI18n")
    private void setWeatherData() {
       // temperatureText.setText("20 °C");
        //weatherDescriptionText.setText("Sunny");
        //windSpeedText.setText("5 km/h");
        //location.setText("Katowice");

        List<WeatherForecast> forecastList = getDummyForecast();
        ForecastAdapter adapter = new ForecastAdapter(forecastList);
        forecastRecyclerView.setAdapter(adapter);
    }

    private List<WeatherForecast> getDummyForecast() {
        List<WeatherForecast> forecastList = new ArrayList<>();
        forecastList.add(new WeatherForecast("Tomorrow", "22°C", "Sunny","humidity","windspeed","pressure","visibility","wind dir","conditions"));
        //forecastList.add(new WeatherForecast("14.06", "23°C", "Cloudy"));
        //forecastList.add(new WeatherForecast("15.06", "69°C", "Cloudy"));
        // Add more dummy data...

        return forecastList;
    }
}
