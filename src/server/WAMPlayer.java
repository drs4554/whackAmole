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
public class WAMPlayer implements WAMProtocol, Closeable {

    private Socket socket;
    private Scanner scanner;
    private PrintStream printer;

    /**
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

    public void connect() { printer.println(WELCOME);}

}
