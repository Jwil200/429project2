import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerHandler extends Thread {

    private DatagramSocket socket;
    private boolean running;

    public ServerHandler (int PORT) throws Exception {
        socket = new DatagramSocket(PORT);
        running = true;

        this.start();
    }

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
                    map = Utils.decodeTable(p.getData()); // I wish tuples were real
                }
                catch (Exception e) { continue; }

                // This handles updating the table based on the recieved routing table.
                HashMap<Node, Integer> routingTable = Main.getRoutingTable();
                Node sourceNode = null;
                for (Node n: map.keySet()) { // The source is the node with cost 0.
                    if (map.get(n) == 0) {
                        sourceNode = n;
                        break;
                    } 
                }
                System.out.print("\nRECEIVED A MESSAGE FROM SERVER " + sourceNode.getID() + "\n> ");
                if (!sourceNode.getEnabled()) continue;
                sourceNode.restart();
                Main.incrementPackets();

                int sourceCost = routingTable.get(sourceNode); // Check if matching
                
                for (Node n: map.keySet()) {
                    if (Main.getPrimary().getID() == n.getID()) {
                        if (map.get(n) != sourceCost) {
                            sourceCost = map.get(n);
                            routingTable.put(n, sourceCost);
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

    public void send (byte[] msg, String address, int port) {
        try {
            DatagramPacket p = new DatagramPacket(msg, msg.length, InetAddress.getByName(address), port);
            socket.send(p);
        }
        catch (Exception e) { System.out.println("Issue sending message."); }
    }

    public void close () {
        running = false;
    }
}
