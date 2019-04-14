package client.gui;

import common.WAMProtocol;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import static common.WAMProtocol.*;

/**
 * The client interface for the whack-a-mole game server. Each player
 * gets its own connection to this class. It represents the controller part
 * of the MVC triumvirate
 *
 * @author Sam Chilaka @<soc5881@rit.edu>
 * @author Dhaval Shrimshal @<drs4554@rit.edu>
 */
public class WAMNetworkClient {

    /** Turn on if standard output debug messages are desired. */
    private static final boolean DEBUG = false;

    public int player;

    /**
     * Print method that does something only if DEBUG is true
     *
     * @param logMsg the message to log
     */
    private static void dPrint( Object logMsg ) {
        if ( WAMNetworkClient.DEBUG ) {
            System.out.println( logMsg );
        }
    }

    /** client socket to communicate with server */
    private Socket client;

    /** used to read requests from the server */
    private Scanner networkIn;

    /** Used to write responses to the server. */
    private PrintStream networkOut;

    /** the model which keeps track of the game */
    public WAM wam;

    /** sentinel loop used to control the main loop */
    private boolean go;

    public int rows, cols;

    /**
     * Multithread-safe mutator
     */
    private synchronized void stop() {
        this.go = false;
    }

    /**
     * Called when the server sends a message saying that
     * gameplay is damaged. Ends the game.
     *
     * @param arguments The error message sent from the reversi.server.
     */
    public void error( String arguments ) {
        WAMNetworkClient.dPrint( '!' + ERROR + ',' + arguments );
        dPrint( "Fatal error: " + arguments );
        wam.error(arguments);
        this.stop();
    }

    /**
     * Called from the GUI when it is ready to start receiving messages
     * from the server.
     */
    public void startListener() {
        new Thread(() -> this.run()).start();
    }


    /**
     * The WAM game server already running, this is the client constructor that blocks and waits
     * for the clients to connect.
     * @param host
     * @param port
     */
    public WAMNetworkClient(String host, int port) {
        try {
            this.client = new Socket(host, port);
            this.networkIn = new Scanner(client.getInputStream());
            this.networkOut = new PrintStream(client.getOutputStream());
            go = true;

//            String request = networkIn.next();
            String[] argument = networkIn.nextLine().split("\\s");
            if (!argument[0].equals(WELCOME)) {
                throw new RuntimeException("Expected WELCOME message from server");
            }

            this.wam = new WAM(Integer.parseInt(argument[1]), Integer.parseInt(argument[2]), Integer.parseInt(argument[4]));
            this.player = Integer.parseInt(argument[4]);
            this.rows = Integer.parseInt(argument[1]);
            this.cols = Integer.parseInt(argument[2]);
            WAMNetworkClient.dPrint("Success connecting to server " + client);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void run() {
        while (go) {
            try {
                String request = networkIn.next();
                String args = networkIn.nextLine().trim();

                switch (request) {
                    case MOLE_UP:
                        wam.moleUP(Integer.parseInt(args));
                        break;
                    case MOLE_DOWN:
                        wam.moleDOWN(Integer.parseInt(args));
                        break;
                    case SCORE:
                        wam.updateScore(args);
                        break;
                    case GAME_WON:
                        gameWon();
                        go = false;
                        break;
                    case GAME_LOST:
                        gameLost();
                        go = false;
                        break;
                    case GAME_TIED:
                        gameTied();
                        go = false;
                        break;
                    case ERROR:
                        error(args);
                        go = false;
                        break;
                    default:
                        break;
                }
            } catch (NoSuchElementException err) {
                this.stop();
            } catch (Exception e) {
                System.err.println(e);
                this.stop();
            }
        }
        this.close();
    }

    /**
     * Called when the server sends a message saying that the
     * board has been won by this player. Ends the game.
     */
    public void gameWon() {
        WAMNetworkClient.dPrint( '!' + GAME_WON );
        dPrint( "You won! Yay!" );
        wam.gameWon();
        this.stop();
    }

    /**
     * Called when the server sends a message saying that the
     * game has been won by the other player. Ends the game.
     */
    public void gameLost() {
        WAMNetworkClient.dPrint( '!' + GAME_LOST );
        dPrint( "You lost! Boo!" );
        wam.gameLost();
        this.stop();
    }

    /**
     * Called when the server sends a message saying that the
     * game is a tie. Ends the game.
     */
    public void gameTied() {
        WAMNetworkClient.dPrint( '!' + GAME_TIED );
        dPrint( "You tied! Meh!" );
        wam.gameTied();
        this.stop();
    }

    /**
     * This method should be called at the end of the game to
     * close the client connection.
     */
    public void close() {
        try {
            this.client.close();
        }
        catch( IOException ioe ) {
            // squash
        }
        wam.close();
    }

    /**
     * UI wants to send a new move to the server.
     *
     * @param hole the hole that was whacked
     */
    public void sendMove(int hole, int player) {
        this.networkOut.println( WHACK + " " + hole + " " + player);
    }

}
