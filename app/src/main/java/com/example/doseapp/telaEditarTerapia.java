package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class telaEditarTerapia extends AppCompatActivity {
    private EditText et_nome, et_profissional, et_endereco, et_telefone;
    private Button btn_salvar;
    private TextView et_horario;
    private Switch swt_lembre;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String[] mensagens ={"Preencha todos os campos"};
    private String idTerapia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_terapia);
        inicializarComponentes();
        idTerapia = getIntent().getStringExtra("id terapia");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.editar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        preencherDadosTerapia();

        btn_salvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarBancoDeDados();
                finish();
            }
        });
    }

    protected void inicializarComponentes(){
        et_nome = findViewById(R.id.et_nomeTerapia);
        et_endereco = findViewById(R.id.et_enderecoTerapia);
        et_profissional = findViewById(R.id.et_profissionalTerapia);
        et_horario = findViewById(R.id.et_horaTerapia);
        et_telefone = findViewById(R.id.et_telefoneTerapia);
        swt_lembre = findViewById(R.id.swt_lembreTerapia);
        btn_salvar = findViewById(R.id.btn_salvarTerapia);
        btn_salvar.setText("Editar");
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
                swt_lembre.setChecked(value.getBoolean("lembre-me"));
            }
        });
    }

    public void editarBancoDeDados(){
        String nome = et_nome.getText().toString();
        String profissional =  et_profissional.getText().toString();
        String end = et_endereco.getText().toString();
        String horario = et_horario.getText().toString();
        String tel = et_telefone.getText().toString();
        boolean lembre = swt_lembre.isChecked();

        firebaseFirestore.collection("Terapias").document(idTerapia)
                .update("nome",nome,"profissional",profissional,"endereco",end,"horario",horario,"telefone", tel, "lembre-me", lembre)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                    }
                });
    }
}