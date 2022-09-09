package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class telaCadastroIdosoCuidado extends AppCompatActivity {
    private EditText et_nomeIdoso, et_enderecoIdoso, et_telefoneIdoso, et_dataNascIdoso, et_obsIdoso;
    private RadioGroup rg_genero;
    private Button btn_cadastrarIdoso;
    private RadioButton rb_feminino, rb_masculino, rb_outro;
    private FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
    private String [] mensagens ={"Preencha todos os campos", "Cadastro realizado com sucesso", "Falha no cadastro", "Digite um telefone válido", "Este nome já foi registrado, por favor digite outro"};
    private String userId;
    private IdosoCuidado idosoCuidado;
    private Boolean nomeValido;
    private List<IdosoCuidado> idosoCuidadoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_idoso_cuidado);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.cadastrar_idoso);
        actionBar.setDisplayHomeAsUpEnabled(true);

        btn_cadastrarIdoso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String genero = "";
                if(rb_feminino.isChecked()){
                    genero = "feminino";
                }else if(rb_masculino.isChecked()){
                    genero= "masculino";
                } else if (rb_outro.isChecked()) {
                    genero = "outro";
                }

                String nome = et_nomeIdoso.getText().toString();
                String end = et_enderecoIdoso.getText().toString();
                String tel = et_telefoneIdoso.getText().toString();
                String dataNasc = et_dataNascIdoso.getText().toString();

                if(nome.isEmpty() || end.isEmpty() || dataNasc.isEmpty() || tel.isEmpty() || genero.isEmpty()){
                    Snackbar snackbar = Snackbar.make(view, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }else{
                    Snackbar snackbar = Snackbar.make(view, mensagens[1], Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                    salvarNoBancoDeDados();
                    voltarTelaInicio();
                }
            }
        });
    }

    protected void voltarTelaInicio() {
        Intent intent = new Intent(telaCadastroIdosoCuidado.this, telaInicial.class);
        startActivity(intent);
    }

    protected void inicializarComponentes(){
        et_nomeIdoso = findViewById(R.id.et_nomeIdoso);
        et_enderecoIdoso = findViewById(R.id.et_enderecoIdoso);
        et_telefoneIdoso = findViewById(R.id.et_telefoneIdoso);
        btn_cadastrarIdoso = findViewById(R.id.btn_cadastrarIdoso);
        rg_genero = findViewById(R.id.rg_genero);
        rb_feminino = findViewById(R.id.rb_feminino);
        rb_masculino = findViewById(R.id.rb_masculino);
        rb_outro = findViewById(R.id.rb_outro);
        et_dataNascIdoso = findViewById(R.id.et_dataNascIdoso);
        et_obsIdoso = findViewById(R.id.et_obsIdoso);
    }

    protected boolean validarNome(){

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String nomeColecao = "Idosos cuidados "+userId;
        firebaseFirestore.collection(nomeColecao)
                .orderBy("data de criacao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                IdosoCuidado ic = document.toObject(IdosoCuidado.class);
                                idosoCuidadoList.add(ic);
                            }
                        }
                    }
                });
        for (int i = 0; i < idosoCuidadoList.size(); i++) {
            if(idosoCuidadoList.get(i).getNome().equals(et_nomeIdoso.getText().toString())) return false;
            else return true;
        }
        return false;
    }

    protected void salvarNoBancoDeDados(){
        String genero = "";
        if(rb_feminino.isChecked()){
            genero = "feminino";
        }else if(rb_masculino.isChecked()){
            genero= "masculino";
        }else if (rb_outro.isChecked()) {
            genero = "outro";
        }

        String nome = et_nomeIdoso.getText().toString();
        String end = et_enderecoIdoso.getText().toString();
        String tel = et_telefoneIdoso.getText().toString();
        String dataNasc = et_dataNascIdoso.getText().toString();
        String obs = et_obsIdoso.getText().toString();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> idosoCuidadoMap = new HashMap<>();

        idosoCuidadoMap.put("nome", nome);
        idosoCuidadoMap.put("endereco", end);
        idosoCuidadoMap.put("telefone", tel);
        idosoCuidadoMap.put("data de nascimento", dataNasc);
        idosoCuidadoMap.put("data de criacao", new Date());
        idosoCuidadoMap.put("observacoes", obs);
        idosoCuidadoMap.put("genero", genero);
        String nomeColecao = "Idosos cuidados "+userId;
        firebaseFirestore.collection(nomeColecao)
                .add(idosoCuidadoMap)

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