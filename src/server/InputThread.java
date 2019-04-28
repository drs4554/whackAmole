package server;

public class InputThread {

    public enum status {
        UP,
        DOWN;
    }

    private status[] moles;

    public InputThread(int total) {
        this.moles = new status[total];

    }
}
