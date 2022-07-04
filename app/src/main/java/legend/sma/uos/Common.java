package legend.sma.uos;

import android.app.ProgressDialog;
import android.content.Context;

public class Common {

    public static final String DOCTOR = "DOCTOR";
    public static final String TEACHERS_OWN_SUBJECTS = "TEACHERS_OWN_SUBJECTS";
    public static final String PATIENT = "PATIENT";
    public static final String LAB = "LAB";
    public static final String PhARMACY = "PhARMACY";
    public static final String PhARMACY_MEDICINE = "PhARMACY_MEDICINE";
    public static final String TUTOR_BOOKING_REQUEST = "TUTOR_BOOKING_REQUEST";
    public static final String APPOINTMENT = "APPOINTMENT";

    public static final String DOCTOR_ID = "teacher_document_id";
    public static final String DOCTOR_NUMBER = "number";
    public static final String DOCTOR_NAME = "name";
    public static final String DOCTOR_PIC = "doctor_pic";
    public static final String DOCTOR_EMAIL = "email";
    public static final String DOCTOR_AGE = "age";
    public static final String DOCTOR_EXPERIENCE = "experience";
    public static final String DOCTOR_ADDRESS = "address";
    public static final String DOCTOR_ONLINE_STATUS = "doctor_online_status";

    public static final String Lab_ID = "lab_document_id";
    public static final String Lab_NUMBER = "lab_number";
    public static final String Lab_NAME = "lab_name";
    public static final String Lab_PIC = "lab__pic";
    public static final String Lab_EMAIL = "lab_email";
    public static final String Lab_AGE = "lab_age";
    public static final String Lab_EXPERIENCE = "lab_experience";
    public static final String Lab_ADDRESS = "lab_address";

    public static final String PhARMACY_ID = "pharmacy_document_id";
    public static final String PhARMACY_PERSON_NUMBER = "pharmacy_number";
    public static final String PhARMACY_PERSON_NAME = "pharmacy_person_name";
    public static final String PhARMACY_PIC = "pharmacy_pic";
    public static final String PhARMACY_EMAIL = "pharmacy_email";
    public static final String PhARMACY_PERSON_AGE = "pharmacy_person_age";
    public static final String PhARMACY_PERSON_EXPERIENCE = "pharmacy_person_experience";
    public static final String PhARMACY_ADDRESS = "pharmacy_address";


    public static final String TEACHER_SUB_NAME = "sub_name";
    public static final String TEACHER_SUB_EXPERIENCE = "sub_experience";
    public static final String TEACHER_PR_HOUR_FEE = "pr_hour_fee";
    public static final String TEACHER_CLASS = "sub_class";

    public static final String PATIENT_ID = "patient_id";
    public static final String PATIENT_NAME = "patient_name";
    public static final String PATIENT_NUMBER = "patient_number";
    public static final String PATIENT_EMAIL = "patient_email";
    public static final String PATIENT_ADDRESS = "patient_address";
    public static final String PATIENT_AGE = "patient_dob";
    public static final String PATIENT_PIC = "patient_pic";
    public static final String TUTOR_CLASS = "tutor_class";

    public static final String REQUEST_STATUS = "request_status";
    public static final String PASSWORD = "password";

    public static ProgressDialog pDialog, pDialog1;

    public static void showLoadingDialog(Context mContext) {
        pDialog = new ProgressDialog(mContext);
        pDialog.setMessage(mContext.getString(R.string.please_wait));
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public static void hideLoadingDialog() {
        if (pDialog != null) {
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

}
