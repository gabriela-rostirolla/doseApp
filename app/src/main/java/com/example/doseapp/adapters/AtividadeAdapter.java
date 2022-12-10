package com.example.doseapp.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doseapp.R;
import com.example.doseapp.models.Atividade;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.List;

public class AtividadeAdapter extends RecyclerView.Adapter {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public Context context;
    private List<Atividade> atividadeList;
    private OnItemClick onItemClick;

    public AtividadeAdapter(Context context, List<Atividade> atividadeList, OnItemClick onItemClick) {
        this.context = context;
        this.atividadeList = atividadeList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_atividade, parent, false);
        AtividadeAdapter.AtividadeViewHolder viewHolder = new AtividadeAdapter.AtividadeViewHolder(view, atividadeList, firebaseFirestore, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AtividadeAdapter.AtividadeViewHolder viewHolder = (AtividadeAdapter.AtividadeViewHolder) holder;
        Atividade atividade = atividadeList.get(position);
        viewHolder.tv_atividadeHorario.setText("Atividade: " + atividade.getHorario());
    }

    @Override
    public int getItemCount() {
        return atividadeList.size();
    }

    public static class AtividadeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final OnItemClick onItemClick;
        TextView tv_atividadeHorario;
        ImageButton imgBtn_excluir;

        public AtividadeViewHolder(@NonNull View itemView, List<Atividade> atividadeList, FirebaseFirestore firebaseFirestore, OnItemClick onItemClick) {
            super(itemView);
            tv_atividadeHorario = itemView.findViewById(R.id.tv_atividadeHorario);
            imgBtn_excluir = itemView.findViewById(R.id.imgBtn_excluirAtividade);

            imgBtn_excluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setMessage("Deseja realmente excluir?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DocumentReference document = firebaseFirestore.collection("Diario atividades").document(atividadeList.get(getAbsoluteAdapterPosition()).getId());
                                    document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                            document.delete();
                                            Toast.makeText(itemView.getContext(), "Excluido com sucesso", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(itemView.getContext(), "Operação cancelada", Toast.LENGTH_SHORT).show();
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

    public interface OnItemClick {
        void OnItemClick(int position);
    }
}