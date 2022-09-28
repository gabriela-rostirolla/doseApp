package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class telaEditarMedicamento extends AppCompatActivity {

    private static String idMed;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private EditText et_nomeMed, et_posologia, et_hrInicial, et_dose, et_dataInicio, et_dataFim, et_finalidade;
    private Button btn_salvar;
    private String[] mensagens = {"Preencha todos os campos", "Digite uma data válida", "Digite um nome com mais de 3 letras", "Digite uma funcionalidade com mais de 3 letras", "Não foi possivel editar dados"};
    private Spinner spiPosologia, spiMedicamento;

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

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.unidades_posologia, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiPosologia.setAdapter(adapter);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.unidades_medicamentos, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiMedicamento.setAdapter(adapter1);

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

    public void inicializarComponentes() {
        et_nomeMed = findViewById(R.id.et_nomeMedicamento);
        et_posologia = findViewById(R.id.et_posologia);
        et_hrInicial = findViewById(R.id.et_horaInicial);
        et_dose = findViewById(R.id.et_dose);
        et_dataInicio = findViewById(R.id.et_dataInicio);
        et_dataFim = findViewById(R.id.et_dataFim);
        et_finalidade = findViewById(R.id.et_finalidade);
        btn_salvar = findViewById(R.id.btn_salvarCadMedicamento);
        btn_salvar.setText("Editar");
        spiPosologia = findViewById(R.id.spiPosologia);
        spiMedicamento = findViewById(R.id.spiMedicamentos);
    }

    public void preencherDadosMedicamento() {
        DocumentReference document = firebaseFirestore.collection("Medicamento").document(idMed);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Medicamento med = new Medicamento();
                et_nomeMed.setText(value.getString("nome"));
                et_finalidade.setText(value.getString("finalidade"));
                et_hrInicial.setText(value.getString("hora inicial"));
                et_dataInicio.setText(value.getString("data inicio"));
                et_dataFim.setText(value.getString("data fim"));
                et_dose.setText(value.getString("dose"));
                et_posologia.setText(value.getString("posologia"));
            }
        });
    }

    public void editarBancoDeDados(){
        String nomeMed = et_nomeMed.getText().toString();
        String posologia =  et_posologia.getText().toString();
        String hrInicial = et_hrInicial.getText().toString();
        String dose = et_dose.getText().toString();
        String dataInicio = et_dataInicio.getText().toString();
        String dataFim = et_dataFim.getText().toString();
        String finalidade= et_finalidade.getText().toString();
        String unPosologia = spiPosologia.getSelectedItem().toString();
        String unMed = spiMedicamento.getSelectedItem().toString();

        firebaseFirestore.collection("Medicamento").document(idMed)
                .update("nome",nomeMed,"posologia",posologia,"hora inicial",hrInicial,"dose",dose,"data inicio", dataInicio, "data fim", dataFim,"finalidade", finalidade,"unidade medicamento", unMed, "unidade posologia", unPosologia)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                    }
                });
    }

    public void gerarSnackBar(View view, String texto){
        Snackbar snackbar = Snackbar.make(view, texto, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public boolean validarCampos(View view){
        String nome = et_nomeMed.getText().toString();
        String posologia = et_posologia.getText().toString();
        String horaInicial = et_hrInicial.getText().toString();
        String dose = et_dose.getText().toString();
        String dataInicio= et_dataInicio.getText().toString();
        String dataFim = et_dataFim.getText().toString();
        String finalidade = et_finalidade.getText().toString();

        if(nome.isEmpty() || posologia.isEmpty()||finalidade.isEmpty()||horaInicial.isEmpty()||dose.isEmpty()||dataInicio.isEmpty()||dataFim.isEmpty()){
            gerarSnackBar(view, mensagens[0]);
            return false;
        }else if(nome.length() <3){
            gerarSnackBar(view, mensagens[2]);
            return false;
        }else if(finalidade.length() <3){
            gerarSnackBar(view, mensagens[3]);
            return false;
        }
        return true;
    }

}