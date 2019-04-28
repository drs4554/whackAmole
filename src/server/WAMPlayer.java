package server;

import common.WAMProtocol;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * A class that manages the requests and responses to a single client
 * @author Sam Chilaka
 * @author Dhaval Shrimshal
 */
public class WAMPlayer extends Thread implements WAMProtocol, Closeable  {

    private Socket socket;
    private Scanner scanner;
    private PrintStream printer;


    /**
     * Creates a new {@link WAMPlayer} that will be used to the {@link Socket} to
     * communicate with the client/player.
     *
     * @param socket
     * @throws IOException
     */
    public WAMPlayer(Socket socket) throws IOException {
        this.socket = socket;
        try {
            scanner = new Scanner(socket.getInputStream());
            printer = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new IOException(e);
        }
    }


    /**
     * Sends the initial {@link //CONNECT} request to the client
     */
    public void connect() { printer.println(WELCOME); }


    /**
     * Called to send an {@link #ERROR} to the client. This is called if either
     * client has invalidated themselves with a bad response.
     *
     * @param message error message
     */
    public void error(String message) {
        printer.println(ERROR + " " + message);
    }


    /**
     * Called to send a {@link #GAME_WON} message to the client because the player's
     * most recent move won the game
     */
    public void gameWon() {
        printer.println(GAME_WON);
    }


    /**
     * Called to send a {@link #GAME_LOST} message to the client because the player's
     * most recent move won the game
     */
    public void gameLost() {
        printer.println(GAME_LOST);
    }


    /**
     * Called to send a {@link #GAME_TIED} message to the client because the player's
     * most recent move won the game
     */
    public void gameTied() {
        printer.println(GAME_TIED);
    }


    /**
     * Called to close the clients when the game is over
     */
    @Override
    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
             System.err.println(e);
        }
    }
}
