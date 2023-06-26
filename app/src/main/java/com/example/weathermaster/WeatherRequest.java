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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WeatherRequest {

    private final WeatherRequestListener listener;
    private final Executor executor;

    public WeatherRequest(WeatherRequestListener listener) {
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
            JSONArray daysArray = jsonResponse.getJSONArray("days");

            List<WeatherForecast> forecastList = new ArrayList<>();
            for (int i = 0; i < daysArray.length(); i++) {
                JSONObject dayObject = daysArray.getJSONObject(i);



                String datetime = dayObject.getString("datetime");
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd");

                Date date = inputFormat.parse(datetime);
                String formattedDatetime = outputFormat.format(date);

                double tempValue = dayObject.getDouble("temp");
                String temp = String.format("%.1f°C", tempValue);

                String conditions = dayObject.getString("conditions");

                double humidityValue = dayObject.getDouble("humidity");
                String humidity = String.format("%.1f%%", humidityValue);

                double windspeedValue = dayObject.getDouble("windspeed");
                String windspeed = String.format("%.1f", windspeedValue);

                double pressureValue = dayObject.getDouble("pressure");
                String pressure = String.format("%.1f", pressureValue);

                double visibilityValue = dayObject.getDouble("visibility");
                String visibility = String.format("%.1f", visibilityValue);

                double winddirValue = dayObject.getDouble("winddir");
                String winddir = String.format("%.1f", winddirValue);

                String description = dayObject.getString("description");
                WeatherForecast forecast = new WeatherForecast(formattedDatetime, temp, humidity, windspeed, pressure, visibility, winddir, conditions, description);
                forecastList.add(forecast);
            }

            if (listener != null) {
                listener.onRequestCompleted(forecastList);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public interface WeatherRequestListener {
        void onRequestCompleted(List<WeatherForecast> forecastList);
    }

}
