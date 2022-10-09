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

 public class telaMedicamentos extends Fragment implements MedicamentoAdapter.OnItemClick{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FloatingActionButton floatingActionButton;
    private String mParam1;
    private String mParam2;
    private RecyclerView rv_listaRemedio;
    private List<Medicamento> medicamentoList= new ArrayList<>();
    private MedicamentoAdapter medicamentoAdapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String id;
    private TextView tv_nenhumMedCad;

    public telaMedicamentos() {
    }

    public static telaMedicamentos newInstance(String param1, String param2) {
        telaMedicamentos fragment = new telaMedicamentos();
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
        rv_listaRemedio = v.findViewById(R.id.rv_listaRemedio);
        tv_nenhumMedCad = v.findViewById(R.id.tv_nenhumMedCad);
    }

    protected void listarMedicamentos(){
        medicamentoList.clear();
        rv_listaRemedio.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore.collection("Medicamento")
                .whereEqualTo("id do idoso", id)
                .orderBy("dia de criacao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Medicamento med = new Medicamento();
                                med.setNome(document.getString("nome"));
                                med.setDose(document.getString("dose"));
                                med.setUnidade_intervalo(document.getString("unidade intervalo"));
                                med.setIntervalo(document.getString("intervalo"));
                                med.setLembre(document.getBoolean("lembre-me"));

                                med.setId(document.getId());
                                medicamentoList.add(med);
                            }
                            if(medicamentoList.isEmpty()){
                                tv_nenhumMedCad.setVisibility(View.VISIBLE);
                            }else{
                                tv_nenhumMedCad.setVisibility(View.INVISIBLE);
                            }
                            medicamentoAdapter = new MedicamentoAdapter(getContext(),medicamentoList, telaMedicamentos.this::OnItemClick);
                            rv_listaRemedio.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                            rv_listaRemedio.setHasFixedSize(false);
                            rv_listaRemedio.setAdapter(medicamentoAdapter);
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        listarMedicamentos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tela_medicamento, container, false);
        inicializarComponentes(v);

        id = getActivity().getIntent().getStringExtra("id");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nome = getActivity().getIntent().getStringExtra("nome");
                Intent intent = new Intent();
                intent.setClass(getActivity(), telaCadastroMedicamento.class);
                intent.putExtra("id", id);
                intent.putExtra("nome", nome);
                startActivity(intent);
            }
        });
        return v;
    }

     @Override
     public void OnItemClick(int position) {
         Intent intent = new Intent();
         intent.setClass(getActivity(), telaCadastroMedicamento.class);
         intent.putExtra("id medicamento", medicamentoList.get(position).getId());
         startActivity(intent);
     }
}