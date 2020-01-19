package com.example.geosurvey.activity;

import android.os.Bundle;

import com.example.geosurvey.R;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class TabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab_layout);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        Objects.requireNonNull(tabs.getTabAt(0)).setIcon(R.drawable.ic_person_white_24dp);
        Objects.requireNonNull(tabs.getTabAt(1)).setIcon(R.drawable.ic_satellite_white_24dp);
        Objects.requireNonNull(tabs.getTabAt(2)).setIcon(R.drawable.ic_settings_white_24dp);
    }
}