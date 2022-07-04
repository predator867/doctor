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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import legend.sma.uos.Common;
import legend.sma.uos.R;
import legend.sma.uos.Utils;

public class ActivityResetPassword extends AppCompatActivity {

    private EditText edit_password, edit_passwordconfirm;
    private AppCompatButton btn_continue;
    private FirebaseFirestore firestore;
    private Utils utils;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        /////////////// ini var ////////////////
        edit_password = findViewById(R.id.edit_password);
        edit_passwordconfirm = findViewById(R.id.edit_passwordconfirm);
        btn_continue = findViewById(R.id.btn_continue);
        imgBack = findViewById(R.id.imgBack);

        ////////////////// ini db ////////////////////
        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(this);

        ////////////// btn click ///////////
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEmpty()) {
                    update(getIntent().getStringExtra("documentID"));
                }
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ActivityForgetPasswordAuth.class));
                finish();
            }
        });

    }

    private void update(String documentID) {

        utils.startLoading();

        Map<String, Object> map = new HashMap<>();
        map.put(Common.PASSWORD, edit_password.getText().toString().trim());
        firestore.collection(Common.DOCTOR).document(documentID).update(map).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Toast.makeText(ActivityResetPassword.this, ""+txt_newPassword, Toast.LENGTH_SHORT).show();

                        Toast.makeText(ActivityResetPassword.this, "Password Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ActivityResetPassword.this, ActivityLogin.class);
                        startActivity(intent);
                        finish();

                        utils.endLoading();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ActivityResetPassword.this, "Something Wrong!Try Again", Toast.LENGTH_SHORT).show();
                        utils.endLoading();

                    }

                });


    }

    private boolean checkEmpty() {
        Boolean isEmpty = false;
        if (edit_passwordconfirm.getText().toString().trim().isEmpty())
            edit_passwordconfirm.setError("Please Enter Password");
        else if (edit_password.getText().toString().trim().isEmpty())
            edit_password.setError("Please Enter Password");
        else if (edit_password.getText().toString().length() < 6) {
            edit_password.setError("Please Enter 6 characters Password");
        } else if (edit_passwordconfirm.getText().toString().length() < 6) {
            edit_passwordconfirm.setError("Please Enter 6 characters Password");
        } else if (!edit_password.getText().toString().equals(edit_passwordconfirm.getText().toString()))
            edit_passwordconfirm.setError("Passwords Don't Match");
        else isEmpty = true;
        return isEmpty;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), ActivityForgetPasswordAuth.class));
    }
}