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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

public class telaTurnoManha extends Fragment implements AlimentacaoAdapter.OnItemClick {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private static String diario_id;
    private RecyclerView rv_listaManha;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FloatingActionButton fabAdd;
    private Spinner spiAcao;
    private TextView tv_nenhumCadastro;
    private static List<Atividade> atividadeList = new ArrayList<>();
    private static List<Alimentacao> alimentacaoList = new ArrayList<>();
    private AlimentacaoAdapter alimentacaoAdapter;
    private AtividadeAdapter atividadeAdapter;

    public telaTurnoManha() {
    }

    public static telaTurnoManha newInstance(String param1, String param2) {
        telaTurnoManha fragment = new telaTurnoManha();
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

    protected void inicializarComponentes(View view) {
        fabAdd = view.findViewById(R.id.fab_addDiarioManha);
        rv_listaManha = view.findViewById(R.id.rv_listaTurnoManha);
        spiAcao = view.findViewById(R.id.spiAcao);
        tv_nenhumCadastro = view.findViewById(R.id.tv_nenhumDiarioManha);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tela_turno_manha, container, false);
        inicializarComponentes(v);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.acao, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spiAcao.setAdapter(adapter);

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = getActivity().getIntent().getStringExtra("dia");
                diario_id = getActivity().getIntent().getStringExtra("diario id");
                if (spiAcao.getSelectedItem().toString().equals("Alimentação")) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), telaCadastroDiarioAlimentacao.class);
                    intent.putExtra("Turno", "Manha");
                    intent.putExtra("dia", data);
                    intent.putExtra("diario id", diario_id);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), telaCadastroDiarioAtividade.class);
                    intent.putExtra("Turno", "Manha");
                    intent.putExtra("dia", data);
                    intent.putExtra("diario id", diario_id);
                    startActivity(intent);
                }
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (spiAcao.getSelectedItem().toString().equals("Alimentação")) {
            alimentacaoList.clear();
            atividadeList.clear();
            listarAlimentacao();
        }else{
            alimentacaoList.clear();
            atividadeList.clear();
            listarAtividades();
        }
    }

    protected void listarAlimentacao() {
        rv_listaManha.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore.collection("Alimentacao")
                .whereEqualTo("diario id", diario_id)
                .whereEqualTo("turno", "Manha")
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
//                            if (alimentacaoList.isEmpty()) {
//                                tv_nenhumCadastro.setVisibility(View.VISIBLE);
//                            } else {
//                                tv_nenhumCadastro.setVisibility(View.INVISIBLE);
//                            }
                            alimentacaoAdapter = new AlimentacaoAdapter(getContext(), alimentacaoList, telaTurnoManha.this::OnItemClick);
                            rv_listaManha.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                            rv_listaManha.setHasFixedSize(false);
                            rv_listaManha.setAdapter(alimentacaoAdapter);
                        }
                    }
                });
    }

    protected void listarAtividades() {
        rv_listaManha.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore.collection("Diario atividades")
                .whereEqualTo("diario id", diario_id)
                .whereEqualTo("turno", "Manha")
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
//                            if (alimentacaoList.isEmpty() &&) {
//                                tv_nenhumCadastro.setVisibility(View.VISIBLE);
//                            } else {
//                                tv_nenhumCadastro.setVisibility(View.INVISIBLE);
//                            }
                            atividadeAdapter = new AtividadeAdapter(getContext(), atividadeList, telaTurnoManha.this::OnItemClick);
                            rv_listaManha.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                            rv_listaManha.setHasFixedSize(false);
                            rv_listaManha.setAdapter(atividadeAdapter);
                        }
                    }
                });
    }

    @Override
    public void OnItemClick(int position) {

    }
}