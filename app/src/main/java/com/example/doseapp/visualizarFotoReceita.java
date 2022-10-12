package com.example.doseapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class visualizarFotoReceita extends AppCompatActivity {
    private ImageView imgViewReceita;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizar_foto_receita);
        imgViewReceita = findViewById(R.id.imgViewReceita);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Visualizar imagem");
        actionBar.setDisplayHomeAsUpEnabled(true);

        String img = getIntent().getStringExtra("foto");

        if(img == null||img.isEmpty()){
            Toast.makeText(this, "Nenhuma imagem encontrada", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            byte[] imgBytes;
            imgBytes = Base64.decode(img, Base64.DEFAULT);
            Bitmap imgBitmap = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
            imgViewReceita.setImageBitmap(imgBitmap);
        }
    }
}