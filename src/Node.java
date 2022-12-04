public class Node {
    private String address;
    private int id, port, cost; // Cost of -1 means INF.
    private Node next;
    private boolean enabled;

    private AwaitTimer t;
    private boolean running;
    private int time;

    public Node (int id, String address, int port) {
        this(id, address, port, -1, null);
    }

    public Node (int id, String address, int port, int cost, Node next) {
        this.id = id;
        this.address = address;
        this.port = port;
        this.cost = cost;
        this.next = next;

        running = false;
    }

    public void setCost (int cost) {
        this.cost = cost;
    }

    public int getCost () {
        return cost;
    }

    public int getID () {
        return id;
    }

    public void setNext (Node next) {
        this.next = next;
    }

    public Node getNext () {
        return next;
    }

    public String getAddress () {
        return address;
    }

    public int getPort () {
        return port;
    }

    public String toString () {
        return "ID: " + id + " IP: " + address + " PORT: " + port + " COST: " + cost;
    }  

    // Update Manager

    public void stop () {
        running = false;
    }

    public void start (int time) {
        if (running) return;
        running = true;
        this.time = time;
        t = new AwaitTimer(this, time);
    }

    public void restart () {
        if (!running) return;
        t.close(); // Close and make a new one.
        t = new AwaitTimer(this, time);
    }
}