import java.io.IOException;
import java.net.*;

class Sender {
    private final String destinationIP;
    private final int destinationPort;


    Sender(String destinationIP, int destinationPort) {
        this.destinationIP = destinationIP;
        this.destinationPort = destinationPort;
    }

    public void sendFrame(Frame frame) throws IOException {
        byte[] sendData = frame.serialize().getBytes();
        DatagramSocket socket = new DatagramSocket();
        DatagramPacket packet = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(destinationIP), destinationPort);
        System.out.println(destinationIP + destinationPort);
        socket.send(packet);
        System.out.println("Packet sent: " + new String(packet.getData()));
    }
}
