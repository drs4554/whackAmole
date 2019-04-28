package server;

public class TimeThread extends Thread{

    private int time_millis;

    private WAMServer server;

    public TimeThread(int time, WAMServer server) {
        this.time_millis = time * 1000;
        this.server = server;
    }

    @Override
    public void run(){
        try {
            sleep(time_millis);
            this.server.serFlagThread(false);
        } catch (InterruptedException io) {
            System.err.println(io);
        }
    }


}
