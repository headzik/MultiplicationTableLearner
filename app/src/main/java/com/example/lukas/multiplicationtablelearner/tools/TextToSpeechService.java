package com.example.lukas.multiplicationtablelearner.tools;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TextToSpeechService extends Service {
    private static TextToSpeech textToSpeech;
    private static boolean isTextToSpeechReady = false;
    private static boolean didNotWork = false;

    public static boolean isTextToSpeechReady() {
        return isTextToSpeechReady;
    }
    public static boolean didNotWork() {
        return didNotWork;
    }

    public static TextToSpeech getTextToSpeech() {
        return textToSpeech;
    }
    public TextToSpeechService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        initTTS();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        destroyTTS();
        super.onDestroy();
    }

    private void initTTS() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            public void onInit(final int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Locale currentLocale = Locale.getDefault();
                    if(currentLocale.equals(new Locale("pl", "PL"))) {
                        textToSpeech.setLanguage(currentLocale);
                    } else {
                        textToSpeech.setLanguage(Locale.UK);
                    }
                    textToSpeech.setSpeechRate(0.8f);
                    isTextToSpeechReady = true;
                } else if (status == TextToSpeech.ERROR) {
                    didNotWork = true;
                    destroyTTS();
                }
            }
        });
    }

    private void destroyTTS() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }
}
