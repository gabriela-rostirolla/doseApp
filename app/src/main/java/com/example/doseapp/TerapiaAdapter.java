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

public class TerapiaAdapter extends RecyclerView.Adapter {
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static Context context;
    private static List<Terapia> terapiaList;
    //private IdosoCuidadoAdapter.OnItemClick onItemClick;

    public TerapiaAdapter(Context context, List<Terapia> terapiaList) {
        this.context = context;
        this.terapiaList = terapiaList;
        //this.onItemClick = onItemClick;
    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_terapia, parent, false);
        TerapiaAdapter.TerapiaViewHolder viewHolder = new TerapiaAdapter.TerapiaViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TerapiaAdapter.TerapiaViewHolder viewHolder = (TerapiaAdapter.TerapiaViewHolder) holder;
        Terapia terapia = terapiaList.get(position);
        viewHolder.tv_nomeTerapia.setText(terapia.getNome());
        viewHolder.tv_horaTerapia.setText(terapia.getHorario());
        //viewHolder.tv_diaConsulta.setText(terapia.getDiasSemana());
    }

    @Override
    public int getItemCount() {
        return terapiaList.size();
    }

    public static class TerapiaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //private final IdosoCuidadoAdapter.OnItemClick onItemClick;
        TextView tv_diasTerapia, tv_horaTerapia, tv_nomeTerapia;
        ImageButton imgBtn_excluirTerapia;

        public TerapiaViewHolder(@NonNull View itemView) {
            super(itemView);
            //tv_diasTerapia = itemView.findViewById(R.id.tv_diasSemana);
            tv_horaTerapia = itemView.findViewById(R.id.tv_horaTerapia);
            tv_nomeTerapia = itemView.findViewById(R.id.tv_nomeTerapia);
            imgBtn_excluirTerapia = itemView.findViewById(R.id.imgBtn_excluirTerapia);

            imgBtn_excluirTerapia.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setMessage("Deseja realmente excluir?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DocumentReference document = firebaseFirestore.collection("Terapias").document(terapiaList.get(getAbsoluteAdapterPosition()).getId());
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

            //  this.onItemClick = onItemClick;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            //    onItemClick.OnItemClick(getAbsoluteAdapterPosition());
        }
    }
}