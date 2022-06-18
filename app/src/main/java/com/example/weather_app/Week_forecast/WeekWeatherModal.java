package com.example.weather_app.Week_forecast;

public class WeekWeatherModal{
    private String dayWMD;
    private String statusWMD;
    private String imgStatusWMD;
    private String tempMaxWMD;
    private String tempMinWMD;

    public WeekWeatherModal(String dayWMD, String statusWMD, String imgStatusWMD, String tempMaxWMD, String tempMinWMD) {
        this.dayWMD = dayWMD;
        this.statusWMD = statusWMD;
        this.imgStatusWMD = imgStatusWMD;
        this.tempMaxWMD = tempMaxWMD;
        this.tempMinWMD = tempMinWMD;
    }

    public String getDayWMD() {
        return dayWMD;
    }

    public void setDayWMD(String dayWMD) {
        this.dayWMD = dayWMD;
    }

    public String getStatusWMD() {
        return statusWMD;
    }

    public void setStatusWMD(String statusWMD) {
        this.statusWMD = statusWMD;
    }

    public String getImgStatusWMD() {
        return imgStatusWMD;
    }

    public void setImgStatusWMD(String imgStatusWMD) {
        this.imgStatusWMD = imgStatusWMD;
    }

    public String getTempMaxWMD() {
        return tempMaxWMD;
    }

    public void setTempMaxWMD(String tempMaxWMD) {
        this.tempMaxWMD = tempMaxWMD;
    }

    public String getTempMinWMD() {
        return tempMinWMD;
    }

    public void setTempMinWMD(String tempMinWMD) {
        this.tempMinWMD = tempMinWMD;
    }
}


