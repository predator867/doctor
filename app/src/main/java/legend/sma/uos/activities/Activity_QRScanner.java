package legend.sma.uos.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

import legend.sma.uos.Capture;
import legend.sma.uos.Common;
import legend.sma.uos.R;
import legend.sma.uos.WrapContentLinearLayoutManager;
import legend.sma.uos.adapter.Adapter_ShowRecord;
import legend.sma.uos.model.Model_ShowRecord;

public class Activity_QRScanner extends AppCompatActivity {

    ImageView img_scan;
    CardView custom_actionbar, cv_medicalRecord;

    private TextView txtname, txt_number, txt_email, txt_age, txt_experience, txt_address;
    private FirebaseFirestore firestore;
    private ImageView img_logout, img_edit, imageprofile, documentPhoto;

    AppCompatButton btn_showMedicalRecord;
    private String patientDocumentID;

    ProgressDialog progressDialog;
    private String profilePic;

    RecyclerView rv_list_subjects;
    //    Adapter_Booking adapterSubjectList;
    Adapter_ShowRecord adapterSubjectList;
    ArrayList<Model_ShowRecord> modelBookingOrderArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        img_scan = findViewById(R.id.img_scan);

        custom_actionbar = findViewById(R.id.custom_actionbar);
        custom_actionbar.setVisibility(View.GONE);

        txtname = findViewById(R.id.txtname);
        txt_number = findViewById(R.id.txt_number);
        txt_email = findViewById(R.id.txt_email);
        txt_age = findViewById(R.id.txt_age);
        txt_experience = findViewById(R.id.txt_experience);
        txt_address = findViewById(R.id.txt_address);
        imageprofile = findViewById(R.id.imageprofile);

        rv_list_subjects = findViewById(R.id.list_subjects);
        modelBookingOrderArrayList = new ArrayList<>();
        rv_list_subjects.setLayoutManager(new WrapContentLinearLayoutManager(Activity_QRScanner.this, LinearLayoutManager.VERTICAL, false));
        adapterSubjectList = new Adapter_ShowRecord(getApplicationContext(), modelBookingOrderArrayList);
        rv_list_subjects.setAdapter(adapterSubjectList);

        progressDialog = new ProgressDialog(Activity_QRScanner.this);
        progressDialog.setMessage("Loading...");

        btn_showMedicalRecord = findViewById(R.id.btn_showMedicalRecord);
        btn_showMedicalRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.show();

                if (modelBookingOrderArrayList.size() > 0) {
                    modelBookingOrderArrayList.clear();
                }

                FirebaseFirestore.getInstance()
                        .collection(Common.PATIENT)
                        .document(patientDocumentID)
                        .collection(Common.LAB)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {

                                    for (QueryDocumentSnapshot query : task.getResult()) {


                                        Model_ShowRecord model_showRecord = new Model_ShowRecord();
                                        model_showRecord.setTitle(query.getString("title"));
                                        model_showRecord.setPatName(query.getString("patName"));
                                        model_showRecord.setDocName(query.getString("docName"));
                                        model_showRecord.setDate(query.getString("date"));
                                        model_showRecord.setTime(query.getString("time"));
                                        model_showRecord.setPic(query.getString("pic"));
                                        model_showRecord.setDocID(query.getId());
                                        model_showRecord.setPatId(getIntent().getStringExtra("documentId"));
                                        modelBookingOrderArrayList.add(model_showRecord);

                                    }

                                    adapterSubjectList.notifyDataSetChanged();
                                    progressDialog.dismiss();
                                } else {
                                    progressDialog.dismiss();
                                }
                            }
                        });

            }
        });

        img_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /// ini intentIntegrator
                IntentIntegrator intentIntegrator = new IntentIntegrator(Activity_QRScanner.this);

                // set prompt text
                intentIntegrator.setPrompt("For flash use volume up key");

                // set beep
                intentIntegrator.setBeepEnabled(true);

                // locked orientation
                intentIntegrator.setOrientationLocked(true);

                // set capture activity
                intentIntegrator.setCaptureActivity(Capture.class);

                // ini scan
                intentIntegrator.initiateScan();

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ini intent result
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        // check condition
        if (intentResult.getContents() != null) {

            custom_actionbar.setVisibility(View.VISIBLE);

            patientDocumentID = intentResult.getContents();
            FirebaseFirestore.getInstance()
                    .collection(Common.PATIENT)
                    .document(intentResult.getContents())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult().exists()) {

                                DocumentSnapshot document = task.getResult();

                                String name = document.getString(Common.PATIENT_NAME);
                                String age = document.getString(Common.PATIENT_AGE);
                                String number = document.getString(Common.PATIENT_NUMBER);
                                String email = document.getString(Common.PATIENT_EMAIL);
                                String address = document.getString(Common.PATIENT_ADDRESS);
                                String profilePic = document.getString(Common.PATIENT_PIC);

                                txtname.setText(name);
                                txt_number.setText(number);
                                txt_email.setText(email);
                                txt_age.setText(age);
                                txt_address.setText(address);

                                if (!profilePic.isEmpty()) {
                                    Glide.with(getApplicationContext()).load(profilePic).into(imageprofile);
                                } else {
                                    imageprofile.setImageResource(R.drawable.user_profile);
                                }


                            }
                        }
                    });

//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//
//            builder.setTitle("Result");
//
//            builder.setMessage(intentResult.getContents());
//
//            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//
//                    dialogInterface.dismiss();
//                }
//            });
//
//            builder.show();

        } else {
            Toast.makeText(getApplication(), "you did not scan anything", Toast.LENGTH_SHORT).show();
        }
    }


}