package com.example.weather_app.Week_forecast;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather_app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class WeekWeatherAdapter extends RecyclerView.Adapter<WeekWeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeekWeatherModal> weekWeatherModalArrayList;

    public WeekWeatherAdapter(Context context, ArrayList<WeekWeatherModal> weekWeatherModalArrayList) {
        this.context = context;
        this.weekWeatherModalArrayList = weekWeatherModalArrayList;
    }

    @NonNull
    @Override
    public WeekWeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.weather_row_forecast, parent, false);

        return new WeekWeatherAdapter.ViewHolder(view);
    }


    // hiển thị
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeekWeatherModal modal = weekWeatherModalArrayList.get(position);

        holder.dayWeek.setText(modal.getDayWMD());
        holder.statusWeek.setText(modal.getStatusWMD());
        Picasso.get().load("https://openweathermap.org/img/w/".concat(modal.getImgStatusWMD() + ".png")).into(holder.imgStatusWeek);

        Double tMax = Double.valueOf(modal.getTempMaxWMD());
        holder.tempMax.setText(tMax.intValue()+" °C");
        Double tMin = Double.valueOf(modal.getTempMinWMD());
        holder.tempMin.setText(tMin.intValue()+" °C");
    }

    @Override
    public int getItemCount() {
        return weekWeatherModalArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView dayWeek, statusWeek, tempMax, tempMin;
        ImageView imgStatusWeek;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dayWeek = itemView.findViewById(R.id.idDayWeek);
            statusWeek = itemView.findViewById(R.id.idStatusWeek);
            tempMax = itemView.findViewById(R.id.idTempMax);
            tempMin = itemView.findViewById(R.id.idTempMin);
            imgStatusWeek = itemView.findViewById(R.id.idImgStatusWeek);
        }
    }
}
