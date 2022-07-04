package legend.sma.uos.adapter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import legend.sma.uos.Common;
import legend.sma.uos.Fcm.FCM_Notification;
import legend.sma.uos.R;
import legend.sma.uos.Utils;
import legend.sma.uos.model.Model_Booking;

public class Adapter_Booking extends RecyclerView.Adapter<Adapter_Booking.ViewHolder> {

    ArrayList<Model_Booking> modelBookingOrderArrayList;
    Context context;
    FirebaseFirestore firestore;
    Utils utils;
    private String name;
    private String cur_time_PR = "";
    private String date = "";


    public Adapter_Booking(ArrayList<Model_Booking> modelBookingOrderArrayList, Context context) {
        this.modelBookingOrderArrayList = modelBookingOrderArrayList;
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(context);

        getprofiel();
    }

    private void getprofiel() {


        firestore.collection(Common.DOCTOR)
                .document(utils.getToken())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {

                            DocumentSnapshot document = task.getResult();

                            name = document.getString(Common.DOCTOR_NAME);

                        }
                    }
                });
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_booking, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Model_Booking model = modelBookingOrderArrayList.get(position);


        holder.txt_address.setText("Address : " + model.getPatient_address() + " Year");

        holder.txtname.setText(model.getPatient_name());
        holder.txt_number.setText(model.getPatient_number());
        holder.txt_email.setText(model.getPatient_email());

        Glide.with(holder.itemView)
                .load(model.getPatient_pic())
                .fitCenter().into(holder.imageprofile);

        holder.img_moreinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.img_moreinfo.setVisibility(View.GONE);
                holder.img_hideinfo.setVisibility(View.VISIBLE);

                holder.lay_address.setVisibility(View.VISIBLE);

                holder.lay_callChat.setVisibility(View.VISIBLE);

            }
        });

        holder.img_hideinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.img_moreinfo.setVisibility(View.VISIBLE);
                holder.img_hideinfo.setVisibility(View.GONE);

                holder.lay_address.setVisibility(View.GONE);

                holder.lay_callChat.setVisibility(View.GONE);


            }
        });

        holder.laycontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String contact = "+92 340 9009191"; // use country code with your phone number
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + model.getPatient_number()));
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);

            }
        });

        holder.laychat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String contact = model.getPatient_number(); // use country code with your phone number
                String url = "https://api.whatsapp.com/send?phone=" + contact;
                try {
                    PackageManager pm = context.getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    context.startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    // Toast.makeText(MainActivity.activity, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

            }
        });

        firestore.collection(Common.DOCTOR)
                .document(model.getTeacher_document_id())
                .collection(Common.APPOINTMENT)
                .document(model.getPatient_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            Log.d("TAG T", task.getResult().getId());
                            DocumentSnapshot documentSnapshot = task.getResult();

                            if (documentSnapshot.getString(Common.REQUEST_STATUS).equals("accept")) {
                                holder.btn_accept.setClickable(false);
                                holder.btn_accept.setBackground(context.getResources().getDrawable(R.drawable.btn_silverbg));

                            }

                        }
                    }
                });

        holder.lay_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar mcurrentTime = Calendar.getInstance();
                //mcurrentTime.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
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

                        holder.txt_time.setText(shour + ":" + smins + " " + format);
                        cur_time_PR = shour + ":" + smins + " " + format;
                        // Toast.makeText(context, "" + cur_time, Toast.LENGTH_SHORT).show();

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        holder.lay_calendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar cal = Calendar.getInstance();
                // Get Current Date
                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        (v, year, monthOfYear, dayOfMonth) -> {
                            String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            holder.txt_date.setText(selectedDate);
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
        });

        holder.btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (cur_time_PR.equals("")) {
                    Toast.makeText(context, "Select Time", Toast.LENGTH_SHORT).show();
                } else if (date.equals("")) {
                    Toast.makeText(context, "Select Date", Toast.LENGTH_SHORT).show();
                } else {
                    DocumentReference documentReference = firestore.collection(Common.DOCTOR)
                            .document(model.getTeacher_document_id())
                            .collection(Common.APPOINTMENT)
                            .document(model.getPatient_id());

                    Map<String, Object> map = new HashMap<>();

                    map.put(Common.REQUEST_STATUS, "accept");
                    map.put("time", date + " : " + cur_time_PR);

                    documentReference.update(map)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {

                                    holder.btn_accept.setClickable(false);
                                    holder.btn_accept.setBackground(context.getResources().getDrawable(R.drawable.btn_silverbg));

                                    try {
                                        new FCM_Notification(
                                                model.getPatientFCMToken(),
                                                "Booking Confirm",
                                                "your online consultation booking confirm by Dr." + name,
                                                context
                                        );
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                }


            }
        });


    }


    @Override
    public int getItemCount() {
        return modelBookingOrderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_subName, txt_experienec, txt_address, txt_pr_hour_price;
        AppCompatButton btn_accept;
        LinearLayout lay_address, lay_pr_hour_price, lay_experience, laysubject;
        ImageView img_moreinfo, img_hideinfo, imageprofile;
        TextView txtname, txt_number, txt_email;
        RelativeLayout lay_callChat, laycontact, laychat;

        RelativeLayout lay_time, lay_calendar;
        TextView txt_date, txt_time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            lay_time = itemView.findViewById(R.id.lay_time);
            lay_calendar = itemView.findViewById(R.id.lay_calendar);
            txt_date = itemView.findViewById(R.id.txt_date);
            txt_time = itemView.findViewById(R.id.txt_time);

            laycontact = itemView.findViewById(R.id.laycontact);
            laychat = itemView.findViewById(R.id.laychat);

            txt_address = itemView.findViewById(R.id.txt_address);

            btn_accept = itemView.findViewById(R.id.btn_accept);
            btn_accept.setText("Confirm");

            lay_experience = itemView.findViewById(R.id.lay_experience);
            lay_address = itemView.findViewById(R.id.lay_address);
            img_moreinfo = itemView.findViewById(R.id.img_moreinfo);
            img_hideinfo = itemView.findViewById(R.id.img_hideinfo);
            imageprofile = itemView.findViewById(R.id.imageprofile);
            txtname = itemView.findViewById(R.id.txtname);
            txt_number = itemView.findViewById(R.id.txt_number);
            txt_email = itemView.findViewById(R.id.txt_email);
            lay_callChat = itemView.findViewById(R.id.lay_callChat);
            txt_experienec = itemView.findViewById(R.id.txt_experienec);


            lay_experience.setVisibility(View.GONE);
            lay_address.setVisibility(View.GONE);
            img_hideinfo.setVisibility(View.GONE);
            lay_callChat.setVisibility(View.GONE);

        }
    }
}
