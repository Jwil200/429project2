public class Timer extends Thread {
    private boolean running;
    private Node c;
    private int time;

    public Timer (Node c, int time) {
        this.c = c;
        this.time = time * 3;
        running = true;

        this.start();
    }

    public void run () {
        int elapse = 0;
        while (running && (elapse < time)) {
            try {
                Thread.sleep(1);
                elapse++;
            } catch (Exception e) {}
        }
        if (running) {
            c.stop();
            c.setCost(-1);
        }
    }

    public void close () {
        running = false;
    }
}
