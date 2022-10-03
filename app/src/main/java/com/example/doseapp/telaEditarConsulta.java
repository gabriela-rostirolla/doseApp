package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class telaEditarConsulta extends AppCompatActivity {

    private EditText et_nome, et_profissional, et_end, et_tel;
    private Button btn_salvar;
    private String[] mensagens = {"Preencha todos os dados"};
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String idConsulta;
    private Switch swt_lembre;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private TextView et_data, et_horario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_consulta);

        inicializarComponentes();
        idConsulta = getIntent().getStringExtra("id consulta");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.editar);
        actionBar.setDisplayHomeAsUpEnabled(true);
        preencherDadosConsulta();


        et_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int anoIni = calendar.get(Calendar.YEAR);
                int diaIni = calendar.get(Calendar.DAY_OF_MONTH);
                int mesIni = calendar.get(Calendar.MONTH);
                DatePickerDialog dialog = new DatePickerDialog(telaEditarConsulta.this, android.R.style.Theme_Holo_Dialog_MinWidth, dateSetListener, diaIni, mesIni, anoIni);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1++;

                String mes = "";
                String dia = "";
                if (i1 < 10) mes = "0" + i1;
                else mes = String.valueOf(i1);
                if (i2 < 10) dia = "0" + i2;
                else dia = String.valueOf(i2);
                et_data.setText(dia + "/" + mes + "/" + i);
                et_data.setTextColor(Color.BLACK);
            }
        };

        et_horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int hora = calendar.get(Calendar.HOUR_OF_DAY);
                int min = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(telaEditarConsulta.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int i, int i2) {
                                if (i2 < 10) {
                                    et_horario.setText(i + ":" + 0 + i2);
                                    et_horario.setTextColor(Color.BLACK);
                                } else {
                                    et_horario.setText(i + ":" + i2);
                                    et_horario.setTextColor(Color.BLACK);
                                }
                            }
                        }, hora, min, false);
                timePickerDialog.show();
            }
        });

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

    protected void inicializarComponentes() {
        et_nome = findViewById(R.id.et_nomeConsulta);
        et_profissional = findViewById(R.id.et_profissionalConsulta);
        et_end = findViewById(R.id.et_enderecoConsulta);
        et_tel = findViewById(R.id.et_telefoneConsulta);
        et_data = findViewById(R.id.et_dataConsul);
        et_horario = findViewById(R.id.et_horaConsulta);
        btn_salvar = findViewById(R.id.btn_salvarConsulta);
        btn_salvar.setText("editar");
        swt_lembre = findViewById(R.id.swt_lembreConculta);

    }

    public void preencherDadosConsulta() {
        DocumentReference document = firebaseFirestore.collection("Consultas").document(idConsulta);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                et_nome.setText(value.getString("nome"));
                et_profissional.setText(value.getString("profissional"));
                et_end.setText(value.getString("endereco"));
                et_tel.setText(value.getString("telefone"));
                et_data.setText(value.getString("data"));
                et_horario.setText(value.getString("horario"));
                swt_lembre.setChecked(value.getBoolean("lembre-me"));

            }
        });
    }

    public void editarBancoDeDados() {
        String nome = et_nome.getText().toString();
        String profissional = et_profissional.getText().toString();
        String end = et_end.getText().toString();
        String tel = et_tel.getText().toString();
        String data = et_data.getText().toString();
        String horario = et_horario.getText().toString();
        boolean lembre = swt_lembre.isChecked();

        firebaseFirestore.collection("Consultas").document(idConsulta)
                .update("nome", nome, "profissional", profissional, "endereco", end, "telefonen", tel, "data", data, "horario", horario, "lembre-me", lembre)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                    }
                });
    }

    protected void gerarSnackBar(View view, String texto) {
        Snackbar snackbar = Snackbar.make(view, texto, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    public boolean validarCampos(View view) {
        String nome = et_nome.getText().toString();
        String endereco = et_end.getText().toString();
        String data = et_data.getText().toString();
        String tel = et_tel.getText().toString();
        String profissional = et_profissional.getText().toString();
        String horario = et_horario.getText().toString();

        if (nome.isEmpty() || endereco.isEmpty() || data.isEmpty() || tel.isEmpty() || profissional.isEmpty() || horario.isEmpty()) {
            gerarSnackBar(view, mensagens[0]);
            return false;
        } else if (nome.length() < 4) {
            gerarSnackBar(view, "Digite um nome com mais de três letras");
            return false;
        } else if (validarTelefone(tel) == false || tel.length() < 11) {
            gerarSnackBar(view, "Digite um número de telefone válido");
            return false;
        } else if (endereco.length() < 4) {
            gerarSnackBar(view, "Digite um endereço válido");
            return false;
        } else if (profissional.length() < 3) {
            gerarSnackBar(view, "Digite o nome do profissional com mais de três letras");
            return false;
        }
        return true;
    }

    protected boolean validarTelefone(String tel) {
        Pattern pattern = Pattern.compile("^((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");
        Matcher matcher = pattern.matcher(tel);
        return (matcher.matches());
    }
}