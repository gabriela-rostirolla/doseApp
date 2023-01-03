package com.example.doseapp.activitys;

import static com.example.doseapp.classes.Main.autenticarUsuario;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doseapp.R;
import com.example.doseapp.classes.Main;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class telaLogin extends AppCompatActivity {
    private TextView txt_fazer_cadastro;
    private EditText et_email, et_senha;
    private Button btn_entrar;
    private TextView tv_redefinirSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_login);
        getSupportActionBar().hide();

        iniciarComponentes();

        tv_redefinirSenha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                redefinirSenha();
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
                if (email.isEmpty() || senha.isEmpty()) {
                    Toast.makeText(telaLogin.this, getString(R.string.camposVazios), Toast.LENGTH_SHORT).show();
                } else {
                    autenticarUsuario(telaLogin.this, email, senha);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
//        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (FirebaseAuth.getInstance().getUid() != null) {
            telaPrincipal();
        }
    }

    protected void redefinirSenha() {
        if (et_email.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.emailVazio), Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(telaLogin.this);
            builder.setTitle("Recuperar Senha")
                    .setMessage("Um email será enviado para o endereço de email: " + et_email.getText().toString() + "! Deseja continuar?")
                    .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Main.redefinirSenha(telaLogin.this, et_email.getText().toString());
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

    protected void iniciarComponentes() {
        txt_fazer_cadastro = findViewById(R.id.txt_fazer_cadastro);
        et_email = findViewById(R.id.et_email);
        et_senha = findViewById(R.id.et_senha);
        btn_entrar = findViewById(R.id.btn_entrar);
        tv_redefinirSenha = findViewById(R.id.txt_recuperar_senha);
    }

    protected void telaPrincipal() {
        Intent intent = new Intent(telaLogin.this, telaInicial.class);
        startActivity(intent);
        finish();
    }
}