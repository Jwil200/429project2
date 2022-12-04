import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {
    public static String ip () {
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		}
		catch (Exception e) {
			return "127.0.0.0"; // On a failure to get post local host.
		}
	}

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

    // Reading / making messages

    public static byte[] appendToArray (byte[] target, byte[] arr, int start, int numAppend) {
        for (int i = start; i < start + numAppend; i++) {
            target[i] = arr[i - start];
        }
        return target;
    }

    public static byte[] encodeTable (HashMap<Node, Integer> routingTable) {
        // Primary node has cost 0.
        Node primary = null;
        byte[] message = new byte[Main.MESSAGE_LENGTH];
        int i = 0;
        for (Node n: routingTable.keySet()) {
            int start = 8 + 12 * i;
            if (routingTable.get(n) == 0) {
                primary = n;
                continue;
            }
            appendToArray(message, ipToBytes(n.getAddress()), start, 4);            // IP
            appendToArray(message, intToBytes(n.getPort()), start + 4, 2);          // Port
            appendToArray(message, new byte[2], start + 6, 2);                      // Blank
            appendToArray(message, intToBytes(n.getID()), start + 8, 2);            // ID
            appendToArray(message, intToBytes(routingTable.get(n)), start + 10, 2); // Cost
            i++;
        }
        appendToArray(message, intToBytes(routingTable.size() - 1), 0, 2);
        appendToArray(message, intToBytes(primary.getPort()), 2, 2);
        appendToArray(message, ipToBytes(primary.getAddress()), 4, 4);
        return message;
    }

    // For packaging and parsing the multiple args.
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
}
