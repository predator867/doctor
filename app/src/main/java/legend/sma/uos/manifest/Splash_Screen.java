package legend.sma.uos.manifest;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;

import legend.sma.uos.Common;
import legend.sma.uos.R;
import legend.sma.uos.Utils;
import legend.sma.uos.activities.MainActivity;
import legend.sma.uos.lab.Activity_LabPerson;
import legend.sma.uos.pharmacy.Activity_Pharmacy;

public class Splash_Screen extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 1000;
    FirebaseAuth mAuth;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


    }

    @Override
    protected void onStart() {
        super.onStart();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                utils = new Utils(Splash_Screen.this);

                if (utils.isLoggedIn()) {

                    if (utils.getLoginTyp().equals("Doctor")) {

                        FirebaseFirestore.getInstance().collection(Common.DOCTOR)
                                .document(utils.getToken())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful() && task.getResult().exists()) {

                                            DocumentSnapshot documentSnapshot = task.getResult();

                                            if (documentSnapshot.getString(Common.DOCTOR_NAME).equals("")
                                                    || documentSnapshot.getString(Common.DOCTOR_PIC).equals("")
                                                    || documentSnapshot.getString(Common.DOCTOR_EMAIL).equals("")
                                                    || documentSnapshot.getString(Common.DOCTOR_AGE).equals("")
                                                    || documentSnapshot.getString(Common.DOCTOR_EXPERIENCE).equals("")
                                                    || documentSnapshot.getString(Common.PASSWORD).equals("")) {

                                                startActivity(new Intent(Splash_Screen.this, SignUp_Activity.class)
                                                        .putExtra("radio", "Doctor"));
                                                finish();

                                            } else {
                                                startActivity(new Intent(Splash_Screen.this, MainActivity.class));
                                                finish();
                                            }

                                        }

                                    }
                                });

                        /////////////////////
                        FirebaseMessaging.getInstance().getToken()
                                .addOnCompleteListener(new OnCompleteListener<String>() {
                                    @Override
                                    public void onComplete(@NonNull Task<String> task) {
                                        if (!task.isSuccessful()) {
                                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                                            return;
                                        }

                                        // Get new FCM registration token
                                        String token = task.getResult();

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("doctorFCMToken", task.getResult());
                                        FirebaseFirestore.getInstance().collection(Common.DOCTOR)
                                                .document(utils.getToken())
                                                .update(map);

                                    }
                                });

                    } else if (utils.getLoginTyp().equals("Lab")) {

                        FirebaseFirestore.getInstance().collection(Common.LAB)
                                .document(utils.getToken())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful() && task.getResult().exists()) {

                                            DocumentSnapshot documentSnapshot = task.getResult();

                                            if (documentSnapshot.getString(Common.Lab_NAME).equals("")
                                                    || documentSnapshot.getString(Common.Lab_PIC).equals("")
                                                    || documentSnapshot.getString(Common.Lab_EMAIL).equals("")
                                                    || documentSnapshot.getString(Common.Lab_AGE).equals("")
                                                    || documentSnapshot.getString(Common.Lab_EXPERIENCE).equals("")
                                                    || documentSnapshot.getString(Common.PASSWORD).equals("")) {

                                                startActivity(new Intent(Splash_Screen.this, SignUp_Activity.class)
                                                        .putExtra("radio", "Lab"));
                                                finish();

                                            } else {
                                                startActivity(new Intent(Splash_Screen.this, Activity_LabPerson.class));
                                                finish();
                                            }

                                        }

                                    }
                                });

                    } else {

                        FirebaseFirestore.getInstance().collection(Common.PhARMACY)
                                .document(utils.getToken())
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful() && task.getResult().exists()) {

                                            DocumentSnapshot documentSnapshot = task.getResult();

                                            if (documentSnapshot.getString(Common.PhARMACY_PERSON_NAME).equals("")
                                                    || documentSnapshot.getString(Common.PhARMACY_PIC).equals("")
                                                    || documentSnapshot.getString(Common.PhARMACY_EMAIL).equals("")
                                                    || documentSnapshot.getString(Common.PhARMACY_PERSON_AGE).equals("")
                                                    || documentSnapshot.getString(Common.PhARMACY_PERSON_EXPERIENCE).equals("")
                                                    || documentSnapshot.getString(Common.PASSWORD).equals("")) {

                                                startActivity(new Intent(Splash_Screen.this, SignUp_Activity.class)
                                                        .putExtra("radio", "Pharmacy"));
                                                finish();

                                            } else {
                                                startActivity(new Intent(Splash_Screen.this, Activity_Pharmacy.class));
                                                finish();
                                            }

                                        }

                                    }
                                });

                    }

                } else {

                    startActivity(new Intent(Splash_Screen.this, ActivityLogin.class));
                    finish();

                }
            }
        }, SPLASH_TIME_OUT);


    }
}