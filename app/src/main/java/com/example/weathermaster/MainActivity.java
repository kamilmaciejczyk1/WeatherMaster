package com.example.weathermaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class MainActivity extends AppCompatActivity implements WeatherRequest.WeatherRequestListener, WeatherAlertsRequest.WeatherAlertsRequestListener {
    private RecyclerView forecastRecyclerView;
    private WeatherRequest weatherRequest;
    private WeatherAlerts weatherAlerts;
    private Executor executor;
    private ForecastAdapter forecastAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forecastRecyclerView = findViewById(R.id.forecast_recycler_view);

        SSLUtils.disableCertificateValidation();

        // if (dzienna,godzinowa,7dni, alerty) {weatherRequest/weatherAlerts}
        weatherRequest = new WeatherRequest(this);
        executor = Executors.newSingleThreadExecutor();
        Button hourlyForecastButton = findViewById(R.id.hourly_forecast_button);
        Button sevenDaysForecastButton = findViewById(R.id.seven_days_forecast_button);
        Button mapButton = findViewById(R.id.map_button);
        Button simulateAlertButton = findViewById(R.id.simulate_alert_button);
        simulateAlertButton.setOnClickListener(v -> simulateWeatherAlert());


        hourlyForecastButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HourlyForecastActivity.class);
            startActivity(intent);
        });

        sevenDaysForecastButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SevenDaysForecastActivity.class);
            startActivity(intent);
        });

        mapButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, Map.class);
            startActivity(intent);
        });
        executeWeatherRequest("North Bay");
    }

    public void simulateWeatherAlert() {
        Log.d("MainActivity", "Simulateweatheralert() called");

        // Symulacja danych z API
        String weatherAlertsUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Gdansk/2023-06-26/2023-06-26?unitGroup=us&key=5JFQTUSUAU529CC89JAB3XYS4&contentType=json";

        // Tworzymy obiekt WeatherAlertsRequest
        WeatherAlertsRequest weatherAlertsRequest = new WeatherAlertsRequest(this);

        // Uruchamiamy żądanie do API w osobnym wątku, aby nie blokować głównego wątku
        executor.execute(() -> weatherAlertsRequest.executeRequest(weatherAlertsUrl));
    }

    public void createNotification(String title, String content, String ends, String onset) {
        Log.d("MainActivity", "CreateNotification() called");

        String CHANNEL_ID = "weather_alerts";

        // Przetwarzanie dat
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        LocalDateTime onsetDateTime = LocalDateTime.parse(onset, inputFormatter);
        LocalDateTime endsDateTime = LocalDateTime.parse(ends, inputFormatter);

        String onsetFormatted = onsetDateTime.format(outputFormatter);
        String endsFormatted = endsDateTime.format(outputFormatter);

        // Dodanie do zawartości powiadomienia
        String notificationContent = content + "\nStart: " + onsetFormatted + "\nEnd: " + endsFormatted;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationContent));




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Weather Alerts";
            String description = "Notifications about weather alerts";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        int REQUEST_CODE = 9099;
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQUEST_CODE);
        }

        notificationManager.notify(1001, builder.build());
    }


    private void executeWeatherRequest(String city) {
        String dailyForecastUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + city +"/2023-06-26/2023-06-26?unitGroup=metric&elements=datetime%2Ctemp%2Chumidity%2Cwindspeed%2Cwinddir%2Cpressure%2Cvisibility%2Cconditions%2Cdescription&include=days&key=5JFQTUSUAU529CC89JAB3XYS4&contentType=json";
        //String dailyForecastUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Gliwice/2023-06-26/2023-06-26?unitGroup=us&key=5JFQTUSUAU529CC89JAB3XYS4&contentType=json";
        executor.execute(() -> weatherRequest.executeRequest(dailyForecastUrl));
    }

    @Override
    public void onRequestCompleted(List<WeatherForecast> forecastList) {
        runOnUiThread(() -> {
            forecastAdapter = new ForecastAdapter(forecastList);
            forecastRecyclerView.setAdapter(forecastAdapter);
        });
    }


    @Override
    public void onAlertsRequestCompleted(List<WeatherAlerts> weatherAlertsList) {

    }

    @Override
    public void onNoAlertsAvailable(List<WeatherAlerts> weatherAlertsList) {

    }
}
