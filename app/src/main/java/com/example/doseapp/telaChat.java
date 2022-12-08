package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.datepicker.YearGridAdapter;

public class telaChat extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_chat);
    }

//    private class MensagemItem extends Item<ViewHolder> {
//        private final boolean rec;
//
//        public MensagemItem(boolean rec) {
//            this.rec = rec;
//        }
//
//        @Override
//        public void bind(@NonNull YearGridAdapter.ViewHolder viewHolder, int position){
//
//        }
//
//        @Override
//        public int getLayout(){
//            return rec ? R.layout.item_mensagem_rec : R.layout.item_mensagem_env;
//
//        }
//
//    }
}