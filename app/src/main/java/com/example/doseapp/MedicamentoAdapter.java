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

public class MedicamentoAdapter extends RecyclerView.Adapter {
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static Context context;
    private static List<Medicamento> medicamentoList;
    private OnItemClick onItemClick;

    public MedicamentoAdapter(Context context, List<Medicamento> medicamentoList, OnItemClick onItemClick) {
        this.context = context;
        this.medicamentoList = medicamentoList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_remedio, parent, false);
        MedicamentoViewHolder viewHolder = new MedicamentoAdapter.MedicamentoViewHolder(view, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MedicamentoAdapter.MedicamentoViewHolder viewHolder = (MedicamentoAdapter.MedicamentoViewHolder) holder;
        Medicamento medicamento = medicamentoList.get(position);

        if (medicamento.isLembre() == false) {
            viewHolder.imgBtn_alarme.setImageResource(R.drawable.ic_alarm_off);
        }
        viewHolder.tv_nomeMedicamento.setText(medicamento.getNome());
        viewHolder.tv_dose.setText(medicamento.getDose());
        viewHolder.tv_posologia.setText(medicamento.getIntervalo() + " " + medicamento.getUnidade_intervalo());
    }

    @Override
    public int getItemCount() {
        return medicamentoList.size();
    }

    public static class MedicamentoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_dose, tv_posologia, tv_nomeMedicamento;
        ImageButton imgBtn_excluirMed, imgBtn_alarme;
        OnItemClick onItemClick;

        public MedicamentoViewHolder(@NonNull View itemView, OnItemClick onItemClick) {
            super(itemView);
            tv_dose = itemView.findViewById(R.id.tv_dose);
            tv_posologia = itemView.findViewById(R.id.tv_posologia);
            tv_nomeMedicamento = itemView.findViewById(R.id.tv_nomeMedicamento);
            imgBtn_excluirMed = itemView.findViewById(R.id.imgBtn_excluirMed);
            imgBtn_alarme = itemView.findViewById(R.id.imgBtn_alert);


            imgBtn_alarme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (medicamentoList.get(getAbsoluteAdapterPosition()).isLembre() == true) {
                        imgBtn_alarme.setImageResource(R.drawable.ic_alarm_off);
                        firebaseFirestore.collection("Medicamento").document(medicamentoList.get(getAbsoluteAdapterPosition()).getId()).update("lembre-me", false);
                        medicamentoList.get(getAbsoluteAdapterPosition()).setLembre(false);
                        gerarSnackBar(view, "Alarme atualizado com sucesso!");
                    } else if (medicamentoList.get(getAbsoluteAdapterPosition()).isLembre() == false) {
                        medicamentoList.get(getAbsoluteAdapterPosition()).setLembre(true);
                        firebaseFirestore.collection("Medicamento").document(medicamentoList.get(getAbsoluteAdapterPosition()).getId()).update("lembre-me", true);
                        imgBtn_alarme.setImageResource(R.drawable.ic_alarm);
                        gerarSnackBar(view, "Alarme atualizado com sucesso!");
                    }
                }
            });

            imgBtn_excluirMed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setMessage("Deseja realmente excluir?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DocumentReference document = firebaseFirestore.collection("Medicamento")
                                            .document(medicamentoList.get(getAbsoluteAdapterPosition()).getId());
                                    document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                            document.delete();
                                            gerarSnackBar(view, "Excluido com sucesso!");
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    gerarSnackBar(view, "Operação cancelada");
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

    public static void gerarSnackBar(View view, String texto) {
        Snackbar snackbar = Snackbar.make(view, texto, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }
}