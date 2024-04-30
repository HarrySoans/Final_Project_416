import java.io.IOException;
import java.net.DatagramSocket;
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

    public static void main(String[] args) throws IOException {
        Switch s = new Switch("s1", "localhost", 3000);
        s.receiveFrames();
    }
}
