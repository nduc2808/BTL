package com.example.weather_app;


import static com.example.weather_app.MainActivity.SHARED_PREFS;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import android.widget.Toast;

/**
 * Implementation of App Widget functionality.
 */
public class WeatherWidget extends AppWidgetProvider {
    static String CLICK_ACTION = AppWidgetManager.ACTION_APPWIDGET_UPDATE;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent intent = new Intent(context, WeatherWidget.class);
        intent.setAction(CLICK_ACTION);

//        PendingIntent là 1 Intent đặc biệt, cho phép những ứng dụng khác quyền truy cập vào ứng dụng để execute một đoạn code
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

//      Lấy các thông tin từ không gian lưu trữ SharedPreferences
        SharedPreferences sharedPref = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget);

        remoteViews.setTextViewText(R.id.idWidgetTemp, sharedPref.getString("temperature", ""));
        remoteViews.setTextViewText(R.id.idWidgetCity, sharedPref.getString("city", ""));
        remoteViews.setTextViewText(R.id.idWidgetAQI, sharedPref.getString("aqi", ""));
        remoteViews.setTextViewText(R.id.idWidgetStatus, sharedPref.getString("status", ""));

        remoteViews.setOnClickPendingIntent(R.id.idWidget, pendingIntent);
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

//  update App Widget trong khoảng thời gian được định nghĩa bởi thuộc tính updatePeriodMillis
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
            Toast.makeText(context, "Widget đã được cập nhật", Toast.LENGTH_SHORT).show();
        }
    }

    // xử lý sự kiện khi click vào widget
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (action != null && CLICK_ACTION.equals(intent.getAction())) {

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            ComponentName thisAppWidget = new ComponentName(context.getPackageName(), this.getClass().getName());
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisAppWidget);

            onUpdate(context, appWidgetManager, appWidgetIds);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}