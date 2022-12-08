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
import android.widget.Toast;

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

public class MensagemAdapter extends RecyclerView.Adapter {

    private List<IdosoCuidado> idosoCuidadoList;
    public Context context;
    int i = 0;

    private String idNovoCuidador;

    public MensagemAdapter(List<IdosoCuidado> idosoCuidadoList, Context context) {
        this.idosoCuidadoList = idosoCuidadoList;
        this.context = context;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(idosoCuidadoList.get(0).isCuidado()){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensagem_env, parent, false);
            MensagemViewHolder viewHolder = new MensagemViewHolder(view);
            return viewHolder;
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mensagem_rec, parent, false);
            MensagemViewHolder viewHolder = new MensagemViewHolder(view);
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MensagemViewHolder viewHolderClass = (MensagemViewHolder) holder;
        IdosoCuidado idosoCuidado = idosoCuidadoList.get(position);

//        if (idosoCuidado.isCuidado() == false) {
//            viewHolderClass.imgBtn_cuidado.setImageResource(R.drawable.ic_baseline_work_off_24);
//        }
        viewHolderClass.tv_msg.setText("  "+idosoCuidado.getNome()+"  ");
    }

    @Override
    public int getItemCount() {
        return idosoCuidadoList.size();
    }

    public class MensagemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_msg;

        public MensagemViewHolder(@NonNull View itemView) {
            super(itemView);
                tv_msg = itemView.findViewById(R.id.tv_msg);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
        }
    }

    public static void gerarToast(View view, String texto) {
        Toast.makeText(view.getContext(), texto, Toast.LENGTH_SHORT).show();
    }
}