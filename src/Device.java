import org.json.simple.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class Device {
    String name;
    String ip;
    int port;
    String vIP;
    String gatewayRouter;
    String subnet;
    JSONObject jsonData = Parser.parseJSONFile("src/config.json");

    Device(String name, String ip, int port, String vIP, String gatewayRouter, String subnet) throws IOException {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.vIP = vIP;
        this.subnet = subnet;
        this.gatewayRouter = gatewayRouter;
    }

    //when device starts we want to prompt sending function

    public void sendMessage() throws IOException {
        int destPort = 0;
        String destIp = null;
        Device[] deviceList = Parser.parseDevices(jsonData);
        Scanner scanner  = new Scanner(System.in);
        System.out.println("Enter a destination: ");
        String dest = scanner.nextLine();
        System.out.println("Enter a message");
        String message = scanner.nextLine();
        String destName = dest.split("\\.")[1];
        for(Device d : deviceList) {
            if(d.name.equals(destName)) {
                destPort = d.port;
                destIp = d.ip;
                break;
            }
        }

        Frame frame = new Frame(vIP, dest, message);
        String destSubnet = dest.split("\\.")[0];
        //check if destination is currently in switch subnet if so we jut send it to the PC


        Sender sender = new Sender("localhost", 3000);
//        Sender sender = new Sender(destIp, destPort);
        try {
            sender.sendFrame(frame);
        }catch(Exception e) {
            System.out.println("Error sending frame: " + e.getMessage());
        }
    }

    public void startThreads() {
        Thread sendingThread = new Thread(() -> {
            try {
                sendMessage();
            } catch (IOException e) {
                System.out.println("Error sending message: " + e.getMessage());
            }
        });

        Thread receivingThread = new Thread(this::receivePackets);

        sendingThread.start();
        receivingThread.start();
    }

    private void receivePackets() {
    }

    //as a pc I want to send a frame to a switch

    public static void main(String[] args) throws IOException {
        Device d = new Device("p1", "localhost", 4000, "n1.p1", "n1.r1", "n1");
        d.startThreads();
    }
}
