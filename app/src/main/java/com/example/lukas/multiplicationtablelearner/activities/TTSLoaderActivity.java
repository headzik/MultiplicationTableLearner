package com.example.lukas.multiplicationtablelearner.activities;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lukas.multiplicationtablelearner.R;
import com.example.lukas.multiplicationtablelearner.tools.TextToSpeechService;

public class TTSLoaderActivity extends AppCompatActivity {
    private Intent intent;

    private final int CHECK_CODE = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        intent = new Intent(this, MainActivity.class);
        startService(new Intent(this, TextToSpeechService.class));

        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == CHECK_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                new Thread(new Runnable() {
                    public void run() {
                        while(!TextToSpeechService.isTextToSpeechReady()) {
                            if(TextToSpeechService.didNotWork()) finish();
                        }
                        TextToSpeechService.getTextToSpeech().speak(getString(R.string.greetings),
                                TextToSpeech.QUEUE_FLUSH, null);
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        while(!TextToSpeechService.getTextToSpeech().isSpeaking()) {

                        }
                        startActivity(intent);
                        finish();
                    }
                }).start();
            }else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }
}
