package com.example.doseapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NotificacaoWorkManager extends Worker{

    public NotificacaoWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String titulo = getInputData().getString("titulo");
        String descricao = getInputData().getString("descricao");
        int id = (int) getInputData().getLong("id_notificacao", 0);
        oreo(titulo, descricao);
        return Result.success();
    }

    public static void salvarNotificacao(long duracao, Data data, String tag){
        OneTimeWorkRequest notificacao = new OneTimeWorkRequest.Builder(NotificacaoWorkManager.class)
                .setInitialDelay(duracao, TimeUnit.MILLISECONDS)
                .addTag(tag)
                .setInputData(data)
                .build();
        WorkManager instance = WorkManager.getInstance();
        instance.enqueue(notificacao);

    }

    private void oreo(String t, String d){
        String id = "message";
        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),id);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel nc = new NotificationChannel(id, "novo", NotificationManager.IMPORTANCE_HIGH);
            nc.setDescription("Descricao");
            nc.setShowBadge(true);
            assert nm !=null;
            nm.createNotificationChannel(nc);
        }

        Intent intent = new Intent(getApplicationContext(), telaCadastroMedicamento.class);

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
        nm.notify(idNotificacao,builder.build());
    }
}