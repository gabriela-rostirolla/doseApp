package com.example.doseapp;

import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class telaDiario extends Fragment implements DiarioDeCuidadoAdapter.OnItemClick {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FloatingActionButton floatingActionButton;
    private String mParam1;
    private TextView tv_nenhumDiarioCad;
    private String id;
    private RecyclerView rv_listaDiario;
    private DiarioDeCuidadoAdapter adapter;
    private String mParam2;
    private List<DiarioDeCuidado> diarioDeCuidadoList = new ArrayList<>();
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private DateFormat dataFormat = new SimpleDateFormat("dd/MM/yyyy");

    protected void inicializarComponentes(View view) {
        floatingActionButton = view.findViewById(R.id.fab_addDiario);
        rv_listaDiario = view.findViewById(R.id.rv_listaDiario);
        tv_nenhumDiarioCad = view.findViewById(R.id.tv_nenhumDiarioCad);
    }

    public telaDiario() {
    }

    public static telaDiario newInstance(String param1, String param2) {
        telaDiario fragment = new telaDiario();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tela_diario, container, false);
        Date data = new Date();
        inicializarComponentes(v);
        String dia = dataFormat.format(data);
        id = getActivity().getIntent().getStringExtra("id");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (diarioDeCuidadoList.isEmpty()||!diarioDeCuidadoList.get(diarioDeCuidadoList.size()-1).getData().equals(dia) ){
                    Map<String, Object> map = new HashMap<>();
                    map.put("dia", dia);
                    map.put("id do idoso", id);
                    firebaseFirestore.collection("Diarios").add(map);
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), telaDiarios.class);
                    intent.putExtra("dia", dia);
                    startActivity(intent);
                } else {
                    gerarSnackBar(v, "Um diário de cuidado já foi feito no dia " + dia);
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        diarioDeCuidadoList.clear();
        listarDiario();
    }

    protected void listarDiario() {
        rv_listaDiario.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore.collection("Diarios")
                .whereEqualTo("id do idoso", id)
                .orderBy("dia", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DiarioDeCuidado diario = new DiarioDeCuidado();
                                diario.setData(document.getString("dia"));
                                diario.setId(document.getId());
                                diarioDeCuidadoList.add(diario);
                            }
                            if (diarioDeCuidadoList.isEmpty()) {
                                tv_nenhumDiarioCad.setVisibility(View.VISIBLE);
                            } else {
                                tv_nenhumDiarioCad.setVisibility(View.INVISIBLE);
                            }
                            adapter = new DiarioDeCuidadoAdapter(getContext(), diarioDeCuidadoList, telaDiario.this::OnItemClick);
                            rv_listaDiario.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
                            rv_listaDiario.setHasFixedSize(false);
                            rv_listaDiario.setAdapter(adapter);
                        }
                    }
                });
    }

    @Override
    public void OnItemClick(int position) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), telaDiarios.class);
        intent.putExtra("dia", diarioDeCuidadoList.get(position).getData());
        intent.putExtra("diario id", diarioDeCuidadoList.get(position).getId());
        startActivity(intent);
    }

    protected void gerarSnackBar(View view, String texto) {
        Snackbar snackbar = Snackbar.make(view, texto, Snackbar.LENGTH_SHORT);
        snackbar.setBackgroundTint(Color.WHITE);
        snackbar.setTextColor(Color.BLACK);
        snackbar.show();
    }
}