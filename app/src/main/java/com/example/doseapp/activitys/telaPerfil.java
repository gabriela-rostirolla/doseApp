package com.example.doseapp.activitys;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class telaPerfil extends AppCompatActivity {
    Button btn_editar;
    CircleImageView img_perfil;
    FirebaseFirestore banco_dados = FirebaseFirestore.getInstance();
    String userID;
    EditText et_editNome, et_editEmail;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    String foto;

    int PICK_IMAGE = 1;

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
                try {
                    String imagem_armazenada_bd = task.getResult().getString("imagem");
                    if (!imagem_armazenada_bd.isEmpty() || imagem_armazenada_bd!=null){
                        carregarImagem(imagem_armazenada_bd);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

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

    private void selecionarImagem() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        someActivityResultLauncher.launch(intent);
    }

    private void carregarImagem(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(img_perfil);
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
                    .update("nome", et_editNome.getText().toString(), "imagem", foto)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            gerarToast("Dados atualizados com sucesso");
                            Log.d("documento_atualizado", "DocumentSnapshot successfully updated!");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            gerarToast("Falha ao atualizar dados! Erro: " + e);
                        }
                    });
        } else {
            gerarToast("Digite um nome válido");
        }
    }

    private final ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // Aqui você pode obter a imagem selecionada
                    if (data != null) {
                        Uri imageUri = data.getData();
                        carregarImagem(String.valueOf(imageUri));
                        // Chame o método para enviar a imagem para o Firebase Firestore
                        foto = imageUri.toString();
                    }
                }
            });

    public void gerarToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}