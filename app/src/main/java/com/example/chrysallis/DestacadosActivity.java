package com.example.chrysallis;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import com.example.chrysallis.adapters.EventoAdapter;
import com.example.chrysallis.adapters.ViewPagerAdapter;
import com.example.chrysallis.classes.Evento;
import com.example.chrysallis.classes.Socio;
import com.example.chrysallis.components.languageManager;
import com.example.chrysallis.fragments.FragmentChat;
import com.example.chrysallis.fragments.FragmentExplore;
import com.example.chrysallis.fragments.FragmentHome;
import com.example.chrysallis.fragments.FragmentProfile;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;

public class DestacadosActivity extends AppCompatActivity {
    private MenuItem prevMenuItem;

    private FragmentHome homeFragment;
    private FragmentProfile profileFragment;
    private FragmentExplore exploreFragment;
    private FragmentChat chatFragment;
    private ViewPager viewPager;
    private int pageAnterior;
    private static ArrayList<Fragment> fragments = new ArrayList<>();
    private static BottomNavigationView navView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_destacados);
        //Initializing viewPager
        viewPager = findViewById(R.id.viewpager);
        navView = findViewById(R.id.nav_view);

        //cambia el maximo de paginas en que se eliminan las vistas
        viewPager.setOffscreenPageLimit(5); //!importante

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    navView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                navView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = navView.getMenu().getItem(position);


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        Intent intent = getIntent();
        Socio socio = (Socio)intent.getSerializableExtra("socio");
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeFragment=new FragmentHome(socio);
        profileFragment=new FragmentProfile(socio);
        exploreFragment=new FragmentExplore(socio);
        chatFragment=new FragmentChat(socio);
        fragments.add(homeFragment);
        fragments.add(profileFragment);
        fragments.add(exploreFragment);
        fragments.add(chatFragment);
        adapter.addFragments(fragments);
        viewPager.setAdapter(adapter);
    }



    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fm = getSupportFragmentManager();

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    pageAnterior = viewPager.getCurrentItem();
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_profile:
                    pageAnterior = viewPager.getCurrentItem();
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_explore:
                    pageAnterior = viewPager.getCurrentItem();
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_chat:
                    pageAnterior = viewPager.getCurrentItem();
                    fm.beginTransaction().detach(chatFragment).attach(chatFragment).addToBackStack(null).commitAllowingStateLoss();
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onBackPressed() {
        viewPager.setCurrentItem(pageAnterior);
    }

    public static void refrescar(FragmentManager fm){
        for(Fragment f : fragments){
            fm.beginTransaction().detach(f).attach(f).addToBackStack(null).commitAllowingStateLoss();
        }
        navView.getMenu().findItem(R.id.navigation_profile).setTitle(R.string.title_perfil);
        navView.getMenu().findItem(R.id.navigation_home).setTitle(R.string.home);
        navView.getMenu().findItem(R.id.navigation_explore).setTitle(R.string.title_explorar);
        navView.getMenu().findItem(R.id.navigation_chat).setTitle(R.string.title_chat);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        languageManager.loadLocale(this);
    }
}


