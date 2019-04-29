package server;

/**
 * Thread class that controls the timeliness of the game
 *
 * @author Dhaval Shrishrimal
 * @author Sam Chilaka
 */
public class TimeThread extends Thread{

    /**
     * time in milli seconds
     */
    private int time_millis;

    /**
     * server instance
     */
    private WAMServer server;

    /**
     * constructor
     * @param time the time in milli seconds
     * @param server server instance
     */
    public TimeThread(int time, WAMServer server) {
        this.time_millis = time * 1000;
        this.server = server;
    }

    /**
     * the run method
     */
    @Override
    public void run(){
        try {
            sleep(time_millis);
            this.server.setFlagThread(false);
        } catch (InterruptedException io) {
            System.err.println(io);
        }
    }


}
