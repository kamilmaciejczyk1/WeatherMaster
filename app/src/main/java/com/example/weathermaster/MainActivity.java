package com.example.weathermaster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;

import java.time.LocalDate;
import java.util.ArrayList;
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
        Button searchButton = findViewById(R.id.search_button);
        Button addButton = findViewById(R.id.add_location_button);
        SearchView searchbar = findViewById(R.id.searchbar);
        int closeButtonId = searchbar.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView clearSearchBar = findViewById(closeButtonId);
        Spinner citySpinner = findViewById(R.id.citySpinner);

// Przygotowanie danych miast
        List<String> cityList = new ArrayList<String>();
        cityList.add("Katowice");
        cityList.add("Gliwice");

// Utwórz adapter i przypisz go do Spinera
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(spinnerAdapter);

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
