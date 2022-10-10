package com.example.doseapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.speech.RecognitionService;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ConsultaAdapter extends RecyclerView.Adapter {
    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public static Context context;
    private static List<Consulta> consultaList;
    private OnItemClick onItemClick;
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

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
        if (consulta.isLembre() == false) {
            viewHolder.imgBtn_alarme.setImageResource(R.drawable.ic_alarm_off);
        }

        String data = format.format(new Date());

        viewHolder.tv_nomeConsulta.setText(consulta.getNome());
        viewHolder.tv_horaConsulta.setText(consulta.getHorario());
        String[] dataCad = consulta.getData().split("/");
        String[] dataAt = data.split("/");

        if (Integer.parseInt(dataAt[2]) <= Integer.parseInt(dataCad[2])) {
            if (Integer.parseInt(dataAt[1]) <= Integer.parseInt(dataCad[1])) {
                if (data.equals(consulta.getData())) {
                    viewHolder.v_indicadorConsulta.setBackgroundColor(Color.parseColor("#32CD32"));
//                    if (Calendar.HOUR_OF_DAY == Integer.parseInt(hr[0])) {
//                        if (Calendar.MINUTE >= Integer.parseInt(hr[1])) {
//                            viewHolder.v_indicador.setBackgroundColor(Color.parseColor("#CD5C5C"));
//                        }else{
//                            viewHolder.v_indicador.setBackgroundColor(Color.parseColor("#32CD32"));
//                        }
//                    } else if (calendar.get(Calendar.HOUR_OF_DAY) < Integer.parseInt(hr[0])) {
//                        viewHolder.v_indicador.setBackgroundColor(Color.parseColor("#32CD32"));
//                    } else {
//                        viewHolder.v_indicador.setBackgroundColor(Color.parseColor("#CD5C5C"));
//                    }
                } else if (Integer.parseInt(dataAt[0]) < Integer.parseInt(dataCad[0])) {
                    viewHolder.v_indicadorConsulta.setBackgroundColor(Color.parseColor("#6495ED"));
                } else {
                    viewHolder.v_indicadorConsulta.setBackgroundColor(Color.parseColor("#CD5C5C"));
                }
            } else {
                viewHolder.v_indicadorConsulta.setBackgroundColor(Color.parseColor("#CD5C5C"));
            }
        } else {
            viewHolder.v_indicadorConsulta.setBackgroundColor(Color.parseColor("#CD5C5C"));
        }
        //Se ele marcar como concluído, usar a cor #F4A460
        viewHolder.tv_diaConsulta.setText(dataCad[0] + "/" + dataCad[1]);
    }

    @Override
    public int getItemCount() {
        return consultaList.size();
    }

    public static class ConsultaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_diaConsulta, tv_horaConsulta, tv_nomeConsulta;
        ImageButton imgBtn_excluirConsulta, imgBtn_alarme;
        OnItemClick onItemClick;
        View v_indicadorConsulta;


        public ConsultaViewHolder(@NonNull View itemView, OnItemClick onItemClick) {
            super(itemView);
            tv_diaConsulta = itemView.findViewById(R.id.tv_diaConsulta);
            tv_horaConsulta = itemView.findViewById(R.id.tv_horaConsulta);
            tv_nomeConsulta = itemView.findViewById(R.id.tv_nomeConsulta);
            imgBtn_excluirConsulta = itemView.findViewById(R.id.imgBtn_excluirConsul);
            imgBtn_alarme = itemView.findViewById(R.id.imgBtn_alertConsul);
            v_indicadorConsulta = itemView.findViewById(R.id.v_indicadorConsulta);

            imgBtn_alarme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (consultaList.get(getAbsoluteAdapterPosition()).isLembre() == true) {
                        imgBtn_alarme.setImageResource(R.drawable.ic_alarm_off);
                        firebaseFirestore.collection("Consultas").document(consultaList.get(getAbsoluteAdapterPosition()).getId()).update("lembre-me", false);
                        consultaList.get(getAbsoluteAdapterPosition()).setLembre(false);
                        gerarSnackBar(view, "Alarme atualizado com sucesso!");
                    } else if (consultaList.get(getAbsoluteAdapterPosition()).isLembre() == false) {
                        consultaList.get(getAbsoluteAdapterPosition()).setLembre(true);
                        firebaseFirestore.collection("Consultas").document(consultaList.get(getAbsoluteAdapterPosition()).getId()).update("lembre-me", true);
                        imgBtn_alarme.setImageResource(R.drawable.ic_alarm);
                        gerarSnackBar(view, "Alarme atualizado com sucesso!");
                    }
                }
            });

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