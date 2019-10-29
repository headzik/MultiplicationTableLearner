package com.example.lukas.multiplicationtablelearner.activities;

import android.speech.tts.TextToSpeech;
import android.support.v4.view.MotionEventCompat;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import com.example.lukas.multiplicationtablelearner.tools.NumberGenerator;
import com.example.lukas.multiplicationtablelearner.R;

public class TestModeActivity extends BaseActivity {

    private int numberForEquation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                if(askedToGoBack || numberForEquation < 0) return;
                counter = lastCount;
                if(multiplicand < 0) { //first number (multiplicand) not provided
                    setMultiplicand();
                } else {
                    textToSpeech.speak(getString(R.string.provide_second_number), TextToSpeech.QUEUE_FLUSH, map);
                }
            }

            @Override
            public boolean onFling(MotionEvent start, MotionEvent end, float velocityX, float velocityY) {
                if(pointerCount == 2) { //if double touch fling
                    if(velocityX > FLING_VELOCITY_LIMIT) { //to the right
                        askToGoBack();
                    } else if(velocityX < -FLING_VELOCITY_LIMIT) { // to the left
                        textToSpeech.stop();
                        counter = lastCount;
                        if((numberForEquation > 0 && multiplicand >= 0) ||
                                numberForEquation < 0) {
                            getResult();
                        }
                        if(!gotResult) displayNumber(Integer.toString(NumberGenerator.getNumber() + counter));
                    }
                } else if (pointerCount == 0){ //single touch fling
                    if(velocityX > FLING_VELOCITY_LIMIT) { //to the right
                        if(!gotResult) {
                            changePositionLeft();
                        }
                    } else if (velocityX < -FLING_VELOCITY_LIMIT) { //to the left
                        if(!gotResult) {
                            changePositionRight();
                        }
                    } else if(velocityY > FLING_VELOCITY_LIMIT) { // down
                        textToSpeech.stop();
                        if(gotResult) {
                            startOver();
                        } else {
                            counter = lastCount;
                            displayNumber(Integer.toString(NumberGenerator.getNumber() + counter));
                            if(numberForEquation < 0) {
                                readEquation();
                            } else {
                                readNumber();
                            }
                        }
                    }
                }
                return true;
            }
        };

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        textToSpeech.speak(getString(R.string.test_mode), TextToSpeech.QUEUE_FLUSH, null);
        startOver();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(!canSkip) return true;

        int action = MotionEventCompat.getActionMasked(event);

        if(action == MotionEvent.ACTION_DOWN) {
            resetPointerCount();
            lastCount = counter;
        }

        if(action == MotionEventCompat.ACTION_POINTER_DOWN) {
            setPointerCount(event.getPointerCount());
        }

        if(action == MotionEvent.ACTION_UP ) {
            if(askedToGoBack) {
                textToSpeech.stop();
                if(pointerCount > 1) {
                    counter = lastCount;
                    askedToGoBack = false;
                    if(numberForEquation < 0) {
                        readEquation();
                    } else {
                        readNumber();
                    }
                } else {
                    finish();
                }
                return true;
            } else if(!gotResult) {
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
        resetNumberForEquation();
        chooseNextQuestion();
    }

    private void getResult() {
        Integer result;
        if(numberForEquation < 0) {
            result = multiplicand*multiplier;
            Integer answer = NumberGenerator.getNumber()+counter;
            displayNumber(Integer.toString(answer));
            if(answer == result) {
                textToSpeech.speak(getString(R.string.congrats) + answer.toString() +
                        getString(R.string.is_correct), TextToSpeech.QUEUE_FLUSH, null);
            } else {
                textToSpeech.speak(getString(R.string.unfortunately) +
                        getString(R.string.real_answer) + result.toString(), TextToSpeech.QUEUE_FLUSH, null);
            }
        } else {
            multiplier = NumberGenerator.getNumber() + counter;
            result = multiplicand*multiplier;
            displayNumber(multiplier.toString());
            if(result == numberForEquation) {
                textToSpeech.speak(getString(R.string.congrats), TextToSpeech.QUEUE_FLUSH, null);
            } else {
                textToSpeech.speak(getString(R.string.unfortunately), TextToSpeech.QUEUE_FLUSH, null);
            }
            textToSpeech.speak(multiplicand.toString() + getString(R.string.times)
                    + multiplier.toString() + getString(R.string.equals) + result.toString(), TextToSpeech.QUEUE_ADD, null);
        }
        gotResult = true;
        textToSpeech.speak(getString(R.string.ending_instruction), TextToSpeech.QUEUE_ADD, null);
    }

    private void getNumber() {
        numberForEquation = (int) (Math.random()* 10);
        readNumber();
    }

    private void readNumber() {
        textToSpeech.speak(Integer.toString(numberForEquation) + getString(R.string.is_result), TextToSpeech.QUEUE_ADD, map);
    }

    private void getEquation() {
        multiplicand = (int) (Math.random()* 10);
        multiplier = (int) (Math.random()* 10);
        readEquation();
    }

    private void readEquation() {
        textToSpeech.speak(Integer.toString(multiplicand) + getString(R.string.times)
                + Integer.toString(multiplier) + getString(R.string.equals),
                TextToSpeech.QUEUE_ADD, map);
    }

    private void chooseNextQuestion() {
        if((int)(Math.random()*2) == 0) {
            getEquation();
        } else {
            getNumber();
        }
    }

    private void resetNumberForEquation() { numberForEquation = -1; }

    @Override
    protected void displayNumber(String number) {
        TextView view;
        if((numberForEquation > 0 && multiplicand < 0) ||
                numberForEquation < 0) {
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
        view.setTextSize(view.getHeight());
        float width = view.getPaint().measureText(number);
        if(width > view.getWidth()) {
            view.setTextSize(view.getHeight()/(float)1.5);
        }
    }
}
