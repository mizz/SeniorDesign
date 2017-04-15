package com.rent_it_app.rent_it.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.rent_it_app.rent_it.ConfirmRentalActivity;
import com.rent_it_app.rent_it.HomeActivity;
import com.rent_it_app.rent_it.SignInActivity;
import com.rent_it_app.rent_it.firebase.NotificationUtils;
import com.rent_it_app.rent_it.testing.NotificationActivity;
import com.rent_it_app.rent_it.R;

import java.util.Map;

/**
 * Created by Nagoya on 2/16/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    private NotificationUtils notificationUtils;
    public static final String DATA_BROADCAST = "myData";
    String rental_id,item_name,renter_name,estimated_profit,return_date,notification_type;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO: Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated.
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage == null)
            return;


        String clickAction = remoteMessage.getNotification().getClickAction();

        if (remoteMessage.getData().size() > 0) {
            //Log.d(TAG, "I have data");
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            notification_type = remoteMessage.getData().get("notificationType").toString();
            Log.d(TAG, "Notification Type: " + notification_type);

            //if(notification_type.contentEquals("rental_request")) {

                rental_id = remoteMessage.getData().get("rentalId");
                item_name = remoteMessage.getData().get("itemName");
                renter_name = remoteMessage.getData().get("renter");
                estimated_profit = remoteMessage.getData().get("estimatedProfit");
                return_date = remoteMessage.getData().get("returnDate");


                Log.d(TAG, "profit " + estimated_profit + "return " + return_date);
            //}

            String title = remoteMessage.getNotification().getTitle();
            sendNotification(remoteMessage.getNotification().getBody(),clickAction,title);

           /* try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                //Log.d("FCM.getData():", json.toString(2));
                rental_id = json.getString("rentalId");
                //Log.d("FCM.rental_id:", rental_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
        }
        //testing if LocalBroadcastManager works
        /*Intent intent = new Intent("mydata");
        // add data
        intent.putExtra("rentalId", rental_id);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        getApplicationContext().sendBroadcast(new Intent(DATA_BROADCAST));*/

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());



            /*// play notification sound
            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationSound();


            // Notifications are shown here
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            mBuilder.setSmallIcon(R.drawable.chat_bubble_green);
            mBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
            mBuilder.setContentText(remoteMessage.getNotification().getBody());
            mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            mBuilder.setSound(alarmSound);

            Intent resultIntent = new Intent(this, ConfirmRentalActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(ConfirmRentalActivity.class);

            // Adds the Intent that starts the Activity to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int notificationID = 100;

            // notificationID allows you to update the notification later on.
            mNotificationManager.notify(notificationID, mBuilder.build());*/
        }


    }

    private void sendNotification(String messageBody,String clickAction,String title) {
        Intent intent = new Intent(clickAction);
        //if (clickAction.contentEquals("RENTAL_REQUEST")) {
            intent.putExtra("rentalId", rental_id);
            intent.putExtra("itemName", item_name);
            intent.putExtra("renter", renter_name);
            intent.putExtra("estimatedProfit", estimated_profit);
            intent.putExtra("returnDate", return_date);
            intent.putExtra("notificationType", notification_type);
        //}
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_send)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(100 /* ID of notification */, notificationBuilder.build());
    }

    /*private void handleNotification(RemoteMessage remoteMessage) {
        ///if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
            // app is in foreground, broadcast the push message
            *//*Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("message", message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);*//*

        String rental_id;

        if (remoteMessage.getData().size() > 0) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                Log.d("FCM.getData():", json.toString(2));
                rental_id = json.getString("rentalId");
                Log.d("FCM.rental_id:", rental_id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // play notification sound
        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        notificationUtils.playNotificationSound();


        // Notifications are shown here
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        mBuilder.setSmallIcon(R.drawable.chat_bubble_green);
        mBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
        mBuilder.setContentText(remoteMessage.getNotification().getBody());
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setSound(alarmSound);

        Intent resultIntent = new Intent(this, ConfirmRentalActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ConfirmRentalActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notificationID = 100;

        // notificationID allows you to update the notification later on.
        mNotificationManager.notify(notificationID, mBuilder.build());

    //}else{
        // If the app is in background, firebase itself handles the notification
    //}
    }*/

    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

        try {
            JSONObject data = json.getJSONObject("data");

            String title = data.getString("title");
            String message = data.getString("message");
            boolean isBackground = data.getBoolean("is_background");
            String imageUrl = data.getString("image");
            String timestamp = data.getString("timestamp");
            JSONObject payload = data.getJSONObject("payload");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);


            //if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            /*} else {
                // app is in background, show the notification in notification tray
                Intent resultIntent = new Intent(getApplicationContext(), NotificationActivity.class);
                resultIntent.putExtra("message", message);

                // check for image attachment
                if (TextUtils.isEmpty(imageUrl)) {
                    showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent);
                } else {
                    // image is present, show notification with image
                    showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl);
                }
            }*/
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl);
    }
}
