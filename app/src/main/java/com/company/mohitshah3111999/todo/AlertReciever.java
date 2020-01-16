package com.company.mohitshah3111999.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import static com.company.mohitshah3111999.todo.ScheduleForCurrentDayActivity.arrayList;
import static com.company.mohitshah3111999.todo.ScheduleForCurrentDayActivity.staticDescription;
import static com.company.mohitshah3111999.todo.ScheduleForCurrentDayActivity.staticTitle;

public class AlertReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context, staticTitle, staticDescription);
        NotificationCompat.Builder builder = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, builder.build());
    }
}
