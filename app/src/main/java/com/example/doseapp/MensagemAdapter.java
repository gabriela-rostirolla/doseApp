package com.example.doseapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MensagemAdapter extends RecyclerView.Adapter {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public Context context;
    private boolean rec;
    private List<String> list;

    public MensagemAdapter(Context context, List<String> receitaList) {
        this.context = context;
    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (rec) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_mensagem_rec, parent, false);
            MensagemAdapter.MensagemViewHolder viewHolder = new MensagemAdapter.MensagemViewHolder(view, firebaseFirestore);
            return viewHolder;
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_mensagem_env, parent, false);
            MensagemAdapter.MensagemViewHolder viewHolder = new MensagemAdapter.MensagemViewHolder(view, firebaseFirestore);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MensagemAdapter.MensagemViewHolder viewHolder = (MensagemAdapter.MensagemViewHolder) holder;
//        viewHolder.tv_receitaData.setText(dataCad[0]+"/"+dataCad[1]);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MensagemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_receitaData, tv_nomeReceita;
        ImageButton imgBtn_excluirReceita;
        View v_indicador;

        public MensagemViewHolder(@NonNull View itemView, FirebaseFirestore firebaseFirestore) {
            super(itemView);
            tv_nomeReceita = itemView.findViewById(R.id.tv_nomeReceita);
            tv_receitaData = itemView.findViewById(R.id.tv_dataRenovacao);
            imgBtn_excluirReceita = itemView.findViewById(R.id.imgBtn_excluirReceita);
            v_indicador = itemView.findViewById(R.id.v_indicadorReceita);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }
}