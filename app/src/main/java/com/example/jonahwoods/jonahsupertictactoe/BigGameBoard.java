package com.example.jonahwoods.jonahsupertictactoe;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.text.Bidi;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.graphics.Color.BLUE;

/**
 * Created by jonahwoods on 11/7/17.
 */

/*
    Represents the bigger version of the
    game board; in other words the game board
    that contains smaller game boards.
 */
public class BigGameBoard extends GameBoard{

    //Letter of the player who has the current move
    public String currentLetter;
    //Button most recently chosen
    private ButtonClass chosenButton;
    //Game board most recently played on
    private SmallGameBoard chosenGame = null;

    //Variables for where the next user will be able to play
    private int nextGamePosition;
    private SmallGameBoard nextGame = null;

    //List of all the clicked buttons on all of the small boards (Used for undo primarily)
    public List<ButtonClass> allClickedButtons = new ArrayList<>();
    //All of the buttons on the entire big game
    public ButtonClass[] allButtons = new ButtonClass[81];

    //All of the small games on the big games
    private SmallGameBoard[] games = new SmallGameBoard[9];

    //Variable for one player games
    private boolean computerIsMoving;

    //Sets layout parameters used for construction of big game boards specifically
    private LinearLayout.LayoutParams bigRowHeightWeight = new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, 0, 20);
    private LinearLayout.LayoutParams vertBarWidthWeight = new LinearLayout.LayoutParams(0, LinearLayoutCompat.LayoutParams.MATCH_PARENT, 1);

///////////////////Constructor and related methods////////////////////
    public BigGameBoard(MainActivity m){
        super(m);
        createSmallBoards();
        createBoard(bigRowHeightWeight, vertBarWidthWeight);
        currentLetter = "o";
        setPadding(0,0,0,0);
        int z = 0;
        for (SmallGameBoard g: games){
            for(ButtonClass b:g.getAllButtons()){
                allButtons[z] = b;
                z++;
            }
        }
    }

    private void createSmallBoards(){
        objects = new SmallGameBoard[9];
        for(int z = 0; z<9; z++) {
            SmallGameBoard g = new SmallGameBoard(this, z);
            objects[z] = g;
        }
        games = (SmallGameBoard[]) objects;
    }

    @Override
    public String toString() {
        String[] letters = getLettersOfAllGames();
        return "Big Game [Letters: (" +  letters[0] + ", " + letters[1] + ", " + letters[2] + ", " + letters[3] +
                ", " + letters[4] + ", " + letters[5] + ", " + letters[6] + ", " + letters[7] + ", " + letters[8] + ")]";
    }

/////////////////////Methods for when a button is chosen//////////////////
    //Button action after one has been clicked
     public void  buttonAction(ButtonClass button){
        //Sets the chosenButton to most recently chosen button
        chosenButton = button;
        chosenGame = chosenButton.getSmallGame();
        allClickedButtons.add(chosenButton);
        nextGamePosition = chosenButton.getButtonPosition();
        checkAndUpdateIfSmallGameWin(chosenGame);
        updateBigGameBoardForNextMove();
    }

    //Once the button is clicked, the bigGameBoard must change its appearance and the available buttons
    private void updateBigGameBoardForNextMove(){
        changeLetterAndVisuals();
        /*
           This next step is essential to the game:
            The player just chose a position on a smallGameBoard; this position is where he will
            be able to choose on the big board. For example, if player 1 puts an x in the top-left
            corner of a small game, then player 2 should play in the top-left small game (provided
            it is not already won).
        */
        nextGamePosition = chosenButton.getButtonPosition();
        nextGame = getGame(nextGamePosition);
        //If the next game is already won, then the next player can choose anywhere
        if (checkGameWin(nextGame.getLettersOfAllButtons())){
            for (ButtonClass b : allButtons) {
                b.setAvailable(true);
            }
            for(SmallGameBoard game: games){
                if (game.getGameBoardColor() == R.color.blackTransparent){
                    game.changeGameColor(R.color.black);
                }
            }
        } else {
        //If next game is not won, then the player must play on the next game board
            setAvailableButtons(nextGamePosition);
            //Sets all boards besides the next game board to half transparent
            for (SmallGameBoard game: games){
                if(game.getGameBoardColor() == R.color.black){
                    game.changeGameColor(R.color.blackTransparent);
                }
            }
            nextGame.changeGameColor(R.color.black);
        }
        //For playerVsComputer, this is when the computer makes its move
        if (!activity.getPlayerVsPlayer() && !computerIsMoving){
            computerMove(activity.getDifficulty());
            computerIsMoving = false;
        }
    }

    //Checks for a board win and updates the board if so
    private void checkAndUpdateIfSmallGameWin(SmallGameBoard gameToCheckForWin){
        String[] letters = gameToCheckForWin.getLettersOfAllButtons();
        if (checkGameWin(letters)) {
            //Set buttons' properties "partOfWonBoard = true" so they cannot be clicked again
            for (ButtonClass b : chosenGame.getAllButtons()) {
                b.setPartOfWonBoard(true);
            }
            chosenGame.changeGameColor(activity.getColor());
            chosenGame.setLetterOfWinner(currentLetter);
            //If this win leads to a big game win, then MainActivity finishes the game
            if(checkForBigGameWin()){
                activity.finishGame();
            }
        }
    }

    private void changeLetterAndVisuals(){
        if (currentLetter == "x"){
            currentLetter = "o";
            activity.adjustVisualsOnClick(1);
        } else {
            currentLetter = "x";
            activity.adjustVisualsOnClick(2);
        }
    }

    //checks for a board win using the "letterOfWinner" for each small game
    private boolean checkForBigGameWin(){
        boolean truth = false;
        if (checkGameWin(getLettersOfAllGames())){
            truth = true;
        }
        return truth;
    }

