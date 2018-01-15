package com.example.jonahwoods.jonahsupertictactoe;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by jonahwoods on 4/29/17.
 */

/*
    Represents the buttons inside each SmallGameBoard.
    These are objects the user clicks on or the computer
    chooses for their respective move.
 */
public class  ButtonClass extends android.support.v7.widget.AppCompatButton {

    private Context context;
    private SmallGameBoard smallGame;

    //Describes where the button is on a small game
    //numbering is 0-8 starting in top left of each game board
    private int buttonPosition;

    //Variables to determine the state of the button
    //Clicked = true --> button has been chosen at some point by a player
    private boolean clicked;
    //available = true --> button is currently available to be chosen
    private boolean available;
    //partOfWonBoard = true --> button is on a smallGameBoard that has been won
    //In the rules of the game, a button of this type (partOfWonBoard == true) cannot be chosen
    private boolean partOfWonBoard;
    private String letter = null;

    //Only constructor for buttons
    //Needs the context, the small game board that it is part of, and the position on the game board
    public ButtonClass(Context c, SmallGameBoard g, int position) {
        super(c);
        smallGame = g;
        context = c;
        buttonPosition = position;
        setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        setGravity(Gravity.CENTER);
        clicked = false;
        available = true;
        partOfWonBoard = false;
        LinearLayout.LayoutParams buttonWidthWeight = new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 10);
        setLayoutParams(buttonWidthWeight);
        setPadding(2, 2, 2, 2);
        //Remove shadow from behind buttons
        setStateListAnimator(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // get int representing the type of action which caused this event
        int action = e.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (!clicked && available && !partOfWonBoard) {
                changeButtonAppearanceOnClick(smallGame.getBigGame().getCurrentLetter());
                setClicked(true);
                smallGame.getBigGame().buttonAction(this);
            }
        }
        return true;
    }

    public void resetButtonResource(){
        setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        clicked = false;
        available = true;
        partOfWonBoard = false;
        letter = null;
        setClickable(true);
        setText(null);
    }

    private void changeButtonAppearanceOnClick(String playerLetter){
        setTextSize(getMeasuredHeight() / 5);
        setPadding(0, 0, 0, 0);
        letter = playerLetter;
        setText(letter);
        if (letter == "x") {
            setTextColor(ContextCompat.getColor(context, R.color.player2));
        } else {
            setTextColor(ContextCompat.getColor(context, R.color.player1));
        }
    }

    @Override
    public String toString() {
        return "button: pos = " + String.valueOf(buttonPosition) + " smallGame = " + String.valueOf(smallGame.getSmallGamePosition()) + ".";
    }


//////////////////Getters and Setters/////////////////
    public String getLetter(){
        return letter;
    }
    public SmallGameBoard getSmallGame(){return smallGame;}
    public int getButtonPosition(){return buttonPosition;}
    public boolean getClicked(){return clicked;}
    public boolean getAvailable(){return available;}
    public boolean getPartOfWonBoard(){return partOfWonBoard;}

    public void setLetter(String l){letter = l;}
    public void setClicked(Boolean truth){clicked = truth;}
    public void setAvailable(boolean truth){available = truth;}
    public void setPartOfWonBoard(boolean truth){partOfWonBoard = truth;}
}