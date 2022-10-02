package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;
import java.util.Date;

public class telaEditarMedicamento extends AppCompatActivity {

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
    private ArrayAdapter<CharSequence> adapter, adapter2;
    private static String idMed;
    private static int anoIni, anoFim, mesIni, mesFim, diaIni, diaFim, hora, min;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_medicamento);
        inicializarComponentes();
        idMed = getIntent().getStringExtra("id medicamento");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.editar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        preencherDadosMedicamento();

        adapter = ArrayAdapter.createFromResource(this, R.array.unidade_intervalo, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiIntervalo.setAdapter(adapter);

        adapter2 = ArrayAdapter.createFromResource(this, R.array.op_via, android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiVia.setAdapter(adapter2);

        et_dataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                anoIni = calendar.get(Calendar.YEAR);
                diaIni = calendar.get(Calendar.DAY_OF_MONTH);
                mesIni = calendar.get(Calendar.MONTH);
                DatePickerDialog dialog = new DatePickerDialog(telaEditarMedicamento.this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListenerInicio, diaIni, mesIni, anoIni);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListenerInicio = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1++;

                et_dataInicio.setText(i2 + "/" + i1 + "/" + i);
            }
        };

        et_dataFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                anoFim = calendar.get(Calendar.YEAR);
                diaFim = calendar.get(Calendar.DAY_OF_MONTH);
                mesFim = calendar.get(Calendar.MONTH);
                DatePickerDialog dialog = new DatePickerDialog(telaEditarMedicamento.this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListenerFim, diaFim, mesFim, anoFim);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        et_hrInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                hora = calendar.get(Calendar.HOUR_OF_DAY);
                min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(telaEditarMedicamento.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int i,
                                                  int i2) {

                                et_hrInicial.setText(i + ":" + i2);
                            }
                        }, hora, min, false);
                timePickerDialog.show();
            }
        });

        dateSetListenerFim = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1++;
                et_dataFim.setText(i2 + "/" + i1 + "/" + i);
            }
        };

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

    protected void inicializarComponentes() {
        spiVia = findViewById(R.id.spiVia);
        et_nomeMed = findViewById(R.id.et_nomeMedicamento);
        et_concentracao = findViewById(R.id.et_concentracao);
        et_recomendacao = findViewById(R.id.et_recomendacao);
        et_dose = findViewById(R.id.et_dose);
        et_intervalo = findViewById(R.id.et_intervalo);
        spiIntervalo = findViewById(R.id.spiPosologia);
        et_hrInicial = findViewById(R.id.et_horaInicial);
        et_hrInicial.setTextColor(Color.BLACK);
        usoContinuo = findViewById(R.id.cb_usoContinuo);
        et_dataInicio = findViewById(R.id.et_dataInicio);
        et_dataInicio.setTextColor(Color.BLACK);
        et_dataFim = findViewById(R.id.et_dataFim);
        et_dataFim.setTextColor(Color.BLACK);
        et_observacoes = findViewById(R.id.et_obs);
        swt_lembre = findViewById(R.id.swt_lembreMedicamento);
        btn_salvar = findViewById(R.id.btn_salvarCadMedicamento);
    }

    public void preencherDadosMedicamento() {
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
                usoContinuo.setChecked(value.getBoolean("uso continuo"));
                et_dataInicio.setText(value.getString("data inicio"));
                et_dataFim.setText(value.getString("data fim"));
                swt_lembre.setChecked(value.getBoolean("lembre-me"));
                et_observacoes.setText(value.getString("observacoes"));
            }
        });
    }

    public void editarBancoDeDados() {

        String via =  spiVia.getSelectedItem().toString();
        String nome = et_nomeMed.getText().toString();
        String concentracao = et_concentracao.getText().toString();
        String recomendacao = et_recomendacao.getText().toString();
        String dose = et_dose.getText().toString();
        String intervalo = et_intervalo.getText().toString();
        String unidade_intervalo = spiIntervalo.getSelectedItem().toString();
        String hora_inicial = et_hrInicial.getText().toString();
        boolean uso_continuo = usoContinuo.isChecked();
        String data_inicio = et_dataInicio.getText().toString();
        String data_fim= et_dataFim.getText().toString();
        String observacoes = et_observacoes.getText().toString();
        boolean lembre= swt_lembre.isChecked();

        firebaseFirestore.collection("Medicamento").document(idMed)
                .update("via", via, "nome", nome, "concentracao", concentracao, "recomendacao/finalidade", recomendacao, "dose", dose, "intervalo", intervalo, "unidade intervalo", unidade_intervalo, "hora inicial", hora_inicial, "uso continuo", uso_continuo, "data inicio", data_inicio, "data fim", data_fim, "observacoes", observacoes, "lembre-me", lembre)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                    }
                });
    }

    public void gerarSnackBar(View view, String texto) {
        Snackbar snackbar = Snackbar.make(view, texto, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public boolean validarCampos(View view) {
        String nome = et_nomeMed.getText().toString();
        String horaInicial = et_hrInicial.getText().toString();
        String dose = et_dose.getText().toString();
        String posologia = et_intervalo.getText().toString();
        String dataInicio = et_dataInicio.getText().toString();
        String dataFim = et_dataFim.getText().toString();

        if (nome.isEmpty() || posologia.isEmpty() || horaInicial.isEmpty() || dose.isEmpty() || dataInicio.isEmpty() || dataFim.isEmpty()) {
            gerarSnackBar(view, mensagens[0]);
            return false;
        } else if (nome.length() < 3) {
            gerarSnackBar(view, mensagens[2]);
            return false;
        }
        return true;
    }

}