package legend.sma.uos.model;

public class Model_Booking {

    String patient_id;
    String patient_name;
    String patient_number;
    String patient_email;
    String patient_address;
    String patient_dob;
    String patient_pic;
    String teacher_document_id;
    String request_status;
    String patientFCMToken;
    String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPatientFCMToken() {
        return patientFCMToken;
    }

    public void setPatientFCMToken(String patientFCMToken) {
        this.patientFCMToken = patientFCMToken;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public String getPatient_number() {
        return patient_number;
    }

    public void setPatient_number(String patient_number) {
        this.patient_number = patient_number;
    }

    public String getPatient_email() {
        return patient_email;
    }

    public void setPatient_email(String patient_email) {
        this.patient_email = patient_email;
    }

    public String getPatient_address() {
        return patient_address;
    }

    public void setPatient_address(String patient_address) {
        this.patient_address = patient_address;
    }

    public String getPatient_dob() {
        return patient_dob;
    }

    public void setPatient_dob(String patient_dob) {
        this.patient_dob = patient_dob;
    }

    public String getPatient_pic() {
        return patient_pic;
    }

    public void setPatient_pic(String patient_pic) {
        this.patient_pic = patient_pic;
    }

    public String getTeacher_document_id() {
        return teacher_document_id;
    }

    public void setTeacher_document_id(String teacher_document_id) {
        this.teacher_document_id = teacher_document_id;
    }

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String request_status) {
        this.request_status = request_status;
    }
}
