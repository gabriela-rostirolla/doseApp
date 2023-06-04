package com.example.doseapp.activitys;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.IOException;
import java.net.URI;

import de.hdodenhof.circleimageview.CircleImageView;

public class telaPerfil extends AppCompatActivity {
    Button btn_editar;
    CircleImageView img_perfil;
    FirebaseFirestore banco_dados = FirebaseFirestore.getInstance();
    String userID;
    EditText et_editNome, et_editEmail;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_perfil);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.perfil);
        actionBar.setDisplayHomeAsUpEnabled(true);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        banco_dados.collection("Usuarios").document(userID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String nome = task.getResult().getString("nome");
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

                img_perfil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        selecionarImagem();
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

    @SuppressLint("NonConstantResourceId")
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
                                startActivity(new Intent(telaPerfil.this, telaLogin.class));
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
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void inicializarComponentes() {
        et_editNome = findViewById(R.id.et_editNome);
        et_editEmail = findViewById(R.id.et_editEmail);
        btn_editar = findViewById(R.id.btn_editar);
        img_perfil = findViewById(R.id.img_perfil);
    }

    protected void editar_perfil(View view) {
        if (!et_editNome.getText().toString().isEmpty()) {
            firebaseFirestore.collection("Usuarios").document(userID)
                    .update("nome", et_editNome.getText().toString(), "imagem", "hello")
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            gerarToast("Dados atualizados com sucesso");
                            Log.d("documento_atualizado", "DocumentSnapshot successfully updated!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            gerarToast("Falha ao atualizar dados! Erro: "+e );
                        }
                    });
        } else {
            gerarToast("Digite um nome válido");
        }
    }

    public void selecionarImagem(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            uri = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                img_perfil.setImageDrawable(new BitmapDrawable(bitmap));
            } catch (IOException e) {
                Toast.makeText(this, "Erro: "+e, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void gerarToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}