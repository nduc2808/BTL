package com.example.weather_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Context context;

    private LinearLayout home;
    private TextView cityName,countryName, txtTemperature, txtStatus, txtDay;
    private TextView txtCloud, txtWind, txtHumidity, txtUV, txtAQI, txtPressure;
    private TextInputEditText editCity;
    private ImageView imageView, imgSearch, airQuality, select;
    private RecyclerView forecastTime;


    private ArrayList<WeatherModal> weatherModalArrayList;
    private WeatherAdapter weatherAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        home = (LinearLayout) findViewById(R.id.idHome);
        cityName = findViewById(R.id.idCityName);
        countryName = findViewById(R.id.idCountryName);
        txtTemperature = findViewById(R.id.idTemperature);
        txtStatus = findViewById(R.id.idStatus);
        txtDay = findViewById(R.id.idTextViewDay);
        txtHumidity = findViewById(R.id.idHumidity);
        txtWind = findViewById(R.id.idWind);
        txtCloud = findViewById(R.id.idCloud);
        txtUV = findViewById(R.id.idUV);
        txtAQI = findViewById(R.id.idAQI);
        txtPressure = findViewById(R.id.idPressure);
        select = findViewById(R.id.idSelect);
        forecastTime = findViewById(R.id.idModalItem);

        editCity = findViewById(R.id.idEditCity);
        imgSearch = findViewById(R.id.idSearch);
        imageView = findViewById(R.id.idImage);
        airQuality = findViewById(R.id.idImgAQI);


        // Arraylist gồm các đối tượng con là weatherModal
        weatherModalArrayList = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this,weatherModalArrayList);
        forecastTime.setAdapter(weatherAdapter);


        // Click thực hiện tìm kiếm theo tên thành phố
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isConnect()) {
                    Toast.makeText(MainActivity.this, "No Internet Access", Toast.LENGTH_LONG).show();

                } else {
                    String city = editCity.getText().toString();
                    if (city.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please enter city name", Toast.LENGTH_LONG).show();
                    } else {
                        getWeatherDayModal(city);
                        getCurrentWeatherData(city);
                    }
                }
            }
        });

        // Click chuyển sang màn hình AirQuality
        airQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AirQualityActivity.class);
                String city = editCity.getText().toString();
                intent.putExtra("name", city);
                startActivity(intent);

            }
        });

        // Click thực hiện show menu
        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(v);
            }
        });
    }

    private boolean isConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);

        // đóng giao diện bàn phím khi nhập xong
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editCity.getWindowToken(), 0);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public void showMenu(View v) {
        final Intent IntForecast = new Intent(this, MainActivity2.class);

//        final Intent toDist = new Intent(this, DistractionsActivity.class);

        PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.idForecastWeek)
                    startActivity(IntForecast);

                if (item.getItemId() == R.id.idAddLocation)
                    Toast.makeText(MainActivity.this, "Add Location", Toast.LENGTH_SHORT).show();

                return true;
            }
        });
        popupMenu.show();
    }

    // Lấy dữ liệu từ api về thông tin thời tiết trong ngày
    public void getCurrentWeatherData (String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://api.weatherapi.com/v1/forecast.json?key=7bbb4235c9624cf8b7633806222403&q=" + data + "&days=1&aqi=yes";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject jsonLocation = jsonObject.getJSONObject("location");

                            // get data property 'name'
                            String name = jsonLocation.getString("name");
                            cityName.setText(name);

                            // get data property 'country'
                            String country = jsonLocation.getString("country");
                            countryName.setText(country);

                            // get data property 'localtime'
                            String time = jsonLocation.getString("localtime");
                            txtDay.setText(time);
//                            String time = jsonLocation.getString("localtime_epoch");
//                            long l = Long.valueOf(time);
//                            Date date = new Date(l*1000L);
//                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy HH:mm aa");
//                            String Day = simpleDateFormat.format(date);
//                            txtDay.setText(Day);


                            // get data property 'temperature'
                            JSONObject jsonCurrent = jsonObject.getJSONObject("current");

                            int isDay = jsonCurrent.getInt("is_day");
                            if (isDay == 1) {
                                home.setBackgroundResource(R.drawable.sunday);
                            }
                            else {
                                home.setBackgroundResource(R.drawable.night);
                            }

                            String temperature = jsonCurrent.getString("temp_c");
                            Double dbTemperature = Double.valueOf(temperature);
                            String cvTemperature = String.valueOf(dbTemperature.intValue());
                            txtTemperature.setText(cvTemperature + "°c");


                            JSONObject jsonCondition = jsonCurrent.getJSONObject("condition");
                            String icon = jsonCondition.getString("icon");
                            Picasso.get().load("http://"+ icon).into(imageView);

                            String status = jsonCondition.getString("text");
                            txtStatus.setText(status);

                            String humidity = jsonCurrent.getString("humidity");
                            txtHumidity.setText(humidity + "%");

                            String wind = jsonCurrent.getString("wind_kph");
                            txtWind.setText(wind + "km/h");

                            String cloud = jsonCurrent.getString("cloud");
                            txtCloud.setText(cloud + "%");

                            String uv = jsonCurrent.getString("uv");
                            txtUV.setText(uv);

                            String pressure = jsonCurrent.getString("pressure_mb");
                            txtPressure.setText(pressure + "mb");

                            JSONObject jsonAirQuality = jsonCurrent.getJSONObject("air_quality");
                            String pm2_5 = jsonAirQuality.getString("pm2_5");
                            Double dbPm2_5 = Double.valueOf(pm2_5);
                            String cvPm2_5 = String.valueOf(dbPm2_5.intValue());
                            txtAQI.setText(cvPm2_5);

                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(editCity.getWindowToken(), 0);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Please enter valid city name...", Toast.LENGTH_LONG).show();
                }
            });
        requestQueue.add(stringRequest);
        }

    //Lấy dữ liệu từ api về và set các giá trị cho từng Modal
    private void getWeatherDayModal(String city) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://api.weatherapi.com/v1/forecast.json?key=7bbb4235c9624cf8b7633806222403&q=" + city + "&days=1&aqi=yes";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject jsonForecast = jsonObject.getJSONObject("forecast");

                            JSONObject jsonForecastDay = jsonForecast.getJSONArray("forecastday").getJSONObject(0);

                            JSONArray hourArray = jsonForecastDay.getJSONArray("hour");

                            for (int i = 0; i < hourArray.length(); i++) {
                                JSONObject hourObject = hourArray.getJSONObject(i);

                                String time_modal = hourObject.getString("time");
//                                long l = Long.valueOf(time);
//                                Date date = new Date(l*1000L);
//                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
//                                String timeConvert = simpleDateFormat.format(date);

                                String temp_modal = hourObject.getString("temp_c");

                                JSONObject jsonConditionModal = hourObject.getJSONObject("condition");
                                String icon_modal = jsonConditionModal.getString("icon");

                                String windSpeed_modal = hourObject.getString("wind_kph");
                                weatherModalArrayList.add(new WeatherModal(time_modal, temp_modal, icon_modal, windSpeed_modal));

                            }

                            weatherAdapter.notifyDataSetChanged();

                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
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