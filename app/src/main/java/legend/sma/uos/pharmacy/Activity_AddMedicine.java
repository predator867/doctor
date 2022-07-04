package legend.sma.uos.pharmacy;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import legend.sma.uos.Common;
import legend.sma.uos.R;
import legend.sma.uos.Utils;

public class Activity_AddMedicine extends AppCompatActivity {

    RoundedImageView img;
    EditText edit_phr_name, edit_med_name, edit_med_price;
    AppCompatButton btn_add;

    FirebaseFirestore firestore;
    Utils utils;
    private Context context;

    ActivityResultLauncher<String> resultLauncher;
    private Uri filePath;
    private Bitmap bitmap;
    private ByteArrayOutputStream stream;
    ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        ///////// get context
        context = Activity_AddMedicine.this;

        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(context);

        img = findViewById(R.id.img);
        imgBack = findViewById(R.id.imgBack);
        edit_phr_name = findViewById(R.id.edit_phr_name);
        edit_med_name = findViewById(R.id.edit_med_name);
        edit_med_price = findViewById(R.id.edit_med_price);
        btn_add = findViewById(R.id.btn_add);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Activity_Pharmacy.class));
            }
        });

        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEmpty()) {
                    upload(edit_phr_name, edit_med_name, edit_med_price);
                }
            }
        });


        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultLauncher.launch("image/*");
            }
        });

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

    }

    private void upload(EditText edit_phr_name, EditText edit_med_name, EditText edit_med_price) {

        if (filePath != null) {

            ProgressDialog progressDialog = new ProgressDialog(Activity_AddMedicine.this);
            progressDialog.setMessage("Uploading...");
            progressDialog.show();

            StorageReference storageReference = FirebaseStorage.getInstance().getReference("medicinePic/")
                    .child(UUID.randomUUID().toString());


            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                img.setImageBitmap(bitmap);
                stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to 50% of original image quality

            } catch (IOException e) {
                e.printStackTrace();
            }

//            BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
//            Bitmap bitmap = drawable.getBitmap();
//            stream = new ByteArrayOutputStream();
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to 50% of original image quality

            UploadTask uploadTask = storageReference.putBytes(stream.toByteArray());

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> uriTaskSubcategory = taskSnapshot.getStorage().getDownloadUrl();
                    while (!uriTaskSubcategory.isSuccessful()) ;
                    Uri downladUri1 = uriTaskSubcategory.getResult();
                    if (uriTaskSubcategory.isSuccessful()) {

                        /*String pharmacy_name;
                                String medicine_name;
                                String medicine_price;
                                String medicine_pic;*/

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("pharmacy_name", edit_phr_name.getText().toString());
                        hashMap.put("medicine_name", edit_med_name.getText().toString());
                        hashMap.put("medicine_price", edit_med_price.getText().toString());
                        hashMap.put("medicine_pic", downladUri1.toString());

                        firestore.collection(Common.PhARMACY_MEDICINE)
                                .document()
                                .set(hashMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        finish();
                                        overridePendingTransition(0, 0);
                                        startActivity(getIntent());
                                        overridePendingTransition(0, 0);

                                        Toast.makeText(Activity_AddMedicine.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d("TAG", "onFailure: " + e.getMessage());
                                    }
                                });

                    }

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

        } else {
            Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show();
        }

    }

    private boolean checkEmpty() {

        Boolean isEmpty = false;
        if (edit_phr_name.getText().toString().trim().isEmpty())
            edit_phr_name.setError("Please Pharmacy Name");
        else if (edit_med_name.getText().toString().trim().isEmpty())
            edit_med_name.setError("Please Medicine Name");
        else if (edit_med_price.getText().toString().equals(""))
            edit_med_price.setError("Enter Price");
        else isEmpty = true;
        return isEmpty;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), Activity_Pharmacy.class));
    }
}