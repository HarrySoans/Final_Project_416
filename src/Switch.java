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
        List<Device> connectedDevices;
        private DatagramSocket socket;
        private Map<String, String> forwardingTable;
        JSONObject jsonData = Parser.parseJSONFile("src/config.json");


    Switch(String name, String ip, int port) throws IOException {
        super(ip, port, name);
        this.connectedDevices = new ArrayList<>();
        this.socket = new DatagramSocket(port);
        this.forwardingTable = new HashMap<>();
        this.connectedDevices = Parser.parseConnectedDevices(jsonData, this.deviceName);
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

    public void initializeConnectedDevices() {

    }

    //should receive frame from device
    public void receiveFrames() {
        Receiver receiver = new Receiver(socket, this);
        Thread receiverThread = new Thread(receiver);
        receiverThread.start();
    }

    public void processFrame(Frame frame, String ip) throws IOException {
        int port = 0;
        PC[] PCList = Parser.parseDevices(jsonData);
        String deviceName = frame.srcMac.split("\\.")[1];
        String srcMac = frame.getSrcMac();
        String destMac = frame.getDestMac();
        String message = frame.getMessage();
        System.out.println("Source MAC: " + srcMac);
        System.out.println("Destination MAC: " + destMac);
        for(PC d : PCList) {
            if(d.deviceName.equals(deviceName)) {
                port = d.port;
                break;
            }
        }

        if(forwardingTable.isEmpty()) {
            for (Device d : connectedDevices) {
                this.forwardFrame(frame, d.ip+":"+d.port);
                forwardingTable.put(d.deviceName, d.ip+":"+d.port);
            }
        }

        //check forwarding table
        if(!forwardingTable.containsKey(srcMac)) {
            forwardingTable.put(srcMac, ip+":"+port);
        }

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
        ip = Parser.getIpByName(router, jsonData);

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
        for (Device d : s.connectedDevices) {
            System.out.println(d.deviceName);
            System.out.println();
        }
    }
}

