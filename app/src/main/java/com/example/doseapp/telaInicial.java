package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

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

public class telaInicial extends AppCompatActivity implements IdosoCuidadoAdapter.onItemClick {

    private FloatingActionButton fab_addIdosoCuidado;
    private RecyclerView rv_listaIdosos;
    private List<IdosoCuidado> idosoCuidadoList;
    private IdosoCuidadoAdapter idosoCuidadoAdapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
        inicializarComponentes();
        listarIdososCuidados();

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

    protected void inicializarComponentes(){
        fab_addIdosoCuidado = findViewById(R.id.fab_addIdosoCuidado);
        rv_listaIdosos=findViewById(R.id.rv_listaIdoso);
    }

    protected void listarIdososCuidados(){
        rv_listaIdosos.setLayoutManager(new LinearLayoutManager(this));
        idosoCuidadoList = new ArrayList<>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String nomeColecao = "Idosos cuidados "+userId;
        firebaseFirestore.collection(nomeColecao)
                .orderBy("data de criacao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                IdosoCuidado ic = document.toObject(IdosoCuidado.class);
                                idosoCuidadoList.add(ic);
                            }
                            idosoCuidadoAdapter = new IdosoCuidadoAdapter(idosoCuidadoList, telaInicial.this::onItemClick, telaInicial.this);
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
    public void onItemClick(int position) {
        Intent intent = new Intent(telaInicial.this, telaDadosDosIdosos.class);
        intent.putExtra("nome", idosoCuidadoList.get(position).getNome());
        startActivity(intent);
    }
}