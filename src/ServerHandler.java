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
                System.out.println(address + " " + port);
                System.out.println();
                String recieved = new String(p.getData(), 0, p.getLength());

                HashMap<Node, Integer> map = null;
                try {
                    map = Utils.decodeTable(p.getData()); // I wish tuples were real
                }
                catch (Exception e) { continue; }

                Utils.printMap(map);

                /* 
                HashMap<Node, Integer> routingTable = Main.getRoutingTable();
                Node sourceNode = null;
                for (Node n: routingTable.keySet()) { // We find the source by finding where it isn't.
                    if (!map.containsKey(n)) {
                        sourceNode = n;
                        break;
                    }
                }
                for (Node n: map.keySet()) {
                    if (routingTable.get(n) > map.get(n) + routingTable.get(sourceNode)) {
                        routingTable.put(n, map.get(n) + routingTable.get(sourceNode));
                    }
                }
                */

                
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
