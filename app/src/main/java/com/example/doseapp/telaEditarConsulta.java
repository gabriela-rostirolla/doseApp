package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
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

public class telaEditarConsulta extends AppCompatActivity {

    private EditText et_nome, et_profissional, et_end, et_tel, et_data, et_horario;
    private Button btn_salvar;
    private String [] mensagens = {"Preencha todos os dados"};
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String idConsulta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_consulta);

        inicializarComponentes();
        idConsulta = getIntent().getStringExtra("id consulta");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.editar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        preencherDadosConsulta();

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarBancoDeDados();
                finish();
            }
        });

    }

    protected void inicializarComponentes(){
        et_nome = findViewById(R.id.et_nomeConsulta);
        et_profissional = findViewById(R.id.et_profissionalConsulta);
        et_end = findViewById(R.id.et_enderecoConsulta);
        et_tel = findViewById(R.id.et_telefoneConsulta);
        et_data = findViewById(R.id.et_dataConsulta);
        et_horario = findViewById(R.id.et_horarioConsulta);
        btn_salvar = findViewById(R.id.btn_salvarConsulta);
        btn_salvar.setText("editar");
    }

    public void preencherDadosConsulta() {
        DocumentReference document = firebaseFirestore.collection("Consultas").document(idConsulta);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                et_nome.setText(value.getString("nome"));
                et_profissional.setText(value.getString("profissional"));
                et_end.setText(value.getString("endereco"));
                et_tel.setText(value.getString("telefone"));
                et_data.setText(value.getString("data"));
                et_horario.setText(value.getString("horario"));
            }
        });
    }

    public void editarBancoDeDados(){
        String nome = et_nome.getText().toString();
        String profissional=  et_profissional.getText().toString();
        String end = et_end.getText().toString();
        String tel = et_tel.getText().toString();
        String data = et_data.getText().toString();
        String horario = et_horario.getText().toString();

        firebaseFirestore.collection("Consultas").document(idConsulta)
                .update("nome",nome,"profissional",profissional,"endereco",end,"telefonen",tel,"data", data, "horario", horario)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                    }
                });
    }
}