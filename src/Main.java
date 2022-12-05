import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    
    public static final int MESSAGE_LENGTH = 256;

    private static ServerHandler server;
    private static Node primary; // We determine primary based on edges defined (assuming first is the ID of this computer)
    private static ArrayList<Node> nodeList;
    private static HashMap<Node, Integer> routingTable;
    private static int numServers;

    public static ArrayList<Node> getNodeList () {
        return nodeList;
    }

    public static HashMap<Node, Integer> getRoutingTable () {
        return routingTable;
    }

    // Required
    public static void update (int id1, int id2, int cost) {
        // TO-DO
        // Use step to send out update
    }

    // Required
    public static void step () {
        // TO-DO
        // Manually call update.
        for (Node n: nodeList) {
            if (n.getID() == primary.getID()) continue;
            server.send(Utils.encodeTable(routingTable), n.getAddress(), n.getPort());
        }
    }

    // Required
    public static void packets () {
        // TO-DO
        // Print packets and then reset value.
    }

    // Required
    public static void display () {
        if (nodeList.isEmpty())
			System.out.println("No peers connected.");
		else {
			System.out.printf("%-12s%-10s%-6s%n", "Source ID", "Next ID", "Cost");
            for (Node n: routingTable.keySet()) {
                Node next = n.getNext();
                String nextID = "-";
                if (next != null) nextID = "" + next.getID();
                System.out.printf("%-12d%-10s%-6s%n", n.getID(), nextID, (routingTable.get(n) == -1 ? "inf" : routingTable.get(n)));
            }
		}
    }

    // Required
    public static void disable (int id) {
        // TO-DO
        Node n = Utils.getNode(nodeList, id);
        n.stop();

        // Check if it is a neighbor.
    }

    // Required
    public static void crash () {
        // TO-DO
        for (Node n: nodeList)
            n.stop();
        server.close();
    }

    public static void main (String[] args) {
        String fileName = "../topology/";
        int interval = 1000;
        Utils.ArgsData a = null;
        try {
            a = Utils.ArgsData.parseArgs(args);
            fileName += a.fileName;
            interval *= a.interval;
        }
        catch (Exception e) {
            fileName += "topology.txt";
            interval *= 120; // 2 min
        }
        
        File f = null;
        Scanner s = null;
        try {
            f = new File(fileName);
            s = new Scanner(f);
        }
        catch (Exception e) { e.printStackTrace(); }

        nodeList = new ArrayList<Node>();
        routingTable = new HashMap<Node, Integer>();

        numServers = s.nextInt();
        s.nextLine();
        int numEdges = s.nextInt();
        s.nextLine();
        for (int i = 0; i < numServers; i++) {
            String[] l = s.nextLine().split(" ");
            Node newNode = new Node(Integer.parseInt(l[0]), l[1], Integer.parseInt(l[2]));
            nodeList.add(newNode);
            routingTable.put(newNode, -1);
        }
        for (int i = 0; i < numEdges; i++) {
            String[] l = s.nextLine().split(" ");
            if (i == 0) { // Check the first edge and set the primary to the first ID in the edge.
                primary = Utils.getNode(nodeList, Integer.parseInt(l[0]));
                primary.setNext(primary);
            }
            Node n = Utils.getNode(nodeList, Integer.parseInt(l[1]));
            routingTable.put(n, Integer.parseInt(l[2]));
            n.setNext(n); // If node is a neighbor then its next hop starts as itself.
        }

        routingTable.put(primary, 0); // Node repesenting this machine should be 0.

        if (!Utils.ip().equals(primary.getAddress())) {
            System.err.println("IP Mismatch. Check your topology file.\nGiven Address: " + primary.getAddress() + "\nExpected Address: " + Utils.ip());
            System.exit(0);
        }

        s.close();

        for (Node n: nodeList) {
            if (n.getID() == primary.getID()) continue;
            n.start(interval);
        }

        try {
            server = new ServerHandler(primary.getPort());
        }
        catch (Exception e) { System.exit(0); }
        
        s = new Scanner(System.in);

        Utils.printMap(Utils.decodeTable(Utils.encodeTable(routingTable)));

        String input = "";

        while (!input.equals("crash")) {
            System.out.print("> ");
            input = s.nextLine();
            args = input.split(" ");
            switch (args[0]) {
                case "update":
                    update(Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
                    break;
                case "step":
                    step();
                    break;
                case "packets":
                    packets();
                    break;
                case "display":
                    display();
                    break;
                case "disable":
                    disable(Integer.parseInt(args[1]));
                    break;
                case "crash":
                    crash();
                    break;
                default:
                    System.err.println("Invalid input.");
            }
        }
        
        s.close();
        System.exit(0);
    }
}