package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.snackbar.SnackbarContentLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class telaLogin extends AppCompatActivity {
    private TextView txt_fazer_cadastro;
    private EditText et_email, et_senha;
    private Button btn_entrar;
    private TextView tv_redefinirSenha;
    String [] mensagens = {"Preencha todos os campos"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);
        getSupportActionBar().hide();
        iniciarComponentes();

        tv_redefinirSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(et_email.getText().toString().isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, "Digite um email", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(telaLogin.this);
                    builder.setTitle("Recuperar Senha")
                            .setMessage("Um email será enviado para o endereço de email: "+et_email.getText().toString()+"! Deseja continuar?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    FirebaseAuth auth = FirebaseAuth.getInstance();
                                    auth.sendPasswordResetEmail(et_email.getText().toString())
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("email_enviado", "Email sent.");
                                                    }
                                                }
                                            });
                                }
                            }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            });
                    builder.create();
                    builder.show();
                }
            }
        });

        txt_fazer_cadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(telaLogin.this, telaCadastro.class);
                startActivity(intent);
            }
        });

        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = et_email.getText().toString();
                String senha = et_senha.getText().toString();

                if(email.isEmpty() || senha.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    autenticarUsuario(view);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser userAtual = FirebaseAuth.getInstance().getCurrentUser();
        if(userAtual != null){
            telaPrincipal();
        }
    }

    protected void iniciarComponentes(){
        txt_fazer_cadastro = findViewById(R.id.txt_fazer_cadastro);
        et_email = findViewById(R.id.et_email);
        et_senha = findViewById(R.id.et_senha);
        btn_entrar = findViewById(R.id.btn_entrar);
        tv_redefinirSenha = findViewById(R.id.txt_recuperar_senha);
    }

    protected void autenticarUsuario(View view){
        String email = et_email.getText().toString();
        String senha = et_senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    telaPrincipal();
                }else{
                    String erro;
                    try{
                        throw task.getException();
                    }catch (FirebaseAuthInvalidUserException exception){
                        erro="Este E-mail não existe";
                    }catch (FirebaseAuthWeakPasswordException exception){
                        erro="Senha incorreta";
                    }catch (Exception exception){
                        erro = "Não foi possível realizar login";
                    }
                    Snackbar snackbar = Snackbar.make(view, erro, Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });

    }

    protected void telaPrincipal(){
        Intent intent = new Intent(telaLogin.this, telaInicial.class);
        startActivity(intent);
        finish();
    }
}