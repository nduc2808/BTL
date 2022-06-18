package com.example.weather_app.Hour_forecast;

public class HourWeatherModal {
    private String timeMD;
    private String temperatureMD;
    private String iconMD;
    private String windSpeedMD;

    public HourWeatherModal(String timeMD, String temperatureMD, String iconMD, String windSpeedMD) {
        this.timeMD = timeMD;
        this.temperatureMD = temperatureMD;
        this.iconMD = iconMD;
        this.windSpeedMD = windSpeedMD;
    }

    public String getTimeMD() {
        return timeMD;
    }

    public void setTimeMD(String timeMD) {
        this.timeMD = timeMD;
    }

    public String getTemperatureMD() {
        return temperatureMD;
    }

    public void setTemperatureMD(String temperatureMD) {
        this.temperatureMD = temperatureMD;
    }

    public String getIconMD() {
        return iconMD;
    }

    public void setIconMD(String iconMD) {
        this.iconMD = iconMD;
    }

    public String getWindSpeedMD() {
        return windSpeedMD;
    }

    public void setWindSpeedMD(String windSpeedMD) {
        this.windSpeedMD = windSpeedMD;
    }
}


