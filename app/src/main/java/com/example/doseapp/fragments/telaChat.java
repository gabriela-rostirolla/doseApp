package com.example.doseapp.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.doseapp.adapters.MensagemAdapter;
import com.example.doseapp.databinding.ActivityTelaChatBinding;
import com.example.doseapp.models.Mensagem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class telaChat extends AppCompatActivity {

    private ActivityTelaChatBinding binding;
    private List<Mensagem> list;
    private FirebaseFirestore firebaseFirestore;
    private MensagemAdapter adapter;
    private OkHttpClient client;
    private static final int NORMAL_CLASURE_STATUS = 1000;

    private final class EchoWebSocketListener extends WebSocketListener{

        private String msg;

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            webSocket.send(msg);
//            webSocket.close(NORMAL_CLASURE_STATUS, "Adeus");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            output(text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output(bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            webSocket.close(NORMAL_CLASURE_STATUS, null);
            output("cloasing: "+ code+"/"+reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            output("Error: "+ t.getMessage());
        }
    }

    private void output (final String txt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                preencherMensagens();
            }
        });
    }

    private void startWebSocket(){
        Request request = new Request.Builder().url("ws://192.168.1.84:81/").build();
        EchoWebSocketListener listener = new EchoWebSocketListener();
        WebSocket ws = client.newWebSocket(request, listener);
        client.dispatcher().executorService().shutdown();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTelaChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        client = new OkHttpClient();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(getIntent().getStringExtra("nome"));
        init();

        binding.btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!binding.etChat.getText().toString().isEmpty()) {
                    try {
                        salvarMensagem();
                        startWebSocket();
                        onStart();
                        binding.etChat.setText("");
                    } catch (Exception e) {
                        Toast.makeText(telaChat.this, "Erro: " + e, Toast.LENGTH_SHORT).show();
                        System.out.println(e);
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        list = new ArrayList<>();
        preencherMensagens();
    }

    private void init() {
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void preencherMensagens() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseFirestore.collection("Mensagem")
                .orderBy("data", Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Mensagem msg = new Mensagem();
                                String msgDecode = document.getString("mensagem");
                                msg.setMensagem(msgDecode);
                                msg.setUsuarioEnv(document.getString("usuarioEnv"));
                                msg.setUsuarioRec(document.getString("usuarioRec"));
                                msg.setId(document.getId());

                                String aux = getIntent().getStringExtra("idRec");
                                if ((aux.equals(msg.getUsuarioEnv()) || aux.equals(msg.getUsuarioRec())) && (userId.equals(msg.getUsuarioRec()) || userId.equals(msg.getUsuarioEnv()))) {
                                    list.add(msg);
                                }
                                if (list.size() == 0) {
                                    binding.tvNenhumaMsg.setVisibility(View.VISIBLE);
                                } else {
                                    binding.tvNenhumaMsg.setVisibility(View.INVISIBLE);
                                }
                            }
                            adapter = new MensagemAdapter(
                                    list,
                                    userId
                            );
                            binding.rvListaMensagem.setLayoutManager(new LinearLayoutManager(telaChat.this));
                            binding.rvListaMensagem.setHasFixedSize(false);
                            binding.rvListaMensagem.setAdapter(adapter);
                        }
                    }
                });
    }

    private void salvarMensagem() throws Exception {
        HashMap<String, Object> map = new HashMap<>();
        String msg = binding.etChat.getText().toString();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        map.put("usuarioEnv", userId);
        map.put("mensagem", msg);
        map.put("data", new Date());
        map.put("usuarioRec", getIntent().getStringExtra("idRec"));
        map.put("status", "enviado");

        firebaseFirestore.collection("Mensagem")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("banco_dados_salvos", "Sucesso ao salvar dados!" + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("erro_banco_dados", "Erro ao salvar dados!", e);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}