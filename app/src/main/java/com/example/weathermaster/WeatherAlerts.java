package com.example.weathermaster;

public class WeatherAlerts {
    private  String[] fields;

    public WeatherAlerts(String event, String headline, String description, String ends, String endsEpoch, String onset, String onsetEpoch, String id, String language, String link) {
        fields = new String[] {event,headline,description,ends,endsEpoch,onset,onsetEpoch,id,language,link};
    }



    public String getField(int index) {
        if (index >= 0 && index < fields.length) {
            return fields[index];
        } else {
            return "";
        }
    }
}
