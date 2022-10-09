package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class telaCadastroReceita extends AppCompatActivity {

    private EditText et_nome, et_hospital, et_tel, et_profissional;
    private TextView et_data, et_dataRen;
    private Button btn_salvar;
    private DatePickerDialog.OnDateSetListener dateSetListenerData;
    private DatePickerDialog.OnDateSetListener dateSetListenerDataRen;
    private String[] mensagens = {"Preencha todos os campos"};
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static String idRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_receita);
        inicializarComponentes();

        idRec = getIntent().getStringExtra("id receita");

        if (idRec == null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.cadastrar_receita);
            actionBar.setDisplayHomeAsUpEnabled(true);

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
        } else {
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

        et_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int ano = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(telaCadastroReceita.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int i, int i2, int i3) {
                                i2++;

                                String mes = "";
                                String dia = "";
                                if (i2 < 10) mes = "0" + i2;
                                else mes = String.valueOf(i2);
                                if (i3 < 10) dia = "0" + i3;
                                else dia = String.valueOf(i3);
                                et_data.setText(dia + "/" + mes + "/" + i);
                                et_data.setTextColor(Color.BLACK);
                            }
                        }, ano, mes, dia);
                datePickerDialog.show();
            }
        });

        et_dataRen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int ano = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(telaCadastroReceita.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int i, int i2, int i3) {
                                i2++;

                                String mes = "";
                                String dia = "";
                                if (i2 < 10) mes = "0" + i2;
                                else mes = String.valueOf(i2);
                                if (i3 < 10) dia = "0" + i3;
                                else dia = String.valueOf(i3);
                                et_dataRen.setText(dia + "/" + mes + "/" + i);
                                et_dataRen.setTextColor(Color.BLACK);
                            }
                        }, ano, mes, dia);
                datePickerDialog.show();
            }
        });

    }

    protected void inicializarComponentes() {
        et_nome = findViewById(R.id.et_nomeReceita);
        et_data = findViewById(R.id.et_dataReceita);
        et_hospital = findViewById(R.id.et_hospitalReceita);
        et_tel = findViewById(R.id.et_telefoneHospital);
        et_profissional = findViewById(R.id.et_profissionalReceita);
        et_dataRen = findViewById(R.id.et_dataRenReceita);
        btn_salvar = findViewById(R.id.btn_salvarCadReceita);
    }

    protected void salvarNoBancoDeDados() {
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
                        Log.d("banco_dados_salvos", "Sucesso ao salvar dados!" + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("erro_banco_dados", "Erro ao salvar dados!", e);
                    }
                });
    }

    public void editarBancoDeDados() {
        String nome = et_nome.getText().toString();
        String data = et_data.getText().toString();
        String hosp = et_hospital.getText().toString();
        String tel = et_tel.getText().toString();
        String profissional = et_profissional.getText().toString();
        String dataRen = et_dataRen.getText().toString();

        firebaseFirestore.collection("Receitas").document(idRec)
                .update("nome", nome, "data", data, "hospital", hosp, "telefone", tel, "profissional", profissional, "data para renovar", dataRen)
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