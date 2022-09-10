package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Source;

public class telaPerfil extends AppCompatActivity {
    Button btn_editar;
    FirebaseFirestore banco_dados = FirebaseFirestore.getInstance();
    String userID;
    EditText et_editNome, et_editEmail;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_perfil);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.perfil);
        actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onStart() {
        super.onStart();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference value = banco_dados.collection("Usuarios").document(userID);
        value.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String nome = value.getString("nome");
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                et_editNome.setText(nome);
                et_editEmail.setText(email);

                et_editEmail.setEnabled(false);
                et_editEmail.setTextColor(Color.GRAY);
                btn_editar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editar_perfil(view);
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_sair:
                android.app.AlertDialog.Builder builder = new AlertDialog.Builder(telaPerfil.this);
                builder.setMessage("Deseja mesmo sair da sua conta?")
                                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(telaPerfil.this, telaLogin.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });
                builder.create();
                builder.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void inicializarComponentes(){
        et_editNome = findViewById(R.id.et_editNome);
        et_editEmail = findViewById(R.id.et_editEmail);
        btn_editar = findViewById(R.id.btn_editar);
    }

    protected void editar_perfil(View view){
        if(et_editNome.getText().toString().isEmpty()==false ){
            firebaseFirestore.collection("Usuarios").document(userID)
                    .update("nome", et_editNome.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Snackbar snackbar = Snackbar.make(view, "Nome atualizado com sucesso", Snackbar.LENGTH_SHORT);
                            snackbar.setBackgroundTint(Color.WHITE);
                            snackbar.setTextColor(Color.BLACK);
                            snackbar.show();
                            Log.d("documento_atualizado", "DocumentSnapshot successfully updated!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("falha_ao_atualizar", "DocumentSnapshot failure updated!");
                        }
                    });
        }else{
            Snackbar snackbar = Snackbar.make(view, "Digite um nome válido", Snackbar.LENGTH_SHORT);
            snackbar.setBackgroundTint(Color.WHITE);
            snackbar.setTextColor(Color.BLACK);
            snackbar.show();

        }
    }
}