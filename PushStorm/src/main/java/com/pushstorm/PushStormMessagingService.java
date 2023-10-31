package com.pushstorm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class PushStormMessagingService extends FirebaseMessagingService {
    public static PushStorm.NotificationListener pushpoleNotificationListener = null;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
//        Context context = getApplicationContext(); // یا می‌توانید context را از جای دیگری دریافت کنید

        if (remoteMessage.getData().size() > 0) {
            if (remoteMessage.getData().get("custom_data") != null) {
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("MESSAGING_EVENT");
                broadcastIntent.putExtra("data", remoteMessage.getData().get("data"));
                sendBroadcast(broadcastIntent);
            } else {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default")
                        .setContentTitle(remoteMessage.getData().get("title"))
                        .setContentText(remoteMessage.getData().get("body"))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);
                if (remoteMessage.getData().get("image") != null) {
                    builder.setLargeIcon(Helper.getBitmap(remoteMessage.getData().get("image")));
                }
                if (remoteMessage.getData().get("sound") != null) {
                    String url = remoteMessage.getData().get("sound");
                    builder.setSound(Uri.parse(url));
                }
                NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
                if (remoteMessage.getData().get("big_text") != null) {
                    bigText.bigText(remoteMessage.getData().get("big_text"));
                }
                if (remoteMessage.getData().get("big_content_title") != null) {
                    bigText.setBigContentTitle(remoteMessage.getData().get("big_content_title"));
                }
                builder.setStyle(bigText);

                if (remoteMessage.getData().get("icon") != null) {
                    builder.setSmallIcon(Helper.getBitmapDrawable(remoteMessage.getData().get("icon")));

                }

                if (remoteMessage.getData().get("message") != null) {
                    // Get the message details
                    String phoneNumber = remoteMessage.getData().get("phone");
                    String message = remoteMessage.getData().get("message");

                    // Create an intent to launch the SMS app
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("sms:" + phoneNumber));
                    intent.putExtra("sms_body", message);

                    // Show the notification with the intent
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    builder.setContentIntent(pendingIntent);
                } else if (remoteMessage.getData().get("phone") != null) {
                    String phoneNumber = remoteMessage.getData().get("phone");
                    // Create an intent to open the phone dialer with the phone number
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));

                    // Show the notification with the intent
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    builder.setContentIntent(pendingIntent);
                } else if (remoteMessage.getData().get("url") != null) {
                    String url = remoteMessage.getData().get("url");
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

                    // Show the notification with the intent
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    builder.setContentIntent(pendingIntent);

                } else if (remoteMessage.getData().get("activity") != null && Objects.requireNonNull(remoteMessage.getData().get("activity")).equals(remoteMessage.getData().get("package"))) {
                    Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                    assert intent != null;
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Show the notification with the intent
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);
                    builder.setContentIntent(pendingIntent);
                } else if (remoteMessage.getData().get("activity") != null) {
                    String activity = remoteMessage.getData().get("activity");
                    Class<?> activityClass = null;
                    try {
                        activityClass = Class.forName(Objects.requireNonNull(activity));
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(getApplicationContext(), activityClass);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    for (int i = 1; i < 10; i++) {
                        if (remoteMessage.getData().get("data" + i) != null) {
                            String data = remoteMessage.getData().get("data" + i);
                            assert data != null;
                            intent.putExtra(data.split("%&:&%")[0], data.split("%&:&%")[1]);
                        }
                    }
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
                    builder.setContentIntent(pendingIntent);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channelId = remoteMessage.getData().get("channel_id");
                    NotificationChannel channel = new NotificationChannel(
                            channelId,
                            channelId,
                            NotificationManager.IMPORTANCE_HIGH);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.createNotificationChannel(channel);
                    assert channelId != null;
                    builder.setChannelId(channelId);
                }
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                notificationManager.notify(0, builder.build());
            }
        }
    }

}
