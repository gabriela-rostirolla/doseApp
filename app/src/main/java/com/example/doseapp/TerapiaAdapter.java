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

import java.util.Calendar;
import java.util.List;

public class TerapiaAdapter extends RecyclerView.Adapter {
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static Context context;
    private static List<Terapia> terapiaList;
    private OnItemClick onItemClick;

    public TerapiaAdapter(Context context, List<Terapia> terapiaList, OnItemClick onItemClick) {
        this.context = context;
        this.terapiaList = terapiaList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_terapia, parent, false);
        TerapiaAdapter.TerapiaViewHolder viewHolder = new TerapiaAdapter.TerapiaViewHolder(view, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TerapiaAdapter.TerapiaViewHolder viewHolder = (TerapiaAdapter.TerapiaViewHolder) holder;
        Terapia terapia = terapiaList.get(position);
        viewHolder.tv_nomeTerapia.setText(terapia.getNome());
        viewHolder.tv_horaTerapia.setText(terapia.getHorario());
        List<String> list = terapia.getDiasSemana();
        Calendar calendar = Calendar.getInstance();

        int diaSemana = calendar.get(Calendar.DAY_OF_WEEK);
        String diaSemAt = "";

        if(diaSemana == 1) diaSemAt = "dom";
        else if(diaSemana == 2) diaSemAt = "seg";
        else if(diaSemana == 3) diaSemAt = "ter";
        else if(diaSemana == 4) diaSemAt = "qua";
        else if(diaSemana == 5) diaSemAt = "qui";
        else if(diaSemana == 6) diaSemAt = "sex";
        else if(diaSemana == 7) diaSemAt = "sab";

        if(list.contains(diaSemAt)) viewHolder.v_indicador.setBackgroundColor(Color.parseColor("#32CD32"));

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals("dom")) viewHolder.tv_dom.setTextColor(Color.parseColor("#6495ED"));
            else if (list.get(i).equals("seg")) viewHolder.tv_seg.setTextColor(Color.parseColor("#6495ED"));
            else if (list.get(i).equals("ter")) viewHolder.tv_ter.setTextColor(Color.parseColor("#6495ED"));
            else if (list.get(i).equals("qua")) viewHolder.tv_qua.setTextColor(Color.parseColor("#6495ED"));
            else if (list.get(i).equals("qui")) viewHolder.tv_qui.setTextColor(Color.parseColor("#6495ED"));
            else if (list.get(i).equals("sex")) viewHolder.tv_sex.setTextColor(Color.parseColor("#6495ED"));
            else if (list.get(i).equals("sab")) viewHolder.tv_sab.setTextColor(Color.parseColor("#6495ED"));
        }
    }

    @Override
    public int getItemCount() {
        return terapiaList.size();
    }

    public static class TerapiaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_horaTerapia, tv_nomeTerapia, tv_dom, tv_seg, tv_ter, tv_qua, tv_qui, tv_sex, tv_sab;
        ImageButton imgBtn_excluirTerapia;
        OnItemClick onItemClick;
        View v_indicador;

        public TerapiaViewHolder(@NonNull View itemView, OnItemClick onItemClick) {
            super(itemView);
            tv_dom = itemView.findViewById(R.id.tv_dom);
            tv_seg = itemView.findViewById(R.id.tv_seg);
            tv_ter = itemView.findViewById(R.id.tv_ter);
            tv_qua = itemView.findViewById(R.id.tv_qua);
            tv_qui = itemView.findViewById(R.id.tv_qui);
            tv_sex = itemView.findViewById(R.id.tv_sex);
            tv_sab = itemView.findViewById(R.id.tv_qua);
            tv_horaTerapia = itemView.findViewById(R.id.tv_horaTerapia);
            tv_nomeTerapia = itemView.findViewById(R.id.tv_nomeTerapia);
            imgBtn_excluirTerapia = itemView.findViewById(R.id.imgBtn_excluirTerapia);
            v_indicador = itemView.findViewById(R.id.v_indicadorTerapia);

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