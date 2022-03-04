package com.refeed_ppb1.notificationpapb2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.style.UpdateAppearance;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public Button notifyBtn;
    public Button updateBtn;
    public Button cancelBtn;

    private NotificationManager mNotificationManager;
    private final static String NOTIF_CHANNEL_ID = BuildConfig.APPLICATION_ID;
    private final static int NOTIF_ID = 0;
    private final static String RHCOMP_URL = "https://rh-comp.blogspot.com";
    private final static String UPDATE_EVENT = "update-notif-event";

    private NotificationReceiver mNotifReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notifyBtn = findViewById(R.id.notify_btn);
        updateBtn = findViewById(R.id.update_btn);
        cancelBtn = findViewById(R.id.cancel_btn);
        mNotifReceiver = new NotificationReceiver();

        registerReceiver(mNotifReceiver, new IntentFilter(UPDATE_EVENT));

        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, "channe-name",
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(channel);
        }

        notifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateNotification();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelNotification();
            }
        });

        notifyBtn.setEnabled(true);
        updateBtn.setEnabled(false);
        cancelBtn.setEnabled(false);

    }

    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                NOTIF_CHANNEL_ID);
        builder.setContentTitle("You've been notified");
        builder.setContentText("This is notification text");
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent contentIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingContentIntent = PendingIntent.getActivity(getApplicationContext(), NOTIF_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingContentIntent);

        Intent learnMoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(RHCOMP_URL));
        PendingIntent learnMorePendingIntent = PendingIntent.getActivity(getApplicationContext(), NOTIF_ID, learnMoreIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_baseline_notifications_24, "Visit rh-comp", learnMorePendingIntent);

        Intent updateIntent = new Intent(UPDATE_EVENT);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(getApplicationContext(), NOTIF_ID, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.addAction(R.drawable.ic_baseline_notifications_24, "Update", updatePendingIntent);

        Notification notification = builder.build();
        mNotificationManager.notify(NOTIF_ID, notification);

        notifyBtn.setEnabled(false);
        updateBtn.setEnabled(true);
        cancelBtn.setEnabled(true);
    }

    private void updateNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),
                NOTIF_CHANNEL_ID);
        builder.setContentTitle("You've been notified");
        builder.setContentText("Launching birb!");
        builder.setSmallIcon(R.drawable.ic_baseline_notifications_24);
        // Diganti jadi PRIORITY_DEFAULT, kenapa?
        // Karena kalo tetap PRIORITY_HIGH semisal ada notif lain, nanti akan membuat notif baru lagi
        // kalau jadi PRIORITY_DEFAULT, nanti hanya notif yang sudah ada yang akan diubah
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Bitmap mascotBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.launch_shima_enaga);
        builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(mascotBitmap)
                            .setBigContentTitle("This notification has been updated"));

        Intent contentIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingContentIntent = PendingIntent.getActivity(getApplicationContext(), NOTIF_ID, contentIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingContentIntent);

        Notification notification = builder.build();
        mNotificationManager.notify(NOTIF_ID, notification);

        notifyBtn.setEnabled(false);
        updateBtn.setEnabled(false);
        cancelBtn.setEnabled(true);
    }

    private void cancelNotification() {
        mNotificationManager.cancel(NOTIF_ID);
        notifyBtn.setEnabled(true);
        updateBtn.setEnabled(false);
        cancelBtn.setEnabled(false);
    }

    public class NotificationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == UPDATE_EVENT)
            updateNotification();
        }
    }
}