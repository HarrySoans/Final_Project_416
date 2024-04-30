import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
            int port = ((Number)swInstance.get("port")).intValue();
            switchList.add(new Switch(name, ip, port));
        }

        return switchList.toArray(new Switch[0]);
    }

    public static PC[] parseDevices(JSONObject jsonData) throws IOException {
        JSONArray arr = (JSONArray)jsonData.get("devices");
        List<PC> PCList = new ArrayList();
        Iterator var3 = arr.iterator();

        while(var3.hasNext()) {
            Object d = var3.next();
            JSONObject device = (JSONObject)d;
            String name = (String)device.get("name");
            String ip = (String)device.get("ip");
            int port = ((Number) device.get("port")).intValue();
            String vIP = device.get("subnet") + "." + name;
            String subnet = (String) device.get("subnet");
            String gatewayRouter = (String)device.get("gateway_router");
            PCList.add(new PC(name, ip, port, vIP, gatewayRouter, subnet));
        }

        return PCList.toArray(new PC[0]);
    }

//    public Device getDeviceByName(JSONObject jsonData, String deviceName) throws IOException {
//        Device[] devices = Parser.parseDevices(jsonData);
//        for(Object d : devices) {
//            JSONObject device = (JSONObject) d;
//            if(device.get("name").equals(deviceName)) {
//                return
//            }
//        }
//    }

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

    public static List<Device> parseConnectedDevices(JSONObject jsonData, String deviceName) throws IOException {
        Link[] links = Parser.parseLinks(jsonData);
        PC[] pcList = Parser.parseDevices(jsonData);
        Router[] routerList = Parser.parseRouters(jsonData);
        Switch[] switchList = Parser.parseSwitches(jsonData);
        List<Device> connected = new ArrayList<>();

        for (Link l : links) {
            if(l.getNode1().equals(deviceName)) {
                //put the device in 2 inside the connected
                if(l.getNode2().startsWith("r")) {
                    //create router instance and put here
                    for (Router r : routerList) {
                        if(r.getName().equals(l.getNode2())){
                            connected.add(r);
                        }
                    }
                }else if(l.getNode2().startsWith("p")){
                    //create pc instance and put here
                    for (PC p : pcList) {
                        if(p.getDeviceName().equals(l.getNode2())) {
                            connected.add(p);
                        }
                    }
                }else if (l.getNode2().startsWith("s")){
                    for (Switch s : switchList){
                        if(s.getDeviceName().equals(l.getNode2())) {
                            connected.add(s);
                        }
                    }
                }
            }
        }
        return connected;
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
            routerList.add(new Router(ip, port, name));
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

//    public static

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
