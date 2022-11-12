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
import android.widget.Toast;

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

public class AlimentacaoAdapter extends RecyclerView.Adapter {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public Context context;
    private List<Alimentacao> alimentacaoList;
    private OnItemClick onItemClick;

    public AlimentacaoAdapter(Context context, List<Alimentacao> alimentacaoList, OnItemClick onItemClick) {
        this.context = context;
        this.alimentacaoList = alimentacaoList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_alimentacao, parent, false);
        AlimentacaoAdapter.AlimentacaoViewHolder viewHolder = new AlimentacaoAdapter.AlimentacaoViewHolder(view, alimentacaoList, firebaseFirestore, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AlimentacaoAdapter.AlimentacaoViewHolder viewHolder = (AlimentacaoAdapter.AlimentacaoViewHolder) holder;
        Alimentacao alimentacao = alimentacaoList.get(position);
        viewHolder.tv_alimentacaoHorario.setText("Alimentação: " + alimentacao.getHorario());
    }

    @Override
    public int getItemCount() {
        return alimentacaoList.size();
    }

    public static class AlimentacaoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final OnItemClick onItemClick;
        TextView tv_alimentacaoHorario;
        ImageButton imgBtn_excluir;

        public AlimentacaoViewHolder(@NonNull View itemView,List<Alimentacao> alimentacaoList, FirebaseFirestore firebaseFirestore, OnItemClick onItemClick) {
            super(itemView);
            tv_alimentacaoHorario = itemView.findViewById(R.id.tv_alimentacaoHorario);
            imgBtn_excluir = itemView.findViewById(R.id.imgBtn_excluirAlimentacao);

            imgBtn_excluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setMessage("Deseja realmente excluir?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DocumentReference document = firebaseFirestore.collection("Alimentacao").document(alimentacaoList.get(getAbsoluteAdapterPosition()).getId());
                                    document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                            document.delete();
                                            Toast.makeText(view.getContext(), view.getContext().getString(R.string.excluidoComSucesso), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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

    public interface OnItemClick {
        void OnItemClick(int position);
    }
}
