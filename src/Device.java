public class Device {
    String name;
    String ip;
    int port;
    String vIP;
    String gatewayRouter;

    Device(String name, String ip, int port, String vIP, String gatewayRouter) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.vIP = vIP;
        this.gatewayRouter = gatewayRouter;
    }
}
