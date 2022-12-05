/** 
 * Class for associating the node to the neccessary information for the server 
 * Defines the values for ip address and port number along with the getting the timer information 
 * on how long each interval will take before updating the routing table
 */
public class Node {
    private String address;
    private int id, port; // Cost of -1 means INF.
    private Node next;
    private boolean enabled;

    private AwaitTimer t;
    private boolean running;
    private int time;
    /**
     * creates the node with the pulled information for id, ip address, and port number
     * @param id value given to the node number; 0 for local host and 1 + is the remote host 
     * @param address ip address associated to the node 
     * @param port port number associated to the node to form the udp connectivity 
     */
    public Node (int id, String address, int port) {
        this(id, address, port, null);
    }

    public Node (int id, String address, int port, Node next) {
        this.id = id;
        this.address = address;
        this.port = port;
        this.next = next;

        running = false;
        enabled = true;
    }
    /** 
     * returns the id for the node 
     */
    public int getID () {
        return id;
    }
    /**
     * prepared the next object for the following node 
     */
    public void setNext (Node next) {
        this.next = next;
    }
    /**
     * returns the next node following the localhost
     */
    public Node getNext () {
        return next;
    }
    /**
     * returns the ip address for the node
     */
    public String getAddress () {
        return address;
    }
    /**
     * returns the port number for the udp connectivity for the node 
     */
    public int getPort () {
        return port;
    }
    /**
     * returns the string that identifies the node's id with the ip address and port number 
     */
    public String toString () {
        return "ID: " + id + " IP: " + address + " PORT: " + port;
    }
    /**
     * returns enabled for the the node that is available to receive packets and route updates 
     */
    public boolean getEnabled () {
        return enabled;
    }
    /**
     * stops the node from recieving routes and packets essentially showing it as offline or disconnected
     */
    public void stop () {
        enabled = false;
        running = false;
    }
    /**
     * sets the new timer to begin within intervals
     */
    public void start (int time) {
        if (running) return;
        running = true;
        this.time = time;
        t = new AwaitTimer(this, time);
    }
    /**
     * has the node's connection re-establish and restarts the timer for that node
     */
    public void restart () {
        if (!running) return;
        t.close(); // Close and make a new one.
        t = new AwaitTimer(this, time);
    }
}
