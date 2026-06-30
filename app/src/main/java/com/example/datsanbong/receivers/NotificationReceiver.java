package com.example.datsanbong.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.datsanbong.PaymentActivity;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "DAT_SAN_BONG_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        String tenSan = intent.getStringExtra("tenSan");
        String gioBatDau = intent.getStringExtra("gioBatDau");

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Nhắc nhở lịch đặt sân",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Kênh hiển thị thông báo nhắc lịch đá bóng");
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }

        Intent i = new Intent(context, PaymentActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle("Sắp đến giờ ra sân rồi! ⚽")
                .setContentText("Trận đấu tại " + (tenSan != null ? tenSan : "Sân bóng") + " sẽ bắt đầu lúc " + (gioBatDau != null ? gioBatDau : "") + ".")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}