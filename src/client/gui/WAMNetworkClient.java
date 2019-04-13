package client.gui;

import common.WAMProtocol;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

import static common.WAMProtocol.*;

public class WAMNetworkClient {

    /** Turn on if standard output debug messages are desired. */
    private static final boolean DEBUG = false;

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
    private WAM wam;
    /** sentinel loop used to control the main loop */
    private boolean go;

    /**
     * Accessor that takes multithreaded access into account
     *
     * @return whether it ok to continue or not
     */
    private synchronized boolean goodToGo() {
        return this.go;
    }

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
        wam.error();
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
     * Tell the local user to choose a move. How this is communicated to
     * the user is up to the View (UI).
     */
    private void makeMove() {
        wam.makeMove();
    }


    /**
     * The WAM game server already running, this is the client constructor that blocks and waits
     * for the clients to connect.
     * @param host
     * @param port
     * @param wam
     */
    public WAMNetworkClient(String host, int port, WAM wam) {
        try {
            client = new Socket(host, port);
            networkIn = new Scanner(client.getInputStream());
            networkOut = new PrintStream(client.getOutputStream());
            wam = wam;
            go = true;

            String request = networkIn.next();
            String argument = networkIn.nextLine();
            if (!request.equals(WELCOME)) {
                throw new RuntimeException("Expected WELCOME message from server");
            } WAMNetworkClient.dPrint("Success connecting to server " + client);
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void moveMade( String args ) {
        WAMNetworkClient.dPrint( '!' + WHACK + ',' + args );

        String[] fields = args.trim().split( " " );
        int hole = Integer.parseInt(fields[0]);

        // Update the board model.
        wam.moveMade(hole);
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
    public void sendMove(int hole) {
        this.networkOut.println( WHACK + " " + hole );
    }

}
