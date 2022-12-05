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

    private static int packets;

    public static void incrementPackets () {
        packets++;
    }

    public static ArrayList<Node> getNodeList () {
        return nodeList;
    }

    public static HashMap<Node, Integer> getRoutingTable () {
        return routingTable;
    }

    public static Node getPrimary () {
        return primary;
    }

    // Required
    public static void update (int id1, int id2, int cost) {
        // Use step to send out update

        if (cost < -1 || cost == 0) {
            System.out.println("update INVALID COST, MUST BE -1 OR GREATER THAN 1");
            return;
        }

        Node n1 = Utils.getNode(nodeList, id1);
        Node n2 = Utils.getNode(nodeList, id2);
        
        if (n1 == null || n2 == null) {
            System.out.println("update INVALID ID");
            return;
        }
        
        if (primary.getID() == id1) {
            // Send to id2 only
            routingTable.put(n2, cost);
            server.send(Utils.encodeTable(routingTable), n2.getAddress(), n2.getPort());
        }
        else if (primary.getID() == id2) {
            routingTable.put(n1, cost);
            server.send(Utils.encodeTable(routingTable), n1.getAddress(), n1.getPort());
        }
        else {
            // Both are different
            // We setup fake routing tables to update just one connection.
            HashMap<Node, Integer> f1Route = new HashMap<Node, Integer>();
            HashMap<Node, Integer> f2Route = new HashMap<Node, Integer>();
            Node tempNode1 = new Node(id1, n1.getAddress(), n1.getPort());
            tempNode1.setNext(tempNode1);
            Node tempNode2 = new Node(id2, n2.getAddress(), n2.getPort());
            tempNode2.setNext(tempNode2);
            f1Route.put(tempNode1, cost);
            f1Route.put(tempNode2, 0);
            f2Route.put(tempNode2, cost);
            f2Route.put(tempNode1, 0);
            server.send(Utils.encodeTable(f1Route), n1.getAddress(), n1.getPort());
            server.send(Utils.encodeTable(f2Route), n2.getAddress(), n2.getPort());
        }

        System.out.println("UPDATE SUCCESS");
    }

    // Required
    public static void step () {
        // Manually call update.
        for (Node n: nodeList) {
            if (n.getID() == primary.getID()) continue;
            if (n.getEnabled()) server.send(Utils.encodeTable(routingTable), n.getAddress(), n.getPort());
        }

        System.out.println("STEP SUCCESS");
    }

    // Required
    public static void packets () {
        // Print packets and then reset value.
        System.out.println("PACKETS SINCE LAST CALL: " + packets);
        packets = 0;

        System.out.println("PACKETS SUCCESS");
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

        System.out.println("DISPLAY SUCCESS");
    }

    // Required
    public static void disable (int id) {
        Node n = Utils.getNode(nodeList, id);
        if (n == null) {
            System.out.println("disable INVALID ID GIVEN");
            return;
        }
        n.stop();

        System.out.println("DISABLE SUCCESS");
    }

    // Required
    public static void crash () {
        // TO-DO
        for (Node n: nodeList)
            n.stop();
        server.close();

        System.out.println("CRASH SUCCESS");
    }

    public static void main (String[] args) {
        packets = 0;
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
            server = new ServerHandler(primary.getPort(), interval);
        }
        catch (Exception e) { System.exit(0); }
        
        s = new Scanner(System.in);

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
                    System.err.println("Invalid input/command.");
            }
            
        }
        
        s.close();
        System.exit(0);
    }
}