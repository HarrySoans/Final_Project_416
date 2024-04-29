class Frame {
    public String srcMac;
    public String destMac;
    public String message;
    int inPort;

    public Frame(String srcMac, String message, String destMac) {
        this.srcMac = srcMac;
        this.destMac = destMac;
        this.message = message;
    }

    public String getSrcMac() {
        return this.srcMac;
    }

    public String getDestMac() {
        return this.destMac;
    }

    public String getMessage(){return this.message;}

    public String serialize() {
        return srcMac + ':' + destMac + ":" + message;
    }

    public static Frame deserialize(String data) {
        String[] parts = data.split(":");
        String srcMac = parts[0];
        String destMac = parts[1];
        String message = parts[2];
        return new Frame(srcMac, destMac, message);
    }
}
