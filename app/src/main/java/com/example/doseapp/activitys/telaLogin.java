package com.example.doseapp.activitys;

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
                redefinirSenha(view);
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
                    autenticarUsuario(view);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            telaPrincipal();
        }
    }

    protected void redefinirSenha(View view) {
        if (et_email.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.emailVazio), Toast.LENGTH_SHORT).show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(telaLogin.this);
            builder.setTitle("Recuperar Senha")
                    .setMessage("Um email será enviado para o endereço de email: " + et_email.getText().toString() + "! Deseja continuar?")
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
                                                Toast.makeText(telaLogin.this, getString(R.string.emailEnviado), Toast.LENGTH_SHORT).show();
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

    protected void iniciarComponentes() {
        txt_fazer_cadastro = findViewById(R.id.txt_fazer_cadastro);
        et_email = findViewById(R.id.et_email);
        et_senha = findViewById(R.id.et_senha);
        btn_entrar = findViewById(R.id.btn_entrar);
        tv_redefinirSenha = findViewById(R.id.txt_recuperar_senha);
    }

    protected void autenticarUsuario(View view) {
        String email = et_email.getText().toString();
        String senha = et_senha.getText().toString();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    telaPrincipal();
                    Toast.makeText(telaLogin.this, getString(R.string.loginFeitoComSucesso), Toast.LENGTH_SHORT).show();
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException exception) {
                        erro = getString(R.string.emailInex);
                    } catch (FirebaseAuthInvalidCredentialsException exception) {
                        erro = getString(R.string.senhaIncorreta);
                    } catch (Exception exception) {
                        erro = getString(R.string.falhaAoLogar);
                    }
                    Toast.makeText(telaLogin.this, erro, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    protected void telaPrincipal() {
        Intent intent = new Intent(telaLogin.this, telaInicial.class);
        startActivity(intent);
        finish();
    }
}