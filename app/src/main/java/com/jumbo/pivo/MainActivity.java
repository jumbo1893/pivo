package com.jumbo.pivo;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MenuItem;

import com.jumbo.pivo.gui.piva.PivoFragment;
import com.jumbo.pivo.gui.pokuty.PokutyFragment;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;

    private static final String TAG = MainActivity.class.toString();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_piva:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.nav_pokuty:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.nav_nastaveni:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the activity toolbar
        //getSupportActionBar().hide();

        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);


        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                        navigation.setSelectedItemId(R.id.nav_piva);
                        break;
                    case 1:
                        navigation.setSelectedItemId(R.id.nav_pokuty);
                        break;
                    case 2:
                        navigation.setSelectedItemId(R.id.nav_nastaveni);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    private void setupViewPager(ViewPager viewPager) {
        BottomNavPagerAdapter adapter = new BottomNavPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PivoFragment());
        adapter.addFragment(new PokutyFragment());
        adapter.addFragment(new PivoFragment());
        viewPager.setAdapter(adapter);
    }


}