import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
/** 
* public class to handle the server connection between the localhost and the remote host
* this controls the connection of the sockets for each host 
*/
public class ServerHandler extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private UpdateTimer update;

    /** 
     * checks the port allocation for the socket of the host connections
     * sets the session to running because the socket is defined
     * @param port the port allocation for the host device
     */
    public ServerHandler (int PORT, int interval) throws Exception {
        socket = new DatagramSocket(PORT);
        running = true;
        update = new UpdateTimer(interval);

        this.start();
    }
    /**
     * establishes the socket with the ip address and port number associated to the host device
     * associates the connected devices to the route table with the cost of the route
     * increments the packets that are received from the remote host 
     * checks the route table for the cost to get from one host to the other
     * and also finds if there are nodes that have become unreachable 
     */
    public void run () {
        byte[] buffer = new byte[256];

        while (running) {
            DatagramPacket p = new DatagramPacket(buffer, buffer.length);
            try {
                socket.receive(p);

                InetAddress address = p.getAddress();
                int port = p.getPort();
                p = new DatagramPacket(buffer, buffer.length, address, port);
                //String recieved = new String(p.getData(), 0, p.getLength());

                HashMap<Node, Integer> map = null;
                try {
                    map = Utils.decodeTable(p.getData()); 
                }
                catch (Exception e) { continue; }

                // This handles updating the table based on the recieved routing table.
                HashMap<Node, Integer> routingTable = distance_vector_routing.getRoutingTable();
                Node sourceNode = null;
                for (Node n: map.keySet()) { // The source is the node with cost 0.
                    if (map.get(n) == 0) {
                        sourceNode = n;
                        break;
                    } 
                }
                if (!sourceNode.getEnabled()) continue; // Ignore anything from a disabled server
                System.out.print("\nRECEIVED A MESSAGE FROM SERVER " + sourceNode.getID() + "\n>>> ");
                sourceNode.restart();
                distance_vector_routing.incrementPackets();

                int sourceCost = routingTable.get(sourceNode); // Check if matching
                
                for (Node n: map.keySet()) {
                    if (distance_vector_routing.getPrimary().getID() == n.getID()) {
                        if (map.get(n) != sourceCost) {
                            sourceCost = map.get(n);
                            routingTable.put(sourceNode, sourceCost);
                            break;
                        }
                    }
                }

                for (Node n: map.keySet()) {
                    int currCost = routingTable.get(n); // What does it currently take
                    int sourceToNodeCost = map.get(n);
                    if (sourceToNodeCost == -1) continue; // Node unreachable
                    int potentialCost = sourceCost + sourceToNodeCost; // What would it take through this node
                    // At this point we know its reachable through source, so either currently we think we can't reach it
                    // or its easier to through the source
                    if ((currCost == -1) || (currCost > potentialCost)) {
                        routingTable.put(n, potentialCost); // Update cost and next hop
                        n.setNext(sourceNode);
                    }
                }

                
            }
            catch (Exception e) {}                  
        }
    }
    /** 
     * sends the necessary packets from one host using the ip address and ports associated to the other host 
     */
    public void send (byte[] msg, String address, int port) {
        try {
            DatagramPacket p = new DatagramPacket(msg, msg.length, InetAddress.getByName(address), port);
            socket.send(p);
        }
        catch (Exception e) { System.out.println("Issue sending message."); }
    }
    /**
     * closes the session 
     */
    public void close () {
        running = false;
        update.close();
    }
}
