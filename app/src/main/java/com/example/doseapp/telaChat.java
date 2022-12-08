package com.example.doseapp;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class telaChat extends AppCompatActivity {

    private RecyclerView rv;
    private static MensagemAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_chat);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        inicializarComponentes();
        actionBar.setTitle("Chat");

        List<IdosoCuidado> list = new ArrayList<>();
        IdosoCuidado cuidado = new IdosoCuidado();
        cuidado.setNome("Mensagem 11111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111111");
        cuidado.setCuidado(false);
        list.add(cuidado);

        IdosoCuidado cuidado1 = new IdosoCuidado();
        cuidado1.setNome("Mensagem 2");
        cuidado1.setCuidado(false);
        list.add(cuidado1);

        IdosoCuidado cuidado2 = new IdosoCuidado();
        cuidado2.setNome("Mensagem 3");
        cuidado2.setCuidado(true);
        list.add(cuidado2);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MensagemAdapter(list, getApplicationContext());
        rv.setHasFixedSize(false);
        rv.setAdapter(adapter);
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
    private void inicializarComponentes() {
        rv = findViewById(R.id.rv_listaMensagem);
    }

}