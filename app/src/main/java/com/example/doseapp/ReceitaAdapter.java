package com.example.doseapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

public class ReceitaAdapter extends RecyclerView.Adapter {
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static Context context;
    private static List<Receita> receitaList;
    private OnItemClick onItemClick;

    public ReceitaAdapter(Context context, List<Receita> receitaList, OnItemClick onItemClick) {
        this.context = context;
        this.receitaList = receitaList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_receita, parent, false);
        ReceitaAdapter.ReceitaViewHolder viewHolder = new ReceitaAdapter.ReceitaViewHolder(view, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReceitaAdapter.ReceitaViewHolder viewHolder = (ReceitaAdapter.ReceitaViewHolder) holder;
        Receita receita = receitaList.get(position);
        viewHolder.tv_nomeReceita.setText(receita.getNome());
        viewHolder.tv_receitaData.setText(receita.getDataRenovar());
        //viewHolder.tv_diaConsulta.setText(terapia.getDiasSemana());
    }

    @Override
    public int getItemCount() {
        return receitaList.size();
    }

    public static class ReceitaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //private final IdosoCuidadoAdapter.OnItemClick onItemClick;
        TextView tv_receitaData, tv_nomeReceita;
        ImageButton imgBtn_excluirReceita;
        OnItemClick onItemClick;

        public ReceitaViewHolder(@NonNull View itemView,  OnItemClick onItemClick) {
            super(itemView);
            //tv_diasTerapia = itemView.findViewById(R.id.tv_diasSemana);
            tv_nomeReceita = itemView.findViewById(R.id.tv_nomeReceita);
            tv_receitaData = itemView.findViewById(R.id.tv_dataRenovacao);
            imgBtn_excluirReceita = itemView.findViewById(R.id.imgBtn_excluirReceita);

            imgBtn_excluirReceita.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setMessage("Deseja realmente excluir?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DocumentReference document = firebaseFirestore.collection("Receitas").document(receitaList.get(getAbsoluteAdapterPosition()).getId());
                                    document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                            document.delete();
                                            Snackbar snackbar = Snackbar.make(view, "Excluido com sucesso!", Snackbar.LENGTH_SHORT);
                                            snackbar.setBackgroundTint(Color.WHITE);
                                            snackbar.setTextColor(Color.BLACK);
                                            snackbar.show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //return;
                                    Snackbar snackbar = Snackbar.make(view, "Operação cancelada", Snackbar.LENGTH_SHORT);
                                    snackbar.setBackgroundTint(Color.WHITE);
                                    snackbar.setTextColor(Color.BLACK);
                                    snackbar.show();
                                }
                            });
                    builder.create();
                    builder.show();
                }
            });

            this.onItemClick = onItemClick;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClick.OnItemClick(getAbsoluteAdapterPosition());
        }
    }

    public interface OnItemClick{
        void OnItemClick(int position);
    }
}