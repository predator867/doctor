package legend.sma.uos.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import legend.sma.uos.R;
import legend.sma.uos.model.Model_Booking;

public class Adapter_ConfirmBooking extends RecyclerView.Adapter<Adapter_ConfirmBooking.ViewHolder> {

    ArrayList<Model_Booking> modelBookingOrderArrayList;
    Context context;
    FirebaseFirestore firestore;


    public Adapter_ConfirmBooking(ArrayList<Model_Booking> modelBookingOrderArrayList, Context context) {
        this.modelBookingOrderArrayList = modelBookingOrderArrayList;
        this.context = context;
        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_academy, parent, false);

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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


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
            btn_accept.setVisibility(View.GONE);

        }
    }
}
