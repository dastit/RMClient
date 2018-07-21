/*package com.example.diti.redminemobileclient;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import android.os.Process;

import com.example.diti.redminemobileclient.fragments.TaskListFragment;


public class TaskExecutingTimerService extends Service {
    private static final String TAG                             = "TaskExecutingTimerServ";
    public static final  String ACTION_START_FOREGROUND_SERVICE = "ACTION_START_FOREGROUND_SERVICE";
    public static final  String ACTION_STOP_FOREGROUND_SERVICE  = "ACTION_STOP_FOREGROUND_SERVICE";
    public static final  String ACTION_STOP                     = "ACTION_STOP";


    private Looper                     mServiceLooper;
    private ServiceHandler             mServiceHandler;
    private NotificationCompat.Builder notificationBuilder;
    private Notification               notification;
    private Integer                        mIssueId;
    private String                     mIssueSubj;

    public TaskExecutingTimerService() {
    }

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            long endTime = System.currentTimeMillis() + 5 * 10000;
            while (System.currentTimeMillis() < endTime) {
                synchronized (this) {
                    try {
                        Log.d(TAG, "Выполняется задача сервиса");
                        wait(endTime - System.currentTimeMillis());
                    } catch (Exception e) {
                    }
                }
            }

            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                                                 Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Log.d(TAG, "Сервис создан");
        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            Message msg = mServiceHandler.obtainMessage();
            msg.arg1 = startId;

            mIssueId = intent.getIntExtra(TaskListFragment.EXTRA_ISSUE_NUM, 0);
            mIssueSubj = intent.getStringExtra(TaskListFragment.EXTRA_ISSUE_SUBJ);

            switch(action){
                case ACTION_START_FOREGROUND_SERVICE:
                    startForeground();
                    mServiceHandler.sendMessage(msg);
                    Log.d(TAG, "Сервис запущен");
                    break;
                case ACTION_STOP_FOREGROUND_SERVICE:
                    stopForeground(msg);
                    Log.d(TAG, "Сервис остановлен");
                    break;
                case ACTION_STOP:
                    stopForeground(msg);
                    Toast.makeText(getApplicationContext(), "You click Stop button.", Toast.LENGTH_LONG).show();
                    break;

            }
        }
        // If we get killed, after returning from here, restart
        return START_REDELIVER_INTENT;
    }

    private void startForeground() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID).setSmallIcon(R.drawable.outline_timer_24)
                    .setContentTitle("Выполняется задача №"+mIssueId)
                    .setContentText(mIssueSubj);
        } else {
            notificationBuilder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.outline_timer_24)
                    .setContentTitle("Выполняется задача №"+mIssueId)
                    .setContentText(mIssueSubj);
        }

        Intent playIntent = new Intent(this, TaskExecutingTimerService.class);
        playIntent.setAction(ACTION_STOP);
        PendingIntent pendingPlayIntent = PendingIntent.getService(this, 0, playIntent, 0);
        NotificationCompat.Action playAction = new NotificationCompat.Action(R.drawable.outline_stop_24, "Stop", pendingPlayIntent);
        notificationBuilder.addAction(playAction);

        notification = notificationBuilder.build();
        startForeground(1, notification);
    }

    private void stopForeground(Message msg){
        stopForeground(true);
        stopSelf(msg.arg1);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service done", Toast.LENGTH_SHORT).show();
    }
}
*/