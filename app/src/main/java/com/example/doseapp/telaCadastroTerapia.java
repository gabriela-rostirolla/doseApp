package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class telaCadastroTerapia extends AppCompatActivity {
    private EditText et_nome, et_profissional, et_endereco, et_telefone;
    private Button btn_salvar;
    private TextView et_horario;
    private Switch swt_lembre;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String[] mensagens = {"Preencha todos os campos", "Digite um nome mais longo", "Digite um endereço válido", "Digite um nome válido de profissional", "Digite um número de telefone válido"};
    private Chip chipDom, chipSeg, chipTer, chipQua, chipQui, chipSex, chipSab;
    private static String idTerapia, nomeIdoso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_terapia);
        inicializarComponentes();

        idTerapia = getIntent().getStringExtra("id terapia");
        if (idTerapia == null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.cadastrar_terapia);
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
            preencherDadosTerapia();
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    String[] hr = et_horario.getText().toString().split(":");
                    if (swt_lembre.isChecked()) {
                        ArrayList<Integer> list = new ArrayList<>();
                        if (chipDom.isChecked()) list.add(Calendar.SUNDAY);
                        if (chipSeg.isChecked()) list.add(Calendar.MONDAY);
                        if (chipTer.isChecked()) list.add(Calendar.TUESDAY);
                        if (chipQua.isChecked()) list.add(Calendar.WEDNESDAY);
                        if (chipQui.isChecked()) list.add(Calendar.THURSDAY);
                        if (chipSex.isChecked()) list.add(Calendar.SUNDAY);
                        if (chipSab.isChecked()) list.add(Calendar.SATURDAY);

                        intent.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(hr[0]));
                        intent.putExtra(AlarmClock.EXTRA_DAYS, list);
                        intent.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(hr[1]));
                        intent.putExtra(AlarmClock.EXTRA_MESSAGE,et_nome.getText().toString());

                        startActivity(intent);
                    }
                    editarBancoDeDados();
                    finish();
                }
            });
        }

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
                                }
                            }
                        }, hora, min, true);
                timePickerDialog.show();
            }
        });
    }

    protected void inicializarComponentes() {
        et_nome = findViewById(R.id.et_nomeTerapia);
        et_endereco = findViewById(R.id.et_enderecoTerapia);
        et_profissional = findViewById(R.id.et_profissionalTerapia);
        et_horario = findViewById(R.id.et_horaTerapia);
        et_telefone = findViewById(R.id.et_telefoneTerapia);
        btn_salvar = findViewById(R.id.btn_salvarTerapia);
        swt_lembre = findViewById(R.id.swt_lembreTerapia);
        chipDom = findViewById(R.id.chip_dom);
        chipSeg = findViewById(R.id.chip_seg);
        chipTer = findViewById(R.id.chip_ter);
        chipQua = findViewById(R.id.chip_qua);
        chipQui = findViewById(R.id.chip_qui);
        chipSex = findViewById(R.id.chip_sex);
        chipSab = findViewById(R.id.chip_sab);
    }

    protected void salvarNoBancoDeDados() {
        String nome = et_nome.getText().toString();
        String end = et_endereco.getText().toString();
        String profissional = et_profissional.getText().toString();
        String horario = et_horario.getText().toString();
        String tel = et_telefone.getText().toString();
        String id = getIntent().getStringExtra("id");
        List<String> listDiasSemana = new ArrayList<>();

        if (chipDom.isChecked()) listDiasSemana.add("dom");
        if (chipSeg.isChecked()) listDiasSemana.add("seg");
        if (chipTer.isChecked()) listDiasSemana.add("ter");
        if (chipQua.isChecked()) listDiasSemana.add("qua");
        if (chipQui.isChecked()) listDiasSemana.add("qui");
        if (chipSex.isChecked()) listDiasSemana.add("sex");
        if (chipSab.isChecked()) listDiasSemana.add("sab");

        Map<String, Object> terapiaMap = new HashMap<>();
        terapiaMap.put("nome", nome);
        terapiaMap.put("endereco", end);
        terapiaMap.put("profissional", profissional);
        terapiaMap.put("horario", horario);
        terapiaMap.put("telefone", tel);
        terapiaMap.put("lembre-me", swt_lembre.isChecked());
        terapiaMap.put("id do idoso", id);
        terapiaMap.put("dias da semana", listDiasSemana);
        terapiaMap.put("dia de criacao", new Date());

        firebaseFirestore.collection("Terapias")
                .add(terapiaMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("banco_dados_salvo", "Sucesso ao salvar dados!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("erro_banco_dados", "Erro ao sarvar dados!");
                    }
                });
    }

    public void preencherDadosTerapia() {
        DocumentReference document = firebaseFirestore.collection("Terapias").document(idTerapia);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                et_nome.setText(value.getString("nome"));
                et_profissional.setText(value.getString("profissional"));
                et_endereco.setText(value.getString("endereco"));
                et_horario.setText(value.getString("horario"));
                et_telefone.setText(value.getString("telefone"));
                boolean aux = value.getBoolean("lembre-me");
                swt_lembre.setChecked(aux);
                List<String> listDiasDaSemana = (List<String>) value.get("dias da semana");
                for (int i = 0; i < listDiasDaSemana.size(); i++) {
                    if (listDiasDaSemana.get(i).equals("dom")) chipDom.setChecked(true);
                    else if (listDiasDaSemana.get(i).equals("seg")) chipSeg.setChecked(true);
                    else if (listDiasDaSemana.get(i).equals("ter")) chipTer.setChecked(true);
                    else if (listDiasDaSemana.get(i).equals("qua")) chipQua.setChecked(true);
                    else if (listDiasDaSemana.get(i).equals("qui")) chipQui.setChecked(true);
                    else if (listDiasDaSemana.get(i).equals("sex")) chipSex.setChecked(true);
                    else if (listDiasDaSemana.get(i).equals("sab")) chipSab.setChecked(true);
                }
            }
        });
    }

    public void editarBancoDeDados() {
        String nome = et_nome.getText().toString();
        String profissional = et_profissional.getText().toString();
        String end = et_endereco.getText().toString();
        String horario = et_horario.getText().toString();
        String tel = et_telefone.getText().toString();
        boolean lembre = swt_lembre.isChecked();

        List<String> listDiasSemana = new ArrayList<>();
        if (chipDom.isChecked()) listDiasSemana.add("dom");
        if (chipSeg.isChecked()) listDiasSemana.add("seg");
        if (chipTer.isChecked()) listDiasSemana.add("ter");
        if (chipQua.isChecked()) listDiasSemana.add("qua");
        if (chipQui.isChecked()) listDiasSemana.add("qui");
        if (chipSex.isChecked()) listDiasSemana.add("sex");
        if (chipSab.isChecked()) listDiasSemana.add("sab");

        firebaseFirestore.collection("Terapias").document(idTerapia)
                .update("nome", nome, "profissional", profissional, "endereco", end, "horario", horario, "telefone", tel, "lembre-me", lembre, "dias da semana", listDiasSemana)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                    }
                });
    }

    public boolean validarCampos(View view) {
        String nome = et_nome.getText().toString();
        String end = et_endereco.getText().toString();
        String profissional = et_profissional.getText().toString();
        String horario = et_horario.getText().toString();
        String tel = et_telefone.getText().toString();

        if (nome.isEmpty() || end.isEmpty() || profissional.isEmpty() || horario.isEmpty() || tel.isEmpty()) {
            gerarSnackBar(view, mensagens[0]);
        } else if (nome.length() < 2) {
            gerarSnackBar(view, mensagens[1]);
            return false;
        } else if (end.length() < 3) {
            gerarSnackBar(view, mensagens[2]);
            return false;
        } else if (profissional.length() < 2) {
            gerarSnackBar(view, mensagens[3]);
            return false;
        } else if (tel.length() < 11) {
            gerarSnackBar(view, mensagens[4]);
        }
        return true;
    }

    public void gerarSnackBar(View view, String texto) {
        Snackbar snackbar = Snackbar.make(view, texto, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }
}