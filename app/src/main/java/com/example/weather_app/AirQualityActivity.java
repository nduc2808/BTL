package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class AirQualityActivity extends AppCompatActivity {

    public ImageView imgPrevious;
    public TextView cityName, indexAQI, txtInfo, textContent;
    public TextView PM25, PM10, txtSO2, txtNO2, txtO3, txtCO;
    String nameCity = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);
        
        imgPrevious = findViewById(R.id.idPrevious);
        cityName = findViewById(R.id.idCityName);
        txtInfo = findViewById(R.id.idInfo);
        textContent = findViewById(R.id.idTextContent);
        PM25 = findViewById(R.id.idAQI);
        PM10 = findViewById(R.id.idPM10);
        txtSO2 = findViewById(R.id.idSO2);
        txtNO2 = findViewById(R.id.idNO2);
        txtO3 = findViewById(R.id.idO3);
        txtCO = findViewById(R.id.idCO);

        Intent intent = getIntent();
        String city = intent.getStringExtra("name");
        Log.d("Du lieu truyen qua", city);

        if (city.equals("")) {
            nameCity = "ha noi";
            getAirQuality(nameCity);
        }
        else {
            nameCity = city;
            getAirQuality(nameCity);
        }

        imgPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void getAirQuality(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(AirQualityActivity.this);
        String url = "http://api.weatherapi.com/v1/forecast.json?key=7bbb4235c9624cf8b7633806222403&q=" + data + "&days=1&aqi=yes";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//
//                            JSONObject jsonLocation = jsonObject.getJSONObject("location");
//
//                            // get data property 'name'
//                            String name = jsonLocation.getString("name");
//                            cityName.setText(name);
//
//                            JSONObject jsonCurrent = jsonObject.getJSONObject("current");
//                            JSONObject airQuality = jsonCurrent.getJSONObject("air_quality");
//
//                            String pm25 = airQuality.getString("pm2_5");
//                            PM25.setText(pm25);
//
//                            String pm10 = airQuality.getString("pm10");
//                            PM10.setText(pm10);
//
//                            String so2 = airQuality.getString("so2");
//                            txtSO2.setText(so2);
//
//                            String no2 = airQuality.getString("no2");
//                            txtNO2.setText(no2);
//
//                            String o3 = airQuality.getString("o3");
//                            txtO3.setText(o3);
//
//                            String co = airQuality.getString("co");
//                            txtCO.setText(co);
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest);

    }
}