package com.example.doseapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class telaCadastroReceita extends AppCompatActivity {

    private EditText et_nome, et_hospital, et_tel, et_profissional;
    private TextView tv_data, tv_dataRen, tv_fotoReceita;
    private Button btn_salvar;
    private DatePickerDialog.OnDateSetListener dateSetListenerData;
    private DatePickerDialog.OnDateSetListener dateSetListenerDataRen;
    private String[] mensagens = {"Preencha todos os campos"};
    private ImageButton imgBtn_visRec;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static String idRec;
    private static String foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_receita);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        idRec = getIntent().getStringExtra("id receita");

        if (idRec == null) {
            actionBar.setTitle(R.string.cadastrar_receita);

            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCampos(view) == true) {
                        salvarNoBancoDeDados();
                        finish();
                    }
                }
            });
        } else {
            actionBar.setTitle(R.string.editar);
            preencherDadosReceita();
            tv_dataRen.setTextColor(Color.BLACK);
            tv_data.setTextColor(Color.BLACK);
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCampos(view)) {
                        editarBancoDeDados();
                        finish();
                    }
                }
            });

        }

        tv_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visualizarCalendario(tv_data);
            }
        });

        tv_dataRen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                visualizarCalendario(tv_dataRen);
            }
        });

        tv_fotoReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                someActivityResultLauncher.launch(intent);
            }
        });

        imgBtn_visRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(telaCadastroReceita.this, visualizarFotoReceita.class);
                intent.putExtra("foto", foto);
                startActivity(intent);
            }
        });
    }

    protected void inicializarComponentes() {
        et_nome = findViewById(R.id.et_nomeReceita);
        tv_data = findViewById(R.id.tv_dataReceita);
        imgBtn_visRec = findViewById(R.id.imgBtn_visRec);
        et_hospital = findViewById(R.id.et_hospitalReceita);
        et_tel = findViewById(R.id.et_telefoneHospital);
        et_profissional = findViewById(R.id.et_profissionalReceita);
        tv_dataRen = findViewById(R.id.tv_dataRenReceita);
        tv_fotoReceita = findViewById(R.id.tv_fotoReceita);
        btn_salvar = findViewById(R.id.btn_salvarCadReceita);
    }

    protected void salvarNoBancoDeDados() {
        String nome = et_nome.getText().toString();
        String data = tv_data.getText().toString();
        String hospital = et_hospital.getText().toString();
        String tel = et_tel.getText().toString();
        String profissional = et_profissional.getText().toString();
        String dataRen = tv_dataRen.getText().toString();
        String id = getIntent().getStringExtra("id");
        Map<String, Object> receitaMap = new HashMap<>();
        receitaMap.put("nome", nome);
        receitaMap.put("data", data);
        receitaMap.put("hospital", hospital);
        receitaMap.put("telefone", tel);
        receitaMap.put("profissional", profissional);
        receitaMap.put("data para renovar", dataRen);
        receitaMap.put("id do idoso", id);
        receitaMap.put("foto", foto);
        receitaMap.put("dia de criacao", new Date());

        firebaseFirestore.collection("Receitas")
                .add(receitaMap)
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

    public void visualizarCalendario(TextView tv_data) {
        Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(telaCadastroReceita.this,
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
                        tv_data.setText(dia + "/" + mes + "/" + i);
                        tv_data.setTextColor(Color.BLACK);
                    }
                }, ano, mes, dia);
        datePickerDialog.show();
    }

    protected void editarBancoDeDados() {
        String nome = et_nome.getText().toString();
        String data = tv_data.getText().toString();
        String hosp = et_hospital.getText().toString();
        String tel = et_tel.getText().toString();
        String profissional = et_profissional.getText().toString();
        String dataRen = tv_dataRen.getText().toString();

        firebaseFirestore.collection("Receitas").document(idRec)
                .update("nome", nome, "data", data, "hospital", hosp, "telefone", tel, "profissional", profissional, "data para renovar", dataRen, "foto",foto)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                    }
                });
    }

    protected void preencherDadosReceita() {
        DocumentReference document = firebaseFirestore.collection("Receitas").document(idRec);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                foto = value.getString("foto");
                et_nome.setText(value.getString("nome"));
                et_hospital.setText(value.getString("hospital"));
                et_tel.setText(value.getString("telefone"));
                et_profissional.setText(value.getString("profissional"));
                tv_data.setText(value.getString("data"));
                tv_dataRen.setText(value.getString("data para renovar"));
            }
        });
    }

    protected void gerarSnackBar(View view, String texto) {
        Snackbar snackbar = Snackbar.make(view, texto, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }

    protected boolean validarCampos(View view) {
        String nome = et_nome.getText().toString();
        String data = tv_data.getText().toString();
        String hospital = et_hospital.getText().toString();
        String tel = et_tel.getText().toString();
        String profissional = et_profissional.getText().toString();
        String dataRen = tv_dataRen.getText().toString();

        if (nome.isEmpty() || data.isEmpty() || hospital.isEmpty() || tel.isEmpty() || profissional.isEmpty() || dataRen.isEmpty()) {
            gerarSnackBar(view, mensagens[0]);
            return false;
        }
        return true;
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        try {
                            Bitmap fotoBitmap = (Bitmap) data.getExtras().get("data");
                            ByteArrayOutputStream fotoStream = new ByteArrayOutputStream();
                            fotoBitmap.compress(Bitmap.CompressFormat.PNG, 90, fotoStream);
                            byte[] fotoByte = fotoStream.toByteArray();
                            foto = Base64.encodeToString(fotoByte, Base64.DEFAULT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
}