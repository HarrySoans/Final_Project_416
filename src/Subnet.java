public class Subnet {
    String node;
    int port;

    Subnet(String node, int port) {
        this.node = node;
        this.port = port;
    }

    public String getNode() {
        return this.node;
    }

    public int getPort() {
        return this.port;
    }
}
