package com.example.doseapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class telaPerfil extends AppCompatActivity {
    private TextView txt_nomeV, txt_emailV;
    Button btn_editar, btn_deslogar;
    FirebaseFirestore banco_dados = FirebaseFirestore.getInstance();
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_perfil);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.perfil);
        actionBar.setDisplayHomeAsUpEnabled(true);

        btn_deslogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(telaPerfil.this, telaLogin.class);
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference value = banco_dados.collection("Usuarios").document(userID);
        value.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String nome = " Nome: "+value.getString("nome");
                String email = " E-mail: "+FirebaseAuth.getInstance().getCurrentUser().getEmail();
                txt_nomeV.setText(nome);
                txt_emailV.setText(email);
            }
        });
    }

    private void inicializarComponentes(){
        txt_nomeV = findViewById(R.id.txt_nomeV);
        txt_emailV = findViewById(R.id.txt_emailV);
        btn_deslogar = findViewById(R.id.btn_deslogar);
        btn_editar = findViewById(R.id.btn_editar);
    }
}