/////////////////////////////Methods for Undo Button/////////////////////////////
    public void undoClick(){
        //For Two Player Games
        if (activity.getPlayerVsPlayer()) {
            undoAction();
        } else {
            //One Player games vs the computer
            computerIsMoving = true;
            for(int i=0;i<2;i++) {
                undoAction();
            }
            computerIsMoving = false;
        }
    }
    //Basically this method takes the most recently clicked button and undoes everything
    // that happened since the click before it
    private void undoAction(){
        if (allClickedButtons.size() > 0) {
            ButtonClass button = allClickedButtons.get(allClickedButtons.size() - 1);
            //If the button constituted a gameWin, then undo that impact as well
            if (checkGameWin(button.getSmallGame().getLettersOfAllButtons())) {
                //Resets "partOfWonBoard" for the buttons on the same smallGame
                for (ButtonClass b: button.getSmallGame().getAllButtons()) {
                    b.setPartOfWonBoard(false);
                }
                button.getSmallGame().changeGameColor(R.color.black);
            }
            button.resetButtonResource();
            allClickedButtons.remove(button);
            if(allClickedButtons.size() > 0) {
                chosenButton = allClickedButtons.get(allClickedButtons.size()-1);
                chosenGame = chosenButton.getSmallGame();
                updateBigGameBoardForNextMove();
            } else {
                activity.startGame();
            }
        } else {
            Toast.makeText(activity,
                    "Cannot undo at this time",
                    Toast.LENGTH_SHORT).show();
        }
    }

///////////////////Methods for the PlayerVsComputer (playerVsPlayer = false) scenario/////////////////
    private ButtonClass figureComputerMove(int difficulty){
        //First list contains all the options the computer has
        List<ButtonClass> list1 = new ArrayList<>();
        for(ButtonClass b: allButtons){
            if (!b.getClicked() && b.getAvailable() && !b.getPartOfWonBoard()){
                list1.add(b);
            }
        }
        //The second list is a refinement to buttons that will get the computer a win on a board
        List<ButtonClass> list2 = new ArrayList<>();
        for (ButtonClass b: list1){
            //If the button results in a board win for the computer, then the computer would add the button to possible placements
            b.setLetter(currentLetter);
            if (checkGameWin(b.getSmallGame().getLettersOfAllButtons())){
                list2.add(b);
            }
            System.out.println(list2);
            //If the button results in a board win for the user, the computer would also want to choose that position
            if(currentLetter == "x"){
                b.setLetter("o");
            } else {
                b.setLetter("x");
            }
            if (checkGameWin(b.getSmallGame().getLettersOfAllButtons())){
                list2.add(b);
            }
            System.out.println("Part 2");
            System.out.println(list2);
            b.setLetter(null);
        }
        //If the next move results in a board that is won, remove it from options, unless only option
        List<ButtonClass> buttonsToRemove = new ArrayList<>();
        if(list2.size()>0) {
            buttonsToRemove = new ArrayList<>();
            for (ButtonClass b : list2) {
                if (list2.size() > 1 && games[b.getButtonPosition()].getLetterOfWinner() != null) {
                    buttonsToRemove.add(b);
                }
            }
        }
        for (ButtonClass b: list1) {
            if (games[b.getButtonPosition()].getLetterOfWinner() != null) {
                buttonsToRemove.add(b);
            }
        }
        for(ButtonClass b: buttonsToRemove) {
            list1.remove(b);
            if(list2.contains(b)) {
                list2.remove(b);
            }
        }
        System.out.println("Part 3");
        System.out.println(list2);
        //Computer randomly chooses from possible positions
        Random rand = new Random();
        if (difficulty == 1 && list2.size() > 0){
            return list2.get(rand.nextInt(list2.size()));
        } else {
            return list1.get(rand.nextInt(list1.size()));
        }
    }

    private void computerMove(int difficulty){
        computerIsMoving = true;
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 10;
        float x = 0.0f;
        float y = 0.0f;
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_DOWN,
                x,
                y,
                metaState
        );
        figureComputerMove(difficulty).dispatchTouchEvent(motionEvent);
    }

///////////////////Getters and Setters for instance variables/////////////
    public SmallGameBoard[] getAllGames(){return games;}
    public String getCurrentLetter(){return currentLetter;}

/////////////Getters and Setters for other important variables/////////////
    //Gets the buttons that are NOT available for the player based on the buttons that are available
    public ButtonClass[] getNotAvailableButtons(int availableGamePosition){
        ButtonClass[] buttons = new ButtonClass[81-9];
        int z = 0;
        for (SmallGameBoard game: games){
            if (game.getSmallGamePosition() != availableGamePosition){
                for (ButtonClass b: game.getAllButtons()){
                    buttons[z] = b;
                    z++;
                }
            }
        }
        return buttons;
    }
    //Finds the game and sets buttons to available
    private void setAvailableButtons(int gamePosition){
        ButtonClass[] availableButtons = getGame(gamePosition).getAllButtons();
        ButtonClass[] notAvailableButtons = getNotAvailableButtons(gamePosition);
        for (ButtonClass b: availableButtons) {
            b.setAvailable(true);
        }
        for (ButtonClass b: notAvailableButtons){
            b.setAvailable(false);
        }
    }
    public SmallGameBoard getGame(int position){return games[position];}
    //complex get method to get the letterOfWinner for all small games
    public String[] getLettersOfAllGames(){
        String[] letters = new String[9];
        int z = 0;
        for (SmallGameBoard game: getAllGames()) {
            String letter = game.getLetterOfWinner();
            letters[z] = letter;
            z++;
        }
        return letters;
    }
}
