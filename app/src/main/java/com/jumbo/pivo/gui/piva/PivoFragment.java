package com.jumbo.pivo.gui.piva;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jumbo.pivo.R;
import com.jumbo.pivo.TabViewPagerAdapter;
import com.jumbo.pivo.gui.MenuFrag;

public class PivoFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager firstViewPager;

    public PivoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment, container, false);

        firstViewPager = rootView.findViewById(R.id.viewpager_content);

        tabLayout = rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(firstViewPager);

        setupViewPager(firstViewPager);
        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        TabViewPagerAdapter adapter = new TabViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new MenuFrag(), "Menu");
        adapter.addFragment(new PridatHraceFrag(), "Přidat\nhráče");
        adapter.addFragment(new PridatZapasFrag(), "Přidat\nzápas");
        adapter.addFragment(new PridatPivoFrag(), "Přidat\npivo");
        adapter.addFragment(new StatistikyFrag(), "Statistiky");
        viewPager.setAdapter(adapter);
    }


}
