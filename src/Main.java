import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

/** 
 * main class that calls for all the required functions of the project 
 * Update, Step, Packets, Display, Disable, and Crash. 
 * This class also prompts the user for input to call for the functions. 
 */
public class Main {
    
    public static final int MESSAGE_LENGTH = 256;

    private static ServerHandler server;
    private static Node primary; // We determine primary based on edges defined (assuming first is the ID of this computer)
    private static ArrayList<Node> nodeList;
    private static HashMap<Node, Integer> routingTable;
    private static int numServers;

    public static void help() {
        
			System.out.println("\nList of Commands supported:" + "\n>> help"
						+ "\n-- update <server id 1> <server id 2> <link cost>" + "\n-- step" + "\n-- packets"
						+ "\n-- display" + "\n-- disable <server id>" + "\n-- crash" + "\n-- exit\n");

    }

    public static void sendMessage (int id, String message) {
        Node n = Utils.getNode(nodeList, id);
        server.send(message, n.getAddress(), n.getPort());
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
    }

    // Required
    public static void packets () {
        // TO-DO
        // Print packets and then reset value.
    }

    /**
     * Displays the nodes that are connected to the localhost if there is any
     * @system.out.print to print out the route table for the user to see 
     */
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

    /**
     * disables the node from the route table to make sure the packets do not go to that node
     * @param id takes the id that needs to be disabled from the route table
     */

    public static void disable (int id) {
        // TO-DO
        Node n = Utils.getNode(nodeList, id);
        n.stop();

        // Check if it is a neighbor.
    }

    /**
     * kills the program on the localhost and removes the node from the other neighbors 
     * essentially disconnecting the node from the rest of the environment.
     */
    public static void crash () {
        // TO-DO
        for (Node n: nodeList)
            n.stop();
        server.close();
    }

    /**
     * main class that calls for the filename that is used as the reference for the topology 
     * scans the topology.txt file for the nodes list and inputs it into an array
     * parses for the server's information and the number of edges that is associated to the nodes
     * LocalHost node will be referenced as node 0 and the following nodes that connect will start at 1 
     * Builds the connectivity with the other nodes and formulates the route table and the cost associated 
     * 
     * @case relays the user input for the respective function above. 
     * @s.close to close the session 
     */
    public static void main (String[] args) {
        
        String fileName=null;
        int interval=0;
        int j = 0;
        Scanner s = new Scanner(System.in);
        
        System.out.print("> ");
        String start = s.nextLine();
        StringTokenizer st = new StringTokenizer(start);

        Utils.ArgsData a = null;
        String[] arg = new String[5];
        while(st.hasMoreTokens()){
            arg[j] = st.nextToken();
            j++;
        }
        try {
            a = Utils.ArgsData.parseArgs(arg);
            fileName = a.fileName;
            interval = a.interval;
        }
        catch (Exception e) {
            fileName = "topology.txt";
            interval = 120; // 2 min
        }


        
        File f = null;
        Scanner txt = null;
        try {
            f = new File(fileName);
            txt = new Scanner(f);
        }
        catch (Exception e) { e.printStackTrace(); }

        nodeList = new ArrayList<Node>();
        routingTable = new HashMap<Node, Integer>();

        numServers = txt.nextInt();
        txt.nextLine();
        int numEdges = txt.nextInt();
        txt.nextLine();
        for (int i = 0; i < numServers; i++) {
            String[] l = txt.nextLine().split(" ");
            Node newNode = new Node(Integer.parseInt(l[0]), l[1], Integer.parseInt(l[2]));
            nodeList.add(newNode);
            routingTable.put(newNode, -1);
        }
        for (int i = 0; i < numEdges; i++) {
            String[] l = txt.nextLine().split(" ");
            if (i == 0) { // Check the first edge and set the primary to the first ID in the edge.
                primary = Utils.getNode(nodeList, Integer.parseInt(l[0]));
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

        txt.close();

        for (Node n: nodeList) {
            if (n.getID() == primary.getID()) continue;
            n.start(interval);
        }

        try {
            server = new ServerHandler(primary.getPort());
        }
        catch (Exception e) { System.exit(0); }
        
        //s = new Scanner(System.in);

        String input = "";
        
        while (!input.equals("crash")) {
            System.out.print(">>> ");
            input = s.nextLine();
            args = input.split(" ");
            switch (args[0]) {
                case "help":
                    help();
                    break;
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
                case "exit":
                    System.exit(0);
                    break;
                default:
                    System.err.println("Invalid input.");
            }
        }
        
        s.close();
    }
}