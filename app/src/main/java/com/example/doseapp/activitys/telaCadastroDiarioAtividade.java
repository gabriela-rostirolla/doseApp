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

public class telaCadastroDiarioAtividade extends AppCompatActivity {
    private Button btn_salvar;
    private EditText et_sono, et_exercicios, et_passeio, et_saude, et_outro, et_obs, et_cuidador;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private TextView tv_horario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_diario_atividade);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
            preencherCampos(id);
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

    protected void editarDados(String id){
        String sono = et_sono.getText().toString();
        String exerc =  et_exercicios.getText().toString();
        String passeio = et_passeio.getText().toString();
        String saude = et_saude.getText().toString();
        String outro = et_outro.getText().toString();
        String obs = et_obs.getText().toString();
        String cuidador = et_cuidador.getText().toString();
        String hr = tv_horario.getText().toString();

        DocumentReference doc = firebaseFirestore.collection("Diario atividades").document(id);
        doc.update("cuidador", cuidador,
                        "exercicios", exerc,
                        "sono", sono,
                        "outro", outro,
                        "horario", hr,
                        "observacao", obs,
                        "passeio", passeio,
                        "saude", saude)
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

    protected void preencherCampos(String id){
        DocumentReference doc = firebaseFirestore.collection("Diario atividades").document(id);

        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                et_cuidador.setText(value.getString("cuidador"));
                tv_horario.setText(value.getString("horario"));
                et_sono.setText(value.getString("sono"));
                et_obs.setText(value.getString("observacao"));
                et_outro.setText(value.getString("outro"));
                et_exercicios.setText(value.getString("exercicios"));
                et_passeio.setText(value.getString("passeio"));
                et_saude.setText(value.getString("saude"));
            }
        });
    }

    protected void inicializarComponentes() {
        et_cuidador = findViewById(R.id.et_cuidadorResp);
        btn_salvar = findViewById(R.id.btn_salvarDiario);
        et_sono = findViewById(R.id.et_sonoDiario);
        et_exercicios = findViewById(R.id.et_exercicioDiario);
        et_passeio = findViewById(R.id.et_passeioDiario);
        et_saude = findViewById(R.id.et_saudeDiario);
        et_outro = findViewById(R.id.et_outrosDiarioEt03);
        et_obs = findViewById(R.id.et_obsDiarioEt03);
        tv_horario = findViewById(R.id.et_horario);
    }

    protected void mostrarRelogio(TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(telaCadastroDiarioAtividade.this,
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

    protected void salvarBancoDeDados() {
        Map<String, Object> atividadesMap = new HashMap<>();
        atividadesMap.put("sono", et_sono.getText().toString());
        atividadesMap.put("exercicios", et_exercicios.getText().toString());
        atividadesMap.put("passeio", et_passeio.getText().toString());
        atividadesMap.put("saude", et_saude.getText().toString());
        atividadesMap.put("outro", et_outro.getText().toString());
        atividadesMap.put("observacao", et_obs.getText().toString());
        atividadesMap.put("turno", getIntent().getStringExtra("turno"));
        atividadesMap.put("dia", getIntent().getStringExtra("dia"));
        atividadesMap.put("diario id", getIntent().getStringExtra("diario id"));
        atividadesMap.put("cuidador", et_cuidador.getText().toString());
        atividadesMap.put("horario", tv_horario.getText().toString());

        firebaseFirestore.collection("Diario atividades")
                .add(atividadesMap)
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
            return false;
        } else if (et_exercicios.getText().toString().isEmpty() &&
                et_obs.getText().toString().isEmpty() &&
                et_passeio.getText().toString().isEmpty() &&
                et_saude.getText().toString().isEmpty() &&
                et_sono.getText().toString().isEmpty() &&
                et_outro.getText().toString().isEmpty()
        ) {
            gerarToast(getString(R.string.campos_atividade_vazios));
            return false;
        }
        return true;
    }

    protected void gerarToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}