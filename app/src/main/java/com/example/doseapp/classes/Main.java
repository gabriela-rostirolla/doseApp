package com.example.doseapp.classes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.doseapp.R;
import com.example.doseapp.activitys.telaCadastro;
import com.example.doseapp.activitys.telaInicial;
import com.example.doseapp.activitys.telaLogin;
import com.example.doseapp.models.Consulta;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Main {

    //Cadastrar

    public static void cadastrarUsuario(Context context, String email, String nome, String senha, String userId, FirebaseFirestore firebaseFirestore) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    salvarDadosNoBancoDeDados(firebaseFirestore, email, nome, userId);
                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.camposVazios), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context.getApplicationContext(), telaInicial.class);
                    context.startActivity(intent);
                } else {
                    String erro = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException exception) {
                        erro = context.getString(R.string.senhaInv);
                    } catch (FirebaseAuthInvalidCredentialsException exception) {
                        erro = context.getString(R.string.emailInv);
                    } catch (FirebaseAuthUserCollisionException exception) {
                        erro = context.getString(R.string.contaExistente);
                    } catch (Exception exception) {
                        erro = context.getString(R.string.falhaAoCadastrar);
                    }
                    Toast.makeText(context.getApplicationContext(), erro, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void cadastrarConsulta(FirebaseFirestore firebaseFirestore, String nome, String data,
                                         String endereco, String tel, String profissional, String horario,
                                         String id_idoso) {
        Map<String, Object> consultaMap = new HashMap<>();
        consultaMap.put("nome", nome);
        consultaMap.put("data", data);
        consultaMap.put("endereco", endereco);
        consultaMap.put("telefone", tel);
        consultaMap.put("profissional", profissional);
        consultaMap.put("horario", horario);
        consultaMap.put("id do idoso", id_idoso);
        consultaMap.put("dia de criacao", new Date());
        firebaseFirestore.collection("Consultas")
                .add(consultaMap)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("banco_dados_salvos", "Sucesso ao salvar dados!" + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("erro_banco_dados", "Erro ao salvar dados!", e);
                    }
                });
    }

    public static void salvarDadosNoBancoDeDados(FirebaseFirestore firebaseFirestore, String email, String nome, String userId) {
        Map<String, Object> usuario = new HashMap<>();
        nome = nome.substring(0, 1).toUpperCase().concat(nome.substring(1));
        usuario.put("nome", nome);
        usuario.put("email", email);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DocumentReference documentReference = firebaseFirestore.collection("Usuarios").document(userId);
        documentReference.set(usuario).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("banco_dados", "Dados salvos com sucesso");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("erro_banco_dados", "Erro ao salvar dados" + exception.toString());
            }
        });
    }

    public static void redefinirSenha(Context context, String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("email_enviado", "Email Enviado");
                            Toast.makeText(context.getApplicationContext(), context.getString(R.string.emailEnviado), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static void autenticarUsuario(Context context, String email, String senha) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(context.getApplicationContext(), telaInicial.class);
                    context.startActivity(intent);
                    ((Activity) context).finish();
                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.loginFeitoComSucesso), Toast.LENGTH_SHORT).show();
                } else {
                    String erro;
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException exception) {
                        erro = context.getString(R.string.emailInex);
                    } catch (FirebaseAuthInvalidCredentialsException exception) {
                        erro = context.getString(R.string.senhaIncorreta);
                    } catch (Exception exception) {
                        erro = context.getString(R.string.falhaAoLogar);
                    }
                    Toast.makeText(context.getApplicationContext(), erro, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static void editarConsulta(Context context, FirebaseFirestore firebaseFirestore, String idConsulta, Consulta consulta){
        firebaseFirestore.collection("Consultas").document(idConsulta)
                .update("nome", consulta.getNome(),
                        "profissional", consulta.getProfissional(),
                        "endereco", consulta.getEndereco(),
                        "data", consulta.getData(),
                        "horario", consulta.getHorario(),
                        "telefone", consulta.getTelefone())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.dadosAtualizados), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
