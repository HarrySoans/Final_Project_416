import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Switch extends Device {
        Map<String, String> connectedDevices;
        private DatagramSocket socket;
        private Map<String, String> forwardingTable;
        JSONObject jsonData = Parser.parseJSONFile("src/config.json");


    Switch(String name, String ip, int port) throws IOException {
        super(ip, port, name);
        this.connectedDevices = new HashMap<>();
        this.socket = new DatagramSocket(port);
        this.forwardingTable = new HashMap<>();
        initializeConnectedDevices();
    }

    public String getName() {
        return this.deviceName;
    }

    public String getIp() {
        return this.ip;
    }

    public int getPort() {
        return this.port;
    }

    public void initializeConnectedDevices() throws IOException {
        Link[] links = Parser.parseLinks(jsonData);
        for (Link l : links) {
            if(l.getNode1().equals(this.deviceName)) {
                if(l.getNode2().startsWith("r")) {
                    String router = Parser.getRouterDeviceByName(jsonData, l.getNode2());
                    String[] parts = router.split("/");
                    connectedDevices.put(parts[0], parts[1]);
                }else if (l.getNode2().startsWith("p")) {
                    String pc = Parser.getPCDeviceByName(jsonData, l.getNode2());
                    String[] parts = pc.split("/");
                    connectedDevices.put(parts[0], parts[1]);
                }else if (l.getNode2().startsWith("s")) {
                    String s = Parser.getSwitchDeviceByName(jsonData, l.getNode2());
                    String[] parts = s.split("/");
                    connectedDevices.put(parts[0], parts[1]);
                }
            }
        }
    }

    //should receive frame from device
    public void receiveFrames() {
        Receiver receiver = new Receiver(socket, this);
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();
    }

    public void processFrame(Frame frame, String ip) throws IOException {
        String deviceName = frame.srcMac.split("\\.")[1];
        String srcMac = frame.getSrcMac();
        String destMac = frame.getDestMac();
        String message = frame.getMessage();
        System.out.println("Source MAC: " + srcMac);
        System.out.println("Destination MAC: " + destMac);
        int port = Parser.parsePCPortByName(jsonData, deviceName);

        if(forwardingTable.isEmpty()) {
            for (String key : connectedDevices.keySet()) {
                String val = connectedDevices.get(key);
                this.forwardFrame(frame, val);
                forwardingTable.put(key, val);
            }
        }

//        check forwarding table
//        if(!forwardingTable.containsKey(srcMac)) {
//            forwardingTable.put(srcMac, ip+":"+port);
//            this.forwardFrame(frame, forwardingTable.get(srcMac));
//        }else {
//            System.out.println("entered forward stage...");
//            this.forwardFrame(frame, forwardingTable.get(srcMac));
//        }


        this.forwardFrame(frame, forwardingTable.get(srcMac));

        System.out.println("Forwarding table...");
        for(Object o : forwardingTable.keySet()) {
            String k = (String) o;

            System.out.println(o);
            System.out.println(forwardingTable.get(k));
        }
    }

    public void forwardFrame(Frame frame, String forwardAddress) throws IOException {
        String ip = forwardAddress.split(":")[0];
        String port = forwardAddress.split(":")[1];
        System.out.println("entered forward stage...");
        Sender sender = new Sender(ip, Integer.parseInt(port));
        sender.sendFrame(frame);
    }

    //check

    public void forwardFrameToRouter(Frame frame) {
        // Code to determine the router based on destination MAC address
        String destinationMAC = frame.getDestMac();
        String routerIP = determineRouter(destinationMAC);

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
    private String determineRouter(String macAddress) {
        // Example implementation of routing logic based on MAC address
        List<Object> subnets = Parser.parseSubnets(jsonData);
        String subnet = macAddress.split("\\.")[0];
        String router = null;
        String ip = null;
        for(Object o : subnets) {
            JSONObject ob = (JSONObject) o;
            for(Object k : ob.keySet()) {
                JSONArray a = (JSONArray) ((JSONObject) o).get(k);
                for (Object n : a) {
                    JSONObject net = (JSONObject) n;
                    if(net.get("node").equals(subnet)) {
                        router = (String) k;
                    }
                }
            }
        }
        ip = Parser.parseRouterIPByName(jsonData, router);

        //Based of config
        return ip;
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
        for (String key : s.connectedDevices.keySet()) {
            String val = s.connectedDevices.get(key);
            System.out.println(key + " : " + val);
            System.out.println();
        }
    }
}

