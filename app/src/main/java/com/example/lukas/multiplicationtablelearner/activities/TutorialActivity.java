package com.example.lukas.multiplicationtablelearner.activities;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.view.MotionEventCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.example.lukas.multiplicationtablelearner.tools.NumberGenerator;
import com.example.lukas.multiplicationtablelearner.R;

public class TutorialActivity extends BaseActivity {

    private final int NO_MODE = 0;
    private final int ENTER_NUMBER = 1;
    private final int PROVIDE_EQUATION = 2;
    private final int LEARN_MODE = 3;
    private final int TEST_MODE = 4;

    private final int ENTER_DIGIT_1 = 1;
    private final int ENTER_DIGIT_2 = 2;
    private final int CHANGE_POSITION = 3;

    private int mode = NO_MODE;
    private int submode = NO_MODE;
    private int moveCount = 0;
    private boolean isGoingThroughWhole = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        gestureListener = new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent motionEvent) {
                if(askedToGoBack) return;
                if(mode == PROVIDE_EQUATION) {
                    textToSpeech.stop();
                    counter = lastCount;
                    if(multiplicand < 0) { //first number (multiplicand) not provided
                        setMultiplicand();
                        textToSpeech.stop();
                        provideSecondNumber();
                    } else {
                        textToSpeech.speak(getString(R.string.provide_second_number)
                                + getString(R.string.get_result), TextToSpeech.QUEUE_FLUSH, map);
                    }
                }
            }

            @Override
            public boolean onFling(MotionEvent start, MotionEvent end, float velocityX, float velocityY) {
                if(velocityX > FLING_VELOCITY_LIMIT) { //to the right
                    if(pointerCount == 2) {
                        askToGoBack();
                    }
                    if(submode == CHANGE_POSITION || mode == PROVIDE_EQUATION) {
                        switch(pointerCount) {
                            case 0:
                                changePositionLeft();
                        }
                    }
                } else if(velocityX < -FLING_VELOCITY_LIMIT) { // to the left
                    if(mode == NO_MODE) {
                        isGoingThroughWhole = false;
                        switch(pointerCount) {
                            case 0:
                                mode = ENTER_NUMBER;
                                startEnterNumberInstruction();
                                break;
                            case 2:
                                mode = PROVIDE_EQUATION;
                                startProvideEquationInstruction();
                                break;
                            case 3:
                                mode = LEARN_MODE;
                                startLearnModeInstruction();
                                break;
                            case 4:
                                mode = TEST_MODE;
                                startTestModeInstruction();
                                break;
                        }
                    } else if(mode == PROVIDE_EQUATION) {
                        if(pointerCount == 2 && multiplicand > 0) {
                            counter = lastCount;
                            getResult();
                            finishedProvideEquation();
                        }
                    }
                    if(submode == CHANGE_POSITION || mode == PROVIDE_EQUATION) {
                        switch(pointerCount) {
                            case 0:
                                changePositionRight();
                        }
                    }
                } else if(velocityY > FLING_VELOCITY_LIMIT) { // down
                    if(mode == NO_MODE) {
                        isGoingThroughWhole = true;
                        mode = ENTER_NUMBER;
                        startEnterNumberInstruction();
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
        textToSpeech.speak(getString(R.string.tutorial_mode), TextToSpeech.QUEUE_FLUSH, map);
        textToSpeech.speak(getString(R.string.how_works), TextToSpeech.QUEUE_ADD, map);
        readInstruction();
    }

    private void getResult() {
        multiplier = NumberGenerator.getNumber() + counter;
        Integer result = multiplicand*multiplier;
        textToSpeech.speak(Integer.toString(multiplicand) + getString(R.string.times) + Integer.toString(multiplier) +
                getString(R.string.equals) + result.toString(), TextToSpeech.QUEUE_FLUSH, null);
        gotResult = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);

        if(!canSkip) return true;

        int action = MotionEventCompat.getActionMasked(event);

        if(action == MotionEvent.ACTION_DOWN) {
            resetPointerCount();
            if(mode == NO_MODE && !textToSpeech.isSpeaking()) {
                textToSpeech.stop();
                readInstruction();
            }
            lastCount = counter;
        }

        if(action == MotionEventCompat.ACTION_POINTER_DOWN) {
            setPointerCount(event.getPointerCount());
        }

        if(action == MotionEvent.ACTION_UP) {
            if(askedToGoBack) {
                textToSpeech.stop();
                if(pointerCount > 1) {
                    counter = lastCount;
                    askedToGoBack = false;
                    startOver();
                    switch(mode) {
                        case NO_MODE:
                            readInstruction();
                            break;
                        case ENTER_NUMBER:
                            startEnterNumberInstruction();
                            break;
                        case PROVIDE_EQUATION:
                            startProvideEquationInstruction();
                            break;
                    }
                } else {
                    textToSpeech.stop();
                    finish();
                }
                return true;
            } else {
                if (pointerCount == 0) {
                    counter++;
                } else {
                    if (submode == ENTER_DIGIT_1) {
                        return true;
                    }
                    counter += pointerCount;
                }
                checkCounter();
                if (mode == ENTER_NUMBER || mode == PROVIDE_EQUATION) {
                    readAndShowNumber();
                }
                switch (submode) {
                    case ENTER_DIGIT_1:
                        if (counter == NumberGenerator.MAX_DIGIT_VALUE) {
                            canSkip = false;
                            enterDigit2();
                        }
                        break;
                    case ENTER_DIGIT_2:
                        if (moveCount < 3) {
                            moveCount++;
                        } else {
                            textToSpeech.speak(getString(R.string.too_many_moves), TextToSpeech.QUEUE_FLUSH, null);
                            resetCounter();
                            moveCount = 0;
                        }
                        if (counter == NumberGenerator.MAX_DIGIT_VALUE) {
                            if (moveCount == 3) {
                                canSkip = false;
                                changePositionInstruction();
                                resetCounter();
                                moveCount = 0;
                            }
                        }
                        break;
                    case CHANGE_POSITION:
                        if (NumberGenerator.getNumber() + counter == 401) {
                            canSkip = false;
                            finishedEnterNumber();
                        }
                        break;
                }
            }
        }
        return gestureDetector.onTouchEvent(event);
    }

    private void readInstruction() {
        textToSpeech.speak(getString(R.string.tut_instruction), TextToSpeech.QUEUE_ADD, null);
        mode = NO_MODE;
    }

    private void startEnterNumberInstruction() {
        textToSpeech.speak(getString(R.string.enter_digit_instruction), TextToSpeech.QUEUE_FLUSH, map);
        resetCounter();
        NumberGenerator.resetDigits();
        submode = ENTER_DIGIT_1;
    }


    private void enterDigit2() {
        textToSpeech.speak(getString(R.string.good_job) +
                getString(R.string.enter_digit2_instruction), TextToSpeech.QUEUE_ADD, map);
        startOver();
        submode = ENTER_DIGIT_2;
    }

    private void changePositionInstruction() {
        textToSpeech.speak(getString(R.string.good_job) +
                getString(R.string.change_pos_instruction), TextToSpeech.QUEUE_ADD, map);
        startOver();
        submode = CHANGE_POSITION;
    }

    private void finishedEnterNumber() {
        textToSpeech.speak(getString(R.string.good_job) +
                getString(R.string.finished_enter_number), TextToSpeech.QUEUE_ADD, map);
        startOver();
        if(isGoingThroughWhole) {
            startProvideEquationInstruction();
        } else {
            readInstruction();
        }
        submode = NO_MODE;
    }

    private void startProvideEquationInstruction() {
        int firstQueue;
        if(mode != PROVIDE_EQUATION) {
            mode = PROVIDE_EQUATION;
            firstQueue = TextToSpeech.QUEUE_ADD;
        } else {
            firstQueue = TextToSpeech.QUEUE_FLUSH;
        }
        textToSpeech.speak(getString(R.string.provide_eq_instruction),
                firstQueue, map);
        resetCounter();
        resetMultiplicand();
    }

    private void provideSecondNumber() {
        textToSpeech.speak(getString(R.string.good_job) +
                getString(R.string.provide_sec_number), TextToSpeech.QUEUE_ADD, map);
    }

    private void finishedProvideEquation() {
        textToSpeech.speak(getString(R.string.congrats) +
                getString(R.string.finished_provide_eq), TextToSpeech.QUEUE_ADD, map);
        if(isGoingThroughWhole) {
            startLearnModeInstruction();
        } else {
            readInstruction();
        }
        startOver();
        submode = NO_MODE;
    }

    private void startLearnModeInstruction() {
        int firstQueue;
        if(mode != LEARN_MODE) {
            mode = LEARN_MODE;
            firstQueue = TextToSpeech.QUEUE_ADD;
        } else {
            firstQueue = TextToSpeech.QUEUE_FLUSH;
        }
        textToSpeech.speak(getString(R.string.lm_instruction_start), firstQueue, map);
        textToSpeech.speak(getString(R.string.learn_mode_instruction), TextToSpeech.QUEUE_ADD, map);
        finishedLearnMode();
    }

    private void finishedLearnMode() {
        textToSpeech.speak(getString(R.string.finished_lm), TextToSpeech.QUEUE_ADD, map);
        if(isGoingThroughWhole) {
            startTestModeInstruction();
        } else {
            readInstruction();
        }
        submode = NO_MODE;
    }

    private void startTestModeInstruction() {
        int firstQueue;
        if(mode != TEST_MODE) {
            mode = TEST_MODE;
            firstQueue = TextToSpeech.QUEUE_ADD;
        } else {
            firstQueue = TextToSpeech.QUEUE_FLUSH;
        }
        textToSpeech.speak(getString(R.string.tm_instruction_start), firstQueue, map);
        textToSpeech.speak(getString(R.string.test_mode_instruction), TextToSpeech.QUEUE_ADD, map);
        finishedTestMode();
    }

    private void finishedTestMode() {
        textToSpeech.speak(getString(R.string.finished_tm), TextToSpeech.QUEUE_ADD, map);
        if(isGoingThroughWhole) {
            textToSpeech.speak(getString(R.string.congrats) +
                    getString(R.string.finished_tutorial), TextToSpeech.QUEUE_ADD, map);
            isGoingThroughWhole = false;
        }
        readInstruction();
        submode = NO_MODE;
    }

    @Override
    protected void askToGoBack() {
        counter = lastCount;
        askedToGoBack = true;
        if(mode == PROVIDE_EQUATION || mode == ENTER_NUMBER) readAndShowNumber();
        textToSpeech.speak(getString(R.string.wanna_go_back), TextToSpeech.QUEUE_FLUSH, map);
        textToSpeech.speak(getString(R.string.confirm), TextToSpeech.QUEUE_ADD, null);
    }
}
