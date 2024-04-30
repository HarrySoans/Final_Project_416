public class Device {
    String deviceName;
    public String ip;
    public int port;

    Device(String ip, int port, String deviceName) {
        this.deviceName = deviceName;
        this.ip = ip;
        this.port = port;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }
}
