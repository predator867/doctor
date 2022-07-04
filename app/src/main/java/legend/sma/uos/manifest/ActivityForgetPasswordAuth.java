package legend.sma.uos.manifest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ActivityForgetPasswordAuth extends AppCompatActivity {

    ///////////////// define var ///////////////
    private EditText edit_email, phone;
    private AppCompatButton btn_continue;
    private CountryCodePicker mCountryCodePicker;
    private String completenumber;
    private FirebaseFirestore firestore;
    private Utils utils;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password_auth);

        edit_email = findViewById(R.id.edit_email);
        phone = findViewById(R.id.phone);
        btn_continue = findViewById(R.id.btn_continue);
        mCountryCodePicker = findViewById(R.id.ccp);
        imgBack = findViewById(R.id.imgBack);

        ////////////////// ini db ////////////////////
        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(this);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ActivityLogin.class));
                finish();
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEmpty()) {
                    ///////////// get complete number with country code ////////////
                    mCountryCodePicker.registerCarrierNumberEditText(phone);
                    completenumber = mCountryCodePicker.getFullNumberWithPlus().replace("", "").trim();
                    // Toast.makeText(this, ""+completenumber, Toast.LENGTH_SHORT).show();

                    /////////////// start loading/////////////

                    auth(edit_email.getText().toString().trim(), completenumber);

                }
            }
        });

    }

    private void auth(String email, String completenumber) {

        utils.startLoading();

        firestore.collection(Common.DOCTOR).whereEqualTo("email", email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (!task.getResult().isEmpty()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if (document.getString("number").equals(completenumber)) {

                                Intent intent = new Intent(ActivityForgetPasswordAuth.this, ActivityResetPassword.class);
                                intent.putExtra("documentID", document.getId());
                                startActivity(intent);
                                finish();

                                utils.endLoading();

                            } else
                                Toast.makeText(ActivityForgetPasswordAuth.this, "Number Incorrect", Toast.LENGTH_SHORT).show();
                            utils.endLoading();
                        }
                    } else
                        Toast.makeText(ActivityForgetPasswordAuth.this, "Email Incorrect", Toast.LENGTH_SHORT).show();
                    utils.endLoading();
                }
            }
        });


    }

    private boolean checkEmpty() {
        Boolean isEmpty = false;
        if (edit_email.getText().toString().trim().isEmpty())
            edit_email.setError("Please Enter Email");
        else if (phone.getText().toString().trim().isEmpty())
            phone.setError("Please Enter Password");
        else isEmpty = true;
        return isEmpty;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), ActivityLogin.class));
    }
}