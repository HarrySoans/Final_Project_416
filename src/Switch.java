import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

public class Switch {
        String name;
        String ip;
        int port;
        List<Device> connectedDevices;
        private DatagramSocket socket;
    JSONObject jsonData = Parser.parseJSONFile("src/config.json");

    Switch(String name, String ip, int port) throws IOException {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.connectedDevices = new ArrayList<>();
        this.socket = new DatagramSocket(port);
    }

    public String getName() {
        return this.name;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    //should receive frame from device
    public void receiveFrames() {
        Receiver receiver = new Receiver(socket);
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();
    }

    public void forwardFrameToRouter(Frame frame) {
        // Code to determine the router based on destination MAC address
        String destinationMAC = frame.getDestMac();
        String routerIP = determineRouterForMAC(destinationMAC);

        if (routerIP != null) {
            // Create UDP packet containing the frame data
            DatagramPacket packet = constructUDPPacket(frame.serialize().getBytes(), routerIP, 3000);

            // Send the packet to the router
            try {
                socket.send(packet);
                System.out.println("Frame forwarded to router: " + routerIP);
            } catch (IOException e) {
                System.err.println("Error forwarding frame to router: " + e.getMessage());
            }
        } else {
            System.out.println("No router found for destination MAC: " + destinationMAC);
        }
    }

    // Example method to determine router based on MAC address
    private String determineRouterForMAC(String macAddress) {
        // Example implementation of routing logic based on MAC address
        //Based of config
        return "192.100.1.1";
    }

    // Example method to construct a UDP packet
    //Code could be redundant
    private DatagramPacket constructUDPPacket(byte[] data, String routerIP, int routerPort) {
        try {
            InetAddress address = InetAddress.getByName(routerIP);
            return new DatagramPacket(data, data.length, address, routerPort);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }





    public static void main(String[] args) throws IOException {
        Switch s = new Switch("s1", "localhost", 3000);
        s.receiveFrames();
    }
}

