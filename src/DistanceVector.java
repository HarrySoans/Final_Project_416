import java.util.Iterator;
import java.util.Map;

public class DistanceVector {
    String senderName;
    Map<String, VectorEntry> distanceVector;

    DistanceVector(String senderName, Map<String, VectorEntry> distanceVector) {
        this.senderName = senderName;
        this.distanceVector = distanceVector;
    }

    public void addEntry(String subnet, VectorEntry entry) {
        this.distanceVector.put(subnet, entry);
    }

    public Map<String, VectorEntry> getDV() {
        return this.distanceVector;
    }

    public String getSenderName() {
        return this.senderName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sender Name: ").append(this.senderName).append("\n");
        sb.append("Distance Vector:\n");
        Iterator var2 = this.distanceVector.entrySet().iterator();

        while(var2.hasNext()) {
            Map.Entry<String, VectorEntry> entry = (Map.Entry)var2.next();
            VectorEntry ve = (VectorEntry)entry.getValue();
            String stringVE = ve.toStringEntry();
            sb.append("  Subnet: ").append((String)entry.getKey()).append(", VectorEntry: ").append(stringVE).append("\n");
        }

        return sb.toString();
    }
}
