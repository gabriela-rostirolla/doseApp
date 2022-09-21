package com.example.doseapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import java.util.List;

public class telaDadosDosIdosos extends AppCompatActivity {

    private List<IdosoCuidado> idosoCuidadoList;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_dados_dos_idosos);
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        ActionBar actionBar = getSupportActionBar();
        String id = getIntent().getStringExtra("id");
        DocumentReference document = firebaseFirestore.collection("Idosos cuidados").document(id);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                actionBar.setTitle(value.getString("nome"));
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        });

        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Remédios", telaRemedios.class)
                .add("Consultas", telaConsultas.class)
                .add("Terapias", telaTerapias.class)
                .add("Diário de cuidado", telaDiario.class)
                .add("Receitas", telaReceitas.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);
        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }
}