package com.example.doma;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends Activity {

    private static final int REQUEST_CODE_VOICE = 100;
    private static final int REQUEST_CODE_SMS_PERMISSION = 200;
    private TextToSpeech tts;
    private Button btnComando;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnComando = findViewById(R.id.btnComando);

        // Inicializa TTS
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                tts.setLanguage(new Locale("pt", "BR"));
                speak("Bem-vindo ao Doma.");
            }
        });

        // Verifica permissões de SMS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.RECEIVE_SMS}, REQUEST_CODE_SMS_PERMISSION);
            }
        }

        btnComando.setOnClickListener(v -> startVoiceInput());


    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        try {
            startActivityForResult(intent, REQUEST_CODE_VOICE);
        } catch (Exception e) {
            speak("Reconhecimento de voz não disponível.");
        }
    }

    private void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "DOMA_TTS");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_VOICE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            handleCommand(result.get(0).toLowerCase());
        }
    }

    private void handleCommand(String command) {
        if (command.contains("perigo") || command.contains("socorro")) {
            speak("Alerta de segurança. Acione ajuda imediatamente.");
        } else if (command.contains("hora")) {
            speak("Agora são " + DateFormat.getTimeInstance().format(new Date()));
        } else {
            speak("Comando não reconhecido.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE_SMS_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão de SMS concedida", Toast.LENGTH_SHORT).show();
                speak("Permissão para SMS ativada.");
            } else {
                Toast.makeText(this, "Permissão de SMS negada", Toast.LENGTH_SHORT).show();
                speak("Permissão de SMS negada. O aplicativo pode não funcionar corretamente.");
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
