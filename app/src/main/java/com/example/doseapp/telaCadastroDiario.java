package com.example.doseapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class telaCadastroDiario extends AppCompatActivity {
    private Button btn_continuarDiario;
    private RadioGroup rg_turno, rg_registro;
    private RadioButton rb_manha, rb_tarde, rb_noite, rb_madrugada, rb_atividade, rb_refeicao;
    private DateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");
    private String [] mensagens ={"Preencha todos os dados"};
    private TextView tv_diaDiario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_cadastro_diario);
        inicializarComponentes();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.cadastrar_diario);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Date data = new Date();
        String dia = dataFormat.format(data);
        tv_diaDiario.setText(dia);

        btn_continuarDiario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String titulo = "";

                if(rb_manha.isChecked()){
                    titulo = dia+" - Manhã";
                }else if(rb_tarde.isChecked()){
                    titulo = dia+" - Tarde";
                }else if(rb_noite.isChecked()){
                    titulo =dia+" - Noite";
                }else if (rb_madrugada.isChecked()){
                    titulo =dia+" - Madrugada";
                }else{
                    Snackbar snackbar = Snackbar.make(view, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
                if(rb_atividade.isChecked()){
                    Intent intent = new Intent(telaCadastroDiario.this, telaCadastroDiarioAtividade.class);
                    intent.putExtra("titulo",titulo);
                    startActivity(intent);
                }else if(rb_refeicao.isChecked()){
                    Intent intent = new Intent(telaCadastroDiario.this, telaCadastroDiarioAlimentacao.class);
                    intent.putExtra("titulo",titulo);
                    startActivity(intent);
                }else{
                    Snackbar snackbar = Snackbar.make(view, mensagens[0], Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.WHITE);
                    snackbar.setTextColor(Color.BLACK);
                    snackbar.show();
                }
            }
        });
    }

    protected void inicializarComponentes(){
        btn_continuarDiario = findViewById(R.id.btn_continuarDiario);
        rg_turno = findViewById(R.id.rg_turno);
        rb_manha = findViewById(R.id.rb_manha);
        rb_tarde = findViewById(R.id.rb_tarde);
        rb_noite = findViewById(R.id.rb_noite);
        tv_diaDiario = findViewById(R.id.tv_diaDiario);
        rb_madrugada = findViewById(R.id.rb_madrugada);
        rg_registro = findViewById(R.id.rg_registro);
        rb_atividade = findViewById(R.id.rb_atividades);
        rb_refeicao = findViewById(R.id.rb_refeicao);
    }
}