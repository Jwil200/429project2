/**
 * Class method to determine when to update requirements based on the time 
 * After an allocated time an update will occur on the routing table to change 
 * the variables that we define to update.
 */
public class AwaitTimer extends Thread {
    private boolean running;
    private Node c;
    private int time;
    /**
     * sets the timer to the node and starts the timer. 
     * @param c the node associated to the timer 
     * @param time allocated time in seconds for 3 intervals  
     */
    public AwaitTimer (Node c, int time) {
        this.c = c;
        this.time = time * 3;
        running = true;

        this.start();
    }
    /**
     * while running the timer will elapse until reaches max timer 
     * when timer is running update the routing table based on the node
     */
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
    /**
     * changes the boolean for running to false if the session is closed
     */
    public void close () {
        running = false;
    }
}
