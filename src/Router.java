import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;

public class Router {
    String name;
    String ip;
    int port;
    DistanceVector distanceVector;
    private Map<String, Map<String, VectorEntry>> neighbors;
    JSONObject jsonData = Parser.parseJSONFile("src/config.json");

    Router(String name, String ip, int port) throws IOException {
        this.name = name;
        this.ip = //get from config;
        this.port = //get from config;
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
        JSONObject j = Parser.parseJSONFile("src/config.json");
        Router r1 = Parser.parseRouters(j)[0];
        r1.getNeighbors();
    }
}
