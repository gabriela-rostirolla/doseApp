package com.example.doseapp;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class telaCadastroConsulta extends AppCompatActivity {

    private EditText et_nome, et_profissional, et_end, et_tel;
    private Button btn_salvar;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private TextView tv_data, tv_horario;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch swt_lembreConsulta;
    private static String idConsulta;
//    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_consulta);
        inicializarComponentes();
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

//        sharedPreferences = getSharedPreferences(getString(R.string.sharedPrefesAlarme), Context.MODE_PRIVATE);

        idConsulta = getIntent().getStringExtra("id consulta");
        if (idConsulta == null) {
            actionBar.setTitle(R.string.cadastrar_consulta);
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCampos(view) == true) {
                        if (swt_lembreConsulta.isChecked()) {
                            definirEvento();
                        }
                        salvarNoBancoDeDados();
                        finish();
                    }
                }
            });
        } else {
            actionBar.setTitle(R.string.editar);
            preencherDadosConsulta();
            tv_data.setTextColor(Color.BLACK);
            tv_horario.setTextColor(Color.BLACK);
            btn_salvar.setText("Editar");
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCampos(view) == true) {
                        editarBancoDeDados();
                        if (swt_lembreConsulta.isChecked()) {
                            int callbackId = 42;
                            checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
                            definirEvento();
                        }
                        finish();
                    }
                }
            });
        }

        tv_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarCalendario(tv_data);
            }
        });

        tv_horario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mostrarRelogio(tv_horario);
            }
        });
    }

    protected void mostrarRelogio(TextView tv) {
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(telaCadastroConsulta.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int i, int i2) {
                        if (i2 < 10) {
                            tv.setText(i + ":" + 0 + i2);
                            tv.setTextColor(Color.BLACK);
                        } else {
                            tv.setText(i + ":" + i2);
                            tv.setTextColor(Color.BLACK);
                        }
                    }
                }, hora, min, true);
        timePickerDialog.show();
    }

    protected void definirEvento() {
        String nomeIdoso = getIntent().getStringExtra("nome idoso");
        String[] data = tv_data.getText().toString().split("/");
        String[] hr = tv_horario.getText().toString().split(":");
        GregorianCalendar calDate = new GregorianCalendar(Integer.parseInt(data[2]),
                Integer.parseInt(data[1]) - 1,
                Integer.parseInt(data[0]),
                Integer.parseInt(hr[0]),
                Integer.parseInt(hr[1]));

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, nomeIdoso + " - " + et_nome.getText().toString())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, et_end.getText().toString())
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calDate.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, calDate.getTimeInMillis())
                .putExtra(CalendarContract.Events.CALENDAR_ID, 1)
