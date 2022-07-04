package legend.sma.uos.lab;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import legend.sma.uos.WrapContentLinearLayoutManager;
import legend.sma.uos.adapter.Adapter_ShowRecord;
import legend.sma.uos.model.Model_ShowRecord;

public class Activity_ShowMedicalRecord extends AppCompatActivity {

    Context context;
    Activity activity;

    FirebaseFirestore firestore;
    Utils utils;

    RecyclerView rv_list_subjects;
    //    Adapter_Booking adapterSubjectList;
    Adapter_ShowRecord adapterSubjectList;
    ArrayList<Model_ShowRecord> modelBookingOrderArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_medical_record);

        ///////// get context
        context = Activity_ShowMedicalRecord.this;
        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(this);

        rv_list_subjects = findViewById(R.id.list_subjects);
        modelBookingOrderArrayList = new ArrayList<>();
        rv_list_subjects.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        adapterSubjectList = new Adapter_ShowRecord(getApplicationContext(), modelBookingOrderArrayList);
        rv_list_subjects.setAdapter(adapterSubjectList);

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

    private void fetchSubjects() {

        if (modelBookingOrderArrayList.size() > 0) {
            modelBookingOrderArrayList.clear();
        }

        utils.startLoading();

        FirebaseFirestore.getInstance()
                .collection(Common.PATIENT)
                .document(getIntent().getStringExtra("documentId"))
                .collection(Common.LAB)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {

                            for (QueryDocumentSnapshot query : task.getResult()) {


                                Model_ShowRecord model_showRecord = new Model_ShowRecord();
                                model_showRecord.setTitle(query.getString("title"));
                                model_showRecord.setPatName(query.getString("patName"));
                                model_showRecord.setDocName(query.getString("docName"));
                                model_showRecord.setDate(query.getString("date"));
                                model_showRecord.setTime(query.getString("time"));
                                model_showRecord.setPic(query.getString("pic"));
                                model_showRecord.setDocID(query.getId());
                                model_showRecord.setPatId(getIntent().getStringExtra("documentId"));
                                modelBookingOrderArrayList.add(model_showRecord);

                            }

                            adapterSubjectList.notifyDataSetChanged();
                            utils.endLoading();
                        } else {
                            utils.endLoading();
                        }
                    }
                });


    }
}