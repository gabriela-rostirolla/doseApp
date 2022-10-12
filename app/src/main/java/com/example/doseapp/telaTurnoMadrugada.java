package com.example.doseapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class telaTurnoMadrugada extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private RecyclerView rv_listaMadrugada;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FloatingActionButton fabAdd;
    private Spinner spiAcao;
    private TextView tv_nenhumCadastro;
    private List<Alimentacao> alimentacaoList;

    public telaTurnoMadrugada() {
    }

    public static telaTurnoMadrugada newInstance(String param1, String param2) {
        telaTurnoMadrugada fragment = new telaTurnoMadrugada();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tela_turno_madrugada, container, false);
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

                } else {
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), telaCadastroDiarioAtividade.class);
//                  intent.putExtra("id", id);
                    startActivity(intent);
                }
            }
        });

        return v;
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

    protected void inicializarComponentes(View view) {
        fabAdd = view.findViewById(R.id.fab_addDiarioMadrugada);
        rv_listaMadrugada = view.findViewById(R.id.rv_listaTurnoMadrugada);
        spiAcao = view.findViewById(R.id.spiAcao);
        tv_nenhumCadastro = view.findViewById(R.id.tv_nenhumDiarioMadrugada);
    }
}