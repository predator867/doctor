package legend.sma.uos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import legend.sma.uos.R;
import legend.sma.uos.model.Model_Pharmacy;

public class Adapter_Pharmacy_Doctor extends RecyclerView.Adapter<Adapter_Pharmacy_Doctor.ViewHolder> {

    Context context;
    ArrayList<Model_Pharmacy> pharmacyArrayList;

    public Adapter_Pharmacy_Doctor(Context context, ArrayList<Model_Pharmacy> pharmacyArrayList) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_medicine_list, parent, false);
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

    }

    @Override
    public int getItemCount() {
        return pharmacyArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_phr_name, txt_med_name, txt_med_price;
        RoundedImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_phr_name = itemView.findViewById(R.id.txt_phr_name);
            txt_med_name = itemView.findViewById(R.id.txt_med_name);
            txt_med_price = itemView.findViewById(R.id.txt_med_price);
            img = itemView.findViewById(R.id.img);

        }

    }
}


