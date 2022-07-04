package legend.sma.uos.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import legend.sma.uos.R;
import legend.sma.uos.lab.Activity_AddMedicalRecord;
import legend.sma.uos.model.Model_ShowRecord;

public class Adapter_ShowRecord extends RecyclerView.Adapter<Adapter_ShowRecord.ViewHolder> {

    Context context;
    ArrayList<Model_ShowRecord> pharmacyArrayList;

    public Adapter_ShowRecord(Context context, ArrayList<Model_ShowRecord> pharmacyArrayList) {
        this.context = context;
        this.pharmacyArrayList = pharmacyArrayList;
    }

    public void setfilterList(ArrayList<Model_ShowRecord> filter) {
        this.pharmacyArrayList = filter;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv__show_medicine_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Model_ShowRecord model_pharmacy = pharmacyArrayList.get(position);

        holder.txt_title.setText(model_pharmacy.getTitle());
        holder.txt_patName.setText(model_pharmacy.getPatName());
        holder.txt_date.setText("Date: " + model_pharmacy.getDate() + " Time: " + model_pharmacy.getTime());
        holder.txt_docName.setText(model_pharmacy.getDocName());

        Glide.with(holder.itemView)
                .load(model_pharmacy.getPic())
                .fitCenter().into(holder.img);

        holder.btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                context.startActivity(new Intent((context), Activity_AddMedicalRecord.class)
                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("documentId", model_pharmacy.getPatId())
                        .putExtra("from", "showRecord")
                        .putExtra("docID", model_pharmacy.getDocID()));


            }
        });


    }

    @Override
    public int getItemCount() {
        return pharmacyArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_title, txt_patName, txt_docName, txt_date;
        AppCompatButton btn_view;
        RoundedImageView img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_title = itemView.findViewById(R.id.txt_title);
            txt_patName = itemView.findViewById(R.id.txt_patName);
            txt_docName = itemView.findViewById(R.id.txt_docName);
            txt_date = itemView.findViewById(R.id.txt_date);
            btn_view = itemView.findViewById(R.id.btn_view);
            img = itemView.findViewById(R.id.img);

        }

    }
}


