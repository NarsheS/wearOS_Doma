package com.example.doma;

import android.content.Intent;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class WatchNotificationListener extends NotificationListenerService {

    private static final String TAG = "WatchNotificationTTS";
    private TextToSpeech tts;

    @Override
    public void onCreate() {
        super.onCreate();

        tts = new TextToSpeech(getApplicationContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                int langResult = tts.setLanguage(new Locale("pt", "BR"));
                Log.d(TAG, "TTS iniciado. Resultado idioma: " + langResult);
            } else {
                Log.e(TAG, "Erro ao iniciar TTS");
            }
        });
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String app = sbn.getPackageName();
        String title = sbn.getNotification().extras.getString("android.title", "");
        String text = sbn.getNotification().extras.getString("android.text", "");

        Log.d(TAG, "Notificação recebida de: " + app);

        String toSpeak = title + ". " + text;
        if (tts != null) {
            tts.speak(toSpeak, TextToSpeech.QUEUE_ADD, null, null);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }
}
