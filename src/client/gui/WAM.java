package client.gui;

import java.util.LinkedList;
import java.util.List;

/**
 * The model part of the Whack-a-mole game
 *
 * @author Dhaval Shrimshal @<drs4554@rit.edu>
 * @author Sam Chilaka @<soc5881@rit.edu>
 */
public class WAM {

    //model

    public static int ROWS;

    public static int COLS;

    public String points;

    private int player;

    public int total;

    /** the observers of this model */
    private List<Observer<WAM>> observers;

    public enum status {
        UP,
        DOWN;
    }

    public status[] holes;

    /**
     * The view calls this method to add themselves as an observer of the model.
     *
     * @param observer the observer
     */
    public void addObserver(Observer<WAM> observer) {
        this.observers.add(observer);
    }

    /** when the model changes, the observers are notified via their update() method */
    private void alertObservers() {
        for (Observer<WAM> obs: this.observers ) {
            obs.update(this);
        }
    }

    /**
     * The constructor method for the model of the whack-a-mole game
     * takes in rows, columns and the number of the player in the game
     * @param rows
     * @param cols
     * @param player
     */
    public WAM(int rows, int cols, int player) {
        this.ROWS = rows;
        this.COLS = cols;
        this.player = player;
        this.points = "";
        this.total = rows * cols;

        this.observers = new LinkedList<>();

        this.holes = new status[COLS * ROWS];
        for (int i = 0; i < COLS * ROWS; i++) {
            this.holes[i] = status.DOWN;
        }
    }

    /**
     * the mole is up
     * @param num
     */
    public void moleUP(int num) {
        holes[num] = status.UP;
        alertObservers();
    }

    /**
     * the mole is down
     * @param num
     */
    public void moleDOWN(int num) {
        holes[num] = status.DOWN;
        alertObservers();
    }

    /**
     * called when the game is won
     */
    public void gameWon() {
        alertObservers();
    }

    /**
     * called when the game is over and sent to the players who lost
     */
    public void gameLost() {
        alertObservers();
    }

    /**
     * called when the game is over and it is tied
     */
    public void gameTied() {
        alertObservers();
    }

    /**
     * called to update score
     * @param score
     */
    public void updateScore(String score) {
        this.points = score;
    }

    /**
     * called when an error has occurred
     */
    public void error(String args) {
        alertObservers();
    }

    /**
     * called when the game is over to close all processes
     */
    public void close() {
        alertObservers();
    }

    /**
     * returns the number of ROWS of the game setup
     * @return
     */
    public int getrows() {
        return this.ROWS;
    }

    /**
     * returns the number of columns of the game setup
     * @return
     */
    public int getcols() {
        return this.COLS;
    }

    /**
     * returns the status of the 1D array of holes
     * @return
     */
    public status[] getholes() {
        return this.holes;
    }

    /**
     * returns the player position for the game
     * @return
     */
    public int getplayer() {
        return this.player;
    }
}
