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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class DiarioDeCuidadoAdapter extends RecyclerView.Adapter {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public Context context;
    private List<DiarioDeCuidado> diarioList;
    private OnItemClick onItemClick;

    public DiarioDeCuidadoAdapter(Context context, List<DiarioDeCuidado> diarioList, OnItemClick onItemClick) {
        this.context = context;
        this.diarioList = diarioList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_diario, parent, false);
        DiarioDeCuidadoAdapter.DiarioDeCuidadoViewHolder viewHolder = new DiarioDeCuidadoAdapter.DiarioDeCuidadoViewHolder(view, diarioList, firebaseFirestore, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DiarioDeCuidadoAdapter.DiarioDeCuidadoViewHolder viewHolder = (DiarioDeCuidadoAdapter.DiarioDeCuidadoViewHolder) holder;
        DiarioDeCuidado diario = diarioList.get(position);
        viewHolder.tv_dataDiario.setText("Dia: " + diario.getData());
    }

    @Override
    public int getItemCount() {
        return diarioList.size();
    }

    public static class DiarioDeCuidadoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final OnItemClick onItemClick;
        TextView tv_dataDiario;
        ImageButton imgBtn_excluir;

        public DiarioDeCuidadoViewHolder(@NonNull View itemView, List<DiarioDeCuidado> diarioList, FirebaseFirestore firebaseFirestore, OnItemClick onItemClick) {
            super(itemView);
            tv_dataDiario = itemView.findViewById(R.id.tv_dataDiario);
            imgBtn_excluir = itemView.findViewById(R.id.imgBtn_excluirDiario);

            imgBtn_excluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setMessage("Deseja realmente excluir?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DocumentReference document = firebaseFirestore.collection("Diarios").document(diarioList.get(getAbsoluteAdapterPosition()).getId());
                                    document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                            document.delete();
                                            Toast.makeText(itemView.getContext(), itemView.getContext().
                                                            getString(R.string.excluidoComSucesso),
                                                    Toast.LENGTH_SHORT).show();

                                            firebaseFirestore.collection("Diario atividades")
                                                    .whereEqualTo("diario id", diarioList.get(getAbsoluteAdapterPosition()).getId())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                    Atividade atividade = new Atividade();
                                                                    atividade.setId(doc.getId());
                                                                    firebaseFirestore.collection("Diario atividades").document(atividade.getId()).delete();
                                                                }
                                                            }
                                                        }
                                                    });

                                            firebaseFirestore.collection("Alimentacao")
                                                    .whereEqualTo("diario id", diarioList.get(getAbsoluteAdapterPosition()).getId())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                    Alimentacao alimentacao = new Alimentacao();
                                                                    alimentacao.setId(doc.getId());
                                                                    firebaseFirestore.collection("Diario atividades").document(alimentacao.getId()).delete();
                                                                }
                                                            }
                                                        }
                                                    });
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

    public interface OnItemClick {
        void OnItemClick(int position);
    }
}
