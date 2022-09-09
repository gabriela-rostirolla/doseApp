package com.example.doseapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;

public class telaEditarIdoso extends AppCompatActivity {

    private Button btn_editarIdoso;
    private EditText et_nomeIdosoEdit, et_enderecoIdosoEdit, et_telefoneIdosoEdit, et_nascIdosoEdit, et_obsEdit;
    private List<IdosoCuidado> idosoCuidadoList;
    private String userId;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private RadioButton rb_feminino, rb_masculino, rb_outro;
    int posicao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_editar_idoso);
        inicializarComponentes();
        posicao = getIntent().getIntExtra("posicao", 0);
        listarIdososCuidados();
    }

    protected void inicializarComponentes(){
        btn_editarIdoso = findViewById(R.id.btn_cadastrarIdoso);
        et_nomeIdosoEdit = findViewById(R.id.et_nomeIdoso);
        et_enderecoIdosoEdit = findViewById(R.id.et_enderecoIdoso);
        et_telefoneIdosoEdit = findViewById(R.id.et_telefoneIdoso);
        et_nascIdosoEdit = findViewById(R.id.et_dataNascIdoso);
        et_obsEdit = findViewById(R.id.et_obsIdoso);
        rb_feminino = findViewById(R.id.rb_feminino);
        rb_masculino = findViewById(R.id.rb_masculino);
        rb_outro = findViewById(R.id.rb_outro);
    }

    protected void listarIdososCuidados(){
        idosoCuidadoList = new ArrayList<>();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String nomeColecao = "Idosos cuidados "+userId;
        firebaseFirestore.collection(nomeColecao)
                .orderBy("data de criacao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                IdosoCuidado ic = document.toObject(IdosoCuidado.class);
                                idosoCuidadoList.add(ic);
                            }
                            et_nomeIdosoEdit.setText(idosoCuidadoList.get(posicao).getNome());
                            et_enderecoIdosoEdit.setText(idosoCuidadoList.get(posicao).getEndereco());
                            et_telefoneIdosoEdit.setText(idosoCuidadoList.get(posicao).getTelParaContato());
                            et_nascIdosoEdit.setText(idosoCuidadoList.get(posicao).getNascimento());
                            et_obsEdit.setText(idosoCuidadoList.get(posicao).getObs());

                            if(idosoCuidadoList.get(posicao).getGenero().equals("feminino")) rb_feminino.findFocus();
                            else if(idosoCuidadoList.get(posicao).getGenero().equals( "masculino")) rb_masculino.findFocus();
                            else if(idosoCuidadoList.get(posicao).getGenero().equals( "outro")) rb_outro.findFocus();

                            btn_editarIdoso.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            });
                        }
                    }
                });
    }
}