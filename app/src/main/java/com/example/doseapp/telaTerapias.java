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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class telaTerapias extends Fragment  implements TerapiaAdapter.OnItemClick{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FloatingActionButton floatingActionButton;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private RecyclerView rv_listaTerapia;
    private String mParam1;
    private String mParam2;
    private String id;
    private List<Terapia> terapiaList;
    private TerapiaAdapter terapiaAdapter;
    private TextView tv_nenhumTerCad;

    public telaTerapias() {
    }

    public static telaTerapias newInstance(String param1, String param2) {
        telaTerapias fragment = new telaTerapias();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    protected void listarTerapias(){
        rv_listaTerapia.setLayoutManager(new LinearLayoutManager(getActivity()));
        terapiaList= new ArrayList<>();

        firebaseFirestore.collection("Terapias")
                .whereEqualTo("id do idoso", id)
                //.orderBy("dia de criacao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Terapia ter = new Terapia();
                                ter.setNome(document.getString("nome"));
                                //ter.setDiasSemana(document.getString("dias"));
                                ter.setHorario(document.getString("horario"));
                                ter.setId(document.getId());
                                terapiaList.add(ter);
                            }
                            if(terapiaList.isEmpty()){
                                tv_nenhumTerCad.setVisibility(View.VISIBLE);
                            }else{
                                tv_nenhumTerCad.setVisibility(View.INVISIBLE);
                            }
                            terapiaAdapter = new TerapiaAdapter(getContext(),terapiaList, telaTerapias.this::OnItemClick);
                            rv_listaTerapia.setAdapter(terapiaAdapter);
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        listarTerapias();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    protected void inicializarComponentes(View v){
        floatingActionButton = v.findViewById(R.id.fab_addTerapia);
        rv_listaTerapia = v.findViewById(R.id.rv_listaTerapia);
        tv_nenhumTerCad = v.findViewById(R.id.tv_nenhumTerCad);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tela_terapias, container, false);
        inicializarComponentes(v);
        id= getActivity().getIntent().getStringExtra("id");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), telaCadastroTerapia.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), telaEditarTerapia.class);
        intent.putExtra("id terapia", terapiaList.get(position).getId());
        startActivity(intent);
    }
}