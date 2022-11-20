package com.example.doseapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class NotificacaoMedicamentoWorkManager extends Worker {

    public NotificacaoMedicamentoWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String titulo = getInputData().getString("titulo");
        String descricao = getInputData().getString("descricao");
        String id_med = getInputData().getString("id med");
        int id = (int) getInputData().getLong("id_notificacao", 0);

        oreo(titulo, descricao);
        atualizarMedicamento(id_med);
        return Result.success();
    }

    public static void salvarNotificacao(long duracao, Data data, String tag) {
        OneTimeWorkRequest notificacao = new OneTimeWorkRequest.Builder(NotificacaoMedicamentoWorkManager.class)
                .setInitialDelay(duracao, TimeUnit.MILLISECONDS)
                .addTag(tag)
                .setInputData(data)
                .build();
        WorkManager instance = WorkManager.getInstance();
        instance.enqueue(notificacao);
    }

    private void oreo(String t, String d) {
        String id = "message";
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), id);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel nc = new NotificationChannel(id, "novo", NotificationManager.IMPORTANCE_HIGH);
            nc.setDescription("Descricao");
            nc.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(nc);
        }
        Intent intent = new Intent(getApplicationContext(), telaInicial.class);

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(t)
                .setTicker("nova notificacao")
                .setSmallIcon(R.mipmap.ic_launcher_logo)
                .setContentText(d)
                .setContentIntent(pendingIntent)
                .setContentInfo("novo");

        Random random = new Random();
        int idNotificacao = random.nextInt(10000);

        assert nm != null;
        nm.notify(idNotificacao, builder.build());
    }

    public void atualizarMedicamento(String id) {
        String hr = FirebaseFirestore.getInstance().collection("Medicamento").document(id).get().getResult().getString("horario proximo medicamento");
        String proxHr[] = hr.split(":");
        int intervalo = Integer.parseInt(FirebaseFirestore.getInstance().collection("Medicamento").document(id).get().getResult().getString("intervalo"));
        int novaHr = Integer.parseInt(proxHr[0]) + intervalo;
        if (novaHr > 24) {
            novaHr = novaHr - 24;
        }

        FirebaseFirestore.getInstance().collection("Medicamento").document(id).update("horario proximo medicamento", novaHr + ":" + proxHr[1])
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("banco_dados_salvos", "Sucesso ao atualizar dados!");
                    }
                });

        String dataIni[] = FirebaseFirestore.getInstance().collection("Medicamento").document(id).get().getResult().getString("data inicio").split("/");
        String dataFim[] = FirebaseFirestore.getInstance().collection("Medicamento").document(id).get().getResult().getString("data fim").split("/");

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.set(Integer.parseInt(dataIni[2]), Integer.parseInt(dataIni[1]), Integer.parseInt(dataIni[0]));
        c2.set(Integer.parseInt(dataFim[2]), Integer.parseInt(dataFim[1]), Integer.parseInt(dataFim[0]));
        Calendar c3 = Calendar.getInstance();

        Long alertTime = null;
        if(c1.before(c3) && c2.after(c3)){
            alertTime = c3.getTimeInMillis() - System.currentTimeMillis();
            int random = (int) (Math.random()*50+1);
            String tag = generateKey();
            String nome = FirebaseFirestore.getInstance().collection("Medicamento").document(id).get().getResult().getString("nome");
            Data date = guardarData(nome, id, "Está no horário do medicamento", random);
            NotificacaoMedicamentoWorkManager.salvarNotificacao(alertTime, date, tag);
        }
    }

    private String generateKey(){
        return UUID.randomUUID().toString();
    }

    private Data guardarData(String titulo,String id_med, String descricao, int id_not){
        return new Data.Builder()
                .putString("titulo", titulo)
                .putString("descricao", descricao)
                .putString("id med", id_med)
                .putInt("id_notificacao", id_not).build();
    }
}