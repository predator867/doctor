package legend.sma.uos.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import legend.sma.uos.R;
import legend.sma.uos.fragment.FragmentBooking;
import legend.sma.uos.fragment.Fragment_Account;
import legend.sma.uos.fragment.Fragment_Home;
import legend.sma.uos.fragment.Fragment_Pharmacy;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    /////////////// define var //////////////
    private BottomNavigationView bottom_navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //////////////////// ini var ///////////////
        bottom_navigation = findViewById(R.id.bottom_navigation);

        ////////////////// implement click listener //////////////
        bottom_navigation.setOnNavigationItemSelectedListener(this);

        ////////////////// set a default fragment that will open first when app start///////////
        if (savedInstanceState == null) {
            bottom_navigation.setSelectedItemId(R.id.menu_home);
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_home:

                getSupportFragmentManager().beginTransaction().replace(R.id.Frame_layout, new Fragment_Home())
                        .addToBackStack(null).commit();

                break;
            case R.id.menu_bookingConfirm:

                getSupportFragmentManager().beginTransaction().replace(R.id.Frame_layout, new FragmentBooking())
                        .addToBackStack(null).commit();

                break;
            case R.id.menu_user:

                getSupportFragmentManager().beginTransaction().replace(R.id.Frame_layout, new Fragment_Account())
                        .addToBackStack(null).commit();

                break;
            case R.id.menu_pharmacy:

                getSupportFragmentManager().beginTransaction().replace(R.id.Frame_layout, new Fragment_Pharmacy())
                        .addToBackStack(null).commit();

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}