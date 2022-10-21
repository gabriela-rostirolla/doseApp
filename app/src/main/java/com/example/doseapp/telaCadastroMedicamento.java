package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class telaCadastroMedicamento extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private EditText et_nomeMed, et_dose, et_recomendacao, et_concentracao, et_intervalo, et_observacoes;
    private TextView tv_hrInicial, tv_dataInicio, tv_dataFim;
    private Button btn_salvar;
    private Spinner spiIntervalo, spiVia;
    private CheckBox usoContinuo;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
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
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (idMed == null) {
            actionBar.setTitle(R.string.cadastrar_medicamento);
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCampos()) {
                        salvarNoBancoDeDados();
                        definirAlarme();
                        finish();
                    }
                }
            });
        } else {
            actionBar.setTitle(R.string.editar);
            preencherDadosMedicamento();
            btn_salvar.setText(R.string.editar);
            tv_dataFim.setTextColor(Color.BLACK);
            tv_dataInicio.setTextColor(Color.BLACK);
            tv_hrInicial.setTextColor(Color.BLACK);
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCampos()) {
                        editarBancoDeDados();
                        if (swt_lembre.isChecked()) {
                            definirAlarme();
                        }
                        finish();
                    }
                }
            });
        }

        tv_dataFim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendario(tv_dataFim);
            }
        });

        tv_dataFim.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tv_dataFim.setText(R.string.data_fim);
                tv_dataFim.setTextColor(Color.parseColor("#747474"));
                return false;
            }
        });

        tv_dataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendario(tv_dataInicio);
            }
        });

        tv_dataInicio.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tv_dataInicio.setText(R.string.data_inicio);
                tv_dataInicio.setTextColor(Color.parseColor("#747474"));
                return false;
            }
        });

        tv_hrInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarRelogio(tv_hrInicial);
            }
        });

        tv_hrInicial.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                tv_hrInicial.setText(R.string.hora_inicial);
                tv_hrInicial.setTextColor(Color.parseColor("#747474"));
                return false;
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

    protected void mostrarCalendario(TextView tv) {
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
                        tv.setText(dia + "/" + mes + "/" + i);
                        tv.setTextColor(Color.BLACK);
                    }
                }, ano, mes, dia);
        datePickerDialog.show();
    }

    protected void mostrarRelogio(TextView tv) {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(telaCadastroMedicamento.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int i, int i2) {
                        if (i2 < 10) {
                            tv.setText(i + ":" + 0 + i2);
                            tv.setTextColor(Color.BLACK);
                        } else {
                            tv.setText(i + ":" + i2);
                            tv.setTextColor(Color.BLACK);
                        }
                    }
                }, hora, min, true);
        timePickerDialog.show();
    }

    protected void gerarToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    protected void inicializarComponentes() {
        spiVia = findViewById(R.id.spiVia);
        et_nomeMed = findViewById(R.id.et_nomeMedicamento);
        et_concentracao = findViewById(R.id.et_concentracao);
        et_recomendacao = findViewById(R.id.et_recomendacao);
        et_dose = findViewById(R.id.et_dose);
        et_intervalo = findViewById(R.id.et_intervalo);
        spiIntervalo = findViewById(R.id.spi_intervalo);
        tv_hrInicial = findViewById(R.id.tv_horaInicial);
        usoContinuo = findViewById(R.id.cb_usoContinuo);
        tv_dataInicio = findViewById(R.id.tv_dataInicio);
        tv_dataFim = findViewById(R.id.tv_dataFim);
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
        medicamentoMap.put("hora inicial", tv_hrInicial.getText().toString());
        medicamentoMap.put("uso continuo", usoContinuo.isChecked());
        medicamentoMap.put("data inicio", tv_dataInicio.getText().toString());
        medicamentoMap.put("data fim", tv_dataFim.getText().toString());
        medicamentoMap.put("observacoes", et_observacoes.getText().toString());
        medicamentoMap.put("lembre-me", swt_lembre.isChecked());
        medicamentoMap.put("id do idoso", getIntent().getStringExtra("id"));
        medicamentoMap.put("dia de criacao", new Date());
        medicamentoMap.put("lista dos horarios do medicamento", calcularHorarioDosMedicamento());

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

    protected boolean validarCampos() {
        String nome = et_nomeMed.getText().toString();
        String concentracao = et_concentracao.getText().toString();
        String dose = et_dose.getText().toString();
        String intervalo = et_intervalo.getText().toString();
        String hora_inicial = tv_hrInicial.getText().toString();
        String data_inicio = tv_dataInicio.getText().toString();
        String data_fim = tv_dataFim.getText().toString();
        String recomendacao = et_recomendacao.getText().toString();

        if (nome.isEmpty()) {
            gerarToast(getString(R.string.nomeMedNaoInform));
            et_nomeMed.requestFocus();
            return false;
        } else if (nome.length() < 3) {
            et_nomeMed.requestFocus();
            gerarToast(getString(R.string.nomeMedInvalido));
            return false;
        } else if (concentracao.isEmpty()) {
            gerarToast(getString(R.string.concMedNaoInform));
            et_concentracao.requestFocus();
            return false;
        } else if (recomendacao.isEmpty()) {
            gerarToast(getString(R.string.recMedNaoInform));
            et_recomendacao.requestFocus();
            return false;
        } else if (dose.isEmpty()) {
            gerarToast(getString(R.string.doseMedNaoInform));
            et_dose.requestFocus();
            return false;
        } else if (intervalo.isEmpty()) {
            gerarToast(getString(R.string.intervMedNaoInform));
            et_intervalo.requestFocus();
            return false;
        } else if (24 % Integer.parseInt(intervalo) != 0) {
            gerarToast(getString(R.string.intervMedInvalido));
            et_intervalo.requestFocus();
            return false;
        } else if (Integer.parseInt(intervalo) > 24) {
            gerarToast(getString(R.string.intervMedMaiorQue24));
            et_intervalo.requestFocus();
            return false;
        } else if (hora_inicial.isEmpty()) {
            gerarToast(getString(R.string.hrInicMedNaoInform));
            return false;
        } else if (data_inicio.isEmpty()) {
            gerarToast(getString(R.string.dataInicMedNaoInform));
            return false;
        } else if (!usoContinuo.isChecked() && data_fim.isEmpty()) {
            gerarToast(getString(R.string.dataFimMedNaoInform));
            return false;
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
                tv_hrInicial.setText(value.getString("hora inicial"));
                boolean uso = Boolean.TRUE.equals(value.getBoolean("uso continuo"));
                usoContinuo.setChecked(uso);
                tv_dataInicio.setText(value.getString("data inicio"));
                tv_dataFim.setText(value.getString("data fim"));
                boolean lembre = Boolean.TRUE.equals(value.getBoolean("lembre-me"));
                swt_lembre.setChecked(lembre);
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
        String hora_inicial = tv_hrInicial.getText().toString();
        boolean uso_continuo = usoContinuo.isChecked();
        String data_inicio = tv_dataInicio.getText().toString();
        String data_fim = tv_dataFim.getText().toString();
        String observacoes = et_observacoes.getText().toString();
        boolean lembre = swt_lembre.isChecked();

        firebaseFirestore.collection("Medicamento").document(idMed)
                .update("via", via, "nome", nome,
                        "concentracao", concentracao,
                        "recomendacao ou finalidade", recomendacao,
                        "dose", dose,
                        "intervalo", intervalo,
                        "unidade intervalo", unidade_intervalo,
                        "hora inicial", hora_inicial,
                        "uso continuo", uso_continuo,
                        "data inicio", data_inicio,
                        "data fim", data_fim,
                        "observacoes", observacoes,
                        "lista dos horarios do medicamento", calcularHorarioDosMedicamento(),
                        "lembre-me", lembre)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                    }
                });
    }

    protected List<String> calcularHorarioDosMedicamento() {
        String hr = tv_hrInicial.getText().toString();
        String proxHr[] = hr.split(":");
        List<String> hrMedicamentos = new ArrayList<>();

        int intervalo = Integer.parseInt(et_intervalo.getText().toString());
        int horario = Integer.parseInt(proxHr[0]);

        for (int i = 0; i < 24 / intervalo; i++) {
            hrMedicamentos.add(horario + ":" + proxHr[1]);
            horario = horario + intervalo;
            if (horario > 24) {
                horario = horario - 24;
            }
        }
        return hrMedicamentos;
    }

    private void definirAlarme() {
        List<String> list = calcularHorarioDosMedicamento();

        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
            Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
            String[] hr = list.get(i).split(":");
            intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(hr[0]));
            intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(hr[1]));
            intent.putExtra(AlarmClock.EXTRA_MESSAGE, et_nomeMed.getText().toString());
            startActivity(intent);
        }
    }
}