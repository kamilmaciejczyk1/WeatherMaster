package com.example.weathermaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.util.ArrayList;
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

    private String selectedCity; // Dodaj zmienną przechowującą wybrane miasto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forecastRecyclerView = findViewById(R.id.forecast_recycler_view);

        SSLUtils.disableCertificateValidation();
        // if (dzienna,godzinowa,7dni, alerty) {weatherRequest/weatherAlerts}
        View forecastItem = getLayoutInflater().inflate(R.layout.forecast_item, null);
        weatherRequest = new WeatherRequest(this);
        executor = Executors.newSingleThreadExecutor();
        Button hourlyForecastButton = findViewById(R.id.hourly_forecast_button);
        Button sevenDaysForecastButton = findViewById(R.id.seven_days_forecast_button);
        Button mapButton = findViewById(R.id.map_button);

        Button simulateAlertButton = findViewById(R.id.simulate_alert_button);
        simulateAlertButton.setOnClickListener(v -> simulateWeatherAlert(selectedCity));

        ImageView searchButton = findViewById(R.id.search_button); // Pobranie odwołania do przycisku z layoutu
        ImageView addButton = findViewById(R.id.add_location_button);
        SearchView searchbar = findViewById(R.id.searchbar);
        int closeButtonId = searchbar.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView clearSearchBar = findViewById(closeButtonId);
        Spinner citySpinner = findViewById(R.id.citySpinner);
        TextView city = forecastItem.findViewById(R.id.address_text);
        city.setClickable(true);
// Przygotowanie danych miast
        List<String> cityList = new ArrayList<String>();
        cityList.add("Katowice");
        cityList.add("Gliwice");

// Utwórz adapter i przypisz go do Spinera
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cityList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(getResources().getColor(R.color.transparent)); // Ustaw kolor tekstu dla wybranego elementu
                return textView;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(spinnerAdapter);
        citySpinner.setDropDownWidth(400);
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCityItem = (String) parent.getItemAtPosition(position);
                selectedCity = selectedCityItem;
                executeWeatherRequest(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Obsłuż sytuację, gdy nie jest wybrane żadne miasto
            }
        });

        city.setOnContextClickListener(v -> citySpinner.performClick());
        hourlyForecastButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HourlyForecastActivity.class);
            intent.putExtra("miasto", selectedCity); // Dodaj wybrane miasto do Intentu
            startActivity(intent);
        });

        searchButton.setOnClickListener(v -> {
            if (searchbar.getVisibility() == View.INVISIBLE) {
                searchbar.setVisibility(View.VISIBLE);
                searchbar.setBackgroundColor(getColor(R.color.lightgray));
            } else {
                // Dodaj lokalizację do globalnej listy miejsc jeśli istnieje

                // Albo zrobić nowy ekran z wyszukanym miastem albo od razu szukać po wpisaniu

                // Wyzeruj pole wyszukiwania
                searchbar.setQuery("", false);

                // Zmiana widoczności SearchView
                searchbar.setVisibility(View.INVISIBLE);
            }
        });
        addButton.setOnClickListener(v -> {
            if(cityList.contains(selectedCity)){ return;}
            cityList.add(selectedCity);
        });
        clearSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchbar.setQuery("", false);  // Wyczyść wprowadzoną wartość
                searchbar.clearFocus();        // Zabierz fokus z pola wyszukiwania
                searchbar.setVisibility(View.INVISIBLE); // Ustaw widoczność na INVISIBLE
            }
        });
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                             @Override
                                             public boolean onQueryTextSubmit(String query) {
                                                 String searchQuery = searchbar.getQuery().toString();
                                                 if(searchQuery.isEmpty()){
                                                     searchbar.setVisibility(View.INVISIBLE);
                                                     return false;
                                                 }
                                                 selectedCity = searchQuery;
                                                 executeWeatherRequest(searchQuery);

                                                 searchbar.setVisibility(View.INVISIBLE);
                                                 searchbar.setQuery("", false);
                                                 return false;
                                             }

                                             @Override
                                             public boolean onQueryTextChange(String newText) {
                                                 return false;
                                             }
                                         });

        searchbar.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
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
                selectedCity = "Katowice";
            }

        executeWeatherRequest(selectedCity);
    }

    public void simulateWeatherAlert(String city) {
        Log.d("MainActivity", "Simulateweatheralert() called");
        LocalDate startDate = LocalDate.now();

        // Symulacja danych z API
        String weatherAlertsUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + city + "/"+startDate.toString()+"/"+startDate.toString()+"?unitGroup=us&key=5JFQTUSUAU529CC89JAB3XYS4&contentType=json";

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
                .setSmallIcon(R.drawable.wykrzyknik)
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
        //String dailyForecastUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + city +"/2023-06-26/2023-06-26?unitGroup=metric&elements=datetime%2Ctemp%2Chumidity%2Cwindspeed%2Cwinddir%2Cpressure%2Cvisibility%2Cconditions%2Cdescription&include=days&key=5JFQTUSUAU529CC89JAB3XYS4&contentType=json";
        //String dailyForecastUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Gliwice/2023-06-26/2023-06-26?unitGroup=us&key=5JFQTUSUAU529CC89JAB3XYS4&contentType=json";
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

    @Override
    public void onAlertsRequestCompleted(List<WeatherAlerts> weatherAlertsList) {

    }

    @Override
    public void onNoAlertsAvailable(List<WeatherAlerts> weatherAlertsList) {

    }


}
