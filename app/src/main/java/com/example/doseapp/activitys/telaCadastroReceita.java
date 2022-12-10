package com.example.doseapp.activitys;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.WorkManager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doseapp.workManager.NotificacaoMedicamentoWorkManager;
import com.example.doseapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class telaCadastroReceita extends AppCompatActivity {

    private EditText et_nome, et_hospital, et_tel, et_profissional;
    private TextView tv_data, tv_dataRen, tv_fotoReceita;
    private Button btn_salvar;
    private ImageView imgViewUpload;
    private DatePickerDialog.OnDateSetListener dateSetListenerData;
    private DatePickerDialog.OnDateSetListener dateSetListenerDataRen;

    private ImageButton imgBtn_visRec;
    private Switch lembre;
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

        foto = "";
        idRec = getIntent().getStringExtra("id receita");

        if (idRec == null) {
            actionBar.setTitle(R.string.cadastrar_receita);
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCampos(view) == true) {
                        salvarNoBancoDeDados();
                        if (lembre.isChecked()){
                            definirEvento();
                        }
                        salvarNotificacao();
                        finish();
                    }
                }
            });
        } else {
            actionBar.setTitle(R.string.editar);
            preencherDadosReceita();
            tv_dataRen.setTextColor(Color.BLACK);
            tv_data.setTextColor(Color.BLACK);
            tv_fotoReceita.setText(R.string.atualizarFoto);
            btn_salvar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (validarCampos(view)) {
                        if (lembre.isChecked()) {
                            definirEvento();
                        }
                        salvarNotificacao();
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

        imgViewUpload.setOnClickListener(new View.OnClickListener() {
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

    protected void definirEvento() {
        String nomeIdoso = getIntent().getStringExtra("nome idoso");
        String[] data = tv_dataRen.getText().toString().split("/");
        GregorianCalendar calDate = new GregorianCalendar(Integer.parseInt(data[2]),
                Integer.parseInt(data[1]) - 1,
                Integer.parseInt(data[0]));

        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, nomeIdoso + " - " + et_nome.getText().toString())
                .putExtra(CalendarContract.Events.EVENT_LOCATION, et_hospital.getText().toString())
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, calDate.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true)
                .putExtra(CalendarContract.Events.DESCRIPTION, "Profissional: " +
                        et_profissional.getText().toString() +
                        "\nTelefone: " +
                        et_tel.getText().toString());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
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
        lembre = findViewById(R.id.swt_lembreReceita);
        imgViewUpload = findViewById(R.id.imgViewUpload);
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

    protected void visualizarCalendario(TextView tv_data) {
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
                .update("nome", nome, "data", data, "hospital", hosp, "telefone", tel, "profissional", profissional, "data para renovar", dataRen, "foto", foto)
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
                if (!foto.isEmpty() && foto != null) {
                    byte[] imgBytes;
                    imgBytes = Base64.decode(foto, Base64.DEFAULT);
                    Bitmap imgBitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
                    imgBtn_visRec.setImageBitmap(imgBitmap);
                }
                et_nome.setText(value.getString("nome"));
                et_hospital.setText(value.getString("hospital"));
                et_tel.setText(value.getString("telefone"));
                et_profissional.setText(value.getString("profissional"));
                tv_data.setText(value.getString("data"));
                tv_dataRen.setText(value.getString("data para renovar"));
            }
        });
    }

    protected void gerarToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    protected boolean validarCampos(View view) {
        String nome = et_nome.getText().toString();
        String data = tv_data.getText().toString();
        String hospital = et_hospital.getText().toString();
        String tel = et_tel.getText().toString();
        String profissional = et_profissional.getText().toString();
        String dataRen = tv_dataRen.getText().toString();

        if (nome.isEmpty() || data.isEmpty() || hospital.isEmpty() || tel.isEmpty() || profissional.isEmpty() || dataRen.isEmpty()) {
            gerarToast(getString(R.string.camposVazios));
            return false;
        } else if (nome.length() < 4) {
            gerarToast(getString(R.string.nomeInv));
            return false;
        } else if (!validarTelefone(tel)) {
            gerarToast(getString(R.string.telInv));
            return false;
        } else if (hospital.length() < 3) {
            gerarToast(getString(R.string.nomeHospInv));
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

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        try {
                            Bitmap fotoBitmap = (Bitmap) data.getExtras().get("data");
                            imgBtn_visRec.setImageBitmap(fotoBitmap);
                            ByteArrayOutputStream fotoStream = new ByteArrayOutputStream();
                            fotoBitmap.compress(Bitmap.CompressFormat.PNG, 100, fotoStream);
                            byte[] fotoByte = fotoStream.toByteArray();
                            foto = Base64.encodeToString(fotoByte, Base64.DEFAULT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });


    private void excluirNotificacao(String tag) {
        WorkManager.getInstance(this).cancelAllWorkByTag(tag);
    }

    private String generateKey() {
        return UUID.randomUUID().toString();
    }

    protected int salvarNotificacao() {
        Calendar calendar = Calendar.getInstance();
        String data[] = tv_dataRen.getText().toString().split("/");
        calendar.set(Integer.parseInt(data[2]), Integer.parseInt(data[1]) - 1, Integer.parseInt(data[0]));

        String tag = generateKey();
        Long alertTime = Math.abs(calendar.getTimeInMillis() - System.currentTimeMillis());
        int random = (int) (Math.random() * 50 + 1);
        Data date = guardarData(et_nome.getText().toString(), "Chegou o dia de renovar a receita de " + getIntent().getStringExtra("nome"), random);

        NotificacaoMedicamentoWorkManager.salvarNotificacao(alertTime, date, tag);
        return random;
    }

    private Data guardarData(String titulo, String descricao, int id_not) {
        return new Data.Builder()
                .putString("titulo", titulo)
                .putString("descricao", descricao)
                .putString("id med", getIntent().getStringExtra("id medicamento"))
                .putInt("id_notificacao", id_not).build();
    }
}
