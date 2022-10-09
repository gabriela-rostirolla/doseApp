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

public class alimentacoes extends Fragment implements AlimentacaoAdapter.OnItemClick {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private TextView tv_nenhumCadastro;
    private static String diario_id;
    private static String turno;
    private List<Alimentacao> alimentacaoList = new ArrayList<>();
    private AlimentacaoAdapter alimentacaoAdapter;
    private RecyclerView rv_listaAlimentacao;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FloatingActionButton fabAdd;
    private String mParam1;
    private String mParam2;


    public alimentacoes() {
    }

    public static alimentacoes newInstance(String param1, String param2) {
        alimentacoes fragment = new alimentacoes();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    protected void inicializarComponentes(View view) {
        fabAdd = view.findViewById(R.id.fab_addAlimentacao);
        rv_listaAlimentacao = view.findViewById(R.id.rv_listaAlimentacoes);
        tv_nenhumCadastro = view.findViewById(R.id.tv_nenhumaAlimentacao);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alimentacoes, container, false);
        inicializarComponentes(v);

        diario_id = getActivity().getIntent().getStringExtra("diario id");
        String data = getActivity().getIntent().getStringExtra("dia");
        turno = getActivity().getIntent().getStringExtra("turno");

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), telaCadastroDiarioAlimentacao.class);
                intent.putExtra("Turno", turno);
                intent.putExtra("dia", data);
                intent.putExtra("diario id", diario_id);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        listarAlimentacao();
    }

    public void listarAlimentacao() {
        alimentacaoList.clear();
        rv_listaAlimentacao.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore.collection("Alimentacao")
                .whereEqualTo("diario id", diario_id)
                .whereEqualTo("turno", turno)
                .orderBy("dia", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Alimentacao alimentacao = new Alimentacao();
                                alimentacao.setCuidadorResponsavel(document.getString("cuidador"));
                                alimentacao.setDia(document.getString("dia"));
                                alimentacao.setDiarioId(document.getString("diario id"));
                                alimentacao.setHorario(document.getString("horario"));
                                alimentacao.setLanche(document.getString("lanche"));
                                alimentacao.setObservacao(document.getString("observacao"));
                                alimentacao.setId(document.getId());
                                alimentacao.setTurno(document.getString("turno"));
                                alimentacao.setOutro(document.getString("outro"));
                                alimentacao.setRefeicaoPrincipal(document.getString("refeicao"));
                                alimentacaoList.add(alimentacao);
                            }

                            alimentacaoAdapter = new AlimentacaoAdapter(getContext(), alimentacaoList, alimentacoes.this::OnItemClick);
                            rv_listaAlimentacao.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                            rv_listaAlimentacao.setHasFixedSize(false);
                            rv_listaAlimentacao.setAdapter(alimentacaoAdapter);
                        }
                    }
                });
    }

    @Override
    public void OnItemClick(int position) {

    }
}