package com.example.doseapp.adapters;

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

import com.example.doseapp.R;
import com.example.doseapp.models.Receita;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ReceitaAdapter extends RecyclerView.Adapter {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public Context context;
    private List<Receita> receitaList;
    private OnItemClick onItemClick;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    public ReceitaAdapter(Context context, List<Receita> receitaList, OnItemClick onItemClick) {
        this.context = context;
        this.receitaList = receitaList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_receita, parent, false);
        ReceitaAdapter.ReceitaViewHolder viewHolder = new ReceitaAdapter.ReceitaViewHolder(view, receitaList, firebaseFirestore, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ReceitaAdapter.ReceitaViewHolder viewHolder = (ReceitaAdapter.ReceitaViewHolder) holder;
        Receita receita = receitaList.get(position);
        viewHolder.tv_nomeReceita.setText(receita.getNome());

        String data = format.format(new Date());
        String[] dataCad = receita.getDataRenovar().split("/");
        String[] dataAt = data.split("/");
        String color = "";
        GregorianCalendar dataAtual = (GregorianCalendar) Calendar.getInstance();

        GregorianCalendar dataConsulta = new GregorianCalendar(Integer.parseInt(dataCad[2]),
                Integer.parseInt(dataCad[1]) - 1,
                Integer.parseInt(dataCad[0]));

        if (dataAtual.before(dataConsulta)) color = "#32CD32";
        else color = "#CD5C5C";
        viewHolder.v_indicador.setBackgroundColor(Color.parseColor(color));

//        if (Integer.parseInt(dataAt[2]) <= Integer.parseInt(dataCad[2])) {
//            if (Integer.parseInt(dataAt[1]) <= Integer.parseInt(dataCad[1])) {
//                if (data.equals(receita.getDataRenovar())) {
//                    viewHolder.v_indicador.setBackgroundColor(Color.parseColor("#32CD32"));
//                } else if (Integer.parseInt(dataAt[0]) < Integer.parseInt(dataCad[0])) {
//                    viewHolder.v_indicador.setBackgroundColor(Color.parseColor("#6495ED"));
//                } else {
//                    viewHolder.v_indicador.setBackgroundColor(Color.parseColor("#CD5C5C"));
//                }
//            } else {
//                viewHolder.v_indicador.setBackgroundColor(Color.parseColor("#CD5C5C"));
//            }
//        } else {
//            viewHolder.v_indicador.setBackgroundColor(Color.parseColor("#CD5C5C"));
//        }
        viewHolder.tv_receitaData.setText(dataCad[0]+"/"+dataCad[1]);
    }

    @Override
    public int getItemCount() {
        return receitaList.size();
    }

    public static class ReceitaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_receitaData, tv_nomeReceita;
        ImageButton imgBtn_excluirReceita;
        OnItemClick onItemClick;
        View v_indicador;

        public ReceitaViewHolder(@NonNull View itemView, List<Receita> receitaList, FirebaseFirestore firebaseFirestore, OnItemClick onItemClick) {
            super(itemView);
            tv_nomeReceita = itemView.findViewById(R.id.tv_nomeReceita);
            tv_receitaData = itemView.findViewById(R.id.tv_dataRenovacao);
            imgBtn_excluirReceita = itemView.findViewById(R.id.imgBtn_excluirReceita);
            v_indicador = itemView.findViewById(R.id.v_indicadorReceita);
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