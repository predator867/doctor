package legend.sma.uos.Fcm;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class Fcm_Channel extends Application {

    ///////// create channel when app in foregound and we want to get notification and data //////
    public static final String FCM_ID = "FCM_ID";

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    FCM_ID, "FCM_Channel", NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

        }
    }

}
