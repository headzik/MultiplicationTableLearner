package com.example.lukas.multiplicationtablelearner.activities;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.widget.TextView;

import com.example.lukas.multiplicationtablelearner.tools.NumberGenerator;
import com.example.lukas.multiplicationtablelearner.R;
import com.example.lukas.multiplicationtablelearner.tools.TextToSpeechService;

import java.util.HashMap;

public abstract class BaseActivity extends AppCompatActivity {

    protected boolean gotResult;
    protected boolean askedToGoBack;
    protected GestureDetectorCompat gestureDetector;
    protected TextToSpeech textToSpeech;
    protected Integer counter;
    protected Integer multiplier;
    protected Integer multiplicand;
    protected int lastCount;
    protected int pointerCount;
    protected boolean canSkip;
    protected boolean wasEmpty = false;

    protected final int FLING_VELOCITY_LIMIT = 500;

    protected HashMap<String, String> map = new HashMap<>();

    protected TextView firstNumberTextView;
    protected TextView secondNumberTextView;

    protected GestureDetector.SimpleOnGestureListener gestureListener
            = new GestureDetector.SimpleOnGestureListener() {
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        setContentView(R.layout.activity_base);

        gestureDetector = new GestureDetectorCompat(this, gestureListener);
        textToSpeech = TextToSpeechService.getTextToSpeech();
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {
                canSkip = false;
            }

            @Override
            public void onDone(String utteranceId) {
                canSkip = true;
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
        NumberGenerator.resetDigits();
        firstNumberTextView = (TextView) findViewById(R.id.first_number);
        secondNumberTextView = (TextView) findViewById(R.id.second_number);
    }

    @Override
    protected void onStart() {
        super.onStart();
        resetCounter();
        resetMultiplier();
        resetMultiplicand();
        firstNumberTextView.setText("");
        secondNumberTextView.setText("");
        canSkip = true;
        lastCount = -1;
    }

    protected void resetCounter() {
        counter = 0;
    }

    protected void resetMultiplier() {
        multiplier = -1;
    }

    protected void resetMultiplicand() {
        multiplicand = -1;
    }

    protected void resetPointerCount() {
        pointerCount = 0;
    }

    protected void changePositionLeft() {
        counter = NumberGenerator.getPreviousDigit();
        NumberGenerator.removeDigit();
        String number = Integer.toString(NumberGenerator.getNumber() + counter);
        textToSpeech.speak(number, TextToSpeech.QUEUE_FLUSH, null);
        displayNumber(number);
    }

    protected void changePositionRight() {
        counter = lastCount;
        String number;
        if(NumberGenerator.addDigit(counter)) {
            resetCounter();
            number = NumberGenerator.getNumberAsString();
        }else {
            number = Integer.toString(NumberGenerator.getNumber() + counter);
        }
        textToSpeech.speak(number, TextToSpeech.QUEUE_FLUSH, null);
        displayNumber(number);
    }

    protected void setMultiplicand() {
        multiplicand = NumberGenerator.getNumber() + counter;
        textToSpeech.speak(getString(R.string.multiplicand_is) + Integer.toString(multiplicand) + " ." +
                getString(R.string.provide_second_number), TextToSpeech.QUEUE_FLUSH, map);
        resetCounter();
        NumberGenerator.resetDigits();
    }

    protected void displayNumber(String number) {
        TextView view;
        if(multiplicand < 0) {
            view = firstNumberTextView;
        } else {
            view = secondNumberTextView;
        }
        if(wasEmpty && askedToGoBack) {
            view.setText("");
            return;
        }
        wasEmpty = false;
        if(view.getText().length() == 0) wasEmpty = true;
        view.setText(number);
        float size = view.getHeight();
        view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        if(view.getPaint().measureText(number) > view.getWidth()) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, size/(float)1.5);
        }
    }

    protected void startOver() {
        gotResult = false;
        NumberGenerator.resetDigits();
        resetCounter();
        resetMultiplicand();
        resetMultiplier();
        firstNumberTextView.setText("");
        secondNumberTextView.setText("");
    }

    protected void setPointerCount(int pCount) {
        if (pointerCount < pCount) {
            pointerCount = pCount;
        }
    }

    protected void checkCounter() {
        if (counter > NumberGenerator.MAX_DIGIT_VALUE) {
            counter -= NumberGenerator.MAX_DIGIT_VALUE + 1;
        }
    }

    protected void askToGoBack() {
        counter = lastCount;
        askedToGoBack = true;
        readAndShowNumber();
        textToSpeech.speak(getString(R.string.wanna_go_back), TextToSpeech.QUEUE_FLUSH, map);
        textToSpeech.speak(getString(R.string.confirm), TextToSpeech.QUEUE_ADD, null);
    }

    protected void readAndShowNumber() {
        String number = Integer.toString((NumberGenerator.getNumber() + counter));
        textToSpeech.speak(number, TextToSpeech.QUEUE_FLUSH, null);
        displayNumber(number);
    }
}

