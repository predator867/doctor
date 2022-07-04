package legend.sma.uos.lab;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.Map;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import legend.sma.uos.Common;
import legend.sma.uos.R;
import legend.sma.uos.Utils;
import legend.sma.uos.manifest.ActivityLogin;

public class Activity_LabPersonProfile extends AppCompatActivity implements View.OnClickListener {

    private TextView txtname, txt_number, txt_email, txt_age, txt_experience, txt_address;
    private FirebaseFirestore firestore;
    private ImageView img_logout, img_edit, imageprofile, imgBack;
    private LinearLayout layage, layexperience, layaddress;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;
    Utils utils;
    private FirebaseStorage storage;
    private StorageReference storageReference;

    ByteArrayOutputStream stream;
    Bitmap bitmap;
    boolean imageUploadChecker = false;

    Context context;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lab_person_profile);

        ///////////// ini var ////////////////////

        txtname = findViewById(R.id.txtname);
        txt_number = findViewById(R.id.txt_number);
        txt_email = findViewById(R.id.txt_email);
        txt_age = findViewById(R.id.txt_age);
        txt_experience = findViewById(R.id.txt_experience);
        txt_address = findViewById(R.id.txt_address);
        img_logout = findViewById(R.id.img_logout);
        img_edit = findViewById(R.id.img_edit);
        imageprofile = findViewById(R.id.imageprofile);
        layage = findViewById(R.id.layage);
        layexperience = findViewById(R.id.layexperience);
        layaddress = findViewById(R.id.layaddress);
        imgBack = findViewById(R.id.imgBack);


        //////////////// ini db ///////////////////
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        utils = new Utils(this);

        ///////// get context
        context = Activity_LabPersonProfile.this;

        //////////////// implement clcik listener /////////////
        layaddress.setOnClickListener(this);
        layage.setOnClickListener(this);
        layexperience.setOnClickListener(this);
        imageprofile.setOnClickListener(this);
        img_edit.setOnClickListener(this);
        img_logout.setOnClickListener(this);
        imgBack.setOnClickListener(this);

        //////////////// get profile data ///////////////
        getprofile();


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_logout:
                logout();
                break;
            case R.id.img_edit:
                editemail();
                break;
            case R.id.imageprofile:
                imgProfile();
                break;
            case R.id.layage:

                break;
            case R.id.layexperience:
                addexperience();
                break;
            case R.id.layaddress:
                addAddress();
                break;
            case R.id.imgBack:
                startActivity(new Intent(getApplicationContext(), Activity_LabPerson.class));
                break;

        }
    }

    private void addexperience() {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_editname);
        AppCompatButton btn_update = dialog.findViewById(R.id.btn_update);
        EditText edit_update = dialog.findViewById(R.id.edit_update);
        AppCompatButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        TextView txt_title = dialog.findViewById(R.id.txt_title);

        edit_update.setHint("Experience");
        txt_title.setText("Update your Experience");

        dialog.show();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edit_update.getText().toString().trim().isEmpty()) {

                    Map<String, Object> map = new HashMap<>();
                    map.put(Common.Lab_EXPERIENCE, edit_update.getText().toString().trim());
                    firestore.collection(Common.LAB).document(utils.getToken())
                            .update(map).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Toast.makeText(ActivityResetPassword.this, ""+txt_newPassword, Toast.LENGTH_SHORT).show();

                                    getprofile();

                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(context, "Something Wrong!Try Again", Toast.LENGTH_SHORT).show();

                                }

                            });

                } else {
                    edit_update.setError("Field can't be Empty");
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void addAddress() {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_editname);
        AppCompatButton btn_update = dialog.findViewById(R.id.btn_update);
        EditText edit_update = dialog.findViewById(R.id.edit_update);
        AppCompatButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        TextView txt_title = dialog.findViewById(R.id.txt_title);

        edit_update.setHint("Address");
        txt_title.setText("Update your Address");

        dialog.show();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edit_update.getText().toString().trim().isEmpty()) {

                    Map<String, Object> map = new HashMap<>();
                    map.put(Common.Lab_ADDRESS, edit_update.getText().toString().trim());
                    firestore.collection(Common.LAB).document(utils.getToken())
                            .update(map).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Toast.makeText(ActivityResetPassword.this, ""+txt_newPassword, Toast.LENGTH_SHORT).show();

                                    getprofile();

                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(context, "Something Wrong!Try Again", Toast.LENGTH_SHORT).show();

                                }

                            });

                } else {
                    edit_update.setError("Field can't be Empty");
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


    }

    private void editemail() {

        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_editname);
        AppCompatButton btn_update = dialog.findViewById(R.id.btn_update);
        EditText edit_update = dialog.findViewById(R.id.edit_update);
        AppCompatButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        TextView txt_title = dialog.findViewById(R.id.txt_title);

        edit_update.setHint("Email");
        txt_title.setText("Update your Email");

        dialog.show();

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edit_update.getText().toString().trim().isEmpty()) {

                    Map<String, Object> map = new HashMap<>();
                    map.put(Common.Lab_EMAIL, edit_update.getText().toString().trim());
                    firestore.collection(Common.LAB).document(utils.getToken())
                            .update(map).
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Toast.makeText(ActivityResetPassword.this, ""+txt_newPassword, Toast.LENGTH_SHORT).show();

                                    getprofile();

                                    dialog.dismiss();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    Toast.makeText(context, "Something Wrong!Try Again", Toast.LENGTH_SHORT).show();

                                }

                            });

                } else {
                    edit_update.setError("Field can't be Empty");
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private void logout() {

        MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure want to logout!")
                .setCancelable(false)
                .setPositiveButton("Logout", R.drawable.ic_baseline_logout_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        utils.logout();
                        utils.logoutUserType();
                        startActivity(new Intent(context, ActivityLogin.class));
                        finish();
                    }
                })
                .setNegativeButton("Cancel", R.drawable.ic_baseline_cancel_presentation_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show Dialog
        mDialog.show();

    }

    private void imgProfile() {
        MaterialDialog mDialog = new MaterialDialog.Builder(this)
                .setTitle("Upload")
                .setMessage("Are you sure want to Upload Image!")
                .setCancelable(false)
                .setPositiveButton("Upload", R.drawable.ic_baseline_drive_folder_upload_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {


                        chooseImage();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", R.drawable.ic_baseline_cancel_presentation_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .build();

        // Show Dialog
        mDialog.show();
    }

    /////////////////////////  IMAGE  //////////////////
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageprofile.setImageBitmap(bitmap);
                stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream); //compress to 50% of original image quality
                imageUploadChecker = true;

                //////////// saveimg in user profile and storage
                updateimg();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void updateimg() {

        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        if (filePath != null) {

            DocumentReference documentReference1;
            documentReference1 = firestore.collection(Common.LAB).document(utils.getToken());


            StorageReference storageReference = FirebaseStorage.getInstance().getReference(Common.Lab_PIC)
                    .child(documentReference1.getId());


            BitmapDrawable drawable = (BitmapDrawable) imageprofile.getDrawable();
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

                        Map<String, Object> objectMap = new HashMap<>();
                        objectMap.put(Common.Lab_PIC, downladUri1.toString());

                        firestore.collection(Common.LAB)
                                .document(utils.getToken())
                                .update(objectMap);

                        getprofile();

                        progressDialog.dismiss();

//                        progressBar.setVisibility(android.view.View.GONE);
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


        }
    }


    private void getprofile() {

        utils.startLoading();

        firestore.collection(Common.LAB)
                .document(utils.getToken())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {

                            DocumentSnapshot document = task.getResult();

                            String name = document.getString(Common.Lab_NAME);
                            String age = document.getString(Common.Lab_AGE);
                            String number = document.getString(Common.Lab_NUMBER);
                            String email = document.getString(Common.Lab_EMAIL);
                            String address = document.getString(Common.Lab_ADDRESS);
                            String profilePic = document.getString(Common.Lab_PIC);
                            String experience = document.getString(Common.Lab_EXPERIENCE);


                            txtname.setText(name);
                            txt_number.setText(number);
                            txt_email.setText(email);
                            txt_age.setText(age);
                            txt_address.setText(address);
                            txt_experience.setText(experience);

                            if (!profilePic.isEmpty()) {
                                Glide.with(context).load(profilePic).into(imageprofile);
                            } else {
                                imageprofile.setImageResource(R.drawable.user_profile);
                            }

                            utils.endLoading();

                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), Activity_LabPerson.class));
    }
}