/**
 * Updates the timer for the nodes whenever there is a change in what the timer is for 
 * the specific nodes 
 */
public class UpdateTimer extends Thread {
    private boolean running;
    private int time;
    /**
     * Function to update the timer for the list of nodes with the new time
     * with the updated time the node starts the timer
     * @param nodeList all the nodes that are associated to the route table
     * @param time the seconds in intervals that is being updated
     */
    public UpdateTimer (int time) {
        this.time = time;
        running = true;
        this.start();
    }
    /**
     * runs the timer and increasing elapse to eventually be less than time
     * when elapse is less than the time there is an update that occurs
     */
    public void run () {
        int elapse = 0;
        while (running) {
            while (elapse < time) {
                try {
                    Thread.sleep(1);
                    elapse++;
                } catch (Exception e) {}
            }
            if (running) {
                Distance_Vector_Routing.step();
                System.out.print("Periodic updates sent.\n>>> ");
                elapse = 0;
            }
        }
    }
    /**
     * closes the session
     */
    public void close () {
        running = false;
    }
}
