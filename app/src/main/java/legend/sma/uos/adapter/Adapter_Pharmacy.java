package legend.sma.uos.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import legend.sma.uos.Common;
import legend.sma.uos.R;
import legend.sma.uos.model.Model_Pharmacy;

public class Adapter_Pharmacy extends RecyclerView.Adapter<Adapter_Pharmacy.ViewHolder> {

    Context context;
    ArrayList<Model_Pharmacy> pharmacyArrayList;

    public Adapter_Pharmacy(Context context, ArrayList<Model_Pharmacy> pharmacyArrayList) {
        this.context = context;
        this.pharmacyArrayList = pharmacyArrayList;
    }

    public void setfilterList(ArrayList<Model_Pharmacy> filter) {
        this.pharmacyArrayList = filter;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_medicine_list_doctor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model_Pharmacy model_pharmacy = pharmacyArrayList.get(position);

        holder.txt_phr_name.setText(model_pharmacy.getPharmacy_name());
        holder.txt_med_name.setText(model_pharmacy.getMedicine_name());
        holder.txt_med_price.setText(model_pharmacy.getMedicine_price() + " pkr");

        Glide.with(holder.itemView)
                .load(model_pharmacy.getMedicine_pic())
                .fitCenter().into(holder.img);

        Log.d("TAG", "onBindViewHolder: "+model_pharmacy.getId());
        holder.dellItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore.getInstance()
                        .collection(Common.PhARMACY_MEDICINE)
                        .document(model_pharmacy.getId())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                int actualPosition = holder.getAdapterPosition();
                                pharmacyArrayList.remove(actualPosition);
                                notifyItemRemoved(actualPosition);
                                notifyItemRangeChanged(actualPosition, pharmacyArrayList.size());
                                Toast.makeText(context, "item deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("TAG", "onFailure: " + e.getMessage());
                            }
                        });

            }
        });

    }

    @Override
    public int getItemCount() {
        return pharmacyArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_phr_name, txt_med_name, txt_med_price;
        RoundedImageView img;
        ImageView dellItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_phr_name = itemView.findViewById(R.id.txt_phr_name);
            txt_med_name = itemView.findViewById(R.id.txt_med_name);
            txt_med_price = itemView.findViewById(R.id.txt_med_price);
            img = itemView.findViewById(R.id.img);
            dellItem = itemView.findViewById(R.id.dellItem);

        }

    }
}


