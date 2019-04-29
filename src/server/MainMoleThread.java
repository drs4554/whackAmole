package server;

/**
 * The main thread class for the moles in the game
 *
 * @author Dhaval Shrishrimal
 * @author Sam Chilaka
 */

import java.util.Random;

public class MainMoleThread extends Thread {

    /**
     * total number of moles
     */
    private int total;

    /**
     * instance of the server
     */
    private WAMServer server;

    /**
     * constructor
     * @param total total number of moles
     * @param server server
     */
    public MainMoleThread(int total, WAMServer server) {
        this.total = total;
        this.server = server;
    }

    /**
     * run method
     */
    @Override
    public void run() {
        while(this.server.isFlagThread()){
            Random r = new Random();
            Random time = new Random();
            int nextMole = r.nextInt(total);
            MoleThread m = new MoleThread(nextMole, this.server);
            m.start();
            try {
                sleep(time.nextInt(1000));
            } catch (InterruptedException i) {
                System.err.print(i);
            }
        }
    }
}
