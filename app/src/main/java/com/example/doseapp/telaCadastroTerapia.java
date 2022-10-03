package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class telaCadastroTerapia extends AppCompatActivity {
    private EditText et_nome, et_profissional, et_endereco, et_telefone;
    private Button btn_salvar;
    private TextView et_horario;
    private Switch swt_lembre;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String[] mensagens ={"Preencha todos os campos"};
    private Chip chipDom, chipSeg, chipTer, chipQua, chipQui, chipSex, chipSab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_terapia);
        inicializarComponentes();

        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle(R.string.cadastrar_terapia);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hora = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(telaCadastroTerapia.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int i, int i2) {
                                if (i2 < 10) {
                                    et_horario.setText(i + ":" + 0 + i2);
                                    et_horario.setTextColor(Color.BLACK);
                                } else {
                                    et_horario.setText(i + ":" + i2);
                                    et_horario.setTextColor(Color.BLACK);
                                }                            }
                        }, hora, min, false);
                timePickerDialog.show();
            }
        });

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = et_nome.getText().toString();
                String end = et_endereco.getText().toString();
                String profissional = et_profissional.getText().toString();
                String horario = et_horario.getText().toString();
                String tel = et_telefone.getText().toString();

                if(nome.isEmpty()||end.isEmpty()||profissional.isEmpty()||horario.isEmpty()||tel.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    salvarNoBancoDeDados();
                    finish();
                }
            }
        });
    }

    protected void inicializarComponentes(){
        et_nome = findViewById(R.id.et_nomeTerapia);
        et_endereco = findViewById(R.id.et_enderecoTerapia);
        et_profissional = findViewById(R.id.et_profissionalTerapia);
        et_horario = findViewById(R.id.et_horaTerapia);
        et_telefone = findViewById(R.id.et_telefoneTerapia);
        btn_salvar = findViewById(R.id.btn_salvarTerapia);
        swt_lembre = findViewById(R.id.swt_lembreTerapia);
        chipDom= findViewById(R.id.chip_dom);
        chipSeg = findViewById(R.id.chip_seg);
        chipTer = findViewById(R.id.chip_ter);
        chipQua = findViewById(R.id.chip_qua);
        chipQui = findViewById(R.id.chip_qui);
        chipSex = findViewById(R.id.chip_sex);
        chipSab = findViewById(R.id.chip_sab);
    }

    protected void salvarNoBancoDeDados(){
        String nome = et_nome.getText().toString();
        String end = et_endereco.getText().toString();
        String profissional = et_profissional.getText().toString();
        String horario = et_horario.getText().toString();
        String tel = et_telefone.getText().toString();
        String id = getIntent().getStringExtra("id");
        Map<String, Object> terapiaMap = new HashMap<>();
        terapiaMap.put("nome", nome);
        terapiaMap.put("endereco", end);
        terapiaMap.put("profissional", profissional);
        terapiaMap.put("horario", horario);
        terapiaMap.put("telefone", tel);
        terapiaMap.put("lembre-me", swt_lembre.isChecked());
        terapiaMap.put("id do idoso", id);

        firebaseFirestore.collection("Terapias")
                .add(terapiaMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("banco_dados_salvo","Sucesso ao salvar dados!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("erro_banco_dados", "Erro ao sarvar dados!");
                    }
                });
    }
}