import java.io.IOException;
import java.io.PrintStream;
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

    private void initDistanceVector() {
        for (String node : subnets) {
            String subnet = node;
            VectorEntry entry = new VectorEntry(subnet, 0, this.name);
            distanceVector.addEntry(subnet, entry);
        }
    }

//    private void sendDistanceVectorToNeighbors() {
//        for (String neighbor : neighbors.keySet()) {
//            String ip = parser.getIpByName(neighbor, jsonData);
//            int port = parser.getPortByName(neighbor, jsonData);
//            constructUDPacket(ip, port, distanceVector);
//        }
//    }

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
