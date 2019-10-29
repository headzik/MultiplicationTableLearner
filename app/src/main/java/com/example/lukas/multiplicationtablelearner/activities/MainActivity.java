package com.example.lukas.multiplicationtablelearner.activities;

import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MotionEventCompat;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.lukas.multiplicationtablelearner.R;
import com.example.lukas.multiplicationtablelearner.tools.TextToSpeechService;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public boolean onFling(MotionEvent start, MotionEvent end, float velocityX, float velocityY) {
                if (velocityX > FLING_VELOCITY_LIMIT) { //to the right
                    if(pointerCount == 2) {
                        textToSpeech.speak(getString(R.string.want_to_quit),
                                TextToSpeech.QUEUE_FLUSH, null);
                        textToSpeech.speak(getString(R.string.confirm),
                                TextToSpeech.QUEUE_ADD, null);
                        askedToGoBack = true;
                    }
                } else if (velocityX < -FLING_VELOCITY_LIMIT)  {
                    switch(pointerCount) {
                        case 0:
                            startActivity(new Intent(getBaseContext(), LearnModeActivity.class));
                            break;
                        case 2:
                            startActivity(new Intent(getBaseContext(), TestModeActivity.class));
                            break;
                        case 3:
                            startActivity(new Intent(getBaseContext(), TutorialActivity.class));
                            break;
                    }
                }
                return true;
            }
        };
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        int action = MotionEventCompat.getActionMasked(event);

        if(action == MotionEvent.ACTION_DOWN) {
            resetPointerCount();
            if(textToSpeech.isSpeaking()) return true;
            readInstruction();
        }

        if(action == MotionEventCompat.ACTION_POINTER_DOWN) {
            setPointerCount(event.getPointerCount());
        }

        if(action == MotionEvent.ACTION_UP) {
            if(askedToGoBack) {
                textToSpeech.stop();
                if(pointerCount > 1) {
                    askedToGoBack = false;
                    readInstruction();
                } else {
                    finish();
                }
                return true;
            }
        }
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, TextToSpeechService.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        textToSpeech.speak(getString(R.string.main_menu), TextToSpeech.QUEUE_ADD, null);
        readInstruction();
        resetPointerCount();
    }

    private void readInstruction() {
        textToSpeech.speak(getString(R.string.move_to_lm), TextToSpeech.QUEUE_ADD, null);
        textToSpeech.speak(getString(R.string.move_to_tm), TextToSpeech.QUEUE_ADD, null);
        textToSpeech.speak(getString(R.string.move_to_tt), TextToSpeech.QUEUE_ADD, null);
        textToSpeech.speak(getString(R.string.quit), TextToSpeech.QUEUE_ADD, null);
    }

}
