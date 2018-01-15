package com.example.jonahwoods.jonahsupertictactoe;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jonahwoods on 12/16/17.
 */

/*
    The small games on the big game board.
    Contains elements specific to the small games.
 */
public class SmallGameBoard extends GameBoard{

    private String letterOfWinner = null;

    //Arrays set up for the buttons of this board
    private ButtonClass [] buttons = new ButtonClass[9];
    public List<ButtonClass> clickedButtons;

    //Where on the bigGame this specific game is located
    private int smallGamePosition;
    private BigGameBoard bigGame;

    //Sets layout parameters used for construction of small game boards specifically
    private LinearLayout.LayoutParams smallRowHeightWeight = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0, 10);
    private LinearLayout.LayoutParams vertBarWidthWeight = new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 2);


    public SmallGameBoard(BigGameBoard bigGameBoard, int gamePos){
        super(bigGameBoard.activity);
        bigGame = bigGameBoard;
        smallGamePosition = gamePos;
        createButtons();
        createBoard(smallRowHeightWeight, vertBarWidthWeight);
        setPadding(15,15,15,15);
        clickedButtons = new ArrayList<>();
    }

    //Creates buttons for the GameBoard class to use in each position
    private void createButtons(){
        objects = new ButtonClass[9];
        for(int z = 0; z<9; z++) {
            ButtonClass b = new ButtonClass(activity, this, z);
            objects[z] = b;
        }
        buttons = (ButtonClass[]) objects;
    }

    @Override
    public String toString() {
        String[] letters = getLettersOfAllButtons();
        return "Small Game [Position: " + String.valueOf(smallGamePosition) + ", Winner: " + letterOfWinner + " Letters: (" +  letters[0] + ", " + letters[1] + ", " + letters[2]
                + ", " + letters[3] + ", " + letters[4] + ", " + letters[5] + ", " + letters[6] + ", " + letters[7] + ", " + letters[8] + ")]";
    }

///////////////////Getters and Setters/////////////////////
    public ButtonClass[] getAllButtons() {return buttons;}
    //complex get method to get the letter for all buttons
    public String[] getLettersOfAllButtons(){
        String[] letters = new String[9];
        int z = 0;
        for (ButtonClass button: getAllButtons()) {
            String letter = button.getLetter();
            letters[z] = letter;
            z++;
        }
        return letters;
    }
    public int getSmallGamePosition(){return smallGamePosition;}
    public BigGameBoard getBigGame(){return bigGame;}
    public String getLetterOfWinner(){return letterOfWinner;}
    public void setLetterOfWinner(String letter){letterOfWinner = letter;}
}
