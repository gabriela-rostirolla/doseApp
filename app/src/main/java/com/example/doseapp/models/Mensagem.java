package com.example.doseapp.models;

public class Mensagem {
    private String usuarioEnv, usuarioRec, mensagem, data, id, status;

    public Mensagem() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsuarioEnv() {
        return usuarioEnv;
    }

    public void setUsuarioEnv(String usuarioEnv) {
        this.usuarioEnv = usuarioEnv;
    }

    public String getUsuarioRec() {
        return usuarioRec;
    }

    public void setUsuarioRec(String usuarioRec) {
        this.usuarioRec = usuarioRec;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
