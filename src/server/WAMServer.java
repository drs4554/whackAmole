package server;

import common.WAMProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * The WAMServer class is the server domain for the game. It waits for incoming
 * client connections and pairs them to engage in the game {@link client.gui.WAM WAM games}
 *
 * @author Sam Chilaka
 * @author Dhaval Shrimshal
 */
public class WAMServer implements Runnable, WAMProtocol {

    /**
     * the server socket
     */
    private ServerSocket server;

    /**
     * number of rows
     */
    private int rows;

    /**
     * number of cols
     */
    private int cols;

    /**
     * number of players
     */
    private int num_players;

    /**
     * total time in seconds
     */
    private int time;

    /**
     * an array of all the players
     */
    private WAMPlayer[] players;

    /**
     * is the game running?
     */
    private boolean flagThread;

    /**
     * enum for the mole up and down status
     */
    public enum status {
        UP,
        DOWN
    }

    /**
     * an array of status of all the moles
     */
    private status[] moles;

    /**
     * called on a specific position to pop a mole up for the clients
     * @param num the mole number
     */
    public void moleUP(int num) {
        this.moles[num] = status.UP;
        for (int i = 0; i < this.num_players; i++) {
            this.players[i].moleUP(num);
        }
    }

    /**
     * called on a specific position to indicate the mole is down for the clients
     * @param num the mole number
     */
    public void moleDOWN(int num) {
        this.moles[num] = status.DOWN;
        for (int i = 0; i < this.num_players; i++) {
            this.players[i].moleDOWN(num);
        }
    }

    /**
     * This is the constructor for the server, it takes the parameters for the
     * game setup
     * @param port the port of the server
     * @param rows number of rows
     * @param cols number of cols
     * @param players number of players
     * @param time total time in seconds
     */
    public WAMServer(int port, int rows, int cols, int players, int time) {
        try {
            server = new ServerSocket(port);
            this.players = new WAMPlayer[players];

        } catch (IOException io) {
            System.err.println(io);
        }
        this.num_players = players;
        this.rows = rows;
        this.cols = cols;
        this.time = time;
        this.moles = new status[rows * cols];
        for (int i = 0; i < rows * cols; i ++) {
            this.moles[i] = status.DOWN;
        }
        this.flagThread = true;

    }

    /**
     * Starts a new {@link WAMServer}. Makes the server and calls
     * {@link #run()} in the main thread.
     * @param args used to setup the game parameters which the server uses for
     *             incoming client connections and setting up the game
     */
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("Usage: java WAMServer <port> <rows> <columns> <#players> <duration>");
            System.exit(1);
        }
        if (Integer.parseInt(args[1])<1||Integer.parseInt(args[2])<1||Integer.parseInt(args[3])<2) {
            System.out.println("Usage: java Invalid arguments!");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        int rows = Integer.parseInt(args[1]);
        int cols = Integer.parseInt(args[2]);
        int players = Integer.parseInt(args[3]);
        int time = Integer.parseInt(args[4]);
        WAMServer server = new WAMServer(port, rows, cols, players, time);

        //run the server
        server.run();
    }

    /**
     * returns the number of columns for the game
     * @return number of cols
     */
    public int getCols() {
        return cols;
    }

    /**
     * returns the number of rows for the game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * returns the number of players in the game
     * @return number of players
     */
    public int getNum_players() {
        return num_players;
    }

    /**
     * called to update the scores of every player
     */
    public void getScores() {
        String score = "";
        for (WAMPlayer p : this.players) {
            score += p.getScore() + " ";
        }
        for (WAMPlayer p : this.players) {
            p.updateScores(score);
        }
    }

    /**
     * returns the moles
     * @return the list of status
     */
    public status[] getMoles() {
        return moles;
    }

    /**
     * is the game running?
     * @return boolean
     */
    public boolean isFlagThread() {
        return flagThread;
    }

    /**
     * called to set the status of the thread's flag
     * @param b sets the flag thread
     */
    public void setFlagThread(boolean b) {
        this.flagThread = b;
    }

    /**
     * finds the maximum score, and sends the player messages on who won
     */
    private void findWinner () {
        int[] points = new int[num_players];
        int idx = 0;
        for (WAMPlayer p : this.players) {
            points[idx] = p.getScore();
            idx++;
        }
        Arrays.sort(points);
        for (WAMPlayer p: this.players) {
            // if 2 players or more get the same score
            if (num_players > 1 && points[num_players - 1] == p.getScore() &&
                    points[num_players - 1] == points[num_players - 2]) {
                p.gameTied();
            // if the player wins the game
            } else if (points[num_players - 1] == p.getScore()) {
                p.gameWon();
            // if the player is just bad at the game
            } else {
                p.gameLost();
            }
        }
    }

    /**
     * run method starts the server
     */
    @Override
    public void run() {
        int i = 0;
        // creates the list of players and accepts their connections, with handshakes
        try {
            while(i < this.num_players){
                Socket player = this.server.accept();
                this.players[i] = new WAMPlayer(player, this, i);
                this.players[i].connect();
                i++;
            }
        } catch (IOException io) {
            System.err.println(io);
        }

        // creates the mole up and down thread
        MainMoleThread action = new MainMoleThread(this.rows * this.cols, this);

        // creates the time thread
        TimeThread time = new TimeThread(this.time, this);

        // starts the player threads
        for (WAMPlayer p: this.players) {
            p.start();
        }

        // starts the time and action thread
        time.start();
        action.start();

        // waits until the time is up
        while(this.flagThread){
            System.out.print("");
        }

        // finds the winner and sends the conclusion
        findWinner();

        // changes the status of all the moles up
        for (int idx = 0; idx < this.rows * this.cols; i++) {
            moleDOWN(idx);
        }

        // closes all the player sockets
        for(WAMPlayer p : this.players) {
            p.close();
        }

        //closes the server
        try {
            this.server.close();
        } catch (IOException e) {
            System.err.println(e);
        }

    }
}
