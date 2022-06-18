package com.example.weather_app.Hour_forecast;

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

public class HourWeatherAdapter extends RecyclerView.Adapter<HourWeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<HourWeatherModal> weatherModalArrayList;

    public HourWeatherAdapter(Context context, ArrayList<HourWeatherModal> weatherModalArrayList) {
        this.context = context;
        this.weatherModalArrayList = weatherModalArrayList;
    }

    @NonNull
    @Override
    public HourWeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.weather_modal_item, parent, false);

        return new HourWeatherAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourWeatherAdapter.ViewHolder holder, int position) {
        HourWeatherModal modal = weatherModalArrayList.get(position);
        holder.timeModal.setText(modal.getTimeMD());

        holder.temperatureModal.setText(modal.getTemperatureMD() + "°C");
        Picasso.get().load("https://openweathermap.org/img/w/".concat(modal.getIconMD() + ".png")).into(holder.conditionModal);


        holder.windSpeedModal.setText(modal.getWindSpeedMD() + " Km/h");

    }

    @Override
    public int getItemCount() {
        return weatherModalArrayList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView timeModal, temperatureModal, windSpeedModal;
        ImageView conditionModal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            timeModal = itemView.findViewById(R.id.idTimeModal);
            temperatureModal = itemView.findViewById(R.id.idTemperatureModal);
            windSpeedModal = itemView.findViewById(R.id.idWindSpeedModal);
            conditionModal = itemView.findViewById(R.id.idConditionModal);
        }
    }
}
