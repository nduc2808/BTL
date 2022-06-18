package com.example.weather_app.Add_Location;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weather_app.Database.DBManager;
import com.example.weather_app.MainActivity;
import com.example.weather_app.R;

import java.util.ArrayList;

public class AddLocationAdapter extends RecyclerView.Adapter<AddLocationAdapter.ViewHolder> {
    private Context context;
    private ArrayList<AddLocationModal> addLocationModalArrayList;

    private AddLocationActivity addLocationActivity;

    public AddLocationAdapter(Context context, ArrayList<AddLocationModal> addLocationModalArrayList) {
        this.context = context;
        this.addLocationModalArrayList = addLocationModalArrayList;
        this.addLocationActivity = (AddLocationActivity) context;
    }

    @NonNull
    @Override
    public AddLocationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.weather_modal_add_location, parent, false);

        return new AddLocationAdapter.ViewHolder(view, addLocationActivity);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AddLocationModal modal = addLocationModalArrayList.get(position);

        holder.cityALA.setText(modal.getCityALM());

        Double tMax = Double.valueOf(modal.getTempMaxALM());
        holder.tempMaxALA.setText(tMax.intValue()+" °C | ");

        Double tMin = Double.valueOf(modal.getTempMinALM());
        holder.tempMinALA.setText(tMin.intValue()+" °C");

        Double tCurrent = Double.valueOf(modal.getTempCurrentALM());
        holder.tempCurrentALA.setText(tCurrent.intValue()+" °C");


        // Lắng nghe sự kiện khi click vào 1 modal thì gửi tên thành phố đến MainActivity
        holder.convertMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,MainActivity.class);
                intent.putExtra("nameCity",modal.getCityALM());
                context.startActivity(intent);
            }
        });

        // Khi giữ click thì hỏi các sự kiện muốn xử lý tiếp theo
        holder.convertMain.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Xác nhận xóa " + modal.getCityALM());
            builder.setIcon(R.drawable.ic_delete);
            builder.setMessage("Bạn có chắc chắn muốn xóa ?");
            builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    DBManager dbManager = new DBManager(context);

                    int result = dbManager.deleteCity(modal.getCityALM());
                    if (result > 0) {
                        Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT).show();
                        addLocationModalArrayList.remove(modal);
                        notifyDataSetChanged();
                    } else {
                        Toast.makeText(context, "Thất bại", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return addLocationModalArrayList.size();
    }
    
    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView cityALA, tempMaxALA, tempMinALA, tempCurrentALA;

        CardView convertMain;

        public ViewHolder(@NonNull View itemView, AddLocationActivity addLocationActivity) {
            super(itemView);
            convertMain = itemView.findViewById(R.id.idConvertMain);

            cityALA = itemView.findViewById(R.id.idLocationCity);
            tempMaxALA = itemView.findViewById(R.id.idTempMaxAL);
            tempMinALA = itemView.findViewById(R.id.idTempMinAL);
            tempCurrentALA = itemView.findViewById(R.id.idTempCurrent);

        }
    }
}
