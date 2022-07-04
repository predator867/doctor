package legend.sma.uos;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

    public static final String MyPREFERENCES = "MyPrefs";
    public static final String Token = "Token";
    public static final String WHOLOGIN = "WHOLOGIN";
    public static final String SmsNotifi = "SmsNotifi";
    public static final String FormStatus = "FormStatus";
    final Lottiedialog lottie;//=new lottiedialog(getContext());
    public SharedPreferences sharedPreferences;


    public Utils(Context context) {
        sharedPreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        lottie = new Lottiedialog(context);
    }

    public void startLoading() {
        lottie.show();
    }

    public void endLoading() {
        lottie.dismiss();
    }


    public String getToken() {
        return sharedPreferences.getString(Token, "");
    }

    public void putToken(String token) {
        sharedPreferences.edit().putString(Token, token).commit();
    }

    public String getLoginTyp() {
        return sharedPreferences.getString(WHOLOGIN, "");
    }

    public void putLoginTyp(String token) {
        sharedPreferences.edit().putString(WHOLOGIN, token).commit();
    }

    public void putIsWatch(Boolean value) {
        sharedPreferences.edit().putBoolean(SmsNotifi, value).commit();
    }

    public Boolean getIsWatch() {
        return sharedPreferences.getBoolean(SmsNotifi, false);
    }

    public void putIsBuy(Boolean value) {
        sharedPreferences.edit().putBoolean(FormStatus, value).commit();
    }

    public Boolean getISBuy() {
        return sharedPreferences.getBoolean(FormStatus, false);
    }

    public void logoutUserType() {
        sharedPreferences.edit().putString(WHOLOGIN, "").commit();
    }

    public boolean isLoggedInUserType() {
        boolean LoggedIn = true;
        if (getLoginTyp().equals("") || getLoginTyp().isEmpty()) LoggedIn = false;
        return LoggedIn;
    }

    public void logout() {
        sharedPreferences.edit().putString(Token, "logout").commit();
    }

    public boolean isLoggedIn() {
        boolean LoggedIn = true;
        if (getToken().equals("logout") || getToken().isEmpty()) LoggedIn = false;
        return LoggedIn;
    }

}
