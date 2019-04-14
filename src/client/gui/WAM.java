package client.gui;

import java.util.LinkedList;
import java.util.List;

/**
 * The model part of the Whack-a-mole game
 *
 * @author Sam Chilaka @<soc5881@rit.edu>
 * @author Dhaval Shrimshal @<drs4554@rit.edu>
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

    public void moleUP(int num) {
        holes[num] = status.UP;
        alertObservers();
    }

    public void moleDOWN(int num) {
        holes[num] = status.DOWN;
        alertObservers();
    }

    public void gameWon() {
        alertObservers();
    }

    public void gameLost() {
        alertObservers();
    }

    public void gameTied() {
        alertObservers();
    }

    public void updateScore(String score) {
        this.points = score;
    }

    public void error(String args) {
        alertObservers();
    }

    public void close() {
        alertObservers();
    }

    public int getrows() {
        return this.ROWS;
    }

    public int getcols() {
        return this.COLS;
    }

    public status[] getholes() {
        return this.holes;
    }

    public int getplayer() {
        return this.player;
    }
}
