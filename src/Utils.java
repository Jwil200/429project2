import java.net.Inet4Address;
import java.util.ArrayList;

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

    // For packaging and parsing the multiple args.
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
