package com.example.doseapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.doseapp.R;
import com.example.doseapp.activitys.telaCadastroDiarioAlimentacao;
import com.example.doseapp.activitys.telaCadastroDiarioAtividade;
import com.example.doseapp.adapters.AlimentacaoAdapter;
import com.example.doseapp.adapters.AtividadeAdapter;
import com.example.doseapp.models.Alimentacao;
import com.example.doseapp.models.Atividade;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class telaTurnoTarde extends Fragment implements AtividadeAdapter.OnItemClick {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private TextView tv_nenhumCadastro;
    private static String diario_id;
    private Spinner spi_acao;
    private List<Atividade> atividadeList = new ArrayList<>();
    private List<Alimentacao> alimentacaoList = new ArrayList<>();
    private AtividadeAdapter atividadeAdapter;
    private AlimentacaoAdapter alimentacaoAdapter;
    private RecyclerView rv_listaDiarios;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FloatingActionButton fabAdd;

    public telaTurnoTarde() {
    }

    public static telaTurnoTarde newInstance(String param1, String param2) {
        telaTurnoTarde fragment = new telaTurnoTarde();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        View v = inflater.inflate(R.layout.fragment_tela_turno_tarde, container, false);
        inicializarComponentes(v);
        diario_id = getActivity().getIntent().getStringExtra("diario id");
        String data = getActivity().getIntent().getStringExtra("dia");

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                if (spi_acao.getSelectedItem().equals("Alimentação"))
                    intent.setClass(getActivity(), telaCadastroDiarioAlimentacao.class);
                else if (spi_acao.getSelectedItem().equals("Atividade"))
                    intent.setClass(getActivity(), telaCadastroDiarioAtividade.class);
                intent.putExtra("turno", "Tarde");
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
        spi_acao.setSelection(0);
        spi_acao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        atividadeList.clear();
                        listarAtividades();
                        return;
                    case 1:
                        listarAlimentacao();
                        return;
                    default:
                        return;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    protected void listarAtividades() {
        atividadeList.clear();

        rv_listaDiarios.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore.collection("Diario atividades")
                .whereEqualTo("diario id", diario_id)
                .whereEqualTo("turno", "Tarde")
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
                            if (atividadeList.isEmpty()) {
                                tv_nenhumCadastro.setText("Nenhuma atividade cadastrada");
                                tv_nenhumCadastro.setVisibility(View.VISIBLE);
                            } else {
                                tv_nenhumCadastro.setVisibility(View.INVISIBLE);
                            }
                            atividadeAdapter = new AtividadeAdapter(getContext(), atividadeList, telaTurnoTarde.this::OnItemClick);
                            rv_listaDiarios.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                            rv_listaDiarios.setHasFixedSize(false);
                            rv_listaDiarios.setAdapter(atividadeAdapter);
                        }
                    }
                });
    }

    public void listarAlimentacao() {
        rv_listaDiarios.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore.collection("Alimentacao")
                .whereEqualTo("diario id", diario_id)
                .whereEqualTo("turno", "Tarde")
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
                            if (alimentacaoList.isEmpty()) {
                                tv_nenhumCadastro.setText("Nenhuma alimentação cadastrada");
                                tv_nenhumCadastro.setVisibility(View.VISIBLE);
                            } else {
                                tv_nenhumCadastro.setVisibility(View.INVISIBLE);
                            }
                            alimentacaoAdapter = new AlimentacaoAdapter(getContext(), alimentacaoList, telaTurnoTarde.this::OnItemClick);
                            rv_listaDiarios.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                            rv_listaDiarios.setHasFixedSize(false);
                            rv_listaDiarios.setAdapter(alimentacaoAdapter);
                        }
                    }
                });
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent();
        if (spi_acao.getSelectedItem().equals("Alimentação")) {
            intent.setClass(getActivity(), telaCadastroDiarioAlimentacao.class);
            intent.putExtra("id", alimentacaoList.get(position).getId());
            intent.putExtra("turno", "Tarde");
        } else if (spi_acao.getSelectedItem().equals("Atividade")) {
            intent.setClass(getActivity(), telaCadastroDiarioAtividade.class);
            intent.putExtra("id", atividadeList.get(position).getId());
            intent.putExtra("turno", "Tarde");
        }
        startActivity(intent);
    }

    protected void inicializarComponentes(View view) {
        fabAdd = view.findViewById(R.id.fab_add);
        rv_listaDiarios = view.findViewById(R.id.rv_lista);
        spi_acao = view.findViewById(R.id.spi_acao);
        tv_nenhumCadastro = view.findViewById(R.id.tv_nenhumaAcaoCad);
    }
}