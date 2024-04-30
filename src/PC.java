import org.json.simple.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Scanner;

public class PC extends Device {
    String vIP;
    String gatewayRouter;
    String subnet;
    DatagramSocket socket;
    JSONObject jsonData = Parser.parseJSONFile("src/config.json");

    PC(String name, String ip, int port, String vIP, String gatewayRouter, String subnet) throws IOException {
        super(ip, port, name);
        this.vIP = vIP;
        this.subnet = subnet;
        this.gatewayRouter = gatewayRouter;
        this.socket = new DatagramSocket(port);
    }

    //when device starts we want to prompt sending function

    public void sendMessage() throws IOException {
        int destPort = 0;
        String destIp = null;
        PC[] PCList = Parser.parseDevices(jsonData);
        Scanner scanner  = new Scanner(System.in);
        System.out.println("Enter a destination: ");
        String dest = scanner.nextLine();
        System.out.println("Enter a message");
        String message = scanner.nextLine();
        String destName = dest.split("\\.")[1];
        for(PC d : PCList) {
            if(d.deviceName.equals(destName)) {
                destPort = d.port;
                destIp = d.ip;
                break;
            }
        }

        System.out.println("we are sending rn!!");

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

        Thread receivingThread = new Thread(this::receiveFrames);

        sendingThread.start();
        receivingThread.start();
    }

    public void receiveFrames() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                System.out.println("waiting...");
                socket.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());
                String ip = packet.getAddress().getHostAddress();

                System.out.println("received data: " + data);
            }
        }catch(SocketException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //as a pc I want to send a frame to a switch

    public static void main(String[] args) throws IOException {
        PC d = new PC("p1", "localhost", 4000, "n1.p1", "n1.r1", "n1");
//        Device d = new Device("p2", "localhost", 4001, "n1.p2", "n1.r1", "n1");
        d.startThreads();
    }
}
