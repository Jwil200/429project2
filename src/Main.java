import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

// We assume first ID read is host.

public class Main {

    private static Node primary; // We determine primary based on edges defined (assuming first is the ID of this computer)
    private static ArrayList<Node> nodeList;
    private static int numServers;

    public static void update (int id1, int id2, String cost) {
        if (cost.equals("inf")) update(id1, id2, -1);
        else System.out.println("Unknown input for cost.");
    }


    public static void update (int id1, int id2, int cost) {
        // Need to implement.
    }

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

    public static void main (String[] args) {
        String fileName = "./src/";
        try {
            fileName += args[1]; // Assuming no issues.
        }
        catch (Exception e) { fileName += "topology.txt"; }

        int interval;
        try {
            interval = Integer.parseInt(args[3]);
        }
        catch (Exception e) { interval = 1000; }
        
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
        
        s = new Scanner(System.in);

        String input = "";

        while (!input.equals("crash")) {
            System.out.print("> ");
            input = s.nextLine();
            switch (input) {
                case "display":
                    display();
                    break;
                case "crash":
                    break;
                default:
                    System.err.println("Invalid input.");
            }
        }
        
        s.close();
    }
}