package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class telaCadastroReceita extends AppCompatActivity {

    private EditText et_nome, et_hospital,et_tel, et_profissional;
    private TextView et_data,et_dataRen;
    private Button btn_salvar;
    private DatePickerDialog.OnDateSetListener dateSetListenerData;
    private DatePickerDialog.OnDateSetListener dateSetListenerDataRen;
    private String[]mensagens = {"Preencha todos os campos"};
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_receita);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.cadastrar_receita);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int anoIni = calendar.get(Calendar.YEAR);
                int diaIni = calendar.get(Calendar.DAY_OF_MONTH);
                int mesIni = calendar.get(Calendar.MONTH);
                DatePickerDialog dialog = new DatePickerDialog(telaCadastroReceita.this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListenerData, diaIni, mesIni, anoIni);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListenerData = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1++;

                String mes = "";
                String dia = "";
                if (i1 < 10) mes = "0" + i1;
                else mes = String.valueOf(i1);
                if (i2 < 10) dia = "0" + i2;
                else dia = String.valueOf(i2);
                et_data.setText(dia + "/" + mes + "/" + i);
                et_data.setTextColor(Color.BLACK);
            }
        };

        et_dataRen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int anoFim = calendar.get(Calendar.YEAR);
                int diaFim = calendar.get(Calendar.DAY_OF_MONTH);
                int mesFim = calendar.get(Calendar.MONTH);
                DatePickerDialog dialog = new DatePickerDialog(telaCadastroReceita.this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListenerDataRen, diaFim, mesFim, anoFim);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListenerDataRen = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1++;
                String mes = "";
                String dia = "";
                if (i1 < 10) mes = "0" + i1;
                else mes = String.valueOf(i1);
                if (i2 < 10) dia = "0" + i2;
                else dia = String.valueOf(i2);
                et_dataRen.setText(dia + "/" + mes + "/" + i);
                et_dataRen.setTextColor(Color.BLACK);
            }
        };

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = et_nome.getText().toString();
                String data = et_data.getText().toString();
                String hospital = et_hospital.getText().toString();
                String tel = et_tel.getText().toString();
                String profissional = et_profissional.getText().toString();
                String dataRen = et_dataRen.getText().toString();

                if (nome.isEmpty() || data.isEmpty() || hospital.isEmpty() || tel.isEmpty() || profissional.isEmpty() || dataRen.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                } else {
                    salvarNoBancoDeDados();
                    finish();
                }
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
        }

        protected void salvarNoBancoDeDados(){
            String nome = et_nome.getText().toString();
            String data = et_data.getText().toString();
            String hospital = et_hospital.getText().toString();
            String tel = et_tel.getText().toString();
            String profissional = et_profissional.getText().toString();
            String dataRen = et_dataRen.getText().toString();
            String id = getIntent().getStringExtra("id");
            Map<String, Object> receitaMap = new HashMap<>();
            receitaMap.put("nome", nome);
            receitaMap.put("data", data);
            receitaMap.put("hospital", hospital);
            receitaMap.put("telefone", tel);
            receitaMap.put("profissional", profissional);
            receitaMap.put("data para renovar", dataRen);
            receitaMap.put("id do idoso", id);

            firebaseFirestore.collection("Receitas")
                    .add(receitaMap)
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
}