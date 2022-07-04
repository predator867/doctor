package legend.sma.uos.model;

public class Model_Pharmacy {

    String pharmacy_name;
    String medicine_name;
    String medicine_price;
    String medicine_pic;
    String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPharmacy_name() {
        return pharmacy_name;
    }

    public void setPharmacy_name(String pharmacy_name) {
        this.pharmacy_name = pharmacy_name;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getMedicine_price() {
        return medicine_price;
    }

    public void setMedicine_price(String medicine_price) {
        this.medicine_price = medicine_price;
    }

    public String getMedicine_pic() {
        return medicine_pic;
    }

    public void setMedicine_pic(String medicine_pic) {
        this.medicine_pic = medicine_pic;
    }
}
