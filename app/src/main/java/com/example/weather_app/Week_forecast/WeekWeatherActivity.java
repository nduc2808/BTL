package com.example.weather_app.Week_forecast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weather_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeekWeatherActivity extends AppCompatActivity {
    public ImageView imgBack;
    public TextView txtNameCity;
    public RecyclerView listDay;


    private ArrayList<WeekWeatherModal> weekWeatherModalArrayList;
    private WeekWeatherAdapter weekWeatherAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_weather);

        imgBack = findViewById(R.id.idBack);
        txtNameCity = findViewById(R.id.idCity);
        listDay = findViewById(R.id.idForecastItem);

        Intent intent = getIntent();
        String city = intent.getStringExtra("name");
        load7DaysData(city);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        weekWeatherModalArrayList = new ArrayList<>();
        weekWeatherAdapter = new WeekWeatherAdapter(this, weekWeatherModalArrayList);
        listDay.setAdapter(weekWeatherAdapter);
    }

    // Sau khi lấy tên thành phố trả về tọa độ thành phố đó
    public void load7DaysData(String city) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=7bbb4235c9624cf8b7633806222403&q=" + city + "&days=1&aqi=yes";

        RequestQueue requestQueue = Volley.newRequestQueue(WeekWeatherActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject jsonLocation = jsonObject.getJSONObject("location");

                            String nameCity = jsonLocation.getString("name");
                            txtNameCity.setText(nameCity);
                            String lon = jsonLocation.getString("lon");
                            String lat = jsonLocation.getString("lat");

                            get7DaysData(lat, lon);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });
        requestQueue.add(stringRequest);
    }

    // Lấy dự liệu dự báo trong 7 ngày tới
    public void get7DaysData(String lat, String lon) {
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat + "&lon=" + lon + "&exclude=minutely&lang=vi&units=metric&appid=a465b2e54ab1d221c46f230831ad9604";
        RequestQueue requestQueue = Volley.newRequestQueue(WeekWeatherActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {

                        weekWeatherModalArrayList.clear();
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray jsonArrayDay = jsonObject.getJSONArray("daily");
                            String timezone_city = jsonObject.getString("timezone_offset");
                            long l_timezone = Long.valueOf(timezone_city);


                            // Lấy dữ liệu từ listObject về gán cho từng Object WeekWeatherModal
                            for (int i = 0; i < jsonArrayDay.length(); i++) {
                                JSONObject weekObject = jsonArrayDay.getJSONObject(i);


                                String day = weekObject.getString("dt");
                                long l = Long.valueOf(day);
                                long timezone = 25200*1000L;

                                Date date = new Date((l*1000L) - (timezone - l_timezone*1000L));
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy");
                                String week_date_epoch = simpleDateFormat.format(date);

                                JSONObject week_day = weekObject.getJSONObject("temp");

                                String week_maxTemp = week_day.getString("max");
                                String week_minTemp = week_day.getString("min");

                                JSONArray jsonArrayWeather = weekObject.getJSONArray("weather");
                                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);

                                String week_status = jsonObjectWeather.getString("description");
                                String week_icon = jsonObjectWeather.getString("icon");

                                weekWeatherModalArrayList.add(new WeekWeatherModal(week_date_epoch, week_status, week_icon,
                                        week_maxTemp, week_minTemp));
                            }
                            weekWeatherAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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