package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
import java.util.HashMap;
import java.util.Map;

public class telaCadastroDiarioAtividade extends AppCompatActivity {
    private Button btn_salvar;
    private EditText et_sono, et_exercicios, et_passeio, et_saude, et_outro, et_obs, et_cuidador;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private TextView tv_horario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_diario_atividade);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cadastrar Atividade");
        actionBar.setDisplayHomeAsUpEnabled(true);

        tv_horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hora = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(telaCadastroDiarioAtividade.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int i, int i2) {
                                if (i2 < 10) {
                                    tv_horario.setText(i + ":" + 0 + i2);
                                    tv_horario.setTextColor(Color.BLACK);
                                } else {
                                    tv_horario.setText(i + ":" + i2);
                                    tv_horario.setTextColor(Color.BLACK);
                                }
                            }
                        }, hora, min, true);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void inicializarComponentes() {
        et_cuidador = findViewById(R.id.et_cuidadorResp);
        btn_salvar = findViewById(R.id.btn_salvarDiario);
        et_sono = findViewById(R.id.et_sonoDiario);
        et_exercicios = findViewById(R.id.et_exercicioDiario);
        et_passeio = findViewById(R.id.et_passeioDiario);
        et_saude = findViewById(R.id.et_saudeDiario);
        et_outro = findViewById(R.id.et_outrosDiarioEt03);
        et_obs = findViewById(R.id.et_obsDiarioEt03);
        tv_horario = findViewById(R.id.et_horario);
    }

    protected void salvarBancoDeDados() {

        Map<String, Object> atividadesMap = new HashMap<>();
        atividadesMap.put("sono", et_sono.getText().toString());
        atividadesMap.put("exercicios", et_exercicios.getText().toString());
        atividadesMap.put("passeio", et_passeio.getText().toString());
        atividadesMap.put("saude", et_saude.getText().toString());
        atividadesMap.put("outro", et_outro.getText().toString());
        atividadesMap.put("observacao", et_obs.getText().toString());
        atividadesMap.put("turno", getIntent().getStringExtra("turno"));
        atividadesMap.put("dia", getIntent().getStringExtra("dia"));
        atividadesMap.put("diario id", getIntent().getStringExtra("diario id"));
        atividadesMap.put("cuidador", et_cuidador.getText().toString());
        atividadesMap.put("horario", tv_horario.getText().toString());

        firebaseFirestore.collection("Diario atividades")
                .add(atividadesMap)
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