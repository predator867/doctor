package legend.sma.uos.lab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;

import legend.sma.uos.Capture;
import legend.sma.uos.Common;
import legend.sma.uos.R;

public class Activity_LabPerson extends AppCompatActivity {

    ImageView img_scan;
    CardView custom_actionbar, cv_medicalRecord;

    private TextView txtname, txt_number, txt_email, txt_age, txt_experience, txt_address;
    private FirebaseFirestore firestore;
    private ImageView img_logout, img_edit, imageprofile, imgProfile;

    AppCompatButton btn_addMedicalRecord, btn_showMedicalRecord, btn_photo;


    private String documentId;

    ByteArrayOutputStream stream;
    Bitmap bitmap;
    boolean imageUploadChecker = false;

    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private ImageView documentPhoto;

    TextView edt_disease, edt_height, edt_weight, edt_BType, edt_skin, edt_throat, edt_indigestion,
            edt_teeth, edt_ears, edt_lungs, edt_vision, edt_heart, edt_Abdomen, edt_urine, edt_BPressure;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_person);


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
        imgProfile = findViewById(R.id.imgProfile);
        btn_showMedicalRecord = findViewById(R.id.btn_showMedicalRecord);
        btn_addMedicalRecord = findViewById(R.id.btn_addMedicalRecord);

        cv_medicalRecord = findViewById(R.id.cv_medicalRecord);
        cv_medicalRecord.setVisibility(View.GONE);

        edt_disease = findViewById(R.id.edt_disease);
        edt_height = findViewById(R.id.edt_height);
        edt_weight = findViewById(R.id.edt_weight);
        edt_BType = findViewById(R.id.edt_BType);
        edt_skin = findViewById(R.id.edt_skin);
        edt_throat = findViewById(R.id.edt_throat);
        edt_indigestion = findViewById(R.id.edt_indigestion);
        edt_teeth = findViewById(R.id.edt_teeth);
        edt_ears = findViewById(R.id.edt_ears);
        edt_lungs = findViewById(R.id.edt_lungs);
        edt_vision = findViewById(R.id.edt_vision);
        edt_heart = findViewById(R.id.edt_heart);
        edt_Abdomen = findViewById(R.id.edt_Abdomen);
        edt_urine = findViewById(R.id.edt_urine);
        edt_BPressure = findViewById(R.id.edt_BPressure);
        documentPhoto = findViewById(R.id.documentPhoto);


        //////////////// ini db ///////////////////
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        ///////// click listener show medical record
        btn_showMedicalRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (documentId != null) {

                    startActivity(new Intent(getApplicationContext(), Activity_ShowMedicalRecord.class)
                            .putExtra("documentId", documentId));


                }


            }
        });

        ///////// click listener add medical record
        btn_addMedicalRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), Activity_AddMedicalRecord.class)
                        .putExtra("documentId", documentId)
                        .putExtra("from", "labPerson"));

            }
        });

        //////// click listener goto lab person profile
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), Activity_LabPersonProfile.class));

            }
        });

        ///////// click listner btn scan
        img_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /// ini intentIntegrator
                IntentIntegrator intentIntegrator = new IntentIntegrator(Activity_LabPerson.this);

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

            documentId = intentResult.getContents();

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

        } else {
            Toast.makeText(getApplication(), "you did not scan anything", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}