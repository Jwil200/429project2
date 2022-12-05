import java.net.Inet4Address;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.Socket;
/**
 * 
 * Public Class of all the utilities that the other functions will call to get socket information
 */
public class Utils {
    /**
     * Returns the ip address that the computer uses to establish connectivity to the distance vector table.
     * 
     * @return  the localhost ip address, or if there is an exception will return loopback ip
     */
    public static String ip () {
		try {
			//return Inet4Address.getLocalHost().getHostAddress();
			Socket temp = new Socket("192.168.1.1", 80);
            		return temp.getLocalAddress().getHostAddress();   
		}
		catch (Exception e) {
			return "127.0.0.0"; // On a failure to get post local host.
		}
	}
    /**
     * Reads the array for the node information 
     * @param nodelist searches the arraylist of nodes and returns a node's information
     * @param id if there is an id that matches the node we are looking for 
     */
    public static Node getNode (ArrayList<Node> nodeList, int id) {
        for (Node n: nodeList) {
            if (n.getID() == id) return n;
        }
        return null;
    }
    /**'
     * encodes the messages with the route table information, 
     * returning the message with the port and address that is in the route table
     * @param routingTable a hashmap that shows the route table's information of the IP address and Port number for the node
     */
    public static String encodeTable (HashMap<Node, Integer> routingTable) {
        // Primary node has cost 0.
        Node primary = null;
        String message = "";
        for (Node n: routingTable.keySet()) {
            if (routingTable.get(n) == 0) {
                primary = n;
                continue;
            }
            message += n.getAddress() + n.getPort() + "0x0" + n.getID() + routingTable.get(n);
        }
        return (routingTable.size() - 1) + primary.getPort() + primary.getAddress() + message;
    }

    /**
     * Args data class that looks at the arguments for the filename 
     */
    public static class ArgsData {
        public String fileName;
        public int interval;

        public static ArgsData parseArgs (String[] args) throws Exception {
            ArgsData a = new ArgsData();

            // Search for fileName and interval
            for (int i = 0; i < args.length - 1; i++) {
                switch (args[i]) {
                    case "-t":
                        a.fileName = args[i + 1];
                        break;
                    case "-i":
                        a.interval = Integer.parseInt(args[i + 1]);
                        break;
                    default:
                        // None
                }
            }

            return a;
        }
    }
}
