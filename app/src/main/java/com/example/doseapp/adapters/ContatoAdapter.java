package com.example.doseapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doseapp.R;
import com.example.doseapp.models.Mensagem;
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
import com.google.firebase.firestore.Query;
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
        viewHolderClass.tv_ultMens.setText(user.getUltMen());
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

        firebaseFirestore.collection("Mensagem")
                .orderBy("data", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Mensagem msg = new Mensagem();
                                msg.setUsuarioEnv(document.getString("usuarioEnv"));
                                msg.setUsuarioRec(document.getString("usuarioRec"));
                                msg.setMensagem((document.getString("mensagem")));

                                String aux = user.getId();
                                if ((aux.equals(msg.getUsuarioEnv()) || aux.equals(msg.getUsuarioRec())) && (userId.equals(msg.getUsuarioRec()) || userId.equals(msg.getUsuarioEnv()))) {
                                    if (aux.equals(msg.getUsuarioEnv())) {
                                        try {
                                            viewHolderClass.tv_ultMens.setText(user.getNome() + ": " + document.getString("mensagem"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else if (aux.equals(msg.getUsuarioRec())) {
                                        try {
                                            viewHolderClass.tv_ultMens.setText("Você: " + document.getString("mensagem"));
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    viewHolderClass.tv_dataMsg.setVisibility(View.VISIBLE);
                                    viewHolderClass.tv_dataMsg.setText(String.valueOf(document.getDate("data")));

                                    break;
                                } else {
                                    viewHolderClass.tv_ultMens.setText("");
                                    viewHolderClass.tv_dataMsg.setVisibility(View.INVISIBLE);
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public class ContatoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_nome, tv_ultMens, tv_dataMsg;
        IdosoCuidadoAdapter.OnItemClick onItemClick;

        public ContatoViewHolder(@NonNull View itemView, IdosoCuidadoAdapter.OnItemClick onItemClick) {
            super(itemView);
            tv_nome = itemView.findViewById(R.id.tv_nome);
            tv_ultMens = itemView.findViewById(R.id.tv_ultMens);
            tv_dataMsg = itemView.findViewById(R.id.tv_dataMsg);
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

//    public int contarMensagens(FirebaseFirestore firebaseFirestore){
//        firebaseFirestore.collection("Mensagem")
//                .whereEqualTo("status", "enviado")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Mensagem msg = new Mensagem();
//                                msg.setUsuarioEnv(document.getString("usuarioEnv"));
//                                msg.setUsuarioRec(document.getString("usuarioRec"));
//                                msg.setMensagem((document.getString("mensagem")));
//
//                                if ((aux.equals(msg.getUsuarioEnv()) || aux.equals(msg.getUsuarioRec())) && (userId.equals(msg.getUsuarioRec()) || userId.equals(msg.getUsuarioEnv()))) {
//
//                                    if (aux.equals(msg.getUsuarioEnv())) {
//                                        try {
//                                            viewHolderClass.tv_ultMens.setText(user.getNome() + ": " + document.getString("mensagem"));
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else if (aux.equals(msg.getUsuarioRec())) {
//                                        try {
//                                            viewHolderClass.tv_ultMens.setText("Você: " + document.getString("mensagem"));
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                    break;
//                                } else {
//                                    viewHolderClass.tv_ultMens.setText("");
//                                }
//                            }
//                        }
//                    }
//                });
//    }
}
