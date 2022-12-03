import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    private static ServerHandler server;
    private static Node primary; // We determine primary based on edges defined (assuming first is the ID of this computer)
    private static ArrayList<Node> nodeList;
    private static int numServers;

    public static void sendMessage (int id, String message) {
        Node n = Utils.getNode(nodeList, id);
        server.send(message, n.getAddress(), n.getPort());
    }

    // Required
    public static void update (int id1, int id2, int cost) {
        // TO-DO
    }

    // Required
    public static void step () {
        // TO-DO
        // Manually call update.
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
            for (Node n: nodeList) {
                if (n.getID() == primary.getID()) continue;
                System.out.printf("%-12d%-10d%-6s%n", n.getNext().getID(), n.getID(), (n.getCost() == -1 ? "inf" : n.getCost()));
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
    }

    public static void main (String[] args) {
        String fileName = "./";
        int interval;
        Utils.ArgsData a = null;
        try {
            a = Utils.ArgsData.parseArgs(args);
            fileName += a.fileName;
            interval = a.interval;
        }
        catch (Exception e) {
            fileName += "topology.txt";
            interval = 1000;
        }
        
        File f = null;
        Scanner s = null;
        try {
            f = new File(fileName);
            s = new Scanner(f);
        }
        catch (Exception e) { e.printStackTrace(); }

        nodeList = new ArrayList<Node>();

        numServers = s.nextInt();
        s.nextLine();
        int numEdges = s.nextInt();
        s.nextLine();
        for (int i = 0; i < numServers; i++) {
            String[] l = s.nextLine().split(" ");
            nodeList.add(new Node(Integer.parseInt(l[0]), l[1], Integer.parseInt(l[2])));
        }
        for (int i = 0; i < numEdges; i++) {
            String[] l = s.nextLine().split(" ");
            if (i == 0) { // Check the first edge and set the primary to the first ID in the edge.
                primary = Utils.getNode(nodeList, Integer.parseInt(l[0]));
            }
            Node n = Utils.getNode(nodeList, Integer.parseInt(l[1]));
            n.setCost(Integer.parseInt(l[2]));
            n.setNext(primary);
        }

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
                case "send":
                    sendMessage(Integer.parseInt(args[1]), args[2]);
                    break;
                default:
                    System.err.println("Invalid input.");
            }
        }
        
        s.close();
    }
}