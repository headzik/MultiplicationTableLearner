package com.example.lukas.multiplicationtablelearner.activities;

import android.speech.tts.TextToSpeech;
import android.support.v4.view.MotionEventCompat;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.lukas.multiplicationtablelearner.tools.NumberGenerator;
import com.example.lukas.multiplicationtablelearner.R;

public class LearnModeActivity extends BaseActivity {

    private boolean gotEquation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gotResult = false;
        gestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                if(askedToGoBack) return;
                textToSpeech.stop();
                counter = lastCount;
                if(multiplicand < 0) { //first number (multiplicand) not provided
                    setMultiplicand();
                } else {
                    textToSpeech.speak(getString(R.string.provide_second_number) +
                            getString(R.string.get_result), TextToSpeech.QUEUE_FLUSH, map);
                }
            }

            @Override
            public boolean onFling(MotionEvent start, MotionEvent end, float velocityX, float velocityY) {
                if(pointerCount == 2) { //if double touch swipe
                    if(velocityX > FLING_VELOCITY_LIMIT) { //to the right
                        askToGoBack();
                    } else if(velocityX < -FLING_VELOCITY_LIMIT) { // to the left
                        textToSpeech.stop();
                        counter = lastCount;
                        if(!gotResult) {
                            displayNumber(Integer.toString(NumberGenerator.getNumber() + counter));
                        }
                        if(multiplicand < 0 || gotEquation) {
                            textToSpeech.stop();
                            if(!gotEquation)
                            setMultiplicand();
                            getEquations();
                        } else {
                            getResult();
                        }
                    }
                } else if (pointerCount == 0){ //single touch fling
                    if(velocityX > FLING_VELOCITY_LIMIT) { //to the right
                        if(!gotResult) {
                            textToSpeech.stop();
                            changePositionLeft();
                        }
                    } else if (velocityX < -FLING_VELOCITY_LIMIT) { //to the left
                        if(!gotResult) {
                            textToSpeech.stop();
                            changePositionRight();
                        }
                    } else if(velocityY > FLING_VELOCITY_LIMIT) { // down
                        textToSpeech.stop();
                        if(gotResult) {
                            startOver();
                        } else {
                            counter = lastCount;
                            displayNumber(Integer.toString(NumberGenerator.getNumber() + counter));
                            readEquation();
                        }
                    }
                }
                return true;
            }
        };
        super.onCreate(savedInstanceState);

        textToSpeech.speak(getString(R.string.learn_mode), TextToSpeech.QUEUE_FLUSH, map);
    }

    @Override
    protected void onStart() {
        super.onStart();
        textToSpeech.speak(getString(R.string.provide_eq_or_num), TextToSpeech.QUEUE_ADD, map);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!canSkip) return true;

        int action = MotionEventCompat.getActionMasked(event);

        if(action == MotionEvent.ACTION_DOWN) {
            resetPointerCount();
            lastCount = counter;
        }

        if(action == MotionEventCompat.ACTION_POINTER_DOWN) {
            setPointerCount(event.getPointerCount());
        }

        if(action == MotionEvent.ACTION_UP) {
            if (askedToGoBack) {
                textToSpeech.stop();
                if (pointerCount > 1) {
                    counter = lastCount;
                    askedToGoBack = false;
                    if(gotEquation) {
                        getEquations();
                    } else if(gotResult) {
                        getResult();
                    } else {
                        readAndShowNumber();
                    }
                } else {
                    finish();
                }
                return true;
            } else if(!gotResult || !gotEquation) {
                if (pointerCount == 0) {
                    counter++;
                } else {
                    counter += pointerCount;
                }
                checkCounter();
            }
            if(gotResult) return gestureDetector.onTouchEvent(event);
            readAndShowNumber();
        }

        return gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void startOver() {
        super.startOver();
        gotEquation = false;
        textToSpeech.speak(getString(R.string.provide_eq_or_num), TextToSpeech.QUEUE_ADD, map);
    }

    private void getResult() {
        multiplier = NumberGenerator.getNumber() + counter;
        Integer result = multiplicand*multiplier;
        textToSpeech.speak(Integer.toString(multiplicand) + getString(R.string.times) + Integer.toString(multiplier) +
                getString(R.string.equals) + result.toString(), TextToSpeech.QUEUE_FLUSH, map);
        textToSpeech.speak(getString(R.string.ending_instruction), TextToSpeech.QUEUE_ADD, null);
        gotResult = true;
    }

    private void getEquations() {
        textToSpeech.speak(multiplicand.toString() + getString(R.string.is_result), TextToSpeech.QUEUE_FLUSH, map);
        for(int i = 1; i <= multiplicand; i++) {
            int rest = multiplicand%i;
            if(rest == 0) {
                textToSpeech.speak(Integer.toString(multiplicand/i) + getString(R.string.times) +
                        Integer.toString(i), TextToSpeech.QUEUE_ADD, map);
            }
        }
        textToSpeech.speak(getString(R.string.ending_instruction), TextToSpeech.QUEUE_ADD, null);
        gotResult = true;
        gotEquation = true;
    }

    private void readEquation() {
        Integer number;
        if(multiplicand < 0 || gotEquation) {
            number = NumberGenerator.getNumber() + counter;
        } else {
            number = multiplicand;
        }
        textToSpeech.speak(getString(R.string.multiplicand_is) + number.toString(), TextToSpeech.QUEUE_ADD, null);

    }
}
