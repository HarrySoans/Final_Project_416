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

        return switchList.toArray(new Switch[0]);
    }

    public static Device[] parseDevices(JSONObject jsonData) throws IOException {
        JSONArray arr = (JSONArray)jsonData.get("devices");
        List<Device> deviceList = new ArrayList();
        Iterator var3 = arr.iterator();

        while(var3.hasNext()) {
            Object d = var3.next();
            JSONObject device = (JSONObject)d;
            String name = (String)device.get("name");
            String ip = (String)device.get("ip");
            int port = ((Number) device.get("port")).intValue();
            String vIP = (String)device.get("virtual_ip");
            String gatewayRouter = (String)device.get("gateway_router");
            deviceList.add(new Device(name, ip, port, vIP, gatewayRouter));
        }

        return deviceList.toArray(new Device[0]);
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

        return linkList.toArray(new Link[0]);
    }

    public static Router[] parseRouters(JSONObject jsonData) throws IOException {
        JSONArray arr = (JSONArray)jsonData.get("routers");
        List<Router> routerList = new ArrayList();
        Iterator var3 = arr.iterator();

        while(var3.hasNext()) {
            Object r = var3.next();
            JSONObject router = (JSONObject)r;
            String name = (String)router.get("name");
            routerList.add(new Router(name));
        }

        return routerList.toArray(new Router[0]);
    }

    public static List<Object> parseSubnets(JSONObject jsonData) {
        JSONArray arr = (JSONArray)jsonData.get("subnet");
        List<Object> subnets = new ArrayList<>();
        for(Object r : arr) {
            JSONObject ob = (JSONObject) r;
            subnets.add(ob);
        }
        return subnets;
    }

    public static List<String> parseConnectedSubnets(JSONObject jsonData, String routerName) {
        JSONArray arr = (JSONArray)jsonData.get("subnet");
        List<String> subnets = new ArrayList<>();

        for(Object r : arr) {
            JSONObject ob = (JSONObject) r;
            for(Object key : ob.keySet()) {
                if(key.equals(routerName)) {
                    JSONArray net = (JSONArray) ob.get(key);
                    for(Object o : net) {
                        JSONObject info = (JSONObject) o;
                        String netName = (String) info.get("node");
                        subnets.add(netName);
                    }
                }
            }
        }
        return subnets;
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

        return destinationList.toArray(new Destination[0]);
    }

    public static String getIpByName(String routerName, JSONObject data) {
        System.out.println(routerName);
        JSONArray arr = (JSONArray) data.get("routers");
        String ip = null;
        if(routerName != null) {
            for(Object ob:arr) {
                JSONObject routerObj = (JSONObject) ob;
                if(routerObj.get("name").equals(routerName)) {
                    ip = (String) routerObj.get("ip");
                }
            }
        }
        return ip;
    }

    public static int getPortByName(String routerName, JSONObject data) {
        JSONArray arr = (JSONArray) data.get("routers");
        int port = 0;
        if(routerName != null){
            for (Object ob : arr) {
                JSONObject routerObj = (JSONObject) ob;
                if(routerObj.get("name").equals(routerName)) {
                    Object portObject = routerObj.get("port");
                    if (portObject instanceof Number) {
                        port = ((Number) portObject).intValue();
                        break;
                    }
                }
            }
        }
        return port;
    }

    public static int getSubnetPort(String netName, JSONObject data) {
        JSONArray arr = (JSONArray) data.get("subnet");
        int port = 0;
        for(Object r : arr) {
            JSONObject ob = (JSONObject) r;
            for(Object key : ob.keySet()) {
                JSONArray net = (JSONArray) ob.get(key);
                for(Object o : net) {
                    JSONObject info = (JSONObject) o;
                    String n = (String) info.get("node");
                    if(n.equals(netName)) {
                        port = ((Number) info.get("port")).intValue();
                    }
                }
            }
        }
        return port;
    }
}
