package com.paramgy.braintrain;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //------------------- my variables ---------------------

    TextView gameStateText;
    LinearLayout gameOverBoard;
    TextView winningText;
    TextView gradeText;
    AccurateCountDownTimer accurateTimer;
    boolean isGameOn = true;
    int correctBoxIndex;
    int numberOfCorrectAnswers = 0;
    int totalAnsweredQuestions = 0;
    int timerValue = 45_000;

    //------------------- overrided methods ---------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameStateText = findViewById(R.id.gameStateText);
        gameOverBoard = findViewById(R.id.gameOverLayout);
        winningText = findViewById(R.id.winningText);
        gradeText = findViewById(R.id.gradeText);

        quizGenerator();
        resultDisplay();
    }//end onCreate

    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        numberOfCorrectAnswers = 0;
        totalAnsweredQuestions = 0;
        resultDisplay();
        gameStateText.setVisibility(View.INVISIBLE);
        winningText.setVisibility(View.INVISIBLE);
        gradeText.setVisibility(View.INVISIBLE);
        gameOverBoard.setVisibility(View.INVISIBLE);
        quizGenerator();
        isGameOn = true;
        accurateTimer.cancel();
    }

    //------------------- my methods ---------------------

    //********************** timer related methods ********************

    //Start Timer Method
    public void startTimer() {

        accurateTimer = new AccurateCountDownTimer(timerValue + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerUpdate((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                gameStateText.setVisibility(View.INVISIBLE);
                isGameOn = false;
                //check winning condition
                if (isWinner()) {
                    //Do things for the winner :)
                } else {
                    //make the game over board visible if the plaer loses
                    MediaPlayer lossSound = MediaPlayer.create(getApplicationContext(), R.raw.loss);
                    lossSound.start();
                    gameOverBoard.setVisibility(View.VISIBLE);
                }
            }
        };

        accurateTimer.start();
    } // end start timer

    //Timer Update Method
    public void timerUpdate(int mSeconds) {
        Button timerButton = findViewById(R.id.timer);
        timerButton.setText(String.format("%02d", mSeconds) + " s");
    }// end timer update


//******************* generators methods **********************

    //Quiz Generator Method
    public void quizGenerator() {
        /*
        generate some  add and minus questions!
         */
        TextView quizText = findViewById(R.id.quiz);
        Random random = new Random();
        int x = random.nextInt(50) + 1;
        int y = random.nextInt(50) + 1;
        int correctAnswer=0;
        int operator = random.nextInt(2);

        // Check the operator and make the correct answer according to it
        if(operator==0){
            correctAnswer = x + y;
            quizText.setText(x + " + " + y);
        }
        else{

            if(x>=y) {
                correctAnswer = x - y;
                quizText.setText(x + " - " + y);
            }
            else{
                correctAnswer = y - x;
                quizText.setText(y + " - " + x);
            }
        }
        answersGenerator(correctAnswer);
    }//end quiz generator

    //Answer Generator Method
    /*
     purpose : to generate unique random answers for each question,
     and cover the correct answer between them.
     */
    public void answersGenerator(int correctAnswer) {
        MyRandomNumber myRandomNumber = new MyRandomNumber();
        MyRandomNumber indexGenerator = new MyRandomNumber();

        //Answers Boxes Setting
        Button firstBox = findViewById(R.id.box1);
        Button secondBox = findViewById(R.id.box2);
        Button thirdBox = findViewById(R.id.box3);
        Button fourthBox = findViewById(R.id.box4);
        ArrayList<Button> boxList = new ArrayList<>();
        boxList.add(0, firstBox);
        boxList.add(1, secondBox);
        boxList.add(2, thirdBox);
        boxList.add(3, fourthBox);


        // MyRandomNumber place to put the correctAnswer in
        correctBoxIndex = myRandomNumber.nextInt(4);

        //Put the correct answer in the corresponding index
        boxList.get(correctBoxIndex).setText(Integer.toString(correctAnswer));

        //Put the other  answers in the boxes to cover it!
        // and make one number identical in last digit with the correct answer

        int lastDigitIndex = indexGenerator.nextUniqueInt(4,correctBoxIndex);
        for (int i = 0; i < 4; i++) {
            if (i != correctBoxIndex) {
                int randomAnswer = myRandomNumber.nextUniqueInt(101,correctAnswer);
                int lastDigit = randomAnswer % 10;
                int cLastDigit = correctAnswer % 10;
                if (lastDigitIndex == i) {
                    while (lastDigit != cLastDigit) {
                        randomAnswer = myRandomNumber.nextUniqueInt(101,correctAnswer);
                        lastDigit = randomAnswer % 10;
                    }
                }
                //Put the final unique wrong answers in the boxes
                boxList.get(i).setText(Integer.toString(randomAnswer));
            }
        } // outer for loop
    }//end answer generator

    //****************** other methods ************************

    //Answer Check Method
    //Check the clicked button ( box )  then change gameStateText accordingly
    public void answerCheck(View view) {

        if (isGameOn) {
            Button clickedBox = (Button) view;
            int answerIndex = Integer.parseInt(clickedBox.getTag().toString());

            if (answerIndex == correctBoxIndex) {
                //Do something good
                gameStateText.setTextColor(Color.GREEN);
                gameStateText.setText("Correct!");
                gameStateText.setVisibility(View.VISIBLE);
                //update the correct answers result
                numberOfCorrectAnswers += 1;
            } else {
                //Do something very bad
                gameStateText.setTextColor(Color.RED);
                gameStateText.setText("Wrong!");
                gameStateText.setVisibility(View.VISIBLE);
            }

            //update the total number of answered questions anyway
            totalAnsweredQuestions += 1;
            //generate the next question
            quizGenerator();

            //update the result
            resultDisplay();
        }// if is Game On ?
    }//answer check


    //Result Display Method
    public void resultDisplay() {
        Button resultView = findViewById(R.id.result);

        //update the result with some value
        String result = numberOfCorrectAnswers + " / " + totalAnsweredQuestions;
        resultView.setText(result);
    }// end result dispaly

    //Winner Check Method
    // >> check if the player wins! and do things if he does!
    public boolean isWinner() {
        MediaPlayer winningSound = MediaPlayer.create(this, R.raw.tadaa);
        //To guarantee that it is not random answers!
        double accuracy = (double) numberOfCorrectAnswers / (double) totalAnsweredQuestions;
        // the winning condition
        if (numberOfCorrectAnswers >= 15 && accuracy >= 0.5) {
            String grade = "Grade: ";
            //check grade
            if (accuracy >= 0.95) {
                grade += "GENIUS!";
            } else if (accuracy >= 0.85 && accuracy < 0.95) {
                grade += "EXCELLENT!";
            } else if (accuracy >= 0.7 && accuracy < 0.85) {
                grade += "Very Good!";
            } else if (accuracy >= 0.5 && accuracy < 0.7) {
                grade += "Fair";
            }
            // Make the winning text visible if the player wins
            gradeText.setText(grade);
            gradeText.setVisibility(View.VISIBLE);
            winningText.setVisibility(View.VISIBLE);

            winningSound.start();
            return true;
        }
        return false;
    }// end is winner

    //Game Restart Method
    public void playAgain(View view) {
        startTimer();
        quizGenerator();
        gameOverBoard.setVisibility(View.INVISIBLE);
        isGameOn = true;

        //reset result
        numberOfCorrectAnswers = 0;
        totalAnsweredQuestions = 0;
        resultDisplay();
    } //end play again

}//class end