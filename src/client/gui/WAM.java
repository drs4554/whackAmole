package client.gui;

import java.util.List;

public class WAM {

    //model

    private int ROWS;

    private int COLS;

    private int points;

    private int player;

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
        this.points = 0;

        this.holes = new status[COLS * ROWS];
        for (int i = 0; i < COLS * ROWS; i++) {
            this.holes[i] = status.DOWN;
        }
    }

    public void moleUP(int num) {
        holes[num] = status.UP;
    }

    public void moleDOWN(int num) {
        holes[num] = status.DOWN;
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

    public void updateScore(int score) {
        this.points = score;
    }

    public void error() {
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
