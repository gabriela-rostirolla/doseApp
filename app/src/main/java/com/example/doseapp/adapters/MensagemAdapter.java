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
import com.example.doseapp.databinding.ItemMensagemEnvBinding;
import com.example.doseapp.databinding.ItemMensagemRecBinding;
import com.example.doseapp.models.IdosoCuidado;
import com.example.doseapp.models.Mensagem;

import java.util.List;

public class MensagemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<Mensagem> mensagemList;
    private final String usuarioEnv;
    public static int VIEW_TYPE_SENT = 1;
    public static int VIEW_TYPE_RECEIVED = 2;

    public MensagemAdapter(List<Mensagem> mensagemList, String usuarioEnv) {
        this.mensagemList = mensagemList;
        this.usuarioEnv = usuarioEnv;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new MensagemEnvViewHolder(
                    ItemMensagemEnvBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {
            return new MensagemRecViewHolder(
                    ItemMensagemRecBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT){
            ((MensagemEnvViewHolder) holder).setData(mensagemList.get(position));
        }else{
            ((MensagemRecViewHolder) holder).setData(mensagemList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mensagemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mensagemList.get(position).getUsuarioEnv().equals(usuarioEnv)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class MensagemEnvViewHolder extends RecyclerView.ViewHolder {

        private final ItemMensagemEnvBinding binding;

        MensagemEnvViewHolder(ItemMensagemEnvBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(Mensagem msg) {
            binding.tvMsg.setText(msg.getMensagem());
            //binding.tvHr.setText(msg.getData);
        }
    }

    static class MensagemRecViewHolder extends RecyclerView.ViewHolder {

        private final ItemMensagemRecBinding binding;

        MensagemRecViewHolder(ItemMensagemRecBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(Mensagem msg) {
            binding.tvMsg.setText(msg.getMensagem());
            //binding.tvHr.setText(msg.getData);
        }
    }
}