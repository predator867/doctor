package legend.sma.uos.Fcm;

import static legend.sma.uos.Fcm.Fcm_Channel.FCM_ID;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import legend.sma.uos.R;

public class FCMServiceReceiver extends FirebaseMessagingService {

    ////// used this class for getting forground notification and data ///////
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("TAG", "onMessageReceived: from " + remoteMessage.getFrom());

        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            ////////// create bulder
            Notification builder = new NotificationCompat.Builder(this, FCM_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.online)
                    .setColor(Color.MAGENTA)
                    .build();

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.notify(3424, builder);
        }


    }

    /////// called when msg collap
    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    //////// called when user clear app data, again app install
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        getSharedPreferences("_", MODE_PRIVATE).edit().putString("fcm_token", s).apply();
    }

    public static String getToken(Context context, String key) {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString(key, "empty");
    }

}
