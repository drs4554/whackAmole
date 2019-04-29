package server;

/**
 * The helper thread class to run the moles of the game
 *
 * @author Dhaval Shrishrimal
 * @author Sam Chilaka
 */
public class MoleThread extends Thread{

    /**
     * number of mole
     */
    private int mole;

    /**
     * difficulty
     */
    private static int difficulty = 0;

    /**
     * server
     */
    private WAMServer server;

    /**
     * constructor
     * @param mole the number of mole
     * @param server server
     */
    public MoleThread(int mole, WAMServer server) {
        this.mole = mole;
        this.server = server;
    }

    /**
     * run method
     */
    @Override
    public void run() {
        try {
            this.server.moleUP(this.mole);
            sleep(3000 - (difficulty * 10));
            this.server.moleDOWN(this.mole);
            difficulty += 1;
        }
        catch (InterruptedException io) {
            System.err.print(io);
        }
    }
}
