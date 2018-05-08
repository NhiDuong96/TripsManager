package com.android.cndd.tripsmanager.viewhelper;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.android.cndd.tripsmanager.R;
import com.android.cndd.tripsmanager.view.ITripViewer;
import com.android.cndd.tripsmanager.view.TripsActivity;

public class NotificationService extends IntentService {
    private static final String TAG = "NotificationService";

    public NotificationService() {
        super(NotificationService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Bundle bundle = intent.getBundleExtra("notify");
        ITripViewer viewer = (ITripViewer) bundle.getSerializable("trip");
        int index = bundle.getInt("id");
        if(viewer == null) return;

        Intent i = new Intent(this,TripsActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        int requestID = (int) System.currentTimeMillis();
        PendingIntent pIntent = PendingIntent.getActivity(this, requestID,i, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification noti = new Notification.Builder(this)
                .setContentTitle(viewer.getTitle())
                .setContentText(viewer.getTime())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setSound(RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION))
                .build();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        assert notificationManager != null;
        notificationManager.notify(index, noti);
    }
}
