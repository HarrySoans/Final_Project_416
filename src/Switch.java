import java.io.IOException;
import org.json.simple.JSONObject;

public class Switch {
    String name;
    String ip;
    int port;
    JSONObject jsonData = Parser.parseJSONFile("src/config.json");

    Switch(String name, String ip, int port) throws IOException {
        this.name = name;
        this.ip = ip;
        this.port = port;
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
}
