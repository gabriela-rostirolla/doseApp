package com.example.doseapp.fragments;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.doseapp.R;
import com.example.doseapp.fragments.telaTurnoMadrugada;
import com.example.doseapp.fragments.telaTurnoManha;
import com.example.doseapp.fragments.telaTurnoNoite;
import com.example.doseapp.fragments.telaTurnoTarde;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class telaDiarios extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_diarios);

        ActionBar actionBar = getSupportActionBar();
        String nome = getIntent().getStringExtra("dia");
        actionBar.setTitle(nome);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Manhã", telaTurnoManha.class)
                .add("Tarde", telaTurnoTarde.class)
                .add("Noite", telaTurnoNoite.class)
                .add("Madrugada", telaTurnoMadrugada.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertabDiario);
        viewPagerTab.setViewPager(viewPager);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}