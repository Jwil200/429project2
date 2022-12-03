import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerHandler extends Thread {

    private DatagramSocket socket;
    private boolean running;

    public ServerHandler (int PORT) throws Exception {
        socket = new DatagramSocket(PORT);
        running = true;
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
                String recieved = new String(p.getData(), 0, p.getLength());

                switch (recieved) {
                    case "update":
                        
                        break;
                    default:
                        // Nothing
                }
            }
            catch (Exception e) {}
        }
    }

    public void close () {
        running = false;
    }
}
