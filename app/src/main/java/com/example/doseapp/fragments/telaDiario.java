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
import android.widget.TextView;
import android.widget.Toast;

import com.example.doseapp.R;
import com.example.doseapp.adapters.DiarioDeCuidadoAdapter;
import com.example.doseapp.models.DiarioDeCuidado;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class telaDiario extends Fragment implements DiarioDeCuidadoAdapter.OnItemClick {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FloatingActionButton floatingActionButton;
    private String mParam1;
    private TextView tv_nenhumDiarioCad;
    private String id;
    private static String dia;
    private static boolean validarDiario;
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
        dia = dataFormat.format(data);
        id = getActivity().getIntent().getStringExtra("id");

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarDiario) {
                    adicionarDiario(dia);
                    gerarToast(getString(R.string.diarioCad));
                } else {
                    gerarToast(getString(R.string.diarioJaCad)+" "+ dia);
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

    public static boolean verificarDiario(List<DiarioDeCuidado> list) {
        if (list.isEmpty()) {
            return true;
        } else if (Objects.equals(list.get(0).getData(), dia)) {
            return false;
        } else {
            return true;
        }
    }

    protected void adicionarDiario(String dia) {
        Map<String, Object> map = new HashMap<>();
        map.put("dia", dia);
        map.put("dia de criacao", new Date());
        map.put("id do idoso", id);

        firebaseFirestore.collection("Diarios").add(map);
        Intent intent = new Intent();
        intent.setClass(getActivity(), telaDiarios.class);
        intent.putExtra("dia", dia);
        startActivity(intent);
    }

    protected void listarDiario() {
        rv_listaDiario.setLayoutManager(new LinearLayoutManager(getActivity()));
        firebaseFirestore.collection("Diarios")
                .whereEqualTo("id do idoso", id)
                .orderBy("dia de criacao", Query.Direction.DESCENDING)
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
                            validarDiario = verificarDiario(diarioDeCuidadoList);
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

    protected void gerarToast(String texto) {
        Toast.makeText(getContext(), texto, Toast.LENGTH_SHORT).show();
    }
}