package com.example.doseapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class telaConsultas extends Fragment {

    private static List<Consulta> consultaList;
    private static RecyclerView rv_listaConsulta;
    private static ConsultaAdapter consultaAdapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static FloatingActionButton floatingActionButton;
    private String mParam1;
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

    public void listarConsultas(){
        rv_listaConsulta.setLayoutManager(new LinearLayoutManager(getActivity()));
        consultaList= new ArrayList<>();

        firebaseFirestore.collection("Consultas")
                .whereEqualTo("id do idoso", id)
                //.orderBy("dia de criacao", Query.Direction.DESCENDING)
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
                                consulta.setId(document.getId());
                                System.out.println(consulta.getNome());
                                consultaList.add(consulta);
                            }
                            consultaAdapter = new ConsultaAdapter(getContext(), consultaList);
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

    protected void inicializarComponentes(View v){
        floatingActionButton = v.findViewById(R.id.fab_addConsulta);
        rv_listaConsulta = v.findViewById(R.id.rv_listaConsulta);

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
                Intent intent = new Intent();
                intent.setClass(getActivity(), telaCadastroConsulta.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        return v;
    }
}