package com.example.weather_app.Add_Location;


import java.io.Serializable;

public class AddLocationModal implements Serializable {
    public int id;
    public String cityALM;
    public String tempMaxALM;
    public String tempMinALM;
    public String tempCurrentALM;

    public AddLocationModal(String cityALM, String tempMaxALM, String tempMinALM, String tempCurrentALM) {
        this.cityALM = cityALM;
        this.tempMaxALM = tempMaxALM;
        this.tempMinALM = tempMinALM;
        this.tempCurrentALM = tempCurrentALM;
    }

    public String getCityALM() {
        return cityALM;
    }

    public void setCityALM(String cityALM) {
        this.cityALM = cityALM;
    }

    public String getTempMaxALM() {
        return tempMaxALM;
    }

    public void setTempMaxALM(String tempMaxALM) {
        this.tempMaxALM = tempMaxALM;
    }

    public String getTempMinALM() {
        return tempMinALM;
    }

    public void setTempMinALM(String tempMinALM) {
        this.tempMinALM = tempMinALM;
    }

    public String getTempCurrentALM() {
        return tempCurrentALM;
    }

    public void setTempCurrentALM(String tempCurrentALM) {
        this.tempCurrentALM = tempCurrentALM;
    }

}
