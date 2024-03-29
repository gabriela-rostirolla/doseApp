package com.example.doseapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
//import android.content.SharedPreferences;
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

import com.example.doseapp.R;
import com.example.doseapp.models.Consulta;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class ConsultaAdapter extends RecyclerView.Adapter {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public Context context;
    private List<Consulta> consultaList;
    private OnItemClick onItemClick;
    //    private static SharedPreferences sharedPreferences;
    private String nomeIdoso;

    public ConsultaAdapter(Context context, List<Consulta> consultaList, OnItemClick onItemClick, String nomeIdoso) {
        this.context = context;
        this.consultaList = consultaList;
        this.onItemClick = onItemClick;
        this.nomeIdoso = nomeIdoso;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_consulta, parent, false);
        ConsultaViewHolder viewHolder = new ConsultaAdapter.ConsultaViewHolder(view, consultaList, nomeIdoso, firebaseFirestore, onItemClick);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        sharedPreferences = context.getSharedPreferences(context.getString(R.string.sharedPrefesAlarme), Context.MODE_PRIVATE);
        ConsultaAdapter.ConsultaViewHolder viewHolder = (ConsultaAdapter.ConsultaViewHolder) holder;
        Consulta consulta = consultaList.get(position);

        viewHolder.tv_nomeConsulta.setText(consulta.getNome());
        viewHolder.tv_horaConsulta.setText(consulta.getHorario());
        String[] dataCad = consulta.getData().split("/");
        String[] hr = consulta.getHorario().split(":");
        String color = "";

        GregorianCalendar dataAtual = (GregorianCalendar) Calendar.getInstance();

        GregorianCalendar dataConsulta = new GregorianCalendar(Integer.parseInt(dataCad[2]),
                Integer.parseInt(dataCad[1]) - 1,
                Integer.parseInt(dataCad[0]));

        if (dataAtual.equals(dataConsulta)) {
            if (Integer.parseInt(hr[0]) < Calendar.HOUR_OF_DAY) {
                color = "#CD5C5C";
            } else {
                color = "#32CD32";
            }
        } else if (dataAtual.before(dataConsulta)) {
            color = "#32CD32";
        } else color = "#CD5C5C";
        viewHolder.v_indicadorConsulta.setBackgroundColor(Color.parseColor(color));

        //Se ele marcar como concluído, usar a cor #F4A460
        viewHolder.tv_diaConsulta.setText(dataCad[0] + "/" + dataCad[1]);

//        int alarme = sharedPreferences.getInt("id alarme - " + consulta.getId(), 0);
//        if (alarme == 1) {
//        viewHolder.imgBtn_alarme.setImageResource(R.drawable.ic_alarm);
//        } else {
//            viewHolder.imgBtn_alarme.setImageResource(R.drawable.ic_alarm_off);
//        }
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

        public ConsultaViewHolder(@NonNull View itemView, List<Consulta> consultaList, String nomeIdoso, FirebaseFirestore firebaseFirestore, OnItemClick onItemClick) {
            super(itemView);
            tv_diaConsulta = itemView.findViewById(R.id.tv_diaConsulta);
            tv_horaConsulta = itemView.findViewById(R.id.tv_horaConsulta);
            tv_nomeConsulta = itemView.findViewById(R.id.tv_nomeConsulta);
            imgBtn_excluirConsulta = itemView.findViewById(R.id.imgBtn_excluirConsul);
            imgBtn_alarme = itemView.findViewById(R.id.imgBtn_alertConsul);
            v_indicadorConsulta = itemView.findViewById(R.id.v_indicadorConsulta);

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
                                            Toast.makeText(view.getContext(), R.string.excluidoComSucesso, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(view.getContext(), R.string.operacaoCancelada, Toast.LENGTH_SHORT).show();
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

    public static void gerarToast(View view, String texto) {
        Toast.makeText(view.getContext(), texto, Toast.LENGTH_SHORT).show();
    }
}