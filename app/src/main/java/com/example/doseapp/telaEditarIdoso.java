package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class telaEditarIdoso extends AppCompatActivity {

    private Button btn_editarIdoso;
    private EditText et_nomeIdosoEdit, et_enderecoIdosoEdit, et_telefoneIdosoEdit, et_obsEdit;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private RadioButton rb_feminino, rb_masculino, rb_outro;
    private String id;
    private TextView et_nascIdosoEdit;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_idoso);
        inicializarComponentes();
        id = getIntent().getStringExtra("id");
        listarIdososCuidados();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.editar);
        actionBar.setDisplayHomeAsUpEnabled(true);

        et_nascIdosoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int ano = calendar.get(Calendar.YEAR);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                int mes = calendar.get(Calendar.MONTH);
                DatePickerDialog dialog = new DatePickerDialog(telaEditarIdoso.this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListener, dia, mes, ano);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1++;
                et_nascIdosoEdit.setText(i2 + "/" + i1 + "/" + i);
                et_nascIdosoEdit.setTextColor(Color.BLACK);
            }
        };
        btn_editarIdoso.setText("Editar");

        btn_editarIdoso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editarIdoso(view);
            }
        });
    }

    protected void inicializarComponentes(){
        btn_editarIdoso = findViewById(R.id.btn_cadastrarIdoso);
        et_nomeIdosoEdit = findViewById(R.id.et_nomeIdoso);
        et_enderecoIdosoEdit = findViewById(R.id.et_enderecoIdoso);
        et_telefoneIdosoEdit = findViewById(R.id.et_telefoneIdoso);
        et_nascIdosoEdit = findViewById(R.id.et_dataNascIdoso);
        et_nascIdosoEdit.setTextColor(Color.BLACK);
        et_obsEdit = findViewById(R.id.et_obsIdoso);
        rb_feminino = findViewById(R.id.rb_feminino);
        rb_masculino = findViewById(R.id.rb_masculino);
        rb_outro = findViewById(R.id.rb_outro);
    }

    protected void listarIdososCuidados(){
        DocumentReference document = firebaseFirestore.collection("Idosos cuidados").document(id);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                IdosoCuidado ic = new IdosoCuidado();
                et_nomeIdosoEdit.setText(value.getString("nome"));
                et_enderecoIdosoEdit.setText(value.getString("endereco"));
                et_telefoneIdosoEdit.setText(value.getString("telefone"));
                et_nascIdosoEdit.setText(value.getString("data de nascimento"));
                et_obsEdit.setText(value.getString("observacoes"));
                String genero = value.getString("genero");
                if(genero.equals("feminino")) rb_feminino.setChecked(true);
                else if(genero.equals("masculino")) rb_masculino.setChecked(true);
                else if(genero.equals("outro")) rb_outro.setChecked(true);
            }
        });
    }

    protected void editarIdoso(View view){
        String genero = "";
        if(rb_feminino.isChecked()){
            genero = "feminino";
        }else if(rb_masculino.isChecked()){
            genero= "masculino";
        }else if (rb_outro.isChecked()) {
            genero = "outro";
        }
        String nome = et_nomeIdosoEdit.getText().toString();
        String end = et_enderecoIdosoEdit.getText().toString();
        String tel = et_telefoneIdosoEdit.getText().toString();
        String dataNasc = et_nascIdosoEdit.getText().toString();
        String obs = et_obsEdit.getText().toString();

        if(nome.isEmpty() || end.isEmpty() || tel.isEmpty() || dataNasc.isEmpty()){
            gerarSnackBar(view, "Preencha todos os campos");
        }else if(validarTelefone(tel) == false || tel.length() <11){
            gerarSnackBar(view, "Digite um telefone válido");
        }
        else if(nome.length() <4){
            gerarSnackBar(view, "Digite um nome com mais de 3 letras");
        }else {
            firebaseFirestore.collection("Idosos cuidados").document(id)
                    .update("nome", nome, "endereco", end, "telefone", tel, "data de nascimento", dataNasc, "observacoes", obs, "genero",genero)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("firebase_update", "Sucesso ao atualizar dados");
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            gerarSnackBar(view, "Falha ao editar");
                            Log.w("firebase_failed_update", "Falha ao atualizar dados", e);
                        }
                    });
        }
    }
    protected boolean validarTelefone(String tel){
        Pattern pattern = Pattern.compile( "^((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");
        Matcher matcher = pattern.matcher(tel);
        return (matcher.matches());
    }

    protected void gerarSnackBar(View view, String texto){
        Snackbar snackbar = Snackbar.make(view, texto, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();

    }
}