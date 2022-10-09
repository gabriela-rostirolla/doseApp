package com.example.doseapp;

import android.content.Intent;
import android.hardware.lights.LightState;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class telaReceitas extends Fragment implements ReceitaAdapter.OnItemClick {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FloatingActionButton floatingActionButton;
    private RecyclerView rv_listaReceita;
    private ReceitaAdapter receitaAdapter;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String mParam1;
    private static List<Receita> receitaList= new ArrayList<>();
    private String mParam2;
    private static String id;
    private TextView tv_nenhumRecCad;

    public telaReceitas() {
    }

    public static telaReceitas newInstance(String param1, String param2) {
        telaReceitas fragment = new telaReceitas();
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

    protected void listarReceitas(){
        rv_listaReceita.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore.collection("Receitas")
                .whereEqualTo("id do idoso", id)
                //.orderBy("dia de criacao", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Receita rec = new Receita();
                                rec.setNome(document.getString("nome"));
                                rec.setDataRenovar(document.getString("data para renovar"));
                                rec.setId(document.getId());
                                receitaList.add(rec);
                            }
                            if(receitaList.isEmpty()){
                                tv_nenhumRecCad.setVisibility(View.VISIBLE);
                            }else{
                                tv_nenhumRecCad.setVisibility(View.INVISIBLE);
                            }
                            receitaAdapter = new ReceitaAdapter(getContext(),receitaList, telaReceitas.this::OnItemClick);
                            rv_listaReceita.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                            rv_listaReceita.setHasFixedSize(false);
                            rv_listaReceita.setAdapter(receitaAdapter);
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        receitaList.clear();
        listarReceitas();
    }

    protected void inicializarComponentes(View v){
        floatingActionButton = v.findViewById(R.id.fab_addReceita);
        rv_listaReceita = v.findViewById(R.id.rv_listaReceita);
        tv_nenhumRecCad = v.findViewById(R.id.tv_nenhumRecCad);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tela_receitas, container, false);
        id = getActivity().getIntent().getStringExtra("id");
        inicializarComponentes(v);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), telaCadastroReceita.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), telaCadastroReceita.class);
        intent.putExtra("id receita", receitaList.get(position).getId());
        startActivity(intent);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            imageView.setImageBitmap(imageBitmap);
//        }
//    }
}