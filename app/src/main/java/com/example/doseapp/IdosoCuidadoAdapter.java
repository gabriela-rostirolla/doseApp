package com.example.doseapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.List;

public class IdosoCuidadoAdapter extends RecyclerView.Adapter {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private List<IdosoCuidado> idosoCuidadoList;
    private OnItemClick onItemClick;
    public Context context;
    private String idNovoCuidador;

    public IdosoCuidadoAdapter(List<IdosoCuidado> idosoCuidadoList, OnItemClick onItemClick, Context context) {
        this.idosoCuidadoList = idosoCuidadoList;
        this.onItemClick = onItemClick;
        this.context = context;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_idosos, parent, false);
        IdosoCuidadoViewHolder viewHolder = new IdosoCuidadoViewHolder(view, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        IdosoCuidadoViewHolder viewHolderClass = (IdosoCuidadoViewHolder) holder;
        IdosoCuidado idosoCuidado = idosoCuidadoList.get(position);

        if (idosoCuidado.isCuidado() == false) {
            viewHolderClass.imgBtn_cuidado.setImageResource(R.drawable.ic_baseline_work_off_24);
        }
        viewHolderClass.tv_nomeIdoso.setText(idosoCuidado.getNome());
    }

    @Override
    public int getItemCount() {
        return idosoCuidadoList.size();
    }

    public class IdosoCuidadoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_nomeIdoso;
        ImageButton imgBtn_editar, imgBtn_excluir, imgBtn_compartilhar, imgBtn_cuidado;
        OnItemClick onItemClick;

        public IdosoCuidadoViewHolder(@NonNull View itemView, OnItemClick onItemClick) {
            super(itemView);
            tv_nomeIdoso = itemView.findViewById(R.id.tv_nomeIdoso);
            imgBtn_excluir = itemView.findViewById(R.id.imgBtn_excluir);
            imgBtn_compartilhar = itemView.findViewById(R.id.imgBtn_compartilhar);
            imgBtn_editar = itemView.findViewById(R.id.imgBtn_editar);
            imgBtn_cuidado = itemView.findViewById(R.id.imgBtn_trabalho);
            imgBtn_cuidado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (idosoCuidadoList.get(getAbsoluteAdapterPosition()).isCuidado() == true) {
                        imgBtn_cuidado.setImageResource(R.drawable.ic_baseline_work_off_24);
                        firebaseFirestore.collection("Idosos cuidados").document(idosoCuidadoList.get(getAbsoluteAdapterPosition()).getId()).update("cuidado", false);
                        idosoCuidadoList.get(getAbsoluteAdapterPosition()).setCuidado(false);
                        gerarSnackBar(view, "Período de cuidado finalizado!");
                    } else if (idosoCuidadoList.get(getAbsoluteAdapterPosition()).isCuidado() == false) {
                        idosoCuidadoList.get(getAbsoluteAdapterPosition()).setCuidado(true);
                        firebaseFirestore.collection("Idosos cuidados").document(idosoCuidadoList.get(getAbsoluteAdapterPosition()).getId()).update("cuidado", true);
                        imgBtn_cuidado.setImageResource(R.drawable.ic_baseline_work_24);
                        gerarSnackBar(view, "Período de cuidado inicializado!");
                    }
                }
            });
            imgBtn_excluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setMessage("Deseja realmente excluir?")
                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    DocumentReference document = firebaseFirestore.collection("Idosos cuidados").document(idosoCuidadoList.get(getAbsoluteAdapterPosition()).getId());
                                    document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                            document.delete();
                                            Snackbar snackbar = Snackbar.make(view, "Excluido com sucesso!", Snackbar.LENGTH_SHORT);
                                            snackbar.setBackgroundTint(Color.WHITE);
                                            snackbar.setTextColor(Color.BLACK);
                                            snackbar.show();
                                            firebaseFirestore.collection("Medicamento")
                                                    .whereEqualTo("id do idoso", idosoCuidadoList.get(getAbsoluteAdapterPosition()).getId())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                    Medicamento medicamento = new Medicamento();
                                                                    medicamento.setId(doc.getId());
                                                                    firebaseFirestore.collection("Medicamento").document(medicamento.getId()).delete();
                                                                }
                                                            }
                                                        }
                                                    });
                                            firebaseFirestore.collection("Consultas")
                                                    .whereEqualTo("id do idoso", idosoCuidadoList.get(getAbsoluteAdapterPosition()).getId())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                    Consulta consulta = new Consulta();
                                                                    consulta.setId(doc.getId());
                                                                    firebaseFirestore.collection("Consultas").document(consulta.getId()).delete();
                                                                }
                                                            }
                                                        }
                                                    });
                                            firebaseFirestore.collection("Receitas")
                                                    .whereEqualTo("id do idoso", idosoCuidadoList.get(getAbsoluteAdapterPosition()).getId())
                                                    .get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if (task.isSuccessful()) {
                                                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                                                    Receita receita = new Receita();
                                                                    receita.setId(doc.getId());
                                                                    firebaseFirestore.collection("Consultas").document(receita.getId()).delete();
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

            imgBtn_editar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), telaCadastroIdosoCuidado.class);
                    intent.putExtra("id", idosoCuidadoList.get(getAbsoluteAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });

            imgBtn_compartilhar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Digite o email do destinatário");
                    final View inflater = builder.create().getLayoutInflater().inflate(R.layout.dialog_compartilhar, null);
                    builder.setView(inflater);

                    builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    EditText et_email = inflater.findViewById(R.id.et_emailComp);
                                    firebaseFirestore.collection("Usuarios")
                                            .whereEqualTo("email", et_email.getText().toString())
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        idNovoCuidador = document.getId();
                                                        if (idNovoCuidador.isEmpty()) {
                                                            gerarSnackBar(view, "O email inserido não está vinculado a nenhuma conta");
                                                        } else {
                                                            firebaseFirestore.collection("Idosos cuidados")
                                                                    .document(idosoCuidadoList.get(getAbsoluteAdapterPosition()).getId())
                                                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> t) {
                                                                            List<String> list = (List<String>) t.getResult().get("cuidador id");
                                                                            list.add(idNovoCuidador);
                                                                            firebaseFirestore.collection("Idosos cuidados").document(idosoCuidadoList.get(getAbsoluteAdapterPosition()).getId()).update("cuidador id", list);
                                                                            gerarSnackBar(view, idosoCuidadoList.get(getAbsoluteAdapterPosition()).getNome() + " compartilhado com sucesso!");
                                                                        }
                                                                    });
                                                        }
                                                        return;
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    gerarSnackBar(view, "Não foi possível compartilhar!");
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
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