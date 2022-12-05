import java.util.ArrayList;

public class UpdateTimer extends Thread {
    private boolean running;
    private ArrayList<Node> nodeList;
    private int time;

    public UpdateTimer (ArrayList<Node> nodeList, int time) {
        this.nodeList = nodeList;
        this.time = time;
        running = true;

        this.start();
    }

    public void run () {
        int elapse = 0;
        while (running) {
            while (elapse < time) {
                try {
                    Thread.sleep(1);
                    elapse++;
                } catch (Exception e) {}
            }
            for (Node n: nodeList) {
                // TO-DO
            }
            System.out.println("Periodic updates sent.\n>");
        }
    }

    public void close () {
        running = false;
    }
}
