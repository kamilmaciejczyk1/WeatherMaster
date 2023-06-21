package com.example.weathermaster;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WeatherAlertsRequest {

    private final WeatherAlertsRequestListener listener;
    private final Executor executor;

    public WeatherAlertsRequest(WeatherAlertsRequestListener listener) {
        this.listener = listener;
        this.executor = Executors.newSingleThreadExecutor();
    }

    public void executeRequest(String url) {
        executor.execute(new RequestRunnable(url));
    }

    private class RequestRunnable implements Runnable {
        private final String url;

        public RequestRunnable(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(this.url);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(5000);
                connection.setConnectTimeout(5000);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    String response = readStream(inputStream);
                    if (listener != null) {
                        parseResponse(response);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
    }

    private String readStream(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();
        return stringBuilder.toString();
    }

    private void parseResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            if (jsonResponse.has("alerts")) {
                JSONArray alertsArray = jsonResponse.getJSONArray("alerts");

                List<WeatherAlerts> weatherAlertsList = new ArrayList<>();
                if (alertsArray.length() > 0) {
                    for (int i = 0; i < alertsArray.length(); i++) {
                        JSONObject alertObject = alertsArray.getJSONObject(i);

                        String event = alertObject.getString("event");
                        String headline = alertObject.getString("headline");
                        String description = alertObject.getString("description");
                        String ends = alertObject.getString("ends");
                        String endsEpoch = alertObject.getString("endsEpoch");
                        String onset = alertObject.getString("onset");
                        String onsetEpoch = alertObject.getString("onsetEpoch");
                        String id = alertObject.getString("id");
                        String language = alertObject.getString("language");
                        String link = alertObject.getString("link");

                        WeatherAlerts weatherAlert = new WeatherAlerts(event, headline, description, ends, endsEpoch, onset, onsetEpoch, id, language, link);
                        weatherAlertsList.add(weatherAlert);
                    }
                } else {
                    // Tablica alerts jest pusta
                    weatherAlertsList.add(new WeatherAlerts("No alerts available", "", "", "", "", "", "", "", "", ""));
                }

                if (listener != null) {
                    listener.onAlertsRequestCompleted(weatherAlertsList);
                }
            } else {
                // Brak informacji o alertach
                List<WeatherAlerts> weatherAlertsList = new ArrayList<>();
                weatherAlertsList.add(new WeatherAlerts("No alerts available", "", "", "", "", "", "", "", "", ""));
                if (listener != null) {
                    listener.onNoAlertsAvailable(weatherAlertsList);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public interface WeatherAlertsRequestListener {
        void onAlertsRequestCompleted(List<WeatherAlerts> weatherAlertsList);
        void onNoAlertsAvailable(List<WeatherAlerts> weatherAlertsList);
    }
}
