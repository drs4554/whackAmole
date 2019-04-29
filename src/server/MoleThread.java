package server;

/**
 * The helper thread class to run the moles of the game
 *
 * @author Dhaval Shrishrimal
 * @author Sam Chilaka
 */
public class MoleThread extends Thread{

    private int mole;

    private static int difficulty = 0;

    private WAMServer server;

    public MoleThread(int mole, WAMServer server) {
        this.mole = mole;
        this.server = server;
    }

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
