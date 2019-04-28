package server;

import java.util.Random;

public class MainMoleThread extends Thread {

    private int total;

    private WAMServer server;

    public MainMoleThread(int total, WAMServer server) {
        this.total = total;
        this.server = server;
    }

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
