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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
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

public class telaEditarMedicamento extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private EditText et_nomeMed, et_posologia, et_hrInicial, et_dose, et_dataInicio, et_dataFim, et_recomendacao;
    private Button btn_salvar;
    private String idMed;
    private String[] mensagens = {"Preencha todos os campos", "Digite uma data válida", "Digite um nome com mais de 3 letras", "Digite uma funcionalidade com mais de 3 letras", "Não foi possivel editar dados"};
    private Spinner spiPosologia, spiMedicamento, spiVia;
    private ImageButton ic_calendar_dataInicio, ic_calendar_dataFim, ic_watch;
    private DatePickerDialog.OnDateSetListener dateSetListenerInicio;
    private DatePickerDialog.OnDateSetListener dateSetListenerFim;
    private static int anoIni, anoFim, mesIni, mesFim, diaIni, diaFim, hora, min;
    private static ArrayAdapter<CharSequence> adapter, adapter1, adapter2;

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

        adapter = ArrayAdapter.createFromResource(this, R.array.unidades_posologia, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiPosologia.setAdapter(adapter);

        adapter1 = ArrayAdapter.createFromResource(this, R.array.unidades_medicamentos, android.R.layout.simple_spinner_dropdown_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiMedicamento.setAdapter(adapter1);

        adapter2 = ArrayAdapter.createFromResource(this, R.array.op_via, android.R.layout.simple_spinner_dropdown_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiVia.setAdapter(adapter2);

        ic_calendar_dataInicio.setOnClickListener(new View.OnClickListener() {
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

        ic_calendar_dataFim.setOnClickListener(new View.OnClickListener() {
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

        ic_watch.setOnClickListener(new View.OnClickListener() {
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
        et_nomeMed = findViewById(R.id.et_nomeMedicamento);
        et_posologia = findViewById(R.id.et_posologia);
        ic_watch = findViewById(R.id.ic_watch);
        et_hrInicial = findViewById(R.id.et_horaInicial);
        et_dose = findViewById(R.id.et_dose);
        et_dataInicio = findViewById(R.id.et_dataInicio);
        et_dataFim = findViewById(R.id.et_dataFim);
        btn_salvar = findViewById(R.id.btn_salvarCadMedicamento);
        spiPosologia = findViewById(R.id.spiPosologia);
        spiMedicamento = findViewById(R.id.spiMedicamentos);
        ic_calendar_dataInicio = findViewById(R.id.ic_calendar_dataInicio);
        ic_calendar_dataFim = findViewById(R.id.ic_calendar_dataFim);
        et_recomendacao = findViewById(R.id.et_recomendacao);
        spiVia = findViewById(R.id.spiVia);
    }

    public void preencherDadosMedicamento() {
        DocumentReference document = firebaseFirestore.collection("Medicamento").document(idMed);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Medicamento med = new Medicamento();
                et_nomeMed.setText(value.getString("nome"));
                et_recomendacao.setText(value.getString("recomendacoes"));
                et_posologia.setText(value.getString("intervalo"));
                et_dose.setText(value.getString("dose"));
                et_hrInicial.setText(value.getString("hora inicial"));
                et_dataInicio.setText(value.getString("data inicio"));
                et_dataFim.setText(value.getString("data fim"));
                spiMedicamento.setSelection(adapter1.getPosition(value.getString("unidade dose")));
                spiPosologia.setSelection(adapter.getPosition(value.getString("unidade intervalo")));
                spiVia.setSelection(adapter2.getPosition(value.getString("via")));
            }
        });
    }

    public void editarBancoDeDados() {
        String nomeMed = et_nomeMed.getText().toString();
        String intervalo = et_posologia.getText().toString();
        String hrInicial = et_hrInicial.getText().toString();
        String dose = et_dose.getText().toString();
        String dataInicio = et_dataInicio.getText().toString();
        String dataFim = et_dataFim.getText().toString();
        String unPosologia = spiPosologia.getSelectedItem().toString();
        String unMed = spiMedicamento.getSelectedItem().toString();
        String recomendacoes = et_recomendacao.getText().toString();
        String opVia = spiVia.getSelectedItem().toString();

        firebaseFirestore.collection("Medicamento").document(idMed)
                .update("nome", nomeMed, "intervalo", intervalo, "hora inicial", hrInicial, "dose", dose, "data inicio", dataInicio, "data fim", dataFim, "recomendacoes", recomendacoes, "unidade medicamento", unMed, "unidade posologia", unPosologia, "via", opVia)
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
        String posologia = et_posologia.getText().toString();
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