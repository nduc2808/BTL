package com.example.weather_app.Air_Quality;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.weather_app.R;

import org.json.JSONException;
import org.json.JSONObject;

public class AirQualityActivity extends AppCompatActivity {

    public ImageView imgPrevious;
    public TextView nameCity, indexAQI, txtInfo, textContent;
    public TextView PM25, PM10, txtSO2, txtNO2, txtO3, txtCO;

    LinearLayout airQualityActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_air_quality);
        
        imgPrevious = findViewById(R.id.idPrevious);
        nameCity = findViewById(R.id.idNameCity);
        indexAQI = findViewById(R.id.idIndexAQI);
        txtInfo = findViewById(R.id.idInfo);
        textContent = findViewById(R.id.idTextContent);
        PM25 = findViewById(R.id.idPM25);
        PM10 = findViewById(R.id.idPM10);
        txtSO2 = findViewById(R.id.idSO2);
        txtNO2 = findViewById(R.id.idNO2);
        txtO3 = findViewById(R.id.idO3);
        txtCO = findViewById(R.id.idCO);

        airQualityActivity = findViewById(R.id.idAirQualityActivity);

        Intent intent = getIntent();
        String city = intent.getStringExtra("name");
        getAirQuality(city);

        imgPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    /**
     * Trả về các thông tin liên quan đến chỉ số chất lượng không khí
     * @param data
     */
    private void getAirQuality(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(AirQualityActivity.this);
        String url = "http://api.weatherapi.com/v1/forecast.json?key=7bbb4235c9624cf8b7633806222403&q=" + data + "&days=1&aqi=yes";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            airQualityActivity.setVisibility(View.VISIBLE);
                            JSONObject jsonObject = new JSONObject(response);

                            JSONObject jsonLocation = jsonObject.getJSONObject("location");
                            String name = jsonLocation.getString("name");
                            nameCity.setText(name);

                            JSONObject jsonCurrent = jsonObject.getJSONObject("current");


                            JSONObject jsonAirQuality = jsonCurrent.getJSONObject("air_quality");


                            String index = jsonAirQuality.getString("pm2_5");
                            Double dbIndex = Double.valueOf(index);
                            String cvIndex = String.valueOf(Math.round(dbIndex));
                            indexAQI.setText(cvIndex);

                            if (0 <= dbIndex && dbIndex <= 19) {
                                txtInfo.setText("Tuyệt vời");
                                textContent.setText("Chất lượng không khí lý tưởng cho hầu hết đối tượng; hãy tận hưởng các hoạt động ngoài trời bình thường.");
                            } else if (20 <= dbIndex && dbIndex <= 49) {
                                txtInfo.setText("Vừa phải");
                                textContent.setText("Chất lượng không khí ở mức chấp nhận được đối với hầu hết đối tượng. Tuy nhiên, ở các nhóm đối tượng nhạy cảm có thể xuất hiện các triệu chứng từ nhẹ đến trung bình nếu tiếp xúc quá lâu.");
                            } else if (50 <= dbIndex && dbIndex <= 99) {
                                txtInfo.setText("Xấu");
                                textContent.setText("Không khí đã đạt mức ô nhiễm cao và không phù hợp với các nhóm đối tượng nhạy cảm. Hãy giảm thời gian ở bên ngoài nếu cơ thể xuất hiện các triệu chứng như khó thở hay ngứa cổ.");
                            } else if (100 <= dbIndex && dbIndex <= 149) {
                                txtInfo.setText("Có hại");
                                textContent.setText("Các nhóm đối tượng nhạy cảm có thể cảm nhận được tác động đến sức khỏe ngay lập tức. Các đối tượng khỏe mạnh có thể gặp tình trạng khó thở và ngứa cổ nếu tiếp xúc lâu. Hãy giới hạn hoạt động ngoài trời.");
                            } else if (150 <= dbIndex && dbIndex <= 249) {
                                txtInfo.setText("Rất có hại");
                                textContent.setText("Các nhóm đối tượng nhạy cảm sẽ cảm nhận được tác động đến sức khỏe ngay lập tức và nên tránh hoạt động ngoài trời. Các đối tượng khỏe mạnh nhiều khả năng sẽ gặp tình trạng khó thở và ngứa cổ, hãy cân nhắc việc ở trong nhà và dời lịch cho các hoạt động ngoài trời.");
                            } else {
                                txtInfo.setText("Nguy hiểm");
                                textContent.setText("Mọi tiếp xúc với không khí, dù chỉ vài phút, cũng có thể dẫn đến tác động nghiêm trọng đến sức khỏe đối với mọi đối tượng. Hãy tránh các hoạt động ngoài trời.");
                            }


                            String pm25 = jsonAirQuality.getString("pm2_5");
                            Double dbPm25 = Double.valueOf(pm25);
                            String cvPm25 = String.valueOf(Math.round(dbPm25 * 10.0) / 10.0);
                            PM25.setText(cvPm25);

                            String pm10 = jsonAirQuality.getString("pm10");
                            Double dbPm10 = Double.valueOf(pm10);
                            String cvPm10 = String.valueOf(Math.round(dbPm10 * 10.0) / 10.0);
                            PM10.setText(cvPm10);


                            String so2 = jsonAirQuality.getString("so2");
                            Double dbSo2 = Double.valueOf(so2);
                            String cvSo2 = String.valueOf(Math.round(dbSo2 * 10.0) / 10.0);
                            txtSO2.setText(cvSo2);


                            String no2 = jsonAirQuality.getString("no2");
                            Double dbNo2 = Double.valueOf(no2);
                            String cvNo2 = String.valueOf(Math.round(dbNo2 * 10.0) / 10.0);
                            txtNO2.setText(cvNo2);


                            String o3 = jsonAirQuality.getString("o3");
                            Double dbO3 = Double.valueOf(o3);
                            String cvO3 = String.valueOf(Math.round(dbO3 * 10.0) / 10.0);
                            txtO3.setText(cvO3);


                            String co = jsonAirQuality.getString("co");
                            Double dbCo = Double.valueOf(co);
                            String cvCo = String.valueOf(Math.round(dbCo * 10.0) / 10.0);
                            txtCO.setText(cvCo);


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