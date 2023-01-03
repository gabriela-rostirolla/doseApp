package com.example.doseapp.activitys;

import static com.example.doseapp.classes.Main.cadastrarUsuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class telaCadastro extends AppCompatActivity {
    private EditText et_cadNome, et_cadEmail, et_cadSenha, et_cadConfirmarSenha;
    private Button btn_cadastrar;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro);
        inicializarComponentes();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.cadastrar);
        actionBar.setDisplayHomeAsUpEnabled(true);

        btn_cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = et_cadNome.getText().toString();
                String email = et_cadEmail.getText().toString();
                String senha = et_cadSenha.getText().toString();
                String confSenha = et_cadConfirmarSenha.getText().toString();
                if (nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confSenha.isEmpty()) {
                    Toast.makeText(telaCadastro.this, getString(R.string.camposVazios), Toast.LENGTH_SHORT).show();
                } else if (senha.equals(confSenha) == false) {
                    Toast.makeText(telaCadastro.this, getString(R.string.senhasDiferentes), Toast.LENGTH_SHORT).show();
                } else {
                    cadastrarUsuario(telaCadastro.this, email, nome, senha, FirebaseAuth.getInstance().getCurrentUser().getUid(), firebaseFirestore);
                }
            }
        });
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
        et_cadNome = findViewById(R.id.et_cadNome);
        et_cadEmail = findViewById(R.id.et_cadEmail);
        et_cadSenha = findViewById(R.id.et_cadSenha);
        et_cadConfirmarSenha = findViewById(R.id.et_cadConfirmarSenha);
        btn_cadastrar = findViewById(R.id.btn_cadastrar);
    }
}