package legend.sma.uos.manifest;

import static legend.sma.uos.Common.DOCTOR;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import legend.sma.uos.Common;
import legend.sma.uos.ConnectionDetector;
import legend.sma.uos.R;
import legend.sma.uos.Utils;
import legend.sma.uos.activities.MainActivity;
import legend.sma.uos.lab.Activity_LabPerson;
import legend.sma.uos.pharmacy.Activity_Pharmacy;

public class OtpVerify extends AppCompatActivity {

    private String input_number;
    ///////////// def var /////////////
    EditText opt1, opt2, opt3, opt4, opt5, opt6;
    Button btnverify;
    TextView shownumber, resendotp, countTimer;
    //private ProgressBar progressBar;
    String verficationCode;
    String verificationID, f_name, phone, email, password, referal;
    PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private FirebaseAuth mAuth;
    FirebaseFirestore firestore;
    Context context;
    private static ConnectionDetector connectionDetector;
    //    private FirebaseFirestore firestore;
    int time = 60;

    TextView number;
    private String token;
    ImageView login_back;

    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);

        context = OtpVerify.this;

        utils = new Utils(this);

        input_number = getIntent().getStringExtra("number");

        login_back = findViewById(R.id.login_back);
        opt1 = findViewById(R.id.opt1);
        opt2 = findViewById(R.id.opt2);
        opt3 = findViewById(R.id.opt3);
        opt4 = findViewById(R.id.opt4);
        opt5 = findViewById(R.id.opt5);
        opt6 = findViewById(R.id.opt6);
        btnverify = findViewById(R.id.btn_verify);
        // progressBar = findViewById(R.id.progressBa_splash);
        shownumber = findViewById(R.id.shownumber);
        resendotp = findViewById(R.id.resendotp);
        countTimer = findViewById(R.id.countTimer);

        // progressBar.setVisibility(View.VISIBLE);
        btnverify.setVisibility(View.GONE);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        ///////// check google play services available or not before otp send to user
        if (isGooglePlayServicesAvailable(OtpVerify.this) == true) {

            sendcodetouser(getIntent().getStringExtra("number"));

        }

        resendotp.setEnabled(false);
        connectionDetector = new ConnectionDetector(context);

        shownumber.setText(getIntent().getStringExtra("number"));

        btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (connectionDetector.isConnectingToInternet()) {

                    String entercodeotp = opt1.getText().toString() +
                            opt2.getText().toString() +
                            opt3.getText().toString() +
                            opt4.getText().toString() +
                            opt5.getText().toString() +
                            opt6.getText().toString();

                    if (opt1.getText().toString().trim().isEmpty() && opt2.getText().toString().trim().isEmpty()
                            && opt3.getText().toString().trim().isEmpty() && opt4.getText().toString().trim().isEmpty()
                            && opt5.getText().toString().trim().isEmpty() && opt6.getText().toString().trim().isEmpty() || entercodeotp.length() < 6) {

                        Toast.makeText(OtpVerify.this, "Please Enter Correct OTP", Toast.LENGTH_SHORT).show();

                    } else {

                        verifycode(entercodeotp);

                        // progressBar.setVisibility(View.VISIBLE);
                        btnverify.setVisibility(View.GONE);

                    }

                } else
                    Toast.makeText(getApplicationContext(), getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();

            }
        });


        resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                time = 60;
                resendotp.setEnabled(false);

                resendVerificationCode(getIntent().getStringExtra("number"), forceResendingToken);

            }
        });

        login_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(context, OTPScreen.class));
                finish();

            }
        });

        numbertopmove();

    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {

        // progressBar.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

    private void sendcodetouser(String number) {
        Log.d("TAGCALLED", "sendcodetouser: CALLED");

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
        // OnVerificationStateChangedCallbacks
    }

    public PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    // This callback will be invoked in two situations:
                    // 1 - Instant verification. In some cases the phone number can be instantly
                    //     verified without needing to send or enter a verification code.
                    // 2 - Auto-retrieval. On some devices Google Play services can automatically
                    //     detect the incoming verification SMS and perform verification without
                    //     user action.


                }

                @Override
                public void onVerificationFailed(FirebaseException e) {
                    // This callback is invoked in an invalid request for verification is made,
                    // for instance if the the phone number format is not valid.

                    resendotp.setEnabled(true);
                    Toast.makeText(OtpVerify.this, "Failed to send OTP, Please Try Again!", Toast.LENGTH_LONG).show();

                    Log.d("TAGCALLED", "onVerificationFailed: FAILED " + e);
                    // progressBar.setVisibility(View.GONE);

                    // Show a message and update the UI
                }

                @Override
                public void onCodeSent(@NonNull String verificationId,
                                       @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    super.onCodeSent(verificationId, token);
                    // The SMS verification code has been sent to the provided phone number, we
                    // now need to ask the user to enter the code and then construct a credential
                    // by combining the code with a verification ID.
                    Log.d("TAGCALLED", "onVerificationFailed: SUCCESS");
                    Toast.makeText(context, "OTP Code sent", Toast.LENGTH_SHORT).show();
                    btnverify.setVisibility(View.VISIBLE);
                    //progressBar.setVisibility(View.GONE);
                    verficationCode = verificationId;
                    forceResendingToken = token;


                    new CountDownTimer(60000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            countTimer.setVisibility(View.VISIBLE);
                            resendotp.setVisibility(View.GONE);
                            countTimer.setText("0:" + checkDigit(time));
                            time--;
                        }

                        public void onFinish() {
                            countTimer.setVisibility(View.GONE);
                            resendotp.setVisibility(View.VISIBLE);
                            resendotp.setEnabled(true);
                        }

                    }.start();

                }
            };

    public String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }

    private void verifycode(String codeEnteredByUser) {

//        progressBar.setVisibility(View.VISIBLE);
        PhoneAuthCredential credential =
                PhoneAuthProvider.getCredential(verficationCode, codeEnteredByUser);
        // now last function that will aloww to sign
        Log.d("OTPCRED", verficationCode + "verifycode: " + codeEnteredByUser);
        Log.d("OTPCRED", "verifycode: " + credential);
        signIn(credential);

    }

    private void signIn(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if (getIntent().getStringExtra("radio").equals("Doctor")) {
                                checkUserExit(DOCTOR, Common.DOCTOR_NUMBER);
                            } else if (getIntent().getStringExtra("radio").equals("Lab")) {
                                checkUserExit(Common.LAB, Common.Lab_NUMBER);
                            } else {
                                checkUserExit(Common.PhARMACY, Common.PhARMACY_PERSON_NUMBER);
                            }

                        } else {
                            // Toast.makeText(OtpVerify.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            // progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void checkUserExit(String field_Name_of_collection_in_db, String field_Name_of_number_in_db) {

        firestore.collection(field_Name_of_collection_in_db).document(mAuth.getUid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {

                            Log.d("isSuccessful", "verifycode: " + task.isSuccessful());
                            ///////// get number and if exist or not in db ////
                            DocumentSnapshot document = task.getResult();

                            String number = document.getString(field_Name_of_number_in_db);

                            if (number != null) {

                                Log.d("numbernotnull", "verifycode: " + document.getString(field_Name_of_number_in_db));

                                if (number.equals(getIntent().getStringExtra("number"))) {

                                    Log.d("numberequal", "verifycode: " + number.equals(getIntent().getStringExtra("number")));

                                    if (getIntent().getStringExtra("radio").equals("Doctor")) {
                                        startActivity(new Intent(context, MainActivity.class));
                                        finish();

                                    } else if (getIntent().getStringExtra("radio").equals("Lab")) {
                                        startActivity(new Intent(context, Activity_LabPerson.class));
                                        finish();

                                    } else {
                                        startActivity(new Intent(context, Activity_Pharmacy.class));
                                        finish();

                                    }

                                }

                                // progressBar.setVisibility(View.GONE);
                            } else {

                                createProfile();

                            }
                        }
                    }
                });

    }

    private void createProfile() {


        if (getIntent().getStringExtra("radio").equals("Doctor")) {

            DocumentReference documentReference = firestore.collection(Common.DOCTOR).document(mAuth.getUid());

            Map<String, Object> stringObjectMap = new HashMap<>();
            stringObjectMap.put(Common.DOCTOR_ID, mAuth.getUid());
            stringObjectMap.put(Common.DOCTOR_NAME, "");
            stringObjectMap.put(Common.DOCTOR_EMAIL, "");
            stringObjectMap.put(Common.DOCTOR_AGE, "");
            stringObjectMap.put(Common.DOCTOR_EXPERIENCE, "");
            stringObjectMap.put(Common.DOCTOR_PIC, "");
            stringObjectMap.put(Common.PASSWORD, "");
            stringObjectMap.put(Common.DOCTOR_ONLINE_STATUS, "Offline");
            stringObjectMap.put(Common.DOCTOR_NUMBER, getIntent().getStringExtra("number"));

            documentReference.set(stringObjectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    utils.putToken(documentReference.getId());
                    utils.putLoginTyp("Doctor");
                    startActivity(new Intent(context, SignUp_Activity.class)
                            .putExtra("radio", getIntent().getStringExtra("radio")));
                    finish();


                    //progressBar.setVisibility(View.GONE);

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();


                        }

                    });

        } else if (getIntent().getStringExtra("radio").equals("Lab")) {

            DocumentReference documentReference = firestore.collection(Common.LAB).document(mAuth.getUid());

            Map<String, Object> stringObjectMap = new HashMap<>();
            stringObjectMap.put(Common.Lab_ID, mAuth.getUid());
            stringObjectMap.put(Common.Lab_NAME, "");
            stringObjectMap.put(Common.Lab_EMAIL, "");
            stringObjectMap.put(Common.Lab_AGE, "");
            stringObjectMap.put(Common.Lab_EXPERIENCE, "");
            stringObjectMap.put(Common.Lab_PIC, "");
            stringObjectMap.put(Common.PASSWORD, "");
            stringObjectMap.put(Common.Lab_NUMBER, getIntent().getStringExtra("number"));

            documentReference.set(stringObjectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    utils.putToken(documentReference.getId());
                    utils.putLoginTyp("Lab");
                    startActivity(new Intent(context, SignUp_Activity.class)
                            .putExtra("radio", getIntent().getStringExtra("radio")));
                    finish();

                    //progressBar.setVisibility(View.GONE);

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();


                        }

                    });

        } else {

            DocumentReference documentReference = firestore.collection(Common.PhARMACY).document(mAuth.getUid());

            Map<String, Object> stringObjectMap = new HashMap<>();
            stringObjectMap.put(Common.PhARMACY_ID, mAuth.getUid());
            stringObjectMap.put(Common.PhARMACY_PERSON_NAME, "");
            stringObjectMap.put(Common.PhARMACY_EMAIL, "");
            stringObjectMap.put(Common.PhARMACY_PERSON_AGE, "");
            stringObjectMap.put(Common.PhARMACY_PERSON_EXPERIENCE, "");
            stringObjectMap.put(Common.PhARMACY_PIC, "");
            stringObjectMap.put(Common.PASSWORD, "");
            stringObjectMap.put(Common.PhARMACY_ADDRESS, "");
            stringObjectMap.put(Common.PhARMACY_PERSON_NUMBER, getIntent().getStringExtra("number"));

            documentReference.set(stringObjectMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    utils.putToken(documentReference.getId());
                    utils.putLoginTyp("Pharmacy");
                    startActivity(new Intent(context, SignUp_Activity.class)
                            .putExtra("radio", getIntent().getStringExtra("radio")));
                    finish();

                    //progressBar.setVisibility(View.GONE);

                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();


                        }

                    });

        }


    }

    private void numbertopmove() {

        opt1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    opt2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        opt2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    opt3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        opt3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    opt4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        opt4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    opt5.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        opt5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    opt6.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    public boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(context, OTPScreen.class));

    }
}