import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
			return Inet4Address.getLocalHost().getHostAddress();
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

    // Utils for sending/recieving messages

    public static byte[] intToBytes (int num) {
        byte[] bytes = new byte[2];
        bytes[0] = (byte) (num >> 8);
        bytes[1] = (byte) (num);
        return bytes;
    }

    public static int bytesToInt (byte[] bytes) {
        return ((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff);
    }

    public static byte[] ipToBytes (String ip) {
        try {
            return InetAddress.getByName(ip).getAddress();
        }
        catch (Exception e) { return new byte[4]; }
    }

    public static String bytesToIP (byte[] bytes) {
        String address = "";
        for (byte b : bytes) {
            address += (b & 0xFF) + ".";
        }
        return address.substring(0, address.length() - 1);
    }

    // Reading / making messages

    public static byte[] appendToArray (byte[] target, byte[] arr, int start, int numAppend) {
        for (int i = start; i < start + numAppend; i++) {
            target[i] = arr[i - start];
        }
        return target;
    }

    /**'
     * encodes the messages with the route table information, 
     * returning the message with the port and address that is in the route table
     * @param routingTable a hashmap that shows the route table's information of the IP address and Port number for the node
     */
    public static byte[] encodeTable (HashMap<Node, Integer> routingTable) {
        // Primary node has cost 0.
        Node primary = null;
        byte[] message = new byte[Main.MESSAGE_LENGTH];
        int i = 0;
        for (Node n: routingTable.keySet()) {
            int start = 8 + 12 * i;
            if (routingTable.get(n) == 0) {
                primary = n;
            }
            appendToArray(message, ipToBytes(n.getAddress()), start, 4);            // IP
            appendToArray(message, intToBytes(n.getPort()), start + 4, 2);          // Port
            appendToArray(message, new byte[2], start + 6, 2);                      // Blank
            appendToArray(message, intToBytes(n.getID()), start + 8, 2);            // ID
            appendToArray(message, intToBytes(routingTable.get(n)), start + 10, 2); // Cost
            i++;
        }
        appendToArray(message, intToBytes(routingTable.size()), 0, 2);
        appendToArray(message, intToBytes(primary.getPort()), 2, 2);
        appendToArray(message, ipToBytes(primary.getAddress()), 4, 4);
        return message;
    }

    public static HashMap<Node, Integer> decodeTable (byte[] message) {
        HashMap<Node, Integer> table = new HashMap<Node, Integer>();
        Main.getNodeList();
        int numNodes = bytesToInt(Arrays.copyOfRange(message, 0, 2));
        int sourcePort = bytesToInt(Arrays.copyOfRange(message, 0, 2)); // Necessary?
        String sourceIP = bytesToIP(Arrays.copyOfRange(message, 4, 8));
        for (int i = 0; i < numNodes; i++) {
            int start = 8 + 12 * i;
            int nodeID = bytesToInt(Arrays.copyOfRange(message, start + 8, start + 10));
            Node n = Utils.getNode(Main.getNodeList(), nodeID);
            int nodeCost = bytesToInt(Arrays.copyOfRange(message, start + 10, start + 12));
            table.put(n, (nodeCost == 65535 ? -1 : nodeCost));
        }
        return table;
    }

    /**
     * Args data class that looks at the arguments for the filename 
     */
    public static class ArgsData {
        public String fileName;
        public int interval;

        public static ArgsData parseArgs (String[] args) throws Exception {
            ArgsData a = new ArgsData();
            if (args.length == 0) throw new Exception();

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

    // For Testing
    public static void printMap (HashMap<Node, Integer> map) {
        for (Node n: map.keySet()) {
            System.out.println(n.getID() + " " + (n.getNext() == null ? "-" : n.getNext().getID()) + " " + (map.get(n) == -1 ? "inf" : map.get(n)));
        }
    }
}
