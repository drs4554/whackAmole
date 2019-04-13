package client.gui;

import common.WAMProtocol;

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
        wam.error( arguments );
        this.stop();
    }

    public WAMNetworkClient(String host, int port, WAM wam) {
        try {
            client = new Socket(host, port);
            networkIn = new Scanner(client.getInputStream());
            networkOut = new PrintStream(client.getOutputStream());
            wam = wam;
            go = true;

            String request = networkIn.next();
            String args = networkIn.nextLine();
            if (!request.equals(WAMProtocol.)) {

            }
        }
    }
}
