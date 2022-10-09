package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class telaCadastroMedicamento extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private EditText et_nomeMed, et_dose, et_recomendacao, et_concentracao, et_intervalo, et_observacoes;
    private TextView et_hrInicial, et_dataInicio, et_dataFim;
    private Button btn_salvar;
    private String[] mensagens = {"Preencha todos os campos", "Digite uma data válida", "Digite um nome com mais de 3 letras", "Digite uma funcionalidade com mais de 3 letras", "Não foi possivel editar dados"};
    private Spinner spiIntervalo, spiVia;
    private DatePickerDialog.OnDateSetListener dateSetListenerInicio;
    private DatePickerDialog.OnDateSetListener dateSetListenerFim;
    private CheckBox usoContinuo;
    private Switch swt_lembre;
    private static String idMed;
    private ArrayAdapter<CharSequence> adapter, adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_medicamento);
        inicializarComponentes();

        adapter = ArrayAdapter.createFromResource(this, R.array.unidade_intervalo, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiIntervalo.setAdapter(adapter);

        adapter2 = ArrayAdapter.createFromResource(this, R.array.op_via, android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiVia.setAdapter(adapter2);

        idMed = getIntent().getStringExtra("id medicamento");

        if (idMed == null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.cadastrar_medicamento);
            actionBar.setDisplayHomeAsUpEnabled(true);
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCampos(view) == true) {
                        salvarNoBancoDeDados();
                        finish();
                    }
                }
            });
        } else {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.editar);
            actionBar.setDisplayHomeAsUpEnabled(true);
            preencherDadosMedicamento();
            btn_salvar.setText("Editar");
            et_dataFim.setTextColor(Color.BLACK);
            et_dataInicio.setTextColor(Color.BLACK);
            et_hrInicial.setTextColor(Color.BLACK);
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCampos(view) == true) {
                        editarBancoDeDados();
                        finish();
                    }
                }
            });
        }

        et_dataFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int ano = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(telaCadastroMedicamento.this,
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
                                et_dataFim.setText(dia + "/" + mes + "/" + i);
                                et_dataFim.setTextColor(Color.BLACK);
                            }
                        }, ano, mes, dia);
                datePickerDialog.show();
            }
        });

        et_dataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int ano = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(telaCadastroMedicamento.this,
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
                                et_dataInicio.setText(dia + "/" + mes + "/" + i);
                                et_dataInicio.setTextColor(Color.BLACK);
                            }
                        }, ano, mes, dia);
                datePickerDialog.show();
            }
        });

        et_hrInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hora = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(telaCadastroMedicamento.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int i, int i2) {
                                if (i2 < 10) {
                                    et_hrInicial.setText(i + ":" + 0 + i2);
                                    et_hrInicial.setTextColor(Color.BLACK);
                                } else {
                                    et_hrInicial.setText(i + ":" + i2);
                                    et_hrInicial.setTextColor(Color.BLACK);
                                }
                            }
                        }, hora, min, true);
                timePickerDialog.show();
            }
        });

    }

    protected void gerarSnackBar(View view, String texto) {
        Snackbar snackbar = Snackbar.make(view, texto, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    protected void inicializarComponentes() {
        spiVia = findViewById(R.id.spiVia);
        et_nomeMed = findViewById(R.id.et_nomeMedicamento);
        et_concentracao = findViewById(R.id.et_concentracao);
        et_recomendacao = findViewById(R.id.et_recomendacao);
        et_dose = findViewById(R.id.et_dose);
        et_intervalo = findViewById(R.id.et_intervalo);
        spiIntervalo = findViewById(R.id.spiPosologia);
        et_hrInicial = findViewById(R.id.et_horaInicial);
        usoContinuo = findViewById(R.id.cb_usoContinuo);
        et_dataInicio = findViewById(R.id.et_dataInicio);
        et_dataFim = findViewById(R.id.et_dataFim);
        et_observacoes = findViewById(R.id.et_obs);
        swt_lembre = findViewById(R.id.swt_lembreMedicamento);
        btn_salvar = findViewById(R.id.btn_salvarCadMedicamento);
    }

    protected void salvarNoBancoDeDados() {

        Map<String, Object> medicamentoMap = new HashMap<>();
        medicamentoMap.put("via", spiVia.getSelectedItem().toString());
        medicamentoMap.put("nome", et_nomeMed.getText().toString());
        medicamentoMap.put("concentracao", et_concentracao.getText().toString());
        medicamentoMap.put("recomendacao ou finalidade", et_recomendacao.getText().toString());
        medicamentoMap.put("dose", et_dose.getText().toString());
        medicamentoMap.put("intervalo", et_intervalo.getText().toString());
        medicamentoMap.put("unidade intervalo", spiIntervalo.getSelectedItem().toString());
        medicamentoMap.put("hora inicial", et_hrInicial.getText().toString());
        medicamentoMap.put("uso continuo", usoContinuo.isChecked());
        medicamentoMap.put("data inicio", et_dataInicio.getText().toString());
        medicamentoMap.put("data fim", et_dataFim.getText().toString());
        medicamentoMap.put("observacoes", et_observacoes.getText().toString());
        medicamentoMap.put("lembre-me", swt_lembre.isChecked());
        medicamentoMap.put("id do idoso", getIntent().getStringExtra("id"));
        medicamentoMap.put("dia de criacao", new Date());

        firebaseFirestore.collection("Medicamento")
                .add(medicamentoMap)
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

    protected boolean validarCampos(View view) {
        String nome = et_nomeMed.getText().toString();
        String concentracao = et_concentracao.getText().toString();
        String dose = et_dose.getText().toString();
        String intervalo = et_intervalo.getText().toString();
        String hora_inicial = et_hrInicial.getText().toString();
        String data_inicio = et_dataInicio.getText().toString();
        String data_fim = et_dataFim.getText().toString();

        if (usoContinuo.isChecked()) {
            if (nome.isEmpty() || concentracao.isEmpty() || dose.isEmpty() || intervalo.isEmpty() || hora_inicial.isEmpty() || data_inicio.isEmpty()) {
                gerarSnackBar(view, mensagens[0]);
                return false;
            } else if (nome.length() < 3) {
                et_nomeMed.setFocusable(true);
                gerarSnackBar(view, mensagens[2]);
                return false;
            }
        } else {
            if (nome.isEmpty() || concentracao.isEmpty() || dose.isEmpty() || intervalo.isEmpty() || hora_inicial.isEmpty() || data_inicio.isEmpty() || data_fim.isEmpty()) {
                gerarSnackBar(view, mensagens[0]);
                return false;
            } else if (nome.length() < 3) {
                et_nomeMed.setFocusable(true);
                gerarSnackBar(view, mensagens[2]);
                return false;
            }
        }
        return true;
    }

    protected void preencherDadosMedicamento() {
        DocumentReference document = firebaseFirestore.collection("Medicamento").document(idMed);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                spiVia.setSelection(adapter2.getPosition(value.getString("via")));
                et_nomeMed.setText(value.getString("nome"));
                et_concentracao.setText(value.getString("concentracao"));
                et_recomendacao.setText(value.getString("recomendacao ou finalidade"));
                et_dose.setText(value.getString("dose"));
                et_intervalo.setText(value.getString("intervalo"));
                spiIntervalo.setSelection(adapter.getPosition(value.getString("unidade intervalo")));
                et_hrInicial.setText(value.getString("hora inicial"));
                boolean uso = value.getBoolean("uso continuo");
                usoContinuo.setChecked(uso);
                et_dataInicio.setText(value.getString("data inicio"));
                et_dataFim.setText(value.getString("data fim"));
                swt_lembre.setChecked(value.getBoolean("lembre-me"));
                et_observacoes.setText(value.getString("observacoes"));
            }
        });
    }

    protected void editarBancoDeDados() {
        String via = spiVia.getSelectedItem().toString();
        String nome = et_nomeMed.getText().toString();
        String concentracao = et_concentracao.getText().toString();
        String recomendacao = et_recomendacao.getText().toString();
        String dose = et_dose.getText().toString();
        String intervalo = et_intervalo.getText().toString();
        String unidade_intervalo = spiIntervalo.getSelectedItem().toString();
        String hora_inicial = et_hrInicial.getText().toString();
        boolean uso_continuo = usoContinuo.isChecked();
        String data_inicio = et_dataInicio.getText().toString();
        String data_fim = et_dataFim.getText().toString();
        String observacoes = et_observacoes.getText().toString();
        boolean lembre = swt_lembre.isChecked();

        firebaseFirestore.collection("Medicamento").document(idMed)
                .update("via", via, "nome", nome, "concentracao", concentracao, "recomendacao ou finalidade", recomendacao, "dose", dose, "intervalo", intervalo, "unidade intervalo", unidade_intervalo, "hora inicial", hora_inicial, "uso continuo", uso_continuo, "data inicio", data_inicio, "data fim", data_fim, "observacoes", observacoes, "lembre-me", lembre)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                    }
                });
    }
}