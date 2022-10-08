package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class telaCadastroDiarioAlimentacao extends AppCompatActivity {
    private Button btn_salvar;
    private EditText et_refeicao, et_lanche, et_outro, et_obs, et_cuidador;
    private TextView et_horario;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_diario_alimentacao);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cadastrar Alimentação");
        actionBar.setDisplayHomeAsUpEnabled(true);
        inicializarComponentes();

        et_horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hora = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(telaCadastroDiarioAlimentacao.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int i, int i2) {
                                if (i2 < 10) {
                                    et_horario.setText(i + ":" + 0 + i2);
                                    et_horario.setTextColor(Color.BLACK);
                                } else {
                                    et_horario.setText(i + ":" + i2);
                                    et_horario.setTextColor(Color.BLACK);
                                }
                            }
                        }, hora, min, false);
                timePickerDialog.show();
            }
        });

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salvarBancoDeDados();
                finish();
            }
        });

    }

    protected void inicializarComponentes() {
        btn_salvar = findViewById(R.id.btn_salvarAlimentacao);
        et_refeicao = findViewById(R.id.et_refeicao);
        et_lanche = findViewById(R.id.et_lancheDiario);
        et_outro = findViewById(R.id.et_outrosDiario);
        et_obs = findViewById(R.id.et_obsDiario);
        et_cuidador = findViewById(R.id.et_cuidadorResp);
        et_horario = findViewById(R.id.et_horarioRef);
    }

    protected void salvarBancoDeDados() {
        String refeicao = et_refeicao.getText().toString();
        String lanche = et_lanche.getText().toString();
        String outro = et_outro.getText().toString();
        String obs = et_obs.getText().toString();
        String data = getIntent().getStringExtra("dia");
        String diario_id = getIntent().getStringExtra("diario id");
        String turno = getIntent().getStringExtra("Turno");
        String cuidador = et_cuidador.getText().toString();

        Map<String, Object> alimentacaoMap = new HashMap<>();
        alimentacaoMap.put("refeicao", refeicao);
        alimentacaoMap.put("lanche", lanche);
        alimentacaoMap.put("outro", outro);
        alimentacaoMap.put("turno", turno);
        alimentacaoMap.put("dia", data);
        alimentacaoMap.put("observacao", obs);
        alimentacaoMap.put("diario id", diario_id);
        alimentacaoMap.put("cuidador", cuidador);
        alimentacaoMap.put("horario", et_horario.getText().toString());

        firebaseFirestore.collection("Alimentacao")
                .add(alimentacaoMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("banco_dados_salvos", "Sucesso ao salvar dados!" + documentReference.getId());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("erro_banco_dados", "Erro ao salvar dados!", e);
                    }
                });
    }
}