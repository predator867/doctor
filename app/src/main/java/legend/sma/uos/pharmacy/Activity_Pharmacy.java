package legend.sma.uos.pharmacy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import legend.sma.uos.Common;
import legend.sma.uos.R;
import legend.sma.uos.Utils;
import legend.sma.uos.adapter.Adapter_Pharmacy;
import legend.sma.uos.model.Model_Pharmacy;

public class Activity_Pharmacy extends AppCompatActivity {

    ImageView imgProfile, add_medicine;
    RecyclerView rv_med;
    ArrayList<Model_Pharmacy> pharmacyArrayList;
    Adapter_Pharmacy adapter_pharmacy;

    Context context;
    Activity activity;

    FirebaseFirestore firestore;
    Utils utils;

    SearchView searchViewCustomer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pharmacy);

        ///////// get context
        context = Activity_Pharmacy.this;

        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(context);

        searchViewCustomer = findViewById(R.id.searchViewCustomer);
        imgProfile = findViewById(R.id.imgProfile);
        add_medicine = findViewById(R.id.add_medicine);
        rv_med = findViewById(R.id.rv_med);

        pharmacyArrayList = new ArrayList<>();
        rv_med.setLayoutManager(new LinearLayoutManager(context));
        rv_med.setHasFixedSize(true);

        adapter_pharmacy = new Adapter_Pharmacy(getApplicationContext(), pharmacyArrayList);
        rv_med.setAdapter(adapter_pharmacy);


        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), Activity_PharmacyPersonProfile.class));


            }
        });

        add_medicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(), Activity_AddMedicine.class));

            }
        });

        searchViewCustomer.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {


                searchByName(newText);

                return false;
            }
        });

        /////////////  fetch added Subjects
        fetchSubjects();

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchSubjects();
                pullToRefresh.setRefreshing(false);
            }
        });


    }

    private void searchByName(String newText) {

        ArrayList<Model_Pharmacy> list_filter = new ArrayList<>();
        for (Model_Pharmacy modelInvestor : pharmacyArrayList) {

            if (modelInvestor.getMedicine_name().toLowerCase().contains(newText.toLowerCase())) {
                list_filter.add(modelInvestor);
            }

        }

        if (list_filter.isEmpty()) {
            Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show();
        } else {
            adapter_pharmacy.setfilterList(list_filter);
        }

    }

    private void fetchSubjects() {

        utils.startLoading();

        if (pharmacyArrayList.size() > 0) {
            pharmacyArrayList.clear();
        }

        firestore.collection(Common.PhARMACY_MEDICINE)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {

                            for (QueryDocumentSnapshot query : task.getResult()) {

                                /*String pharmacy_name;
                                String medicine_name;
                                String medicine_price;
                                String medicine_pic;*/

                                Model_Pharmacy model_pharmacy = new Model_Pharmacy();

                                model_pharmacy.setPharmacy_name(query.getString("pharmacy_name"));
                                model_pharmacy.setMedicine_name(query.getString("medicine_name"));
                                model_pharmacy.setMedicine_price(query.getString("medicine_price"));
                                model_pharmacy.setMedicine_pic(query.getString("medicine_pic"));
                                model_pharmacy.setId(query.getId());

                                pharmacyArrayList.add(model_pharmacy);
                            }

                            adapter_pharmacy.notifyDataSetChanged();
                            utils.endLoading();

                        } else {
                            utils.endLoading();
                        }
                    }
                });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}