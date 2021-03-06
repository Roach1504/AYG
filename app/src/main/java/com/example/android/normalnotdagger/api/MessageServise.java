package com.example.android.normalnotdagger.api;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.example.android.normalnotdagger.MainActivity;
import com.example.android.normalnotdagger.R;
import com.example.android.normalnotdagger.models.new_model.messages.MessagesModel;
import com.example.android.normalnotdagger.ui.message.list_dialog.ImessageFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageServise extends Service {


    List<Integer> count = new ArrayList<>();
    private Timer mTimer;
    private MyTimer mMyTimer;
    SharedPreferences user;
    boolean message = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mTimer = new Timer();
        mMyTimer = new MyTimer();
        user = getSharedPreferences("user", Context.MODE_PRIVATE);
        mTimer.schedule(mMyTimer, 1000, 5 * 1000);
        Log.e("servise", "Служба запущена" + user.getString("id", "1"));
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMyTimer.cancel();
        Log.e("servise", "Служба остановлена");
    }

    void show(String name, String text) {

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }


        long[] vibrate = new long[]{0, 2000};
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_mail_outline_amber_500_24dp)
                        .setContentTitle(name)
                        .setContentText(text)
                        .setVibrate(vibrate)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(this, MainActivity.class);
        resultIntent.putExtra("push", "1");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
        message = true;

    }

    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("servise", "Служба создана");
    }

    class MyTimer extends TimerTask {
        @Override
        public void run() {
            if (!user.getString("id", "n").equals("n")) {
                Log.e("servise", "cread");
                App.getApi().getMessages(user.getString("id", "0")).enqueue(new Callback<MessagesModel>() {
                    @Override
                    public void onResponse(Call<MessagesModel> call, Response<MessagesModel> response) {
                        if (count.isEmpty()) {
                            Log.e("TEST", " First time");
                            for (int i = 0; i < response.body().getMessages().size(); i++) {
                                count.add(response.body().getMessages().get(i).getCount());
                            }
                        } else {
                            if (count.size() < response.body().getMessages().size()) {
                                Log.e("TEST", "New dialog");
                                //new dialog
                                show(response.body().getMessages().get(response.body().getMessages().size() - 1).getUserLogin()
                                        , response.body().getMessages().get(response.body().getMessages().size() - 1).getUserMessages().get(response.body().getMessages().get(response.body().getMessages().size() - 1).getUserMessages().size() - 1).getText());
                            } else {
                                for (int i = 0; i < response.body().getMessages().size(); i++) {
                                    if (count.get(i) < response.body().getMessages().get(i).getCount()) {
                                        // new message
                                        Log.e("TEST", "New message");
                                        if (!response.body().getMessages().get(i).getUserMessages().get(response.body().getMessages().get(i).getUserMessages().size() - 1).getFromId().toString().equals(user.getString("id", "e"))) {
                                            ServiseMenager.getInstance().setNewMessag(true);
                                            ServiseMenager.getInstance().setList(response.body().getMessages());
                                            ServiseMenager.getInstance().startReplas();
                                            ServiseMenager.getInstance().startReplasDialog();
                                            show(response.body().getMessages().get(i).getUserLogin()
                                                    , response.body().getMessages().get(i).getUserMessages().get(response.body().getMessages().get(i).getUserMessages().size() - 1).getText());
                                        }
                                        else{
                                            ServiseMenager.getInstance().setNewMessag(true);
                                            ServiseMenager.getInstance().setList(response.body().getMessages());
                                            ServiseMenager.getInstance().startReplas();
                                            ServiseMenager.getInstance().startReplasDialog();
                                            message = true;
                                            Log.e("TEST", "New my message");
                                        }
                                    } else {
                                        Log.e("TEST", "not new message");
                                    }
                                }

                            }
                        }
                        if(message){
                            count = new ArrayList<Integer>();
                            message = false;
                        }
                    }

                    @Override
                    public void onFailure(Call<MessagesModel> call, Throwable t) {
                        Log.e("servise", "not conect");
                    }
                });
            } else {
                Log.e("servise", "nou USER");
            }
        }
    }

}
