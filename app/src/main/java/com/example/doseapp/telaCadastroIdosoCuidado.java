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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class telaCadastroIdosoCuidado extends AppCompatActivity {
    private EditText et_nomeIdoso, et_enderecoIdoso, et_telefoneIdoso, et_obsIdoso;
    private RadioGroup rg_genero;
    private TextView et_dataNascIdoso;
    private Button btn_cadastrarIdoso;
    private RadioButton rb_feminino, rb_masculino, rb_outro;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String[] mensagens = {"Preencha todos os campos", "Cadastro realizado com sucesso", "Falha no cadastro", "O número de telefone deve seguir o exemplo: (00) 0000-0000", "Digite um endereço válido", "Digite um nome com mais de três letras"};
    private String userId;
    private String id;
    private DatePickerDialog.OnDateSetListener dateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_idoso_cuidado);
        inicializarComponentes();

        id = getIntent().getStringExtra("id");

        if (id == null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.cadastrar_idoso);
            actionBar.setDisplayHomeAsUpEnabled(true);

            btn_cadastrarIdoso.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String genero = "";
                    if (rb_feminino.isChecked()) {
                        genero = "feminino";
                    } else if (rb_masculino.isChecked()) {
                        genero = "masculino";
                    } else if (rb_outro.isChecked()) {
                        genero = "outro";
                    }

                    String nome = et_nomeIdoso.getText().toString();
                    String end = et_enderecoIdoso.getText().toString();
                    String tel = et_telefoneIdoso.getText().toString();
                    String dataNasc = et_dataNascIdoso.getText().toString();

                    if (nome.isEmpty() || end.isEmpty() || dataNasc.isEmpty() || tel.isEmpty() || genero.isEmpty()) {
                        gerarSnackBar(view, mensagens[0]);
                    } else if (validarTelefone(tel) == false || tel.length() < 11) {
                        gerarSnackBar(view, mensagens[3]);
                    } else if (nome.length() < 3) {
                        gerarSnackBar(view, mensagens[4]);
                    } else if (end.length() < 4) {
                        gerarSnackBar(view, mensagens[5]);
                    } else {
                        gerarSnackBar(view, mensagens[1]);
                        salvarNoBancoDeDados();
                        finish();
                    }
                }
            });
        } else {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(R.string.editar);
            actionBar.setDisplayHomeAsUpEnabled(true);
            preencherCampos();
            et_dataNascIdoso.setTextColor(Color.BLACK);
            btn_cadastrarIdoso.setText("Editar");
            btn_cadastrarIdoso.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editarIdoso(view);
                    finish();
                }
            });
        }
        et_dataNascIdoso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int ano = calendar.get(Calendar.YEAR);
                int mes = calendar.get(Calendar.MONTH);
                int dia = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(telaCadastroIdosoCuidado.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int i, int i2, int i3) {
                                i2++;

                                String mes = "";
                                String dia = "";
                                if (i2 < 10) mes = "0" + i2;
                                else mes = String.valueOf(i2);
                                if (i3 < 10) dia = "0" + i3;
                                else dia = String.valueOf(i3);
                                et_dataNascIdoso.setText(dia + "/" + mes + "/" + i);
                                et_dataNascIdoso.setTextColor(Color.BLACK);
                            }
                        }, ano, mes, dia);
                datePickerDialog.show();
            }
        });

    }

    protected void gerarSnackBar(View v, String s) {
        Snackbar snackbar = Snackbar.make(v, s, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    protected boolean validarTelefone(String tel) {
        Pattern pattern = Pattern.compile("^((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");
        Matcher matcher = pattern.matcher(tel);
        return (matcher.matches());
    }

    protected void inicializarComponentes() {
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

    protected void salvarNoBancoDeDados() {
        String genero = "";
        if (rb_feminino.isChecked()) {
            genero = "feminino";
        } else if (rb_masculino.isChecked()) {
            genero = "masculino";
        } else if (rb_outro.isChecked()) {
            genero = "outro";
        }

        String nome = et_nomeIdoso.getText().toString();
        String end = et_enderecoIdoso.getText().toString();
        String tel = et_telefoneIdoso.getText().toString();
        String dataNasc = et_dataNascIdoso.getText().toString();
        String obs = et_obsIdoso.getText().toString();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> cuidadores = new ArrayList<>();
        cuidadores.add(userId);
        Map<String, Object> idosoCuidadoMap = new HashMap<>();
        idosoCuidadoMap.put("nome", nome);
        idosoCuidadoMap.put("endereco", end);
        idosoCuidadoMap.put("telefone", tel);
        idosoCuidadoMap.put("data de nascimento", dataNasc);
        idosoCuidadoMap.put("data de criacao", new Date());
        idosoCuidadoMap.put("observacoes", obs);
        idosoCuidadoMap.put("genero", genero);
        idosoCuidadoMap.put("cuidado", false);
        idosoCuidadoMap.put("cuidador id", cuidadores);
        firebaseFirestore.collection("Idosos cuidados")
                .add(idosoCuidadoMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("banco_dados_salvos", "Sucesso ao salvar dados!" + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("erro_banco_dados", "Erro ao salvar dados!", e);
                    }
                });
    }

    protected void preencherCampos() {
        DocumentReference document = firebaseFirestore.collection("Idosos cuidados").document(id);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                IdosoCuidado ic = new IdosoCuidado();
                et_nomeIdoso.setText(value.getString("nome"));
                et_enderecoIdoso.setText(value.getString("endereco"));
                et_telefoneIdoso.setText(value.getString("telefone"));
                et_dataNascIdoso.setText(value.getString("data de nascimento"));
                et_obsIdoso.setText(value.getString("observacoes"));
                String genero = value.getString("genero");
                if (genero.equals("feminino")) rb_feminino.setChecked(true);
                else if (genero.equals("masculino")) rb_masculino.setChecked(true);
                else if (genero.equals("outro")) rb_outro.setChecked(true);
            }
        });
    }

    protected void editarIdoso(View view) {
        String genero = "";
        if (rb_feminino.isChecked()) {
            genero = "feminino";
        } else if (rb_masculino.isChecked()) {
            genero = "masculino";
        } else if (rb_outro.isChecked()) {
            genero = "outro";
        }
        String nome = et_nomeIdoso.getText().toString();
        String end = et_enderecoIdoso.getText().toString();
        String tel = et_telefoneIdoso.getText().toString();
        String dataNasc = et_dataNascIdoso.getText().toString();
        String obs = et_obsIdoso.getText().toString();

        if (nome.isEmpty() || end.isEmpty() || tel.isEmpty() || dataNasc.isEmpty()) {
            gerarSnackBar(view, "Preencha todos os campos");
        } else if (validarTelefone(tel) == false || tel.length() < 11) {
            gerarSnackBar(view, "Digite um telefone válido");
        } else if (nome.length() < 4) {
            gerarSnackBar(view, "Digite um nome com mais de 3 letras");
        } else {
            firebaseFirestore.collection("Idosos cuidados").document(id)
                    .update("nome", nome, "endereco", end, "telefone", tel, "data de nascimento", dataNasc, "observacoes", obs, "genero", genero)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("firebase_update", "Sucesso ao atualizar dados");
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
}