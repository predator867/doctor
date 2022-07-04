package legend.sma.uos.lab;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import legend.sma.uos.Common;
import legend.sma.uos.R;

public class Activity_AddMedicalRecord extends AppCompatActivity {


    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;

    ImageView imgBack;
    AppCompatButton btn_addPic, btn_save;
    EditText edit_notes, edit_patName, edit_docName, edit_title;
    RelativeLayout lay_time, lay_calendar;
    ImageView img;
    TextView txt_date, txt_time;

    ActivityResultLauncher<String> resultLauncher;
    private Uri filePath;
    private Bitmap bitmap;
    private ByteArrayOutputStream stream;
    private String cur_time_PR;
    private String date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medical_record);

        //////////////// ini db ///////////////////
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        txt_date = findViewById(R.id.txt_date);
        txt_time = findViewById(R.id.txt_time);
        imgBack = findViewById(R.id.imgBack);
        btn_addPic = findViewById(R.id.btn_addPic);
        btn_save = findViewById(R.id.btn_save);
        edit_notes = findViewById(R.id.edit_notes);
        edit_patName = findViewById(R.id.edit_patName);
        edit_docName = findViewById(R.id.edit_docName);
        edit_title = findViewById(R.id.edit_title);
        lay_time = findViewById(R.id.lay_time);
        lay_calendar = findViewById(R.id.lay_calendar);
        img = findViewById(R.id.img);


        if (getIntent().getStringExtra("from").equals("showRecord")) {


            FirebaseFirestore.getInstance()
                    .collection(Common.PATIENT)
                    .document(getIntent().getStringExtra("documentId"))
                    .collection(Common.LAB)
                    .document(getIntent().getStringExtra("docID"))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.getResult().exists() && task.isSuccessful()) {

                                DocumentSnapshot query = task.getResult();

                                edit_title.setText(query.getString("title"));
                                edit_docName.setText(query.getString("docName"));
                                txt_date.setText("Date: " + query.getString("date"));
                                txt_time.setText("Time: " + query.getString("time"));
                                edit_patName.setText(query.getString("patName"));
                                edit_notes.setText(query.getString("notes"));

                                Glide.with(getApplicationContext())
                                        .load(query.getString("pic"))
                                        .fitCenter().into(img);

                            }
                        }
                    });


        }

        resultLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {

                        filePath = uri;

                        try {

                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                            img.setImageBitmap(bitmap);
                            stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to 50% of original image quality

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Activity_LabPerson.class));
            }
        });

        btn_addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultLauncher.launch("image/*");
            }
        });

        lay_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                //mcurrentTime.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Activity_AddMedicalRecord.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String formatedtime = "";
                        String shour = "00";
                        String smins = "00";

                        if (selectedMinute < 10) {
                            //shour = "0" + selectedHour;
                            smins = "0" + selectedMinute;

                        } else {
                            //shour = String.valueOf(selectedHour);
                            smins = String.valueOf(selectedMinute);
                        }

                        if (selectedHour < 10) {
                            shour = "0" + selectedHour;
                            // smins = "0" + selectedMinute;

                        } else {
                            shour = String.valueOf(selectedHour);
                            //  smins = String.valueOf(selectedMinute);
                        }

                        String format = "";
                        if (selectedHour > 12) {
                            format = "PM";
                        } else if (selectedHour == 00) {
                            format = "AM";
                        } else if (selectedHour == 12) {
                            format = "PM";
                        } else {
                            format = "AM";
                        }

                        txt_time.setText(shour + ":" + smins + " " + format);
                        cur_time_PR = shour + ":" + smins + " " + format;
                        // Toast.makeText(context, "" + cur_time, Toast.LENGTH_SHORT).show();

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        lay_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openDatePickerDialog(view);

            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ProgressDialog progressDialog = new ProgressDialog(Activity_AddMedicalRecord.this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();

                if (filePath != null) {

                    DocumentReference documentReference1;
                    documentReference1 = firestore.collection(Common.PATIENT).document(getIntent().getStringExtra("documentId"));


                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(Common.Lab_PIC)
                            .child(documentReference1.getId());


                    BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                    Bitmap bitmap = drawable.getBitmap();
                    stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to 50% of original image quality

                    UploadTask uploadTask = storageReference.putBytes(stream.toByteArray());

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTaskSubcategory = taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTaskSubcategory.isSuccessful()) ;
                            Uri downladUri1 = uriTaskSubcategory.getResult();
                            if (uriTaskSubcategory.isSuccessful()) {

                                DocumentReference reference = firestore.collection(Common.PATIENT)
                                        .document(getIntent().getStringExtra("documentId"))
                                        .collection(Common.LAB)
                                        .document();

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("title", edit_title.getText().toString());
                                hashMap.put("docName", edit_docName.getText().toString());
                                hashMap.put("patName", edit_patName.getText().toString());
                                hashMap.put("notes", edit_notes.getText().toString());
                                hashMap.put("date", date);
                                hashMap.put("time", cur_time_PR);
                                hashMap.put("pic", downladUri1.toString());

                                reference.set(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(Activity_AddMedicalRecord.this, "Record Added", Toast.LENGTH_SHORT).show();
                                                progressDialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.dismiss();
                                            }
                                        });

                            }
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                }
                            })
                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Uploading Data " + (int) progress + "%");
                                }
                            });


                }

            }
        });


    }

    private void openDatePickerDialog(View view) {

        Calendar cal = Calendar.getInstance();
        // Get Current Date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (v, year, monthOfYear, dayOfMonth) -> {
                    String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    txt_date.setText(selectedDate);
                    date = selectedDate;
                   /* switch (v.getId()) {
                        case R.id.txt_addDOB:
                            ((TextView) v).setText(selectedDate);

                            Log.e("DOB selected", dob);
                            break;
                    }*/
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));


        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
        datePickerDialog.show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), Activity_LabPerson.class));
    }
}