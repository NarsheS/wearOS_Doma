package com.example.doma;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.Locale;

public class SMSReceiver extends BroadcastReceiver {

    private static TextToSpeech tts;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    if (tts == null) {
                        tts = new TextToSpeech(context.getApplicationContext(), status -> {
                            if (status == TextToSpeech.SUCCESS) {
                                tts.setLanguage(new Locale("pt", "BR"));
                            }
                        });
                    }

                    for (Object pdu : pdus) {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
                        String messageBody = sms.getMessageBody();
                        String sender = sms.getOriginatingAddress();

                        String spokenMessage = "Mensagem de " + sender + ": " + messageBody;
                        Log.d("DOMA_SMS", spokenMessage);

                        tts.speak(spokenMessage, TextToSpeech.QUEUE_ADD, null, "SMS_RECEIVED");
                    }
                }
            }
        }
    }
}
