package com.developer.opdmanager;
import android.os.Bundle;
import android.widget.FrameLayout;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class doctor_dashboard extends AppCompatActivity {
    FrameLayout frameLayout;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_dashboard);
        frameLayout = findViewById(R.id.framelayout1);
        tabLayout = findViewById(R.id.tablayout);

        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout1, new DoctorHome())
                .addToBackStack(null).commit();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new DoctorHome();
                        break;
                    case 1:
                        fragment = new DoctorAppointment();
                        break;
                    case 2:
                        fragment = new DoctorProfile();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout1, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}