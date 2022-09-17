package com.example.doseapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String idIdoso;
    private Context context;
    private List<Medicamento> medicamentoList;
    private IdosoCuidadoAdapter.OnItemClick onItemClick;



    public MedicamentoAdapter(Context context, List<Medicamento> medicamentoList) {
        //this.idIdoso = idIdoso;
        this.context = context;
        this.medicamentoList = medicamentoList;
        //this.onItemClick = onItemClick;
    }


    @NonNull
    @Override

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remedio, parent, false);
        MedicamentoViewHolder viewHolder = new MedicamentoAdapter.MedicamentoViewHolder(view, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MedicamentoAdapter.MedicamentoViewHolder viewHolderClass = (MedicamentoAdapter.MedicamentoViewHolder) holder;
        Medicamento medicamento = medicamentoList.get(position);
        viewHolderClass.tv_nomeMedicamento.setText(medicamento.getNome());
        viewHolderClass.tv_dose.setText(medicamento.getDose());
        viewHolderClass.tv_posologia.setText(medicamento.getPosologia());
    }

    @Override
    public int getItemCount() {
        return medicamentoList.size();
    }

    public class MedicamentoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final IdosoCuidadoAdapter.OnItemClick onItemClick;
        TextView tv_dose, tv_posologia, tv_nomeMedicamento;
        Button imgBtn_editarMed, imgBtn_excluirMed;

        public MedicamentoViewHolder(@NonNull View itemView, IdosoCuidadoAdapter.OnItemClick onItemClick) {
            super(itemView);
            tv_dose = itemView.findViewById(R.id.tv_dose);
            tv_posologia = itemView.findViewById(R.id.tv_posologia);
            tv_nomeMedicamento = itemView.findViewById(R.id.tv_nomeMedicamento);
            imgBtn_editarMed = itemView.findViewById(R.id.imgBtn_editarMed);
            imgBtn_excluirMed = itemView.findViewById(R.id.imgBtn_excluirMed);

            imgBtn_editarMed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(itemView.getContext(), telaEditarMed.class);
                    //intent.putExtra("id", idosoCuidadoList.get(getAbsoluteAdapterPosition()).getId());
                    //context.startActivity(intent);
                }
            });

//            imgBtn_excluirMed.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
//                    builder.setMessage("Deseja realmente excluir?")
//                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    DocumentReference document = firebaseFirestore.collection(nomeColecao).document(idosoCuidadoList.get(getAbsoluteAdapterPosition()).getId());
//                                    document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                                        @Override
//                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                                            document.delete();
//                                            //idosoCuidadoList.remove(idosoCuidadoList.get(getAbsoluteAdapterPosition()));
//                                            Snackbar snackbar = Snackbar.make(view, "Excluido com sucesso!", Snackbar.LENGTH_SHORT);
//                                            snackbar.setBackgroundTint(Color.WHITE);
//                                            snackbar.setTextColor(Color.BLACK);
//                                            snackbar.show();
//                                        }
//                                    });
//                                }
//                            })
//                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    //return;
//                                    Snackbar snackbar = Snackbar.make(view, "Operação cancelada", Snackbar.LENGTH_SHORT);
//                                    snackbar.setBackgroundTint(Color.WHITE);
//                                    snackbar.setTextColor(Color.BLACK);
//                                    snackbar.show();
//                                }
//                            });
//                    builder.create();
//                    builder.show();
//                }
//            });
            this.onItemClick = onItemClick;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemClick.OnItemClick(getAbsoluteAdapterPosition());
        }
    }
}