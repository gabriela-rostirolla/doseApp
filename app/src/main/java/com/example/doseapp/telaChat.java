package com.example.doseapp;

import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class telaChat extends AppCompatActivity {

    private RecyclerView rv;
    private static MensagemAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_chat);
        inicializarComponentes();
        rv.setAdapter(adapter);

        List<String> list = new ArrayList<>();
        list.add("ois");
        list.add("hello");

        adapter = new MensagemAdapter(getApplicationContext(), list);
        rv.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        rv.setHasFixedSize(false);
        rv.setAdapter(adapter);

    }

    private void inicializarComponentes() {
        rv = findViewById(R.id.rv_listaMensagem);
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