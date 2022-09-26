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

public class ConsultaAdapter extends RecyclerView.Adapter {
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static Context context;
    private static List<Consulta> consultaList;
    private static OnItemClick onItemClick;

    public ConsultaAdapter(Context context, List<Consulta> consultaList, OnItemClick onItemClick) {
        this.context = context;
        this.consultaList = consultaList;
        this.onItemClick = onItemClick;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_consulta, parent, false);
        ConsultaViewHolder viewHolder = new ConsultaAdapter.ConsultaViewHolder(view, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ConsultaAdapter.ConsultaViewHolder viewHolder = (ConsultaAdapter.ConsultaViewHolder) holder;
        Consulta consulta = consultaList.get(position);
        viewHolder.tv_nomeConsulta.setText(consulta.getNome());
        viewHolder.tv_horaConsulta.setText(consulta.getHorario());
        viewHolder.tv_diaConsulta.setText(consulta.getData());
    }

    @Override
    public int getItemCount() {
        return consultaList.size();
    }

    public static class ConsultaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_diaConsulta, tv_horaConsulta, tv_nomeConsulta;
        ImageButton imgBtn_excluirConsulta;
        OnItemClick onItemClick;

        public ConsultaViewHolder(@NonNull View itemView, OnItemClick onItemClick) {
            super(itemView);
            tv_diaConsulta = itemView.findViewById(R.id.tv_diaConsulta);
            tv_horaConsulta = itemView.findViewById(R.id.tv_horaConsulta);
            tv_nomeConsulta = itemView.findViewById(R.id.tv_nomeConsulta);
            imgBtn_excluirConsulta = itemView.findViewById(R.id.imgBtn_excluirConsul);

            imgBtn_excluirConsulta.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setMessage("Deseja realmente excluir?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DocumentReference document = firebaseFirestore.collection("Consultas").document(consultaList.get(getAbsoluteAdapterPosition()).getId());
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