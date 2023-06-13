package com.example.weathermaster;

public class WeatherForecast {
    private final String date;
    private final String temperature;
    private final String weatherDescription;
    private final String humidity;
    private final String windspeed;
    private final String pressure;
    private final String visibility;
    private final String winddirection;
    private final String conditions;

    public WeatherForecast(String date, String temperature, String weatherDescription, String humidity, String windspeed, String pressure, String visibility,String winddirection, String conditions) {
        this.date = date;
        this.temperature = temperature;
        this.weatherDescription = weatherDescription;
        this.humidity = humidity;
        this.windspeed = windspeed;
        this.pressure = pressure;
        this.visibility = visibility;
        this.winddirection = winddirection;
        this.conditions = conditions;
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
    public String getHumidity() { return humidity;   }
    public String getWindspeed() { return windspeed;   }
    public String getPressure() { return pressure;   }
    public String getVisibility() { return visibility;   }
    public String getWinddirection() { return winddirection;   }
    public String getConditions() { return conditions;   }
}

