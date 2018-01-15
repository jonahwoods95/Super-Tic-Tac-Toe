package com.example.jonahwoods.jonahsupertictactoe;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.LoginFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.graphics.Color.BLUE;

/**
 * Created by jonahwoods on 4/30/17.
 */

/*
    Represents the general version of the game
    board. This contains elements that are consistent
    for both BigGameBoard and SmallGameBoard.
 */
public class GameBoard extends LinearLayout {

    public MainActivity activity = null;
    //objects describe either games or buttons depending on the game size (big/small)
    public View[] objects;

    //List of objects contained in each row of the board
    private List<LinearLayout> rows = new ArrayList<>();

    //Sets layout parameters used for construction of all game boards
    private LinearLayout.LayoutParams gameWidthWeight = new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 20);
    private LinearLayout.LayoutParams barHeightWeight = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0, 1);

    //Color of the lines that make up the board
    private int gameBoardColor;
    //List of shapes in board
    private List<GradientDrawable> barList = new ArrayList<>();

    //Only constructor for GameBoard
    public GameBoard(MainActivity m) {
        super(m);
        activity = m;
    }

////////////////Methods for Game creation/////////////////
    //Visualizes the game within the activity
    public void createBoard(LayoutParams rowHeight, LayoutParams vertBarWidth){
        setOrientation(LinearLayout.VERTICAL);
        int z = 0;
        //creates 3 rows
        for(int y = 0; y<3; y++) {
            LinearLayout row = new LinearLayout(activity);
            rows.add(row);
            //puts three objects in each row with a vertical bar between them
            for (int x = 0; x < 3; x++) {
                View view = objects[z];
                view.setLayoutParams(gameWidthWeight);
                row.addView(view);
                //adds a vertical bar after first and second view
                if (x == 0 || x == 1) {
                    View vertBar = createVertBar(activity, y, vertBarWidth);
                    row.addView(vertBar);
                }
                z++;
            }
            //Sets height of row
            row.setLayoutParams(rowHeight);
            //places each row in the parent layout
            this.addView(row);
            //adds horizontal bar after first and second row of button/games
            if (y == 0 || y == 1) {
                View bar = createHorizBar();
                this.addView(bar);
            }
        }
    }

    //Creates vertical bar between two objects
    private View createVertBar(Context c, int y, LayoutParams vertBarWidth){
        View vertBar = new View(c);
        //adds a rounded edge to the bars
        GradientDrawable shape = new GradientDrawable();
        if (y == 0) {
            shape.setCornerRadii(new float[]{10, 10, 10, 10, 0, 0, 0, 0});
        }
        if (y == 2) {
            shape.setCornerRadii(new float[]{0, 0, 0, 0, 10, 10, 10, 10});
        }
        shape.setColor(ContextCompat.getColor(c, R.color.black));
        //add shape to array for changing color of bars
        gameBoardColor = R.color.black;
        barList.add(shape);
        vertBar.setBackground(shape);
        vertBar.setLayoutParams(vertBarWidth);
        return vertBar;
    }

    //Creates horizontal bar between two rows
    private View createHorizBar(){
        View bar = new View(activity);
        bar.setLayoutParams(barHeightWeight);
        GradientDrawable shape = new GradientDrawable();
        shape.setCornerRadius(10);
        shape.setColor(ContextCompat.getColor(activity, R.color.black));
        bar.setBackground(shape);
        barList.add(shape);
        return bar;
    }

///////////////////Methods to check for board win//////////////
    //Check for a sequence of letters that represent a row, column, or diagonal win
    private boolean checkForRowWin(String[] lettersToCheck) {
        boolean truth = false;
        for (int z = 0; z<lettersToCheck.length; z+=3) {
            String a = lettersToCheck[z];
            String b = lettersToCheck[z + 1];
            String c = lettersToCheck[z + 2];
            if (((a == b) && b == c) && (a != null && b != null && c != null)) {
                truth = true;
                break;
            }
        }
        return truth;
    }
    private boolean checkForColumnWin(String[] lettersToCheck){
        boolean truth = false;
        for (int z = 0; z<3; z++) {
            String a = lettersToCheck[z];
            String b = lettersToCheck[z + 3];
            String c = lettersToCheck[z + 6];
            if (((a == b) && b == c) && (a != null && b != null && c != null)) {
                truth = true;
                break;
            }
        }
        return truth;
    }

    private boolean checkForDiagonalWin(String[] lettersToCheck){
        boolean truth = false;
        String a = lettersToCheck[0];
        String b = lettersToCheck[4];
        String c = lettersToCheck[8];
        if (((a == b) && (b == c)) && (a != null && b != null && c != null)) {
            truth = true;
        }
        a = lettersToCheck[2];
        c = lettersToCheck[6];
        if (((a == b) && (b == c)) && (a != null && b != null && c != null)) {
            truth = true;
        }
        return truth;
    }
    //Checks this set of letters for any type of win
    public boolean checkGameWin (String[] letters){
        boolean truth = false;
        if (checkForDiagonalWin(letters) || checkForRowWin(letters) || checkForColumnWin(letters)){
            truth = true;
        }
        return truth;
    }

////////////////////Other methods/////////////////

    //Changes color of bars
    public void changeGameColor(int color){
        for(GradientDrawable v: barList){
            v.setColor(ContextCompat.getColor(activity, color));
        }
        gameBoardColor = color;
    }

    public int getGameBoardColor(){return gameBoardColor;}
}
