package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class telaCadastroIdosoCuidado extends AppCompatActivity {
    private EditText et_nomeIdoso,et_dataNascIdoso, et_enderecoIdoso, et_telefoneIdoso, et_obsIdoso;
    private RadioGroup rg_genero;
    private ImageButton imgBtn_calendario;
    private Button btn_cadastrarIdoso;
    private RadioButton rb_feminino, rb_masculino, rb_outro;
    private FirebaseFirestore firebaseFirestore= FirebaseFirestore.getInstance();
    private String [] mensagens ={"Preencha todos os campos", "Cadastro realizado com sucesso", "Falha no cadastro", "O número de telefone deve seguir o exemplo: (00) 0000-0000", "Digite um endereço válido", "Digite um nome com mais de três letras"};
    private String userId;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_idoso_cuidado);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.cadastrar_idoso);
        actionBar.setDisplayHomeAsUpEnabled(true);

        imgBtn_calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int ano= calendar.get(Calendar.YEAR);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                int mes = calendar.get(Calendar.MONTH);
                DatePickerDialog dialog = new DatePickerDialog(telaCadastroIdosoCuidado.this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListener, dia, mes, ano);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1++;
                et_dataNascIdoso.setText(i2+"/"+i1+"/"+i);
            }
        };

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
                    gerarSnackBar(view, mensagens[0]);
                }else if(validarTelefone(tel) == false){
                    gerarSnackBar(view, mensagens[3]);
                }else if(nome.length()<3){
                    gerarSnackBar(view, mensagens[4]);
                }else if(nome.length() <4){
                    gerarSnackBar(view, mensagens[5]);
                }else{
                    gerarSnackBar(view, mensagens[1]);
                    salvarNoBancoDeDados();
                    finish();
                }
            }
        });
    }

    protected void gerarSnackBar(View v,String s){
        Snackbar snackbar = Snackbar.make(v, s, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    protected boolean validarTelefone(String tel){
        Pattern pattern = Pattern.compile( "^((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");
        Matcher matcher = pattern.matcher(tel);
        return (matcher.matches());
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
        imgBtn_calendario = findViewById(R.id.icon_calendar);
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
        idosoCuidadoMap.put("cuidador id", userId);
        firebaseFirestore.collection("Idosos cuidados")
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