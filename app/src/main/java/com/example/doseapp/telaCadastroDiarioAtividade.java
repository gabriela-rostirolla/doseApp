package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class telaCadastroDiarioAtividade extends AppCompatActivity {
    private Button btn_salvar;
    private TextView tv_titulo;
    private EditText et_sono, et_exercicios, et_passeio, et_saude, et_outro, et_obs;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_diario_atividade);
        inicializarComponentes();
        String titulo = getIntent().getStringExtra("titulo");
        tv_titulo.setText(titulo);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cadastrar Atividade");
        actionBar.setDisplayHomeAsUpEnabled(true);

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarBancoDeDados();
                finish();
            }
        });
    }

    protected void inicializarComponentes(){
        btn_salvar = findViewById(R.id.btn_salvarDiario);
        tv_titulo = findViewById(R.id.tv_diaTurnoDiarioEt03);
        et_sono = findViewById(R.id.et_sonoDiario);
        et_exercicios = findViewById(R.id.et_exercicioDiario);
        et_passeio = findViewById(R.id.et_passeioDiario);
        et_saude = findViewById(R.id.et_saudeDiario);
        et_outro = findViewById(R.id.et_outrosDiarioEt03);
        et_obs = findViewById(R.id.et_obsDiarioEt03);
    }
    protected void salvarBancoDeDados(){
        String sono = et_sono.getText().toString();
        String exercicios = et_exercicios.getText().toString();
        String passeio = et_passeio.getText().toString();
        String saude = et_saude.getText().toString();
        String outro = et_outro.getText().toString();
        String obs = et_obs.getText().toString();
        Map<String, Object> atividadesMap = new HashMap<>();
        atividadesMap.put("sono", sono);
        atividadesMap.put("exercicios", exercicios);
        atividadesMap.put("passeio", passeio);
        atividadesMap.put("saude", saude);
        atividadesMap.put("outro", outro);
        atividadesMap.put("observacao", obs);

        firebaseFirestore.collection("Diario atividades")
                .add(atividadesMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("banco_dados_salvos", "Sucesso ao salvar dados!"+documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("erro_banco_dados", "Erro ao salvar dados!", e);
                    }
                });
    }
}