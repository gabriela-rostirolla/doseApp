package com.example.doseapp.adapters;

import android.annotation.SuppressLint;
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

import com.example.doseapp.R;
import com.example.doseapp.models.Medicamento;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class MedicamentoAdapter extends RecyclerView.Adapter {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    public Context context;
    private RecyclerView rv;
    private List<Medicamento> medicamentoList;
    private OnItemClick onItemClick;
    private SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
    @SuppressLint("SimpleDateFormat")
    private final SimpleDateFormat formatterData = new SimpleDateFormat("dd/MM/yyyy");

    public MedicamentoAdapter(Context context, RecyclerView rv, List<Medicamento> medicamentoList, OnItemClick onItemClick) {
        this.context = context;
        this.rv = rv;
        this.medicamentoList = medicamentoList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_remedio, parent, false);
        MedicamentoViewHolder viewHolder = new MedicamentoAdapter.MedicamentoViewHolder(view, rv, context, medicamentoList, firebaseFirestore, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MedicamentoAdapter.MedicamentoViewHolder viewHolder = (MedicamentoAdapter.MedicamentoViewHolder) holder;
        Medicamento medicamento = medicamentoList.get(position);
//        if (medicamento.isLembre() == false) {
//            viewHolder.imgBtn_alarme.setImageResource(R.drawable.ic_alarm_off);
//        }

        if (medicamento.getUnidade_intervalo().equals("h")) {
            String hr = medicamento.getProxMed();
            String proxHr[] = hr.split(":");
            Calendar calendar = Calendar.getInstance();
            if (calendar.get(Calendar.HOUR_OF_DAY) == Integer.parseInt(proxHr[0]) &&
                    calendar.get(Calendar.MINUTE) < Integer.parseInt(proxHr[1])) {
                viewHolder.v_indicador.setBackgroundColor(Color.parseColor("#32CD32"));
            }
        }
        viewHolder.tv_proximoMed.setText("Próximo às " + medicamento.getProxMed());
        viewHolder.tv_nomeMedicamento.setText(medicamento.getNome());
        viewHolder.tv_dose.setText(medicamento.getDose());
        viewHolder.tv_posologia.setText(medicamento.getIntervalo() + " " + medicamento.getUnidade_intervalo());
    }

    @Override
    public int getItemCount() {
        return medicamentoList.size();
    }

    public class MedicamentoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_dose, tv_posologia, tv_nomeMedicamento, tv_proximoMed;
        ImageButton imgBtn_excluirMed, imgBtn_alarme;
        OnItemClick onItemClick;
        View v_indicador;

        public MedicamentoViewHolder(@NonNull View itemView, RecyclerView rv, Context context, List<Medicamento> medicamentoList, FirebaseFirestore firebaseFirestore, OnItemClick onItemClick) {
            super(itemView);
            tv_dose = itemView.findViewById(R.id.tv_dose);
            tv_posologia = itemView.findViewById(R.id.tv_posologia);
            tv_nomeMedicamento = itemView.findViewById(R.id.tv_nomeMedicamento);
            imgBtn_excluirMed = itemView.findViewById(R.id.imgBtn_excluirMed);
            imgBtn_alarme = itemView.findViewById(R.id.imgBtn_alert);
            v_indicador = itemView.findViewById(R.id.v_indicador);
            tv_proximoMed = itemView.findViewById(R.id.tv_hrProxMed);

//            imgBtn_alarme.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (medicamentoList.get(getAbsoluteAdapterPosition()).isLembre() == true) {
//                        imgBtn_alarme.setImageResource(R.drawable.ic_alarm_off);
//                        firebaseFirestore.collection("Medicamento").document(medicamentoList.get(getAbsoluteAdapterPosition()).getId()).update("lembre-me", false);
//                        medicamentoList.get(getAbsoluteAdapterPosition()).setLembre(false);
//                        gerarToast("Alarme atualizado com sucesso!", itemView.getContext());
//                    } else if (medicamentoList.get(getAbsoluteAdapterPosition()).isLembre() == false) {
//                        medicamentoList.get(getAbsoluteAdapterPosition()).setLembre(true);
//                        firebaseFirestore.collection("Medicamento").document(medicamentoList.get(getAbsoluteAdapterPosition()).getId()).update("lembre-me", true);
//                        imgBtn_alarme.setImageResource(R.drawable.ic_alarm);
//                        gerarToast("Alarme atualizado com sucesso!", itemView.getContext());
//                    }
//                }
//            });

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
                                            medicamentoList.remove(getAbsoluteAdapterPosition());
                                            //telaMedicamentos.listarMed(medicamentoList,rv,firebaseFirestore, onItemClick, context, medicamentoList.get(getAbsoluteAdapterPosition()).getId());
                                            MedicamentoAdapter adapter = new MedicamentoAdapter(itemView.getContext(), rv, medicamentoList, onItemClick);
                                            adapter.notifyItemRemoved(getAbsoluteAdapterPosition());
                                            gerarToast("Excluido com sucesso!", itemView.getContext());
                                        }
                                    });
                                }
                            })
                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    gerarToast("Operação cancelada", itemView.getContext());
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

    public static void gerarToast(String texto, Context context) {
        Toast.makeText(context, texto, Toast.LENGTH_SHORT).show();
    }
}