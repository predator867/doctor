package legend.sma.uos.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

import legend.sma.uos.Common;
import legend.sma.uos.R;
import legend.sma.uos.SliderAdapterExample;
import legend.sma.uos.SliderItem;
import legend.sma.uos.Utils;
import legend.sma.uos.WrapContentLinearLayoutManager;
import legend.sma.uos.adapter.Adapter_Booking;
import legend.sma.uos.model.Model_Booking;


public class Fragment_Home extends Fragment {

    TextView txt_name;

    Context context;
    Activity activity;

    FirebaseFirestore firestore;

    RecyclerView rv_list_subjects;
    Adapter_Booking adapterSubjectList;
    ArrayList<Model_Booking> modelBookingOrderArrayList;

    Utils utils;


    private SliderAdapterExample adapterSlider;
    private SliderView sliderView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment__home, container, false);

        txt_name = view.findViewById(R.id.txt_name);
        rv_list_subjects = view.findViewById(R.id.list_subjects);
        sliderView = view.findViewById(R.id.image_slider);
        rv_list_subjects.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        ///////// get context
        context = getContext();
        activity = (Activity) view.getContext();
        firestore = FirebaseFirestore.getInstance();
        utils = new Utils(getContext());

        modelBookingOrderArrayList = new ArrayList<>();


        firestore.collection(Common.DOCTOR)
                .document(utils.getToken())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (value.exists()) {

                                txt_name.setText(value.getString(Common.DOCTOR_NAME));
                            }
                        }
                    }
                });

        /////////////  fetch added Subjects
        fetchSubjects();

        /////////// get images from firebase ////////////
        getSliderImage();


        return view;
    }

    private void getSliderImage() {

        utils.startLoading();

        adapterSlider = new SliderAdapterExample(this.getContext());

        sliderView.setSliderAdapter(adapterSlider);
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(3);
        sliderView.setAutoCycle(true);

        sliderView.startAutoCycle();

        List<SliderItem> sliderItemList = new ArrayList<>();

        firestore.collection("Gallery").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                SliderItem sliderItem = new SliderItem();
                                sliderItem.setImageUrl(document.getString("path"));
//                                Model_Image model_image = new Model_Image(
//                                        document.getId(),
//                                        document.getString("path")
//                                );
                                sliderItemList.add(sliderItem);
                            }

                            adapterSlider.renewItems(sliderItemList);

                            utils.endLoading();

//                            Adapter_Image adapter_image = new Adapter_Image(ActivityGallery.this, list);
//                            recyclerView.setAdapter(adapter_image);
                        }
                    }
                });

    }


    private void fetchSubjects() {

        utils.startLoading();

        if (modelBookingOrderArrayList.size() > 0) {
            modelBookingOrderArrayList.clear();
        }

        firestore.collection(Common.DOCTOR)
                .document(utils.getToken())
                .collection(Common.APPOINTMENT)
                .whereEqualTo(Common.REQUEST_STATUS, "pending")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error == null) {
                            if (!value.isEmpty()) {

                                for (DocumentSnapshot queryDocument : value.getDocuments()) {

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
                                    modelConfirmBooking.setPatientFCMToken(queryDocument.getString("patientFCMToken"));
                                    modelConfirmBooking.setDate(queryDocument.getString("date"));

                                    modelBookingOrderArrayList.add(modelConfirmBooking);


                                }

                                adapterSubjectList = new Adapter_Booking(modelBookingOrderArrayList, context);
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