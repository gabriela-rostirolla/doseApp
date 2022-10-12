package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
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
    String [] mensagens ={"Preencha todos os campos", "Cadastro realizado com sucesso", "Senhas não conferem", "Falha no cadastro"};
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro);
        inicializarComponentes();
        //et_cadSenha.setBackgroundResource(android.R.color.transparent);
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
                if(nome.isEmpty() || email.isEmpty() || senha.isEmpty() || confSenha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else if (senha.equals(confSenha)==false){
                    Snackbar snackbar = Snackbar.make(view, mensagens[2], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    cadastrarUsuario(view);
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

    private void inicializarComponentes(){
        et_cadNome = findViewById(R.id.et_cadNome);
        et_cadEmail = findViewById(R.id.et_cadEmail);
        et_cadSenha = findViewById(R.id.et_cadSenha);
        et_cadConfirmarSenha = findViewById(R.id.et_cadConfirmarSenha);
        btn_cadastrar = findViewById(R.id.btn_cadastrar);
    }

    private void cadastrarUsuario(View view){
        String email = et_cadEmail.getText().toString();
        String senha = et_cadSenha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    salvarDadosNoBancoDeDados();
                    Snackbar snackbar = Snackbar.make(view, mensagens[1], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    iniciarTelaInicial();
                }else{
                    String erro ="";
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthWeakPasswordException exception) {
                        erro = "Senha inválida! Digite uma senha com mais de 6 caracteres";
                    }catch(FirebaseAuthInvalidCredentialsException exception){
                        erro = "E-mail inválido";
                    }catch (FirebaseAuthUserCollisionException exception){
                        erro ="Esta conta já existe";
                    }catch (Exception exception){
                        erro = "Não foi possível realizar cadastro";
                    }
                    Snackbar snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }

    private void salvarDadosNoBancoDeDados(){
        String nome = et_cadNome.getText().toString();
        String email = et_cadEmail.getText().toString();
        FirebaseFirestore banco_dados = FirebaseFirestore.getInstance();
        Map<String, Object> usuario = new HashMap<>();
        nome = nome.substring(0,1).toUpperCase().concat(nome.substring(1));
        usuario.put("nome", nome);
        usuario.put("email", email);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = banco_dados.collection("Usuarios").document(userID);
        documentReference.set(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("banco_dados", "Dados salvos com sucesso");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("erro_banco_dados", "Erro ao salvar dados"+ exception.toString());
            }
        });
    }

    private void iniciarTelaInicial(){
        Intent intent = new Intent(telaCadastro.this, telaInicial.class);
        startActivity(intent);
        finish();
    }
}