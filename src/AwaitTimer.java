public class AwaitTimer extends Thread {
    private boolean running;
    private Node c;
    private int time;

    public AwaitTimer (Node c, int time) {
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
            //c.setCost(-1); Should set cost to -1 (inf) here
            Main.getRoutingTable().put(c, -1);
        }
    }

    public void close () {
        running = false;
    }
}
