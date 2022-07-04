package legend.sma.uos.manifest;

import android.content.Context;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;

import legend.sma.uos.R;

public class OTPScreen extends AppCompatActivity {

    private static final int REQUEST_CODE = 1234;
    public static AlertDialog alertDialog;
    Context context;
    private CountryCodePicker country_code;
    private EditText input_phone;
    private TextView title, send_otp;
    private String completenumber;

    RadioGroup radio_group;
    RadioButton radio_doctor, radio_lab, radio_pharmacy, radioButton;
    private String selectRadio = "Select Radio";
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_otp);

        context = OTPScreen.this;

        country_code = findViewById(R.id.country_code);
        input_phone = findViewById(R.id.input_phone);
        imgBack = findViewById(R.id.imgBack);

        send_otp = findViewById(R.id.send_otp);
        radio_group = findViewById(R.id.radio_group);
        radio_doctor = findViewById(R.id.radio_doctor);
        radio_lab = findViewById(R.id.radio_lab);
        radio_pharmacy = findViewById(R.id.radio_pharmacy);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), SignUp_Activity.class));
                finish();
            }
        });
        //////////// bank details
        radio_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                radioButton = findViewById(i);
                switch (radioButton.getId()) {
                    case R.id.radio_doctor: {
                        if (radioButton.isChecked()) {
                            radio_lab.setClickable(false);
                            radio_pharmacy.setClickable(false);
                            selectRadio = "Doctor";
                        }
                    }
                    break;
                    case R.id.radio_lab: {
                        if (radioButton.isChecked()) {
                            radio_doctor.setClickable(false);
                            radio_pharmacy.setClickable(false);
                            selectRadio = "Lab";
                        }
                    }
                    break;
                    case R.id.radio_pharmacy: {
                        if (radioButton.isChecked()) {
                            radio_doctor.setClickable(false);
                            radio_lab.setClickable(false);
                            selectRadio = "Pharmacy";
                        }
                    }
                    break;

                }
            }

        });

        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (selectRadio.equals("Select Radio")) {

                    Toast.makeText(context, "Select Radio", Toast.LENGTH_SHORT).show();

                } else if (input_phone == null && input_phone.equals("")) {
                    Toast.makeText(context, "Please Enter Number", Toast.LENGTH_SHORT).show();

                } else if (input_phone.length() < 8) {

                    Toast.makeText(context, "Please Complete Number: Hint 333 3333333", Toast.LENGTH_LONG).show();

                } else {

                    Log.d("TAG", input_phone.getText().toString());


                    ///////////// get complete number with country code ////////////
                    country_code.registerCarrierNumberEditText(input_phone);
                    completenumber = country_code.getFullNumberWithPlus().replace("", "").trim();
                    // Toast.makeText(this, ""+completenumber, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context, OtpVerify.class);
                    intent.putExtra("number", completenumber);
                    intent.putExtra("radio", selectRadio);
                    startActivity(intent);
                    finish();


                }

            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), SignUp_Activity.class));
    }
}