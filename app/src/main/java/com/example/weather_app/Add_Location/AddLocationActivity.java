package com.example.weather_app.Add_Location;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weather_app.Database.DBManager;
import com.example.weather_app.MainActivity;
import com.example.weather_app.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddLocationActivity extends AppCompatActivity {

    String city = "";
    Context context;

    private ImageView backAdd, imgAdd;
    private RecyclerView RVAddLocation;
    private TextInputEditText editCity;
    private Toolbar toolbar;


    private ArrayList<AddLocationModal> addLocationModalArrayList = new ArrayList<>();
//    private ArrayList<AddLocationModal> selectionList = new ArrayList<>();

    // Adapter là một đối tượng của một lớp cài đặt giao diện
    // Nó đóng vai trò như là một liên kết giữa một tập hợp dữ liệu và một Adapter View,
    // một đối tượng của một lớp thừa kế lớp trừu tượng AdapterView

    private AddLocationAdapter addLocationAdapter;
    DBManager database = new DBManager(AddLocationActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        toolbar = (Toolbar) findViewById(R.id.idToolbarAdd);
        setSupportActionBar(toolbar);

        backAdd = findViewById(R.id.idBackAdd);
        imgAdd = findViewById(R.id.idImgAdd);
        editCity = findViewById(R.id.idEditCityAdd);

        RVAddLocation = findViewById(R.id.idRVAddLocation);


        backAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        RVAddLocation.setLayoutManager(new LinearLayoutManager(this));
        addLocationAdapter = new AddLocationAdapter(this, database.getAll());
        RVAddLocation.setAdapter(addLocationAdapter);

        // Thực hiện thêm thành phố
        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                city = editCity.getText().toString();
                if (!isConnect()) {
                    Toast.makeText(AddLocationActivity.this, "Không thể kết nối internet", Toast.LENGTH_SHORT).show();
                } else {
                    if (city.isEmpty()) {
                        Toast.makeText(AddLocationActivity.this, "Vui lòng nhập tên thành phố", Toast.LENGTH_SHORT).show();
                    } else {
                        getWeatherListCity(city);
                    }
                }
            }
        });
    }


    // Kiểm tra kết nối internet
    private boolean isConnect() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(context.CONNECTIVITY_SERVICE);

        // đóng giao diện bàn phím khi nhập xong
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editCity.getWindowToken(), 0);

        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    /**
     * Xử lý các sự kiện liên quan đến thêm thành phố
     * Thực hiện thêm nếu đúng
     * @param city
     */
    public void getWeatherListCity(String city) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&lang=us&units=metric&appid=a465b2e54ab1d221c46f230831ad9604";
        RequestQueue requestQueue = Volley.newRequestQueue(AddLocationActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            addLocationModalArrayList.clear();
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject jsonTemp = jsonObject.getJSONObject("main");

                            String tempCurr = jsonTemp.getString("temp");
                            String tempMin = jsonTemp.getString("temp_min");
                            String tempMax = jsonTemp.getString("temp_max");
                            String cityName = jsonObject.getString("name");

                            long result = database.addCity(cityName, tempMax, tempMin, tempCurr);

                            if (result > 0) {
                                Intent intent = new Intent(AddLocationActivity.this, MainActivity.class);
                                intent.putExtra("nameCity",cityName);
                                startActivity(intent);
                            } else{
                                Toast.makeText(AddLocationActivity.this, "Thành phố đã tồn tại", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AddLocationActivity.this, "Thành phố không hợp lệ", Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(stringRequest);
    }
}