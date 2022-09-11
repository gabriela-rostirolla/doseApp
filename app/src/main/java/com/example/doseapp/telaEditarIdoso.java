package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class telaEditarIdoso extends AppCompatActivity {

    private Button btn_editarIdoso;
    private EditText et_nomeIdosoEdit, et_enderecoIdosoEdit, et_telefoneIdosoEdit, et_nascIdosoEdit, et_obsEdit;
    private List<IdosoCuidado> idosoCuidadoList;
    private String userId;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private RadioButton rb_feminino, rb_masculino, rb_outro;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_idoso);
        inicializarComponentes();
        id = getIntent().getStringExtra("id");
        listarIdososCuidados();
    }

    protected void inicializarComponentes(){
        btn_editarIdoso = findViewById(R.id.btn_cadastrarIdoso);
        et_nomeIdosoEdit = findViewById(R.id.et_nomeIdoso);
        et_enderecoIdosoEdit = findViewById(R.id.et_enderecoIdoso);
        et_telefoneIdosoEdit = findViewById(R.id.et_telefoneIdoso);
        et_nascIdosoEdit = findViewById(R.id.et_dataNascIdoso);
        et_obsEdit = findViewById(R.id.et_obsIdoso);
        rb_feminino = findViewById(R.id.rb_feminino);
        rb_masculino = findViewById(R.id.rb_masculino);
        rb_outro = findViewById(R.id.rb_outro);
    }

    protected void listarIdososCuidados(){
        idosoCuidadoList = new ArrayList<>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String nomeColecao = "Idosos cuidados "+userId;
        DocumentReference document = firebaseFirestore.collection(nomeColecao).document(id);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                IdosoCuidado ic = new IdosoCuidado();

                et_nomeIdosoEdit.setText(value.getString("nome"));
                et_enderecoIdosoEdit.setText(value.getString("endereco"));
                et_telefoneIdosoEdit.setText(value.getString("telefone"));
                et_nascIdosoEdit.setText(value.getString("data de nascimento"));
                et_obsEdit.setText(value.getString("observacoes"));
                String genero = value.getString("genero");
                if(genero.equals("feminino")) rb_feminino.setChecked(true);
                else if(genero.equals("masculino")) rb_masculino.setChecked(true);
                else if(genero.equals("outro")) rb_outro.setChecked(true);
            }
        });

        btn_editarIdoso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
    }