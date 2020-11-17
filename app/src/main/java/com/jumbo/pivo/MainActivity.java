package com.jumbo.pivo;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MenuItem;

import com.jumbo.pivo.gui.piva.fragment.SectionsPagerAdapter;
import com.jumbo.pivo.gui.piva.MenuFrag;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SectionsPagerAdapter sectionsPagerAdapter;
        //SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        //ViewPager viewPager = findViewById(R.id.view_pager);
        //viewPager.setAdapter(sectionsPagerAdapter);
        //TabLayout tabs = findViewById(R.id.tabs);
        //tabs.setupWithViewPager(viewPager);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    SectionsPagerAdapter sectionsPagerAdapter = null;
                    switch (item.getItemId()) {
                        case R.id.nav_piva:
                            Log.d(TAG, "Přepnuto na PivoMenuFragment");
                            //selectedFragment = new MenuFrag();
                            sectionsPagerAdapter = new SectionsPagerAdapter(MainActivity.this, getSupportFragmentManager());
                            ViewPager viewPager = findViewById(R.id.view_pager);
                            viewPager.setAdapter(sectionsPagerAdapter);
                            break;
                        case R.id.nav_pokuty:
                            Log.d(TAG, "Přepnuto na PokutaMenuFragment");
                            //selectedFragment = new MenuFrag();
                            break;
                        case R.id.nav_nastaveni:
                            Log.d(TAG, "Přepnuto na NastaveniMenuFragment");
                            selectedFragment = new MenuFrag();
                            break;
                    }

                    ViewPager viewPager = findViewById(R.id.view_pager);
                    viewPager.setAdapter(sectionsPagerAdapter);
                    TabLayout tabs = findViewById(R.id.tabs);
                    tabs.setupWithViewPager(viewPager);


                    //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
                }
            };


}