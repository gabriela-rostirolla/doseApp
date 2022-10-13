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

public class telaConsultas extends Fragment implements ConsultaAdapter.OnItemClick {

    private List<Consulta> consultaList;
    private static RecyclerView rv_listaConsulta;
    private static ConsultaAdapter consultaAdapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static FloatingActionButton floatingActionButton;
    private String mParam1;
    private TextView tv_nenhumConsulCad;
    private String mParam2;
    private static String id;

    public telaConsultas() {
    }

    public static telaConsultas newInstance(String param1, String param2) {
        telaConsultas fragment = new telaConsultas();
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

    protected void listarConsultas() {
        rv_listaConsulta.setLayoutManager(new LinearLayoutManager(getActivity()));
        consultaList = new ArrayList<>();

        firebaseFirestore.collection("Consultas")
                .whereEqualTo("id do idoso", id)
                .orderBy("dia de criacao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Consulta consulta = new Consulta();
                                consulta.setNome(document.getString("nome"));
                                consulta.setHorario(document.getString("horario"));
                                consulta.setData(document.getString("data"));
                                boolean aux = document.getBoolean("lembre-me");
                                consulta.setLembre(aux);
                                consulta.setId(document.getId());
                                consultaList.add(consulta);
                            }
                            if (consultaList.isEmpty()) {
                                tv_nenhumConsulCad.setVisibility(View.VISIBLE);
                            } else {
                                tv_nenhumConsulCad.setVisibility(View.INVISIBLE);
                            }
                            consultaAdapter = new ConsultaAdapter(getContext(), consultaList, telaConsultas.this::OnItemClick);
                            rv_listaConsulta.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                            rv_listaConsulta.setHasFixedSize(false);
                            rv_listaConsulta.setAdapter(consultaAdapter);
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        listarConsultas();
    }

    protected void inicializarComponentes(View v) {
        floatingActionButton = v.findViewById(R.id.fab_addConsulta);
        rv_listaConsulta = v.findViewById(R.id.rv_listaConsulta);
        tv_nenhumConsulCad = v.findViewById(R.id.tv_nenhumConsulCad);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tela_consultas, container, false);
        id = getActivity().getIntent().getStringExtra("id");
        inicializarComponentes(v);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nomeIdoso = getActivity().getIntent().getStringExtra("nome");
                Intent intent = new Intent();
                intent.setClass(getActivity(), telaCadastroConsulta.class);
                intent.putExtra("id", id);
                intent.putExtra("nome idoso",nomeIdoso);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), telaCadastroConsulta.class);
        intent.putExtra("id consulta", consultaList.get(position).getId());
        startActivity(intent);
    }
}