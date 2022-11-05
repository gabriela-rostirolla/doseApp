package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class telaCadastroTerapia extends AppCompatActivity {
    private EditText et_nome, et_profissional, et_endereco, et_telefone;
    private Button btn_salvar;
    private TextView et_horario;
    private Switch swt_lembre;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CheckBox chipDom, chipSeg, chipTer, chipQua, chipQui, chipSex, chipSab;
    private static String idTerapia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_terapia);
        inicializarComponentes();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        idTerapia = getIntent().getStringExtra("id terapia");
        if (idTerapia == null) {
            actionBar.setTitle(R.string.cadastrar_terapia);
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
            actionBar.setTitle(R.string.editar);
            preencherDadosTerapia();
            et_horario.setTextColor(Color.BLACK);
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (swt_lembre.isChecked()) {
                        definirAlarme();
                    }
                    editarBancoDeDados();
                    finish();
                }
            });
        }

        et_horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarRelogio(et_horario);
            }
        });
    }

    protected void mostrarRelogio(TextView tv) {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(telaCadastroTerapia.this,
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

    protected void definirAlarme() {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        String[] hr = et_horario.getText().toString().split(":");
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
        intent.putExtra(AlarmClock.EXTRA_MESSAGE, getIntent().getStringExtra("nome idoso") + "-" + et_nome.getText().toString());
        startActivity(intent);
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

    protected void preencherDadosTerapia() {
        DocumentReference document = firebaseFirestore.collection("Terapias").document(idTerapia);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                et_nome.setText(value.getString("nome"));
                et_profissional.setText(value.getString("profissional"));
                et_endereco.setText(value.getString("endereco"));
                et_horario.setText(value.getString("horario"));
                et_telefone.setText(value.getString("telefone"));
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

    protected void editarBancoDeDados() {
        String nome = et_nome.getText().toString();
        String profissional = et_profissional.getText().toString();
        String end = et_endereco.getText().toString();
        String horario = et_horario.getText().toString();
        String tel = et_telefone.getText().toString();

        List<String> listDiasSemana = new ArrayList<>();
        if (chipDom.isChecked()) listDiasSemana.add("dom");
        if (chipSeg.isChecked()) listDiasSemana.add("seg");
        if (chipTer.isChecked()) listDiasSemana.add("ter");
        if (chipQua.isChecked()) listDiasSemana.add("qua");
        if (chipQui.isChecked()) listDiasSemana.add("qui");
        if (chipSex.isChecked()) listDiasSemana.add("sex");
        if (chipSab.isChecked()) listDiasSemana.add("sab");

        firebaseFirestore.collection("Terapias").document(idTerapia)
                .update("nome", nome, "profissional", profissional, "endereco", end, "horario", horario, "telefone", tel, "dias da semana", listDiasSemana)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                    }
                });
    }

    protected boolean validarCampos(View view) {
        String nome = et_nome.getText().toString();
        String end = et_endereco.getText().toString();
        String profissional = et_profissional.getText().toString();
        String horario = et_horario.getText().toString();
        String tel = et_telefone.getText().toString();

        if (nome.isEmpty() || end.isEmpty() || profissional.isEmpty() || horario.isEmpty() || tel.isEmpty()) {
            gerarToast(getString(R.string.camposVazios));
        } else if (nome.length() < 3) {
            gerarToast(getString(R.string.nomeInv));
            return false;
        } else if (end.length() < 3) {
            gerarToast(getString(R.string.endInv));
            return false;
        } else if (profissional.length() < 2) {
            gerarToast(getString(R.string.profInv));
            return false;
        } else if (tel.length() < 11) {
            gerarToast(getString(R.string.telInv));
        }
        return true;
    }

    protected void gerarToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}