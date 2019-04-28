package server;

import common.WAMProtocol;
import java.net.ServerSocket;

/**
 * The WAMServer class is the server domain for the game. It waits for incoming
 * client connections and pairs them to engage in the game {@link client.gui.WAM WAM games}
 * @author Sam Chilaka
 * @author Dhaval Shrimshal
 */
public class WAMServer implements Runnable, WAMProtocol {

    private ServerSocket server;

    public WAMServer(int port, int rows, int cols, int players, int time) {
        try {
            server = new ServerSocket(port);

        }
    }

    /**
     * Starts a new {@link WAMServer}. Makes the server and calls
     * {@link #run()} in the main thread.
     * @param args used to setup the game parameters which the server uses for
     *             incoming client connections and setting up the game
     */
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java WAMServer <port> <rows> <columns> <#players> <duration>");
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
     *
     */
    @Override
    public void run() {

    }
}
