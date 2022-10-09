package com.example.doseapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class atividades extends Fragment implements AtividadeAdapter.OnItemClick {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView tv_nenhumCadastro;
    private static String diario_id;
    private static String turno;
    private List<Atividade> atividadeList = new ArrayList<>();
    private AtividadeAdapter atividadeAdapter;
    private RecyclerView rv_listaAtividade;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FloatingActionButton fabAdd;
    private String mParam1;
    private String mParam2;

    public atividades() {
    }

    protected void inicializarComponentes(View view) {
        fabAdd = view.findViewById(R.id.fab_addAtividade);
        rv_listaAtividade = view.findViewById(R.id.rv_listaAtividades);
        tv_nenhumCadastro = view.findViewById(R.id.tv_nenhumaAtividade);
    }

    @Override
    public void onResume() {
        super.onResume();
        listarAtividades();
    }

    public static atividades newInstance(String param1, String param2) {
        atividades fragment = new atividades();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_atividades, container, false);
        inicializarComponentes(v);

        diario_id = getActivity().getIntent().getStringExtra("diario id");
        String data = getActivity().getIntent().getStringExtra("dia");
        turno = getActivity().getIntent().getStringExtra("turno");

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), telaCadastroDiarioAtividade.class);
                intent.putExtra("turno", turno);
                intent.putExtra("dia", data);
                intent.putExtra("diario id", diario_id);
                startActivity(intent);
            }
        });
        return v;
    }
    
    protected void listarAtividades() {
        atividadeList.clear();
        rv_listaAtividade.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore.collection("Diario atividades")
                .whereEqualTo("diario id", diario_id)
                .whereEqualTo("turno", turno)
                .orderBy("dia", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Atividade atividade = new Atividade();
                                atividade.setCuidadorResp(document.getString("cuidador"));
                                atividade.setDia(document.getString("dia"));
                                atividade.setDiarioId(document.getString("diario id"));
                                atividade.setHorario(document.getString("horario"));
                                atividade.setSaude(document.getString("saude"));
                                atividade.setObservacao(document.getString("observacao"));
                                atividade.setId(document.getId());
                                atividade.setTurno(document.getString("turno"));
                                atividade.setOutro(document.getString("outro"));
                                atividade.setSono(document.getString("sono"));
                                atividade.setExercicios(document.getString("exercicios"));
                                atividade.setPasseio(document.getString("passeio"));
                                atividadeList.add(atividade);
                            }
                            atividadeAdapter = new AtividadeAdapter(getContext(), atividadeList, atividades.this::OnItemClick);
                            rv_listaAtividade.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                            rv_listaAtividade.setHasFixedSize(false);
                            rv_listaAtividade.setAdapter(atividadeAdapter);
                        }
                    }
                });
    }

    @Override
    public void OnItemClick(int position) {

    }
}