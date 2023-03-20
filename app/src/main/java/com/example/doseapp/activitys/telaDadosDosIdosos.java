package com.example.doseapp.activitys;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.doseapp.R;
import com.example.doseapp.fragments.telaConsultas;
import com.example.doseapp.fragments.telaDiario;
import com.example.doseapp.fragments.telaMedicamentos;
import com.example.doseapp.fragments.telaReceitas;
import com.example.doseapp.fragments.telaTerapias;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class telaDadosDosIdosos extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_dados_dos_idosos);

        ActionBar actionBar = getSupportActionBar();
        String id = getIntent().getStringExtra("id");
        String nome = getIntent().getStringExtra("nome");
        actionBar.setTitle(nome);
        actionBar.setDisplayHomeAsUpEnabled(true);

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Remédios", telaMedicamentos.class)
                .add("Consultas", telaConsultas.class)
                .add("Terapias", telaTerapias.class)
                .add("Receitas", telaReceitas.class)
                .add("Diário de cuidado", telaDiario.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
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