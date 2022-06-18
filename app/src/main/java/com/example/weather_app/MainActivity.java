package com.example.weather_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weather_app.Add_Location.AddLocationActivity;
import com.example.weather_app.Air_Quality.AirQualityActivity;
import com.example.weather_app.Hour_forecast.HourWeatherAdapter;
import com.example.weather_app.Hour_forecast.HourWeatherModal;
import com.example.weather_app.Week_forecast.WeekWeatherActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private LinearLayout home;
    private TextView cityName,countryName, txtTemperature, txtStatus, txtDay;
    private TextView txtCloud, txtWind, txtHumidity, txtUV, txtAQI, txtPressure, txtVisibility;
    private ImageView imageView, select;
    private LinearLayout airQuality;
    private RecyclerView forecastTime;
    private LocationRequest locationRequest;
    private ProgressBar progressBar;


    // Tạo một không gian lưu trữ dữ liệu SharedPreferences
    public static final String SHARED_PREFS = "storage";

    private ArrayList<HourWeatherModal> weatherModalArrayList;
    private HourWeatherAdapter weatherAdapter;

    String city = "";

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
        txtVisibility = findViewById(R.id.idVisibility);
        txtAQI = findViewById(R.id.idAQI);
        txtPressure = findViewById(R.id.idPressure);
        select = findViewById(R.id.idSelect);
        forecastTime = findViewById(R.id.idModalItem);
        progressBar = findViewById(R.id.idProgressBar);

        imageView = findViewById(R.id.idImage);
        airQuality = findViewById(R.id.idLinerAQI);

        // Arraylist gồm các đối tượng con là weatherModal
        weatherModalArrayList = new ArrayList<>();
        weatherAdapter = new HourWeatherAdapter(this, weatherModalArrayList);
        forecastTime.setAdapter(weatherAdapter);



        // Nhận tên thành phố từ activity AddLocationActivity
        city = getIntent().getStringExtra("nameCity");
        if (city == null) {
            getCurrentLocation();
        } else {
            cityName.setText(city);
            getWeatherMainActivity(city);
        }

        // Click chuyển sang màn hình AirQuality
        airQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AirQualityActivity.class);
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

    // Yêu cầu cấp quyền định vị vị trí
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (isGPSEnabled()) {
                    getCurrentLocation();
                }else {
                    turnOnGPS();
                }
            }
        }
    }

    // Trả về kết quả của việc yêu cầu cấp vị trí
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                getCurrentLocation();
            }
        }
    }


    /**
     * Lấy tọa độ và đẩy vào hàm getLocation() để lấy set tên thành phố => Widget
     * Lấy tọa độ và đẩy vào hàm getCurrentWeatherData và getWeatherDayModal
     * để trả về thông tin thời tiết hiện tại và dự báo thời tiết các giờ tiếp theo
     */
    private void getCurrentLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(2500);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (isGPSEnabled()) {

                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                            .requestLocationUpdates(locationRequest, new LocationCallback() {
                                @Override
                                public void onLocationResult(@NonNull LocationResult locationResult) {
                                    super.onLocationResult(locationResult);

                                    LocationServices.getFusedLocationProviderClient(MainActivity.this)
                                            .removeLocationUpdates(this);

                                    if (locationResult != null && locationResult.getLocations().size() >0){
                                        int index = locationResult.getLocations().size() - 1;
                                        double latitude = locationResult.getLocations().get(index).getLatitude();
                                        double longitude = locationResult.getLocations().get(index).getLongitude();
                                        getLocation(latitude, longitude);
                                        getCurrentWeatherData(latitude, longitude);
                                        getWeatherDayModal(latitude, longitude);
                                    }
                                }
                            }, Looper.getMainLooper());

                } else {
                    turnOnGPS();
                }

            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    /**
     * Xử lý sự kiện khi bật GPS
     */
    private void turnOnGPS() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(MainActivity.this, "GPS is already tured on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MainActivity.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });

    }

    /**
     * Kiểm tra xem GPS đã hoạt động hay chưa
     * @return
     */
    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;

    }

    // hiển thị các lựu chọn tính năng cho người dùng
    public void showMenu(View v) {
        Intent IntForecast = new Intent(this, WeekWeatherActivity.class);

        Intent IntAddLocation = new Intent(this, AddLocationActivity.class);

        PopupMenu popupMenu = new PopupMenu(MainActivity.this, v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_main, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                // Dự báo thời tiết trong tuần của khu vực hiện tại
                if (item.getItemId() == R.id.idForecastWeek) {
                    IntForecast.putExtra("name",city);
                    startActivity(IntForecast);
                }

                // Thêm khu vực yêu thích để tìm thông tin thời tiết nhanh
                if (item.getItemId() == R.id.idAddLocation) {
                    startActivity(IntAddLocation);
                }

                return true;
            }
        });
        popupMenu.show();
    }

    // Lấy dữ liệu từ api về thông tin thời tiết trong ngày
    public void getCurrentWeatherData (double lat, double lon) {
        // Hàng đợi yêu cầu
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://api.weatherapi.com/v1/forecast.json?key=7bbb4235c9624cf8b7633806222403&q=" + lat + "," + lon + "&days=1&aqi=yes";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            home.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject jsonLocation = jsonObject.getJSONObject("location");

                            // get data property 'country'
                            String country = jsonLocation.getString("country");
                            countryName.setText(country);

                            // get data property 'localtime'
                            String time = jsonLocation.getString("localtime");
                            txtDay.setText(time);

                            // get data property 'temperature'
                            JSONObject jsonCurrent = jsonObject.getJSONObject("current");

                            // kiểm tra biến isDay để set background
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
                            txtWind.setText(wind + " km/h");

                            String cloud = jsonCurrent.getString("cloud");
                            txtCloud.setText(cloud + "%");

                            String uv = jsonCurrent.getString("uv");
                            txtUV.setText(uv);

                            String visibility = jsonCurrent.getString("vis_km");
                            txtVisibility.setText(visibility + " km");

                            String pressure = jsonCurrent.getString("pressure_mb");
                            txtPressure.setText(pressure + "mb");

                            JSONObject jsonAirQuality = jsonCurrent.getJSONObject("air_quality");
                            String pm2_5 = jsonAirQuality.getString("pm2_5");
                            Double dbPm2_5 = Double.valueOf(pm2_5);
                            String cvPm2_5 = String.valueOf(Math.round(dbPm2_5));
                            txtAQI.setText("AQI " + cvPm2_5);

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

    //Lấy dữ liệu từ api về và set các thông tin cho từng Modal
    private void getWeatherDayModal(double lat, double lon) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "https://api.openweathermap.org/data/2.5/onecall?lat=" + lat +"&lon=" + lon +"&exclude=minutely&lang=us&units=metric&appid=a465b2e54ab1d221c46f230831ad9604";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {

                        weatherModalArrayList.clear();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            // timezone_city là thời gian của thành phố hiện tại
                            String timezone_city = jsonObject.getString("timezone_offset");
                            long l_timezone = Long.valueOf(timezone_city);

                            JSONArray hourArray = jsonObject.getJSONArray("hourly");

                            int reduce_hour_array = 23;
                            for (int i = 1; i < hourArray.length() - reduce_hour_array; i++) {
                                JSONObject hourObject = hourArray.getJSONObject(i);


                                String day = hourObject.getString("dt");

                                long l = Long.valueOf(day);   // l : thời gian thành phố bạn đang sống
                                long timezone = 25200*1000L;  // timezone là múi giờ hiện tại đang lưu
                                Date date = new Date((l*1000L) - (timezone - l_timezone*1000L));
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm aaa");
                                String time_modal = simpleDateFormat.format(date);


                                String tempModal = hourObject.getString("temp");
                                Double dbTM = Double.valueOf(tempModal);
                                String temp_modal = String.valueOf(Math.round(dbTM * 10.0) / 10.0);



                                JSONArray jsonForecastDay = hourObject.getJSONArray("weather");
                                JSONObject jsonConditionModal = jsonForecastDay.getJSONObject(0);

                                String icon_modal = jsonConditionModal.getString("icon");

                                String windSpeedModal = hourObject.getString("wind_speed");
                                Double dbWSM = Double.valueOf(windSpeedModal);
                                String windSpeed_modal = String.valueOf(Math.round(dbWSM * 3.6 * 10.0) / 10.0);

                                weatherModalArrayList.add(new HourWeatherModal(time_modal, temp_modal, icon_modal, windSpeed_modal));

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

    /**
     * Từ tọa độ nhận được từ GPS trả về tên thành phố
     * đồng thời gọi function loadWeatherWidget lấy tên thành phố đó
     * @param lat
     * @param lon
     */
    public void getLocation(double lat, double lon) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon="+ lon +"&lang=us&units=metric&appid=a465b2e54ab1d221c46f230831ad9604";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String nameCity = jsonObject.getString("name");

                            city = nameCity;
                            cityName.setText(nameCity);
                            loadWeatherWidget(nameCity);

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


    /**
     * Từ tên thành phố nhận được từ listCity SQlite trả về tọa độ vị trí
     * Từ tọa độ vị trí : 2 methods nhận getCurrentWeatherData và getWeatherDayModal
     * Để trả về thông tin thời tiết hiện tại, dự báo theo giờ
     * @param city
     */
    public void getWeatherMainActivity(String city) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=7bbb4235c9624cf8b7633806222403&q=" + city + "&days=1&aqi=yes";
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonCoordinates = jsonObject.getJSONObject("location");
                            double lon = jsonCoordinates.getDouble("lon");
                            double lat = jsonCoordinates.getDouble("lat");

                            getCurrentWeatherData(lat, lon);
                            getWeatherDayModal(lat, lon);
                            loadWeatherWidget(city);

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

    /*
     * Widget
     * Đọc thông tin thành phố chọn hiện tại
     */
    public void loadWeatherWidget(String city) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://api.weatherapi.com/v1/forecast.json?key=7bbb4235c9624cf8b7633806222403&q=" + city + "&days=1&aqi=yes";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject jsonCurrent = jsonObject.getJSONObject("current");
                            String temperature = jsonCurrent.getString("temp_c");
                            Double dbTemperature = Double.valueOf(temperature);
                            String cvTemperature = String.valueOf(dbTemperature.intValue());

                            JSONObject jsonCondition = jsonCurrent.getJSONObject("condition");

                            String status = jsonCondition.getString("text");

                            JSONObject jsonAirQuality = jsonCurrent.getJSONObject("air_quality");
                            String pm2_5 = jsonAirQuality.getString("pm2_5");
                            Double dbPm2_5 = Double.valueOf(pm2_5);
                            String cvPm2_5 = String.valueOf(Math.round(dbPm2_5));

                            // Sử dụng SharedPreferences lưu trữ và truy xuất dữ liệu dưới dạng key – value.
                            // Lưu các thông tin về nhiệt độ, tên thành phố, AQI, trạng thái hiện tại

                            SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString("temperature", cvTemperature + "°C");
                            editor.putString("city", city + " | AQI ");
                            editor.putString("aqi", cvPm2_5);
                            editor.putString("status", status);

                            editor.apply();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        requestQueue.add(stringRequest);
    }


}