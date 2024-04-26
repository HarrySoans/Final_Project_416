import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Parser {
    public Parser() {
    }

    public static JSONObject parseJSONFile(String fileName) throws IOException {
        try {
            JSONParser parser = new JSONParser();
            FileReader reader = new FileReader(fileName);
            return (JSONObject)parser.parse(reader);
        } catch (ParseException | FileNotFoundException var3) {
            var3.printStackTrace();
            return null;
        }
    }

    public static Switch[] parseSwitches(JSONObject jsonData) throws IOException {
        JSONArray arr = (JSONArray)jsonData.get("switches");
        List<Switch> switchList = new ArrayList();
        Iterator var3 = arr.iterator();

        while(var3.hasNext()) {
            Object s = var3.next();
            JSONObject swInstance = (JSONObject)s;
            String name = (String)swInstance.get("name");
            String ip = (String)swInstance.get("ip");
            int port = (Integer)swInstance.get("port");
            switchList.add(new Switch(name, ip, port));
        }

        return (Switch[])switchList.toArray(new Switch[0]);
    }

    public static Device[] parseDevices(JSONObject jsonData) {
        JSONArray arr = (JSONArray)jsonData.get("devices");
        List<Device> deviceList = new ArrayList();
        Iterator var3 = arr.iterator();

        while(var3.hasNext()) {
            Object d = var3.next();
            JSONObject device = (JSONObject)d;
            String name = (String)device.get("name");
            String ip = (String)device.get("ip");
            int port = (Integer)device.get("port");
            String vIP = (String)device.get("virtual_ip");
            String gatewayRouter = (String)device.get("gateway_router");
            deviceList.add(new Device(name, ip, port, vIP, gatewayRouter));
        }

        return (Device[])deviceList.toArray(new Device[0]);
    }

    public static Link[] parseLinks(JSONObject jsonData) {
        JSONArray arr = (JSONArray)jsonData.get("links");
        List<Link> linkList = new ArrayList();
        Iterator var3 = arr.iterator();

        while(var3.hasNext()) {
            Object l = var3.next();
            JSONObject link = (JSONObject)l;
            String node1 = (String)link.get("node1");
            String node2 = (String)link.get("node2");
            linkList.add(new Link(node1, node2));
        }

        return (Link[])linkList.toArray(new Link[0]);
    }

    public static Router[] parseRouters(JSONObject jsonData) throws IOException {
        JSONArray arr = (JSONArray)jsonData.get("routers");
        List<Router> routerList = new ArrayList();
        Iterator var3 = arr.iterator();

        while(var3.hasNext()) {
            Object r = var3.next();
            JSONObject router = (JSONObject)r;
            String name = (String)router.get("name");
            String ip = (String)router.get("ip");
            int port = ((Number)router.get("port")).intValue();
            routerList.add(new Router(name, ip, port));
        }

        return (Router[])routerList.toArray(new Router[0]);
    }

    public static Map<String, List<Subnet>> parseSubnets(JSONObject jsonData) {
        JSONArray arr = (JSONArray)jsonData.get("subnet");
        Map<String, List<Subnet>> subnetMap = new HashMap();
        Iterator var3 = arr.iterator();

        while(var3.hasNext()) {
            Object o = var3.next();
            JSONObject object = (JSONObject)o;
            Iterator var6 = object.keySet().iterator();

            while(var6.hasNext()) {
                Object key = var6.next();
                String routerName = (String)key;
                JSONArray subnetArray = (JSONArray)object.get(routerName);
                List<Subnet> subnets = new ArrayList();
                Iterator var11 = subnetArray.iterator();

                while(var11.hasNext()) {
                    Object s = var11.next();
                    JSONObject subnet = (JSONObject)s;
                    String node = (String)subnet.get("node");
                    int port = (Integer)subnet.get("port");
                    subnets.add(new Subnet(node, port));
                }

                subnetMap.put(routerName, subnets);
            }
        }

        return subnetMap;
    }

    public static Destination[] parseDestinations(JSONObject jsonData) {
        JSONArray arr = (JSONArray)jsonData.get("destination");
        List<Destination> destinationList = new ArrayList();
        Iterator var3 = arr.iterator();

        while(var3.hasNext()) {
            Object d = var3.next();
            JSONObject destinationObj = (JSONObject)d;
            String name = (String)destinationObj.get("name");
            destinationList.add(new Destination(name));
        }

        return (Destination[])destinationList.toArray(new Destination[0]);
    }
}
