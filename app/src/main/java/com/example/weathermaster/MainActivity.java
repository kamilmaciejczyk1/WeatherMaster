package com.example.weathermaster;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements WeatherRequest.WeatherRequestListener, WeatherHourlyRequest.WeatherHourlyRequestListener{
    private RecyclerView forecastRecyclerView;
    private WeatherRequest weatherRequest;
    private WeatherHourlyRequest weatherHourlyRequest;
    private Executor executor;
    private ForecastAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        forecastRecyclerView = findViewById(R.id.forecast_recycler_view);

        SSLUtils.disableCertificateValidation();

        weatherRequest = new WeatherRequest(this);
        weatherHourlyRequest = new WeatherHourlyRequest(this);
        executor = Executors.newSingleThreadExecutor();

        executeWeatherRequest("Gliwice");
    }

    private void executeWeatherRequest(String city) {
        // ---
        // dla 7dni
        //String url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + city +"/2023-06-13/2023-06-20?unitGroup=metric&elements=datetime%2Ctemp%2Chumidity%2Cwindspeed%2Cwinddir%2Cpressure%2Cvisibility%2Cconditions%2Cdescription&include=days&key=C5WLXD95TLMT6CPABN76MB6CR&contentType=json";

        // dla 1dnia
         //String url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + city +"/2023-06-13/2023-06-13?unitGroup=metric&elements=datetime%2Ctemp%2Chumidity%2Cwindspeed%2Cwinddir%2Cpressure%2Cvisibility%2Cconditions%2Cdescription&include=days&key=C5WLXD95TLMT6CPABN76MB6CR&contentType=json";
        //executor.execute(() -> weatherRequest.executeRequest(url));

        // godzinowo
         String url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + city +"/2023-06-13/2023-06-13?unitGroup=metric&elements=datetime%2Ctemp%2Chumidity%2Cwindspeed%2Cwinddir%2Cpressure%2Cvisibility%2Cconditions%2Cdescription&include=hours&key=C5WLXD95TLMT6CPABN76MB6CR&contentType=json";
         executor.execute(() -> weatherHourlyRequest.executeRequest(url));
    }

    @Override
    public void onRequestCompleted(List<WeatherForecast> forecastList) {
        runOnUiThread(() -> {
            adapter = new ForecastAdapter(forecastList);
            forecastRecyclerView.setAdapter(adapter);
        });
    }

    @Override
    public void onHourlyRequestCompleted(List<WeatherForecast> forecastList) {
        runOnUiThread(() -> {
            adapter = new ForecastAdapter(forecastList);
            forecastRecyclerView.setAdapter(adapter);
        });
    }
}
