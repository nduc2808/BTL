package com.example.weather_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherModal> weatherModalArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherModal> weatherModalArrayList) {
        this.context = context;
        this.weatherModalArrayList = weatherModalArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.weather_modal_item, parent, false);

        return new WeatherAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {
        WeatherModal modal = weatherModalArrayList.get(position);

        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(modal.getTimeMD());
            holder.timeModal.setText(output.format(t));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.temperatureModal.setText(modal.getTemperatureMD() + "°C");
        Picasso.get().load("http://".concat(modal.getIconMD())).into(holder.conditionModal);
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
