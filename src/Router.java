import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONObject;

public class Router {
    String name;
    String ip;
    int port;
    DistanceVector distanceVector;
    private Map<String, Map<String, VectorEntry>> neighbors;
    private final List<String> subnets;
    JSONObject jsonData = Parser.parseJSONFile("src/config.json");

    Router(String name) throws IOException {
        this.name = name;
        this.port = Parser.getPortByName(name, jsonData);
        this.ip = Parser.getIpByName(name, jsonData);
        this.neighbors = new HashMap<>();
        this.subnets = Parser.parseSubnets(jsonData, name);
        initializeNeighbors();
        sendDistanceVectorToNeighbors();
    }

    public void initializeNeighbors() {
        Link[] allNeighbors = Parser.parseLinks(this.jsonData);
        for(Link l : allNeighbors) {
            if(l.getNode1().equals(this.name)) {
                if(l.getNode2().startsWith("r")) {
                    neighbors.put(l.getNode2(), new HashMap<>());
                }
            }else if(l.getNode2().equals(this.name)){
                if(l.getNode1().startsWith("r")) {
                    neighbors.put(l.getNode1(), new HashMap<>());
                }
            }
        }
    }

    private DatagramPacket packetReceiver() {
        DatagramPacket finalMess = null;
        System.out.println("waiting...");
        try {
            DatagramSocket socket = new DatagramSocket(this.port);
            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
            socket.receive(receivePacket);
            finalMess = receivePacket;
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return finalMess;
    }

    public void constructUDPacket(String destinationIP, int destinationPort, DistanceVector payload) {
        try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress address = InetAddress.getByName(destinationIP);
            String stringified = payload.toString();
            byte[] sendData = stringified.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, destinationPort);
            socket.send(sendPacket);
            socket.close();
            System.out.println("packet sent");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean updateDistanceVector(DistanceVector incomingDistanceVectors) {
        boolean isUpdated = false;
        for (VectorEntry entry : incomingDistanceVectors.getDV().values()) {
            String subnet = entry.getName();
            int cost = entry.getCost();
            Map<String, VectorEntry> dv = distanceVector.getDV();

            if (!dv.containsKey(subnet)) {
                distanceVector.addEntry(subnet, new VectorEntry(subnet, cost + 1, incomingDistanceVectors.getSenderName()));
                isUpdated = true;
            }else {
                // check shorter distance
                if((cost + 1) < distanceVector.getDV().get(subnet).cost) {
                    distanceVector.addEntry(subnet, new VectorEntry(subnet, cost + 1, incomingDistanceVectors.getSenderName()));
                    isUpdated = true;
                }
            }
        }
        return isUpdated;
    }

    protected DistanceVector receivePacket(DatagramPacket packet) {
        DistanceVector receivedVector = null;
        try {
            byte[] receivedData = packet.getData();
            String dv = new String(receivedData, 0, packet.getLength());
            String[] lines = dv.split("\n");
            String senderName = lines[0].substring(lines[0].indexOf(":") + 1).trim();
            Map<String, VectorEntry> distanceVector = new HashMap<>();
            for (int i = 2; i < lines.length; i++) {
                String[] parts = lines[i].trim().split(":");
                String subnet = parts[1].trim().split(",")[0].trim();
                VectorEntry entry = VectorEntry.parseVectorEntry(parts[2].trim());
                distanceVector.put(subnet, entry);
            }
            receivedVector = new DistanceVector(senderName, distanceVector);

            System.out.println(receivedVector.senderName);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return receivedVector;
    }


    private void initDistanceVector() {
        for (String node : subnets) {
            String subnet = node;
            VectorEntry entry = new VectorEntry(subnet, 0, this.name);
            distanceVector.addEntry(subnet, entry);
        }
    }

    private void sendDistanceVectorToNeighbors() {
        for (String neighbor : neighbors.keySet()) {
            String ip = Parser.getIpByName(neighbor, jsonData);
            int port = Parser.getPortByName(neighbor, jsonData);
            constructUDPacket(ip, port, distanceVector);
        }
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

    public static void main(String[] args) throws IOException {
        JSONObject jsonData = Parser.parseJSONFile("src/config.json");
        Router r1 = new Router("r2");
        for(String name : r1.subnets) {
            System.out.println("connected to: " + name);
            System.out.println(Parser.getSubnetPort(name, jsonData));
        }
    }
}
