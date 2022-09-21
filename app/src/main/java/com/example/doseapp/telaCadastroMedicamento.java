package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class telaCadastroMedicamento extends AppCompatActivity {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private EditText et_nomeMed, et_posologia, et_hrInicial, et_dose, et_dataInicio, et_dataFim, et_finalidade;
    private Button btn_salvar;
    private String [] mensagens = {"Preencha todos os campos"};
    private Spinner spiPosologia, spiMedicamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_medicamento);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.cadastrar_medicamento);
        actionBar.setDisplayHomeAsUpEnabled(true);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.unidades_posologia, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiPosologia.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.unidades_medicamentos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiMedicamento.setAdapter(adapter1);

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeMed = et_nomeMed.getText().toString();
                String posologia =  et_posologia.getText().toString();
                String hrInicial = et_hrInicial.getText().toString();
                String dose = et_dose.getText().toString();
                String dataInicio = et_dataInicio.getText().toString();
                String dataFim = et_dataFim.getText().toString();
                String finalidade= et_finalidade.getText().toString();
                if(nomeMed.isEmpty()||  posologia.isEmpty()||hrInicial.isEmpty()||dose.isEmpty()||dataInicio.isEmpty()||dataFim.isEmpty()||finalidade.isEmpty()){
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
        et_nomeMed = findViewById(R.id.et_nomeMedicamento);
        et_posologia = findViewById(R.id.et_posologia);
        et_hrInicial = findViewById(R.id.et_horaInicial);
        et_dose = findViewById(R.id.et_dose);
        et_dataInicio = findViewById(R.id.et_dataInicio);
        et_dataFim = findViewById(R.id.et_dataFim);
        et_finalidade = findViewById(R.id.et_finalidade);
        btn_salvar = findViewById(R.id.btn_salvarCadMedicamento);
        spiPosologia = findViewById(R.id.spiPosologia);
        spiMedicamento = findViewById(R.id.spiMedicamentos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_perfil, menu);
        return true;
    }

    protected void salvarNoBancoDeDados(){

        String nomeMed = et_nomeMed.getText().toString();
        String posologia =  et_posologia.getText().toString();
        String hrInicial = et_hrInicial.getText().toString();
        String dose = et_dose.getText().toString();
        String dataInicio = et_dataInicio.getText().toString();
        String dataFim = et_dataFim.getText().toString();
        String finalidade= et_finalidade.getText().toString();
        String unPosologia = spiPosologia.getSelectedItem().toString();
        String unMed = spiMedicamento.getSelectedItem().toString();
        String id = getIntent().getStringExtra("id");

        Map<String, Object> medicamentoMap = new HashMap<>();
        medicamentoMap.put("nome", nomeMed);
        medicamentoMap.put("posologia", posologia);
        medicamentoMap.put("hora inicial", hrInicial);
        medicamentoMap.put("dose", dose);
        medicamentoMap.put("data inicio", dataInicio);
        medicamentoMap.put("data fim", dataFim);
        medicamentoMap.put("finalidade", finalidade);
        medicamentoMap.put("unidade medicamento", unMed);
        medicamentoMap.put("unidade posologia", unPosologia);
        medicamentoMap.put("id do idoso", id);
        medicamentoMap.put("dia de criacao", new Date());
        firebaseFirestore.collection("Medicamento")
                .add(medicamentoMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("banco_dados_salvos", "Sucesso ao salvar dados!"+documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("erro_banco_dados", "Erro ao salvar dados!", e);
                    }
                });
    }
}