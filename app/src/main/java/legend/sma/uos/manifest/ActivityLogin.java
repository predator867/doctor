package legend.sma.uos.manifest;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;

import legend.sma.uos.Common;
import legend.sma.uos.R;
import legend.sma.uos.Utils;
import legend.sma.uos.activities.MainActivity;
import legend.sma.uos.lab.Activity_LabPerson;
import legend.sma.uos.pharmacy.Activity_Pharmacy;

public class ActivityLogin extends AppCompatActivity implements View.OnClickListener {

    ///////////////// define var ///////////////
    private EditText edit_password, phone;
    private TextView txt_forgetpassword, txt_signup, txt_close;
    private AppCompatButton btn_login;
    boolean visibility;
    private FirebaseFirestore firestore;
    private CountryCodePicker mCountryCodePicker;
    private String completenumber;
    private Utils utils;
    ImageView imgBack;

    RadioGroup radio_group;
    RadioButton radio_doctor, radio_lab, radio_pharmacy, radioButton;
    private String selectRadio = "Select Radio";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /////////////// ini var ////////////////////
        edit_password = findViewById(R.id.edit_password);
        phone = findViewById(R.id.phone);
        txt_forgetpassword = findViewById(R.id.txt_forgetpassword);
        txt_signup = findViewById(R.id.txt_signup);
        imgBack = findViewById(R.id.imgBack);
        btn_login = findViewById(R.id.btn_login);
        radio_group = findViewById(R.id.radio_group);
        radio_doctor = findViewById(R.id.radio_doctor);
        radio_pharmacy = findViewById(R.id.radio_pharmacy);
        radio_lab = findViewById(R.id.radio_lab);
        mCountryCodePicker = findViewById(R.id.ccp);

        /////////////////////// ini db ////////////////////
        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(this);

        /////////////////// implement listener ///////////////
        txt_forgetpassword.setOnClickListener(this);
        txt_signup.setOnClickListener(this);
        imgBack.setOnClickListener(this);
        btn_login.setOnClickListener(this);


        //////////// bank details
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = findViewById(i);
                switch (radioButton.getId()) {
                    case R.id.radio_doctor: {
                        if (radioButton.isChecked()) {
                            radio_pharmacy.setClickable(false);
                            radio_lab.setClickable(false);
                            selectRadio = "Doctor";
                        }
                    }
                    break;
                    case R.id.radio_lab: {
                        if (radioButton.isChecked()) {
                            radio_pharmacy.setClickable(false);
                            radio_doctor.setClickable(false);
                            selectRadio = "Lab";
                        }
                    }
                    break;
                    case R.id.radio_pharmacy: {
                        if (radioButton.isChecked()) {
                            radio_lab.setClickable(false);
                            radio_doctor.setClickable(false);
                            selectRadio = "Pharmacy";
                        }
                    }
                    break;

                }
            }

        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.txt_forgetpassword:
                startActivity(new Intent(getApplicationContext(), ActivityForgetPasswordAuth.class));
                finish();
                break;
            case R.id.txt_signup:
                if (utils.isLoggedIn()) {
                    startActivity(new Intent(getApplicationContext(), SignUp_Activity.class));
                    finish();
                } else {
                    startActivity(new Intent(getApplicationContext(), OTPScreen.class));
                    finish();
                }

                break;
            case R.id.btn_login:
                if (checkEmpty()) {

                    ///////////// get complete number with country code ////////////
                    mCountryCodePicker.registerCarrierNumberEditText(phone);
                    completenumber = mCountryCodePicker.getFullNumberWithPlus().replace("", "").trim();
                    // Toast.makeText(this, ""+completenumber, Toast.LENGTH_SHORT).show();

                    auth(completenumber, edit_password.getText().toString().trim());

                }
                break;
            case R.id.imgBack:
                finishAffinity();
                break;
        }
    }

    private void auth(String number, String password) {

        utils.startLoading();

        Log.d("TAG", "auth: " + selectRadio);

        if (selectRadio.equals("Doctor")) {
            loginUser(Common.DOCTOR, Common.DOCTOR_NUMBER, number, password);
        } else if (selectRadio.equals("Lab")) {
            loginUser(Common.LAB, Common.Lab_NUMBER, number, password);
        } else {
            loginUser(Common.PhARMACY, Common.PhARMACY_PERSON_NUMBER, number, password);

        }


    }

    private void loginUser(String user, String db_field_name_number, String number, String password) {

        Log.d("TAG", user + "auth: " + db_field_name_number);


        firestore.collection(user).whereEqualTo(db_field_name_number, number)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    if (document.getString(Common.PASSWORD).equals(password)) {
                                        utils.putToken(document.getId());

                                        if (selectRadio.equals("Doctor")) {
                                            Intent intent = new Intent(ActivityLogin.this, MainActivity.class);
                                            intent.putExtra("documentID", document.getId());
                                            startActivity(intent);
                                            finish();
                                            utils.putLoginTyp("Doctor");
                                        } else if (selectRadio.equals("Lab")) {
                                            Intent intent = new Intent(ActivityLogin.this, Activity_LabPerson.class);
                                            intent.putExtra("documentID", document.getId());
                                            startActivity(intent);
                                            finish();
                                            utils.putLoginTyp("Lab");
                                        } else {
                                            Intent intent = new Intent(ActivityLogin.this, Activity_Pharmacy.class);
                                            intent.putExtra("documentID", document.getId());
                                            startActivity(intent);
                                            finish();
                                            utils.putLoginTyp("Pharmacy");
                                        }


                                        utils.endLoading();
                                    } else
                                        utils.endLoading();
                                    edit_password.setError("Please Enter Correct Password");

                                }
                            } else
                                utils.endLoading();
                            phone.setError("Please Enter Correct Number");
                        }
                    }
                });

    }

    private boolean checkEmpty() {
        Boolean isEmpty = false;
        if (selectRadio.equals("Select Radio")) {
            Toast.makeText(this, "Select Radio", Toast.LENGTH_SHORT).show();
        } else if (phone.getText().toString().trim().isEmpty())
            phone.setError("Please Enter Email");
        else if (edit_password.getText().toString().trim().isEmpty())
            edit_password.setError("Please Enter Password");
        else isEmpty = true;
        return isEmpty;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}