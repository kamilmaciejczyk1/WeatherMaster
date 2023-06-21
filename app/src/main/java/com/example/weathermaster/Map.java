package com.example.weathermaster;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Map extends AppCompatActivity {

    private WebView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        map = (WebView) findViewById(R.id.map);
        WebSettings webSettings = map.getSettings();
        webSettings.setJavaScriptEnabled(true);

        final String OPEN_WEATHER_MAP_API = "https://tile.openweathermap.org/map/precipitation_new/{z}/{x}/{y}.png?appid=1bd674b554c22cc6d3f3847782bf5681";
// Zapytanie do Nominatim API, aby uzyskać współrzędne dla miasta
        String city = "Gliwice"; // Miasto, które chcesz zlokalizować
        String NOMINATIM_API = "https://nominatim.openstreetmap.org/search?q=%s&format=json&limit=1";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, String.format(NOMINATIM_API, city),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            if (jsonArray.length() > 0) {
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                double lat = jsonObject.getDouble("lat");
                                double lon = jsonObject.getDouble("lon");

                                // Teraz mamy współrzędne, więc możemy ustawić początkowy widok mapy
                                String unencodedHtml =
                                        "<html>" +
                                                "<head>" +
                                                "    <title>Leaflet Map</title>" +
                                                "    <meta charset=\"utf-8\" />" +
                                                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet/dist/leaflet.css\"/>" +
                                                "    <script src=\"https://unpkg.com/leaflet/dist/leaflet.js\"></script>" +
                                                "    <style>html, body { height: 100%; margin: 0; } #map { width: 100%; height: 100%; }</style>" +
                                                "</head>" +
                                                "<body>" +
                                                "    <div id=\"map\"></div>" +
                                                "    <script>" +
                                                "        var map = L.map('map').setView([" + lat + ", " + lon + "], 10);" +
                                                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png').addTo(map);" +
                                                "        L.tileLayer('" + OPEN_WEATHER_MAP_API + "', {attribution: 'Weather data © OpenWeatherMap'}).addTo(map);" +
                                                "    </script>" +
                                                "</body>" +
                                                "</html>";

                                map.loadDataWithBaseURL(null, unencodedHtml, "text/html", "UTF-8", null);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error handling
            }
        });

// Add request to queue
        Volley.newRequestQueue(this).add(stringRequest);


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
}