//                .putExtra(CalendarContract.Events.STATUS, status)
                .putExtra(CalendarContract.Events.DESCRIPTION,
                        "Profissional: " +
                                et_profissional.getText().toString() +
                                "\nTelefone: " +
                                et_tel.getText().toString());

        if (intent.resolveActivity(getPackageManager()) != null) {
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putInt("id alarme - " + idConsulta, 1);
//            editor.apply();
            startActivity(intent);
        }
    }

    protected void atualizarEvento() {

    }

    protected void mostrarCalendario(TextView tv) {
        Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(telaCadastroConsulta.this,
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
                        tv.setText(dia + "/" + mes + "/" + i);
                        tv.setTextColor(Color.BLACK);
                    }
                }, ano, mes, dia);
        datePickerDialog.show();
    }

    protected void salvarNoBancoDeDados() {
        String nome = et_nome.getText().toString();
        String endereco = et_end.getText().toString();
        String data = tv_data.getText().toString();
        String tel = et_tel.getText().toString();
        String profissional = et_profissional.getText().toString();
        String horario = tv_horario.getText().toString();
        String id = getIntent().getStringExtra("id");

        Map<String, Object> consultaMap = new HashMap<>();
        consultaMap.put("nome", nome);
        consultaMap.put("data", data);
        consultaMap.put("endereco", endereco);
        consultaMap.put("telefone", tel);
        consultaMap.put("profissional", profissional);
        consultaMap.put("horario", horario);
        consultaMap.put("id do idoso", id);
        consultaMap.put("dia de criacao", new Date());
        firebaseFirestore.collection("Consultas")
                .add(consultaMap)
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

    protected void inicializarComponentes() {
        et_nome = findViewById(R.id.et_nomeConsulta);
        et_profissional = findViewById(R.id.et_profissionalConsulta);
        et_end = findViewById(R.id.et_enderecoConsulta);
        et_tel = findViewById(R.id.et_telefoneConsulta);
        tv_data = findViewById(R.id.et_dataConsul);
        swt_lembreConsulta = findViewById(R.id.swt_lembreConculta);
        tv_horario = findViewById(R.id.et_horaConsulta);
        btn_salvar = findViewById(R.id.btn_salvarConsulta);
    }

    protected void gerarToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    protected boolean validarCampos(View view) {
        String nome = et_nome.getText().toString();
        String endereco = et_end.getText().toString();
        String data = tv_data.getText().toString();
        String tel = et_tel.getText().toString();
        String profissional = et_profissional.getText().toString();
        String horario = tv_horario.getText().toString();

        if (nome.isEmpty() || endereco.isEmpty() || data.isEmpty() || tel.isEmpty() || profissional.isEmpty() || horario.isEmpty()) {
            gerarToast(getString(R.string.camposVazios));
            return false;
        } else if (nome.length() < 3) {
            gerarToast(getString(R.string.nomeInv));
            return false;
        } else if (!validarTelefone(tel)) {
            gerarToast(getString(R.string.telInv));
            return false;
        } else if (endereco.length() < 3) {
            gerarToast(getString(R.string.endInv));
            return false;
        } else if (profissional.length() < 3) {
            gerarToast(getString(R.string.profInv));
            return false;
        }
        return true;
    }

    protected boolean validarTelefone(String tel) {
        Pattern pattern = Pattern.compile("^((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");
        Matcher matcher = pattern.matcher(tel);
        return (matcher.matches());
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

    protected void preencherDadosConsulta() {
        DocumentReference document = firebaseFirestore.collection("Consultas").document(idConsulta);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                et_nome.setText(value.getString("nome"));
                et_profissional.setText(value.getString("profissional"));
                et_end.setText(value.getString("endereco"));
                et_tel.setText(value.getString("telefone"));
                tv_data.setText(value.getString("data"));
                tv_horario.setText(value.getString("horario"));
            }
        });

//        int alarme = sharedPreferences.getInt("id alarme - " + idConsulta, 0);
//        swt_lembreConsulta.setChecked(alarme != 0);
    }

    protected void editarBancoDeDados() {
        String nome = et_nome.getText().toString();
        String profissional = et_profissional.getText().toString();
        String end = et_end.getText().toString();
        String tel = et_tel.getText().toString();
        String data = tv_data.getText().toString();
        String horario = tv_horario.getText().toString();

        firebaseFirestore.collection("Consultas").document(idConsulta)
                .update("nome", nome,
                        "profissional", profissional,
                        "endereco", end,
                        "data", data,
                        "horario", horario,
                        "telefone", tel)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                        gerarToast(getString(R.string.dadosAtualizados));
                    }
                });

//        if (!swt_lembreConsulta.isChecked()) {
//            int alarme =  sharedPreferences.getInt("id alarme - " + idConsulta, 0);
//            if(alarme == 1) {
//                Uri eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, 1);
//                getContentResolver().delete(eventUri, null, null);
//                System.out.println("kfaghkrlr");
//            }
//        }
//
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove("id alarme - " + idConsulta);
//        editor.apply();
    }

    private void checkPermission(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }

        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
    }
}