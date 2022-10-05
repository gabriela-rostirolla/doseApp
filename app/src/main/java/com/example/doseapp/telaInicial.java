package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class telaInicial extends AppCompatActivity implements IdosoCuidadoAdapter.OnItemClick {

    private FloatingActionButton fab_addIdosoCuidado;
    private RecyclerView rv_listaIdosos;
    private TextView tv_nenhumIdosoCad;
    private List<IdosoCuidado> idosoCuidadoList = new ArrayList<>();
    private IdosoCuidadoAdapter idosoCuidadoAdapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.idosos_cadastrados);

        fab_addIdosoCuidado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(telaInicial.this, telaCadastroIdosoCuidado.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        idosoCuidadoList.clear();
        listarIdososCuidados();
    }

    protected void inicializarComponentes() {
        fab_addIdosoCuidado = findViewById(R.id.fab_addIdosoCuidado);
        rv_listaIdosos = findViewById(R.id.rv_listaIdoso);
        tv_nenhumIdosoCad = findViewById(R.id.tv_nenhumIdosoCad);
    }

    protected void listarIdososCuidados() {
        rv_listaIdosos.setLayoutManager(new LinearLayoutManager(this));
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseFirestore.collection("Idosos cuidados")
                .whereEqualTo("cuidador id", userId)
                .orderBy("data de criacao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                IdosoCuidado ic = new IdosoCuidado();
                                ic.setNome(document.getString("nome"));
                                ic.setId(document.getId());
                                boolean aux = document.getBoolean("cuidado");
                                ic.setCuidado(aux);
                                idosoCuidadoList.add(ic);
                            }
                            if (idosoCuidadoList.isEmpty()) {
                                tv_nenhumIdosoCad.setVisibility(View.VISIBLE);
                            } else {
                                tv_nenhumIdosoCad.setVisibility(View.INVISIBLE);
                            }
                            idosoCuidadoAdapter = new IdosoCuidadoAdapter(idosoCuidadoList, telaInicial.this::OnItemClick, telaInicial.this);
                            rv_listaIdosos.addItemDecoration(new DividerItemDecoration(telaInicial.this, DividerItemDecoration.VERTICAL));
                            rv_listaIdosos.setHasFixedSize(false);
                            rv_listaIdosos.setAdapter(idosoCuidadoAdapter);
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_perfil, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_perfil:
                Intent intent = new Intent(telaInicial.this, telaPerfil.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent(telaInicial.this, telaDadosDosIdosos.class);
        intent.putExtra("id", idosoCuidadoList.get(position).getId());
        intent.putExtra("nome", idosoCuidadoList.get(position).getNome());
        startActivity(intent);
    }
}