package com.example.doseapp.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.doseapp.R;
import com.example.doseapp.adapters.ContatoAdapter;
import com.example.doseapp.adapters.IdosoCuidadoAdapter;
import com.example.doseapp.models.IdosoCuidado;
import com.example.doseapp.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class telaContatos extends AppCompatActivity implements IdosoCuidadoAdapter.OnItemClick {

    private FloatingActionButton fab_addIdosoCuidado;
    private RecyclerView rv_lista;
    private List<Usuario> usuarioList = new ArrayList<>();
    private ContatoAdapter contatoAdapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_contatos);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Contatos");

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(telaContatos.this, telaLogin.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        listarCuidadores();
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

    protected void inicializarComponentes() {
        fab_addIdosoCuidado = findViewById(R.id.fab_addIdosoCuidado);
        rv_lista = findViewById(R.id.rv_listaIdoso);
    }

    protected void listarCuidadores() {
        usuarioList.clear();
        rv_lista.setLayoutManager(new LinearLayoutManager(this));
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseFirestore.collection("Usuarios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Usuario ic = new Usuario();
                                ic.setNome(document.getString("nome"));
                                ic.setId(document.getId());
                                if (!document.getId().equals(userId)) {
                                    usuarioList.add(ic);
                                }
                            }
                            contatoAdapter = new ContatoAdapter(usuarioList, telaContatos.this::OnItemClick, telaContatos.this);
                            rv_lista.addItemDecoration(new DividerItemDecoration(telaContatos.this, DividerItemDecoration.VERTICAL));
                            rv_lista.setHasFixedSize(true);
                            rv_lista.setAdapter(contatoAdapter);
                        }
                    }
                });
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent(telaContatos.this, telaChat.class);
        intent.putExtra("idRec", usuarioList.get(position).getId());
        intent.putExtra("nome", usuarioList.get(position).getNome());
        startActivity(intent);
    }
}