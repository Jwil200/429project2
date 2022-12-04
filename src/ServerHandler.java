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
                System.out.println();
                String recieved = new String(p.getData(), 0, p.getLength());

                switch (recieved) {
                    case "update": // Recieved an update from a node.
                        Node n = null; // Get Node somehow. We know its a neighbor. Node that is trying to update.
                        HashMap<Integer, Integer> nodeCost = null; // Somehow get this from the message (Node ID, Cost)
                        ArrayList<Node> nodeList = null; // Need to get this from main.
                        for (Integer id: nodeCost.keySet()) {
                            Node n2 = Utils.getNode(nodeList, id);
                            if (n2.getCost() > (n.getCost() + nodeCost.get(id)))
                                n2.setCost(n.getCost() + nodeCost.get(id));
                        }
                        break;
                    default:
                        System.out.println(recieved);
                }
            }
            catch (Exception e) {}
        }
    }

    public void send (String msg, String address, int port) {
        byte[] buffer = msg.getBytes();
        try {
            DatagramPacket p = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(address), port);
            socket.send(p);
        }
        catch (Exception e) { System.out.println("Issue sending message."); }
    }

    public void close () {
        running = false;
    }
}
