import java.io.IOException;
import java.net.*;

class Receiver implements Runnable {
    //grab packet, look at headers that contain the src IP
    private final DatagramSocket socket;
    private final Switch switchInstance;
    Receiver(DatagramSocket socket, Switch switchInstance) {
        this.socket = socket;
        this.switchInstance = switchInstance;
    }

    public void run() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                System.out.println("waiting...");
                socket.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());
                String ip = packet.getAddress().getHostAddress();

                System.out.println("received data: " + data);

                Frame receivedFrame = Frame.deserialize(data);
//                switchInstance.forwardFrameToRouter(receivedFrame);
                switchInstance.processFrame(receivedFrame, ip);

            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}