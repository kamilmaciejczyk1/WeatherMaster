package com.example.weathermaster;

import java.util.List;

public class WeatherForecast {
    private  String[] fields;

    public WeatherForecast(String date, String temperature, String humidity, String windspeed, String pressure, String visibility, String winddirection, String conditions, String weatherDescription) {
        fields = new String[] {date, temperature, humidity, windspeed, pressure, visibility, winddirection, conditions, weatherDescription};
    }



    public String getField(int index) {
        if (index >= 0 && index < fields.length) {
            return fields[index];
        } else {
            return "";
        }
    }
}



