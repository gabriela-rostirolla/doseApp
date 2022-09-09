package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class telaCadastroConsulta extends AppCompatActivity {

    private EditText et_nome, et_profissional, et_end, et_tel, et_data, et_horario;
    private Button btn_salvar;
    private String [] mensagens = {"Preencha todos os dados"};
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_consulta);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.cadastrar_consulta);
        actionBar.setDisplayHomeAsUpEnabled(true);

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = et_nome.getText().toString();
                String endereco = et_end.getText().toString();
                String data = et_data.getText().toString();
                String tel = et_tel.getText().toString();
                String profissional = et_profissional.getText().toString();
                String horario = et_horario.getText().toString();


                if (nome.isEmpty() || endereco.isEmpty() || data.isEmpty() || tel.isEmpty() || profissional.isEmpty() || horario.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    salvarNoBancoDeDados();
                    voltarTelaAnterior();
                }

            }
        });
    }

    protected void voltarTelaAnterior(){
        Intent intent = new Intent(telaCadastroConsulta.this, telaDadosDosIdosos.class);
        startActivity(intent);
    }

    protected void salvarNoBancoDeDados(){
        String nome = et_nome.getText().toString();
        String endereco = et_end.getText().toString();
        String data = et_data.getText().toString();
        String tel = et_tel.getText().toString();
        String profissional = et_profissional.getText().toString();
        String horario = et_horario.getText().toString();

        Map<String, Object> consultaMap = new HashMap<>();
        consultaMap.put("nome", nome);
        consultaMap.put("data", data);
        consultaMap.put("endereco", endereco);
        consultaMap.put("telefone", tel);
        consultaMap.put("profissional", profissional);
        consultaMap.put("horario", horario);

        firebaseFirestore.collection("Consultas")
                .add(consultaMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("banco_dados_salvos", "Sucesso ao salvar dados!"+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("erro_banco_dados", "Erro ao salvar dados!", e);
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
    }
}