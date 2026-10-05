package com.dileesha.printxpress;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import androidx.core.app.NotificationCompat;

public class NotificationHelper {

    private static final String CHANNEL_ID = "PrintXpress_Alerts";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "PrintXpress Alerts",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private static void send(Context context, int notificationId, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(notificationId, builder.build());
        }
    }

    // Assignment Specific Triggers
    public static void sendLoginAlert(Context context) {
        send(context, 1, "SYSTEM//SECURED", "Authentication successful. Welcome back to Neo Print//Xpress.");
    }

    public static void sendPromoAlert(Context context) {
        send(context, 2, "FESTIVE PROMO ACTIVE \uD83C\uDF89", "Get 20% off all bulk business card orders and festive sticker designs this week!");
    }

    public static void sendOrderConfirmedAlert(Context context, String product) {
        send(context, 3, "ORDER CONFIRMED", "Your order for " + product + " has been received and is now processing.");
    }

    public static void sendOrderCompletedAlert(Context context, String orderId) {
        send(context, 4, "ORDER READY", "Good news! Order #" + orderId + " has been successfully completed and is ready.");
    }
}