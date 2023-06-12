package com.example.weathermaster;

public class WeatherForecast {
    private final String date;
    private final String temperature;
    private final String weatherDescription;

    public WeatherForecast(String date, String temperature, String weatherDescription) {
        this.date = date;
        this.temperature = temperature;
        this.weatherDescription = weatherDescription;
    }

    public String getDate() {
        return date;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWeatherDescription() {
        return weatherDescription;
    }
}

