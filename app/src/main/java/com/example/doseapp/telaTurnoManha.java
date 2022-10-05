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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class telaTurnoManha extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private RecyclerView rv_listaManha;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FloatingActionButton fabAdd;
    private Spinner spiAcao;
    private TextView tv_nenhumCadastro;
    private List<Alimentacao> alimentacaoList;

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
                if (spiAcao.getSelectedItem().toString().equals("Alimentação")) {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), telaCadastroDiarioAlimentacao.class);
//                  intent.putExtra("id", id);
                    startActivity(intent);

                }else{
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), telaCadastroDiarioAtividade.class);
//                  intent.putExtra("id", id);
                    startActivity(intent);
                }
            }
        });

        return v;
    }

    protected void listarAlimentacao() {
        rv_listaManha.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore.collection("Alimentacao")
                .whereEqualTo("turno", "manha")
                //.orderBy("dia de criacao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Alimentacao alimentacao = new Alimentacao();
//                                alimentacao.set(document.getString("nome"));
//                                rec.setDataRenovar(document.getString("data para renovar"));
//                                rec.setId(document.getId());
                                alimentacaoList.add(alimentacao);
                            }
                            if (alimentacaoList.isEmpty()) {
                                tv_nenhumCadastro.setVisibility(View.VISIBLE);
                            } else {
                                tv_nenhumCadastro.setVisibility(View.INVISIBLE);
                            }
//                            receitaAdapter = new ReceitaAdapter(getContext(), receitaList, telaReceitas.this::OnItemClick);
//                            rv_listaReceita.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//                            rv_listaReceita.setHasFixedSize(false);
//                            rv_listaReceita.setAdapter(receitaAdapter);
                        }
                    }
                });
    }
//
//    protected void listarAtividades() {
//        rv_listaReceita.setLayoutManager(new LinearLayoutManager(getActivity()));
//        firebaseFirestore.collection("Receitas")
//                .whereEqualTo("id do idoso", id)
//                //.orderBy("dia de criacao", Query.Direction.DESCENDING)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Receita rec = new Receita();
//                                rec.setNome(document.getString("nome"));
//                                rec.setDataRenovar(document.getString("data para renovar"));
//                                rec.setId(document.getId());
//                                receitaList.add(rec);
//                            }
//                            if (receitaList.isEmpty()) {
//                                tv_nenhumRecCad.setVisibility(View.VISIBLE);
//                            } else {
//                                tv_nenhumRecCad.setVisibility(View.INVISIBLE);
//                            }
//                            receitaAdapter = new ReceitaAdapter(getContext(), receitaList, telaReceitas.this::OnItemClick);
//                            rv_listaReceita.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
//                            rv_listaReceita.setHasFixedSize(false);
//                            rv_listaReceita.setAdapter(receitaAdapter);
//                        }
//                    }
//                });
//    }
}