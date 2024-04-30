import java.io.IOException;
import java.net.*;

class Receiver implements Runnable {
    //grab packet, look at headers that contain the src IP
    private final DatagramSocket socket;

    Receiver(DatagramSocket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                System.out.println("waiting...");
                socket.receive(packet);
                String data = new String(packet.getData(), 0, packet.getLength());

                System.out.println("Data: " + data);

//                Switch.forwardPacket(data);
                //extract the information data
                Frame receivedFrame = Frame.deserialize(data);

                String srcMac = receivedFrame.getSrcMac();
                String destMac = receivedFrame.getDestMac();
                String message = receivedFrame.getMessage();

                System.out.println("Source MAC: " + srcMac);
                System.out.println("Destination MAC: " + destMac);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}