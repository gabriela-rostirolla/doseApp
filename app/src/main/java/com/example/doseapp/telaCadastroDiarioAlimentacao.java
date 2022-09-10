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

public class telaCadastroDiarioAlimentacao extends AppCompatActivity {
    private Button btn_salvar;
    private EditText et_refeicao, et_lanche, et_outro, et_obs;
    private TextView tv_titulo;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_diario_alimentacao);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cadastrar Alimentação");
        actionBar.setDisplayHomeAsUpEnabled(true);

        inicializarComponentes();

        String titulo = getIntent().getStringExtra("titulo");
        tv_titulo.setText(titulo);
        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarBancoDeDados();
                Intent intent = new Intent(telaCadastroDiarioAlimentacao.this, telaDadosDosIdosos.class);
                startActivity(intent);
            }
        });

    }

    protected void inicializarComponentes(){
        btn_salvar = findViewById(R.id.btn_salvarAlimentacao);
        tv_titulo = findViewById(R.id.tv_diaTurnoDiarioEt02);
        et_refeicao = findViewById(R.id.et_refeicao);
        et_lanche = findViewById(R.id.et_lancheDiario);
        et_outro = findViewById(R.id.et_outrosDiario);
        et_obs = findViewById(R.id.et_obsDiario);
    }

    protected void salvarBancoDeDados(){
        String refeicao = et_refeicao.getText().toString();
        String lanche = et_lanche.getText().toString();
        String outro = et_outro.getText().toString();
        String obs = et_obs.getText().toString();
        Map<String, Object> alimentacaoMap = new HashMap<>();
        alimentacaoMap.put("refeicao", refeicao);
        alimentacaoMap.put("lanche", lanche);
        alimentacaoMap.put("outro", outro);
        alimentacaoMap.put("observacao", obs);

        firebaseFirestore.collection("Alimentacao")
                .add(alimentacaoMap)
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