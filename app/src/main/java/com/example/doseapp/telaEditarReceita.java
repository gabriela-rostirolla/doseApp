package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class telaEditarReceita extends AppCompatActivity {
    private EditText et_nome, et_data, et_hospital,et_tel, et_profissional,et_dataRen;
    private Button btn_salvar;
    private String[]mensagens = {"Preencha todos os campos"};
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String idRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_receita);

        inicializarComponentes();
        idRec = getIntent().getStringExtra("id receita");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.editar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        preencherDadosReceita();

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarBancoDeDados();
                finish();
            }
        });

    }

    protected void inicializarComponentes () {
        et_nome = findViewById(R.id.et_nomeReceita);
        et_data = findViewById(R.id.et_dataReceita);
        et_hospital = findViewById(R.id.et_hospitalReceita);
        et_tel = findViewById(R.id.et_telefoneHospital);
        et_profissional = findViewById(R.id.et_profissionalReceita);
        et_dataRen = findViewById(R.id.et_dataRenReceita);
        btn_salvar = findViewById(R.id.btn_salvarCadReceita);
        btn_salvar.setText("Editar");
    }

    public void editarBancoDeDados(){
        String nome = et_nome.getText().toString();
        String data =  et_data.getText().toString();
        String hosp = et_hospital.getText().toString();
        String tel = et_tel.getText().toString();
        String profissional = et_profissional.getText().toString();
        String dataRen = et_dataRen.getText().toString();

        firebaseFirestore.collection("Receitas").document(idRec)
                .update("nome",nome,"data",data,"hospital",hosp,"telefone",tel,"profissional", profissional, "data para renovar", dataRen)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                    }
                });
    }

    public void preencherDadosReceita() {
        DocumentReference document = firebaseFirestore.collection("Receitas").document(idRec);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                et_nome.setText(value.getString("nome"));
                et_hospital.setText(value.getString("hospital"));
                et_tel.setText(value.getString("telefone"));
                et_profissional.setText(value.getString("profissional"));
                et_data.setText(value.getString("data"));
                et_dataRen.setText(value.getString("data para renovar"));
            }
        });
    }
}