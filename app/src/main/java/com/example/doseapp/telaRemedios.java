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
import android.widget.ImageButton;
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

public class telaRemedios extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FloatingActionButton floatingActionButton;
    private TextView tv_dose, tv_posologia, tv_nomeMedicamento;
    private ImageButton imgBtn_compartilhar, imgBtn_excluir;
    private String mParam1;
    private String mParam2;
    private RecyclerView rv_listaRemedio;
    private List<Medicamento> medicamentoList;
    private IdosoCuidadoAdapter idosoCuidadoAdapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public telaRemedios() {
    }

    public static telaRemedios newInstance(String param1, String param2) {
        telaRemedios fragment = new telaRemedios();
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

    protected void inicializarComponentes(View v){
        floatingActionButton = v.findViewById(R.id.fab_addMedicamento);
        tv_dose = v.findViewById(R.id.tv_dose);
        tv_posologia = v.findViewById(R.id.tv_posologia);
        tv_nomeMedicamento = v.findViewById(R.id.tv_nomeMedicamento);
        rv_listaRemedio = v.findViewById(R.id.rv_listaRemedio);
    }

//    @Override
//    public void onItemClick(int position) {
//        //Intent intent = new Intent(getActivity(), tela.class);
//        //intent.putExtra("id", .get(position).getId());
//        //startActivity(intent);
//    }
    public void ListarRemedios(){
        rv_listaRemedio.setLayoutManager(new LinearLayoutManager(getActivity()));
        medicamentoList= new ArrayList<>();
        firebaseFirestore.collection("Medicamentos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Medicamento med = new Medicamento();
                                med.setNome(document.getString("nome"));
                                medicamentoList.add(med);
                            }
                            //idosoCuidadoAdapter = new IdosoCuidadoAdapter(medicamentoList , getActivity(), 1);
                            //rv_listaRemedio.setAdapter(idosoCuidadoAdapter);
                        }
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_tela_medicamento, container, false);
        inicializarComponentes(v);

        //ListarRemedios();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), telaCadastroMedicamento.class);
                startActivity(intent);
            }
        });

        return v;
    }
}