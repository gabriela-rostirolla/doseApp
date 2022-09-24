package com.example.doseapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class IdosoCuidadoAdapter extends RecyclerView.Adapter {

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private List<IdosoCuidado> idosoCuidadoList;
    private OnItemClick onItemClick;
    public Context context;
    //private String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public IdosoCuidadoAdapter(List<IdosoCuidado> idosoCuidadoList, OnItemClick onItemClick, Context context){
        this.idosoCuidadoList = idosoCuidadoList;
        this.onItemClick = onItemClick;
        this.context = context;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_idosos, parent,false);
        IdosoCuidadoViewHolder viewHolder = new IdosoCuidadoViewHolder(view, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        IdosoCuidadoViewHolder viewHolderClass = (IdosoCuidadoViewHolder) holder;
        IdosoCuidado idosoCuidado = idosoCuidadoList.get(position);
        viewHolderClass.tv_nomeIdoso.setText(idosoCuidado.getNome());
    }

    @Override
    public int getItemCount() {
        return idosoCuidadoList.size();
    }

    public class IdosoCuidadoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_nomeIdoso;
        ImageButton imgBtn_editar, imgBtn_excluir, imgBtn_compartilhar;
        OnItemClick onItemClick;

        public IdosoCuidadoViewHolder(@NonNull View itemView, OnItemClick onItemClick) {
            super(itemView);
            tv_nomeIdoso = itemView.findViewById(R.id.tv_nomeIdoso);
            imgBtn_excluir = itemView.findViewById(R.id.imgBtn_excluir);
            imgBtn_compartilhar = itemView.findViewById(R.id.imgBtn_compartilhar);
            imgBtn_editar = itemView.findViewById(R.id.imgBtn_editar);


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
                                            //idosoCuidadoList.remove(idosoCuidadoList.get(getAbsoluteAdapterPosition()));
                                            Snackbar snackbar = Snackbar.make(view, "Excluido com sucesso!", Snackbar.LENGTH_SHORT);
                                            snackbar.setBackgroundTint(Color.WHITE);
                                            snackbar.setTextColor(Color.BLACK);
                                            snackbar.show();
//                                            firebaseFirestore.collection("Receitas")
//                                                    .whereEqualTo("id do idoso", id)
//                                                    .get()
//                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                                            if (task.isSuccessful()){
//                                                                for (QueryDocumentSnapshot doc : task.getResult()) {
//                                                                    Receita receita = new Receita();
//                                                                    receita.setId(doc.getString("id"));
//                                                                    doc.
//                                                                }
//                                                            }
//                                                        }
//                                                    });
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
                    Intent intent = new Intent(itemView.getContext(), telaEditarIdoso.class);
                    intent.putExtra("id", idosoCuidadoList.get(getAbsoluteAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });

//            imgBtn_compartilhar.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Intent intent = new Intent(itemView.getContext(), telaDadosDosIdosos.class);
//                    context.startActivity(intent);
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

    public interface OnItemClick{
        void OnItemClick(int position);
    }
}