package com.developer.opdmanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;

public class appointment_section extends AppCompatActivity {
    FrameLayout frameLayout;
    TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointment_section); // Layout for your activity

        // Load a fragment into the activity (Example)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.framelayout1, new AppointmentSectionFragment()) // Your fragment
                    .commit();
        }
        frameLayout = findViewById(R.id.framelayout1);
        tabLayout = findViewById(R.id.tabLayout);

        getSupportFragmentManager().beginTransaction().replace(R.id.framelayout1, new upcoming_appointment())
                .addToBackStack(null).commit();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment fragment = null;
                switch (tab.getPosition()) {
                    case 0:
                        fragment = new upcoming_appointment();
                        break;
                    case 1:
                        fragment = new completed_appointment();
                        break;
                    case 2:
                        fragment = new canceled_appointment();
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
    public void setArguments(Bundle args) {
    }
}