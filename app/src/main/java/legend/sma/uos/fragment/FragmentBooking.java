package legend.sma.uos.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import legend.sma.uos.Common;
import legend.sma.uos.R;
import legend.sma.uos.Utils;
import legend.sma.uos.WrapContentLinearLayoutManager;
import legend.sma.uos.adapter.Adapter_Booking;
import legend.sma.uos.adapter.Adapter_ConfirmBooking;
import legend.sma.uos.model.Model_Booking;

public class FragmentBooking extends Fragment {

    Context context;
    Activity activity;

    FirebaseFirestore firestore;
    Utils utils;

    RecyclerView rv_list_subjects;
    //    Adapter_Booking adapterSubjectList;
    Adapter_ConfirmBooking adapterSubjectList;
    ArrayList<Model_Booking> modelBookingOrderArrayList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking, container, false);

        rv_list_subjects = view.findViewById(R.id.list_subjects);
        rv_list_subjects.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));


        ///////// get context
        context = getContext();
        activity = (Activity) view.getContext();
        firestore = FirebaseFirestore.getInstance();

        modelBookingOrderArrayList = new ArrayList<>();
        utils = new Utils(getContext());

        /////////////  fetch added Subjects
        fetchSubjects();

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.swiperefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchSubjects();
                pullToRefresh.setRefreshing(false);
            }
        });

        return view;
    }

    private void fetchSubjects() {

        utils.startLoading();

        if (modelBookingOrderArrayList.size() > 0) {
            modelBookingOrderArrayList.clear();
        }
        firestore.collection(Common.DOCTOR)
                .document(utils.getToken())
                .collection(Common.APPOINTMENT)
                .whereEqualTo(Common.DOCTOR_ID, utils.getToken())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (!value.isEmpty()) {

                                for (DocumentSnapshot queryDocument : value.getDocuments()) {

                                    if (queryDocument.getString(Common.REQUEST_STATUS).equals("accept")) {

                                        Model_Booking modelConfirmBooking = new Model_Booking();
                                        modelConfirmBooking.setPatient_id(queryDocument.getId());
                                        modelConfirmBooking.setTeacher_document_id(queryDocument.getString(Common.DOCTOR_ID));
                                        modelConfirmBooking.setPatient_address(queryDocument.getString(Common.PATIENT_ADDRESS));
                                        modelConfirmBooking.setPatient_dob(queryDocument.getString(Common.PATIENT_AGE));
                                        modelConfirmBooking.setPatient_email(queryDocument.getString(Common.PATIENT_EMAIL));
                                        modelConfirmBooking.setPatient_name(queryDocument.getString(Common.PATIENT_NAME));
                                        modelConfirmBooking.setPatient_pic(queryDocument.getString(Common.PATIENT_PIC));
                                        modelConfirmBooking.setPatient_number(queryDocument.getString(Common.PATIENT_NUMBER));
                                        modelConfirmBooking.setRequest_status(queryDocument.getString(Common.REQUEST_STATUS));

                                        modelBookingOrderArrayList.add(modelConfirmBooking);

                                    }

                                }

                                adapterSubjectList = new Adapter_ConfirmBooking(modelBookingOrderArrayList, context);
                                rv_list_subjects.setAdapter(adapterSubjectList);
                                adapterSubjectList.notifyDataSetChanged();

                                utils.endLoading();

                            } else {
                                utils.endLoading();
                            }
                        }
                    }
                });

    }

}