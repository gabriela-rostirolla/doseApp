package com.example.doseapp.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.doseapp.R;
import com.example.doseapp.models.Alimentacao;
import com.example.doseapp.models.Atividade;
import com.example.doseapp.models.Consulta;
import com.example.doseapp.models.DiarioDeCuidado;
import com.example.doseapp.models.Medicamento;
import com.example.doseapp.models.Receita;
import com.example.doseapp.models.Terapia;
import com.example.doseapp.models.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class ContatoAdapter extends RecyclerView.Adapter {

    private List<Usuario> usuarioList;
    private IdosoCuidadoAdapter.OnItemClick onItemClick;
    public Context context;

    public ContatoAdapter(List<Usuario> usuarioList, IdosoCuidadoAdapter.OnItemClick onItemClick, Context context) {
        this.usuarioList = usuarioList;
        this.onItemClick = onItemClick;
        this.context = context;
    }

    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contato, parent, false);
        ContatoAdapter.ContatoViewHolder viewHolder = new ContatoAdapter.ContatoViewHolder(view, onItemClick);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ContatoAdapter.ContatoViewHolder viewHolderClass = (ContatoAdapter.ContatoViewHolder) holder;
        Usuario user = usuarioList.get(position);
        viewHolderClass.tv_nome.setText(user.getNome());
        viewHolderClass.tv_ultMens.setText("Modelo da ultima mensagem");
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public class ContatoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_nome, tv_ultMens;
        IdosoCuidadoAdapter.OnItemClick onItemClick;

        public ContatoViewHolder(@NonNull View itemView, IdosoCuidadoAdapter.OnItemClick onItemClick) {
            super(itemView);
            tv_nome = itemView.findViewById(R.id.tv_nome);
            tv_ultMens = itemView.findViewById(R.id.tv_ultMens);
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
