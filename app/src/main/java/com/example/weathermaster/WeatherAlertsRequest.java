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
    public void parseResponse(String response) {
        try {
            JSONObject jsonResponse = new JSONObject(response);
            List<WeatherAlerts> weatherAlertsList = new ArrayList<>();
            WeatherAlerts weatherAlert = null;
            if (jsonResponse.has("alerts")) {
                JSONArray alertsArray = jsonResponse.getJSONArray("alerts");

                if (alertsArray.length() > 0) {
                    for (int i = 0; i < alertsArray.length(); i++) {
                        JSONObject alertObject = alertsArray.getJSONObject(i);

                        String event = alertObject.getString("event");
                        String headline = alertObject.getString("headline");
                        String description = alertObject.getString("description");
                        String ends = alertObject.getString("ends");
                        String onset = alertObject.getString("onset");

                        weatherAlert = new WeatherAlerts(event, headline, description,"","","","","","",""); //...
                        if (weatherAlert != null) {
                            weatherAlertsList.add(weatherAlert);
                        }

                        // create a notification for this alert
                        // for simplicity, use headline as notification title and description as notification content
                        ((MainActivity) listener).createNotification(headline, description, ends, onset);  // You need to ensure that the listener is MainActivity
                    }
                } else {
                    // No alerts available
                    weatherAlert = new WeatherAlerts("No alerts available", "", "", "", "", "", "", "", "", "");
                    if (weatherAlert != null) {
                        weatherAlertsList.add(weatherAlert);
                    }
                }

                if (listener != null) {
                    listener.onAlertsRequestCompleted(weatherAlertsList);
                }
            } else {
                // No alerts information
                weatherAlert = new WeatherAlerts("No alerts available", "", "", "", "", "", "", "", "", "");
                if (weatherAlert != null) {
                    weatherAlertsList.add(weatherAlert);
                }
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
