package legend.sma.uos.fragment;

import static android.app.Activity.RESULT_OK;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

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

import de.hdodenhof.circleimageview.CircleImageView;
import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import legend.sma.uos.Common;
import legend.sma.uos.R;
import legend.sma.uos.Utils;
import legend.sma.uos.activities.Activity_QRScanner;
import legend.sma.uos.manifest.ActivityLogin;


public class Fragment_Account extends Fragment implements View.OnClickListener {

    //////////// ini var //////////////////
    private TextView txtname, txt_number, txt_email, txt_age, txt_experience, txt_address;
    private FirebaseFirestore firestore;
    private ImageView img_logout, img_edit;
    private LinearLayout layage, layexperience, layaddress, laysacnner;
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

    RelativeLayout lay_medicalHistory;

    TextView txt_status;
    ImageView img_status;
    private SwitchCompat switch_statut;

    CircleImageView user_photo;
    RelativeLayout change_photo;
    private String profilePic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__account, container, false);

        ///////////// ini var ////////////////////
        txt_status = view.findViewById(R.id.txt_status);
        img_status = view.findViewById(R.id.img_status);
        switch_statut = view.findViewById(R.id.switch_statut);
        txtname = view.findViewById(R.id.txtname);
        txt_number = view.findViewById(R.id.txt_number);
        txt_email = view.findViewById(R.id.txt_email);
        txt_age = view.findViewById(R.id.txt_age);
        txt_experience = view.findViewById(R.id.txt_experience);
        txt_address = view.findViewById(R.id.txt_address);
        img_logout = view.findViewById(R.id.img_logout);
        img_edit = view.findViewById(R.id.img_edit);
        user_photo = view.findViewById(R.id.user_photo);
        layage = view.findViewById(R.id.layage);
        layexperience = view.findViewById(R.id.layexperience);
        layaddress = view.findViewById(R.id.layaddress);
        laysacnner = view.findViewById(R.id.laysacnner);
        change_photo = view.findViewById(R.id.change_photo);


        //////////////// ini db ///////////////////
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        utils = new Utils(getContext());

        ///////// get context
        context = getContext();
        activity = (Activity) view.getContext();


        ///////////// check driver is online or not /////
        switch_statut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (switch_statut.isChecked()) {
                    // startService(new Intent(context, WaitingService.class));
                    Log.d("ACCOUNTSTATUS", "onClick: check true");
                    Common.showLoadingDialog(context);
                    changerStatus("Online");


                } else {
                    Log.d("ACCOUNTSTATUS", "onClick: Not check");
                    Common.showLoadingDialog(context);
                    changerStatus("Offline");

                }

            }
        });


        //////////////// implement clcik listener /////////////
        layaddress.setOnClickListener(this);
        layage.setOnClickListener(this);
        layexperience.setOnClickListener(this);
        user_photo.setOnClickListener(this);
        img_edit.setOnClickListener(this);
        img_logout.setOnClickListener(this);
        laysacnner.setOnClickListener(this);
        change_photo.setOnClickListener(this);

        //////////////// get profile data ///////////////
        getprofile();


        return view;
    }


    private void changerStatus(String status) {

        DocumentReference documentReference = firestore.collection(Common.DOCTOR).document(utils.getToken());
        Map<String, Object> stringObjectMap = new HashMap<>();

        if (status.equals("Online")) {
            Log.d("ACCOUNTSTATUS", "onClick: Online" + status.equals("Online"));

            stringObjectMap.put(Common.DOCTOR_ONLINE_STATUS, "Online");

            documentReference.update(stringObjectMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Log.d("ACCOUNTSTATUS", "onClick:yes isSuccessful");

                                switch_statut.setChecked(true);
                                txt_status.setText("You are online");
                                txt_status.setTextColor(getResources().getColor(R.color.teal_200));
                                img_status.setImageDrawable(getResources().getDrawable(R.drawable.onlinemark));
                                Common.hideLoadingDialog();

                            }
                        }
                    });

        } else {
            Log.d("ACCOUNTSTATUS", "onClick: Offline" + status.equals("Offline"));

            stringObjectMap.put(Common.DOCTOR_ONLINE_STATUS, "Offline");

            documentReference.update(stringObjectMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                Log.d("ACCOUNTSTATUS", "onClick:no isSuccessful");

                                switch_statut.setChecked(false);
                                txt_status.setText("You are offline");
                                txt_status.setTextColor(getResources().getColor(R.color.black));
                                img_status.setImageDrawable(getResources().getDrawable(R.drawable.offline));
                                Common.hideLoadingDialog();

                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG", "onFailure: " + e.getMessage());
                }
            });

        }


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
            case R.id.change_photo:
                imgProfile();
                break;
            case R.id.user_photo:

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_photo);
                ImageView temp_photo = dialog.findViewById(R.id.img_photo);

                if (!profilePic.isEmpty()) {
                    Glide.with(context).load(profilePic).into(temp_photo);
                    dialog.show();
                } else {
                    user_photo.setImageResource(R.drawable.user_profile);
                }

                break;
            case R.id.layage:

                break;
            case R.id.layexperience:
                addexperience();
                break;
            case R.id.layaddress:
                addAddress();
                break;
            case R.id.laysacnner:
                startActivity(new Intent(getContext(), Activity_QRScanner.class));
                break;

        }
    }

    private void addexperience() {

        Dialog dialog = new Dialog(this.getActivity());
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
                    map.put(Common.DOCTOR_EXPERIENCE, edit_update.getText().toString().trim());
                    firestore.collection(Common.DOCTOR).document(utils.getToken())
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

                                    Toast.makeText(getContext(), "Something Wrong!Try Again", Toast.LENGTH_SHORT).show();

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

    private void getprofile() {

        utils.startLoading();

        firestore.collection(Common.DOCTOR)
                .document(utils.getToken())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {

                            DocumentSnapshot document = task.getResult();

                            String name = document.getString(Common.DOCTOR_NAME);
                            String age = document.getString(Common.DOCTOR_AGE);
                            String number = document.getString(Common.DOCTOR_NUMBER);
                            String email = document.getString(Common.DOCTOR_EMAIL);
                            String address = document.getString(Common.DOCTOR_ADDRESS);
                            profilePic = document.getString(Common.DOCTOR_PIC);
                            String experience = document.getString(Common.DOCTOR_EXPERIENCE);


                            txtname.setText(name);
                            txt_number.setText(number);
                            txt_email.setText(email);
                            txt_age.setText(age);
                            txt_address.setText(address);
                            txt_experience.setText(experience);

                            if (!profilePic.isEmpty()) {
                                Glide.with(context).load(profilePic).into(user_photo);
                            } else {
                                user_photo.setImageResource(R.drawable.user_profile);
                            }

                            if (document.getString(Common.DOCTOR_ONLINE_STATUS).equals("Online")) {
                                //startService(new Intent(context, WaitingService.class));
                                switch_statut.setChecked(true);
                                txt_status.setText("You are online");
                                // txt_status.setTextColor(getContext().getResources().getColor(R.color.teal_200));
                                // img_status.setImageDrawable(getResources().getDrawable(R.drawable.onlinemark));

                            } else {

                                switch_statut.setChecked(false);
                                txt_status.setText("You are offline");
                                // txt_status.setTextColor(getContext().getResources().getColor(R.color.black));
                                // img_status.setImageDrawable(getResources().getDrawable(R.drawable.offline));

                            }


                            utils.endLoading();

                        }
                    }
                });
    }


    private void addAddress() {

        Dialog dialog = new Dialog(this.getActivity());
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
                    map.put(Common.DOCTOR_ADDRESS, edit_update.getText().toString().trim());
                    firestore.collection(Common.DOCTOR).document(utils.getToken())
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

                                    Toast.makeText(getContext(), "Something Wrong!Try Again", Toast.LENGTH_SHORT).show();

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

        Dialog dialog = new Dialog(this.getActivity());
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
                    map.put(Common.DOCTOR_EMAIL, edit_update.getText().toString().trim());
                    firestore.collection(Common.DOCTOR).document(utils.getToken())
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

                                    Toast.makeText(getContext(), "Something Wrong!Try Again", Toast.LENGTH_SHORT).show();

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

        MaterialDialog mDialog = new MaterialDialog.Builder(this.getActivity())
                .setTitle("Logout")
                .setMessage("Are you sure want to logout!")
                .setCancelable(false)
                .setPositiveButton("Logout", R.drawable.ic_baseline_logout_24, new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        utils.logout();
                        utils.logoutUserType();
                        startActivity(new Intent(getContext(), ActivityLogin.class));
                        getActivity().finish();
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
        MaterialDialog mDialog = new MaterialDialog.Builder(this.getActivity())
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
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), filePath);
                user_photo.setImageBitmap(bitmap);
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

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        if (filePath != null) {

            DocumentReference documentReference1;
            documentReference1 = firestore.collection(Common.DOCTOR).document(utils.getToken());


            StorageReference storageReference = FirebaseStorage.getInstance().getReference(Common.DOCTOR_PIC)
                    .child(documentReference1.getId());


            BitmapDrawable drawable = (BitmapDrawable) user_photo.getDrawable();
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
                        objectMap.put(Common.DOCTOR_PIC, downladUri1.toString());

                        firestore.collection(Common.DOCTOR)
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


}