package com.example.doseapp.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.example.doseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class telaCadastroDiarioAlimentacao extends AppCompatActivity {
    private Button btn_salvar;
    private EditText et_refeicao, et_lanche, et_outro, et_obs, et_cuidador;
    private TextView tv_horario;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_diario_alimentacao);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        inicializarComponentes();

        String id = getIntent().getStringExtra("id");

        if (id == null) {
            actionBar.setTitle(R.string.cadastrar_alimentacao);
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCampos()) {
                        salvarBancoDeDados();
                        finish();
                    }
                }
            });
        } else {
            actionBar.setTitle(R.string.editar);
            btn_salvar.setText(R.string.editar);
            preencherDados(id);
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCampos()) {
                        editarDados(id);
                        finish();
                    }
                }
            });
        }

        tv_horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarRelogio(tv_horario);
            }
        });

    }

    protected void editarDados(String id) {
        String refeicao = et_refeicao.getText().toString();
        String lanche = et_lanche.getText().toString();
        String outro = et_outro.getText().toString();
        String obs = et_obs.getText().toString();
        String turno = getIntent().getStringExtra("turno");
        String cuidador = et_cuidador.getText().toString();

        DocumentReference doc = firebaseFirestore.collection("Alimentacao").document(id);
        doc.update("cuidador", cuidador,
                        "refeicao", refeicao,
                        "lanche", lanche,
                        "outro", outro,
                        "horario", tv_horario.getText().toString(),
                        "observacao", obs,
                        "turno", turno)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        gerarToast(getString(R.string.dadosAtualizados));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        gerarToast(getString(R.string.falhaAoAtualizarDados));
                    }
                });
    }

    protected void mostrarRelogio(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(telaCadastroDiarioAlimentacao.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int i, int i2) {
                        if (i2 < 10) {
                            textView.setText(i + ":" + 0 + i2);
                            textView.setTextColor(Color.BLACK);
                        } else {
                            textView.setText(i + ":" + i2);
                            textView.setTextColor(Color.BLACK);
                        }
                    }
                }, hora, min, true);
        timePickerDialog.show();
    }

    protected void inicializarComponentes() {
        btn_salvar = findViewById(R.id.btn_salvarAlimentacao);
        et_refeicao = findViewById(R.id.et_refeicao);
        et_lanche = findViewById(R.id.et_lancheDiario);
        et_outro = findViewById(R.id.et_outrosDiario);
        et_obs = findViewById(R.id.et_obsDiario);
        et_cuidador = findViewById(R.id.et_cuidadorResp);
        tv_horario = findViewById(R.id.et_horarioRef);
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

    protected void salvarBancoDeDados() {
        String refeicao = et_refeicao.getText().toString();
        String lanche = et_lanche.getText().toString();
        String outro = et_outro.getText().toString();
        String obs = et_obs.getText().toString();
        String data = getIntent().getStringExtra("dia");
        String diario_id = getIntent().getStringExtra("diario id");
        String turno = getIntent().getStringExtra("turno");
        String cuidador = et_cuidador.getText().toString();

        Map<String, Object> alimentacaoMap = new HashMap<>();
        alimentacaoMap.put("refeicao", refeicao);
        alimentacaoMap.put("lanche", lanche);
        alimentacaoMap.put("outro", outro);
        alimentacaoMap.put("turno", turno);
        alimentacaoMap.put("dia", data);
        alimentacaoMap.put("observacao", obs);
        alimentacaoMap.put("diario id", diario_id);
        alimentacaoMap.put("cuidador", cuidador);
        alimentacaoMap.put("horario", tv_horario.getText().toString());

        firebaseFirestore.collection("Alimentacao")
                .add(alimentacaoMap)
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

    protected boolean validarCampos() {
        if (tv_horario.getText().toString().isEmpty()) {
            gerarToast(getString(R.string.hr_vazio));
            return false;
        } else if (et_cuidador.getText().toString().isEmpty()) {
            gerarToast(getString(R.string.cuidador_vazio));
            et_cuidador.findFocus();
            return false;
        } else if (et_lanche.getText().toString().isEmpty() &&
                et_obs.getText().toString().isEmpty() &&
                et_refeicao.getText().toString().isEmpty() &&
                et_outro.getText().toString().isEmpty()
        ) {
            gerarToast(getString(R.string.campos_atividade_vazios));
            return false;
        }
        return true;
    }

    protected void preencherDados(String id) {
        DocumentReference doc = firebaseFirestore.collection("Alimentacao").document(id);

        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                et_cuidador.setText(value.getString("cuidador"));
                tv_horario.setText(value.getString("horario"));
                et_lanche.setText(value.getString("lanche"));
                et_obs.setText(value.getString("observacao"));
                et_outro.setText(value.getString("outro"));
                et_refeicao.setText(value.getString("refeicao"));
            }
        });
    }

    protected void gerarToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}