package server;

import common.WAMProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * The WAMServer class is the server domain for the game. It waits for incoming
 * client connections and pairs them to engage in the game {@link client.gui.WAM WAM games}
 * @author Sam Chilaka
 * @author Dhaval Shrimshal
 */
public class WAMServer implements Runnable, WAMProtocol {

    private ServerSocket server;

    private int rows;

    private int cols;

    private int num_players;

    private int time;

    private WAMPlayer[] players;

    private boolean flagThread;

    public enum status {
        UP,
        DOWN
    }

    private status[] moles;

    /**
     * called on a specific position to pop a mole up for the clients
     * @param num
     */
    public void moleUP(int num) {
        this.moles[num] = status.UP;
        for (int i = 0; i < this.num_players; i++) {
            this.players[i].moleUP(num);
        }
    }

    /**
     * called on a specific position to indicate the mole is down for the clients
     * @param num
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
     * @param port
     * @param rows
     * @param cols
     * @param players
     * @param time
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
        server.run();
    }

    /**
     * returns the number of columns for the game
     * @return
     */
    public int getCols() {
        return cols;
    }

    /**
     * returns the number of rows for the game
     * @return
     */
    public int getRows() {
        return rows;
    }

    /**
     * returns the number of players in the game
     * @return
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
     * @return
     */
    public status[] getMoles() {
        return moles;
    }

    /**
     * is this is a flag or not
     * @return
     */
    public boolean isFlagThread() {
        return flagThread;
    }

    /**
     *
     * @param b
     */
    public void setflagThread(boolean b) {
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
            if (num_players > 1 && points[num_players - 1] == p.getScore() &&
                    points[num_players - 1] == points[num_players - 2]) {
                p.gameTied();
            } else if (points[num_players - 1] == p.getScore()) {
                p.gameWon();
            } else {
                p.gameLost();
            }
        }
    }

    /**
     *
     */
    @Override
    public void run() {
        int i = 0;
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

        MainMoleThread action = new MainMoleThread(this.rows * this.cols, this);
        TimeThread time = new TimeThread(this.time, this);

        for (WAMPlayer p: this.players) {
            p.start();
        }

        time.start();
        action.start();

        while(this.flagThread){
            System.out.print("");
        }

        findWinner();

        for (int idx = 0; idx < this.rows * this.cols; i++) {
            moleDOWN(idx);
        }

        for(WAMPlayer p : this.players) {
            p.close();
        }

        try {
            this.server.close();
        } catch (IOException e) {
            System.err.println(e);
        }

    }
}
