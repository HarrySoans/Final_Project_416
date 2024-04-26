import java.io.IOException;
import java.io.PrintStream;
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
        this.ip = ip;
        this.port = port;
    }

    public void getNeighbors() {
        Link[] allNeighbors = Parser.parseLinks(this.jsonData);
        Link[] var2 = allNeighbors;
        int var3 = allNeighbors.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Link neigh = var2[var4];
            PrintStream var10000 = System.out;
            String var10001 = neigh.getNode1();
            var10000.println("Neighbor Name: " + var10001 + " Neighbor port: " + neigh.getNode2());
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
