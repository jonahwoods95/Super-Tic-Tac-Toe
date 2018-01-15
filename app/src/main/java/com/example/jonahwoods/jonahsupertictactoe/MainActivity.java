package com.example.jonahwoods.jonahsupertictactoe;

import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/*
    The code for this game is looked at from an "Inception"-like mindset--it is
    essentially tic-tac-toe within tic-tac-toe. The GameBoard class is the base
    of the code; it is extended by both BigGameBoard and SmallGameBoard since both
    of these contain similar elements. The BigGameBoard class pertains to
    the bigger picture of the game while the SmallGameBoard class narrows its focus
    to each one of the nine games within the BigGameBoard.
 */
/*
    This is the primary class of the program and the content for the activity_main.xml.
    Its elements relate to what appears on the screen itself.
 */

/*
    Throughout the classes designed for this game, there are a few variable naming techniques I
    implemented to keep the wording systematic. Most importantly, each place on a tic-tac-toe
    board is labelled as a "button."

    1) bigGameBoard/smallGameBoard/gameBoard
        --> variables associated with the visual layout
    2) bigGame/smallGame/game
        --> instances of that specific class
    3) chosenGame/chosenButton
        --> variables indicating the user's (or computer's) most recent move
    4) nextGame/nextGamePostion
        --> variables for elements used in the next move
    4) gamePosition/buttonPosition
        --> Where on the respective board (bigGameBoard/smallGameBoard)
            they are located
        --> Numbered 0-8 starting in the top left corner of each board
        --> Originally I looked at making a 2-dimensional array for clarity on position,
            but this was actually less efficient for the code
 */

public class MainActivity extends AppCompatActivity {

    //Visual elements
    private float screenHeight;
    private BigGameBoard bigGameBoard;
    private Button undoButton;
    private Button newGameButton;
    private Button menuButton;
    private TextView playerTurn;
    private LinearLayout llParent;

    //Variables defined by which player's turn it is
    private int color;
    public String playerName;

    //Variables for one player games
    private boolean playerVsPlayer = false;
    private int difficulty = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        menuButton = (Button) findViewById(R.id.menu);
        menuButton.setOnClickListener(menuButtonListener);
        undoButton = (Button) findViewById(R.id.undo);
        undoButton.setOnClickListener(undoButtonListener);
        newGameButton = (Button) findViewById(R.id.newGame);
        newGameButton.setOnClickListener(newGameButtonListener);
        llParent = (LinearLayout) findViewById(R.id.llParent);
        playerTurn = (TextView) findViewById(R.id.playerTurn);
        setScreenSize();
        startGame();
    }

    public void startGame(){
        //Removes old game board if necessary
        llParent.removeAllViews();
        bigGameBoard = new BigGameBoard(this);
        llParent.addView(bigGameBoard);
        setStartingTextAndColor();
    }

    private void setScreenSize(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenHeight=dm.heightPixels;
    }

    public void adjustVisualsOnClick(int playerNumber){
        if (playerNumber == 1){
            playerName = "Player 1";
            color = R.color.player1;;
        } else {
            playerName = "Player 2";
            color = R.color.player2;;
        }
        playerTurn.setText(playerName + " Turn");
        playerTurn.setBackgroundColor(ContextCompat.getColor(this,color));
    }

    public void finishGame(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Game Over!" +"\n" + playerName + " won the Game.");
        alert.setPositiveButton("Play again?",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startGame();
                    }
                }
        );
        AlertDialog alertDialogMain = alert.create();
        alertDialogMain.setCancelable(true);
        alertDialogMain.show();
    }

    private void setStartingTextAndColor(){
        color = R.color.player1;
        playerTurn.setText("Player 1 Turn");
        playerTurn.setTextSize(getScreenHeight() / 70);
        newGameButton.setTextSize(getScreenHeight() / 120);
        menuButton.setTextSize(getScreenHeight() / 120);
        undoButton.setTextSize(getScreenHeight() / 120);
        playerTurn.setBackgroundColor(ContextCompat.getColor(this,color));
        playerTurn.setTextColor(Color.WHITE);
    }

//////////////////Methods for buttons along bottom of screen/////////////////
    private View.OnClickListener menuButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
            alert.setMessage("Choose how many players");
            final View v1 = v;
            alert.setPositiveButton("One Player",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            playerVsPlayer = false;
                            displayDifficultySettings(v1);
                        }
                    }
            );
            alert.setNegativeButton("Two Player",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            playerVsPlayer = true;
                            startGame();
                        }
                    }
            );
            AlertDialog alertDialogMain = alert.create();
            alertDialogMain.setCancelable(true);
            alertDialogMain.show();
        }
    };

    //If user chooses a one person game, the following allows them to choose a difficulty
    private void displayDifficultySettings(View v){
        AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
        alert.setMessage("Choose diffuculty");
        alert.setPositiveButton("Average",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        difficulty = 0;
                        startGame();
                    }
                }
        );
        alert.setNegativeButton("Hard",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        difficulty = 1;
                        startGame();
                    }
                }
        );
        AlertDialog alertDialogMain = alert.create();
        alertDialogMain.setCancelable(true);
        alertDialogMain.show();
    }

    private View.OnClickListener undoButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            bigGameBoard.undoClick();
        }
    };

    private View.OnClickListener newGameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startGame();
        }
    };

///////////////////////Getters and Setters//////////////////////
    public int getDifficulty(){return difficulty;}
    public float getScreenHeight(){return screenHeight;}
    public int getColor(){return color;}
    public boolean getPlayerVsPlayer(){return playerVsPlayer;}

}
