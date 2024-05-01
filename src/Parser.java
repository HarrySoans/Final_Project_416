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

    public static int parseRouterPortByName(String routerName, JSONObject data) {
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

    public static int parsePCPortByName(JSONObject jsonData, String deviceName) {
        JSONArray arr = (JSONArray)jsonData.get("devices");
        int port = 0;
        for (Object d : arr) {
            JSONObject ob = (JSONObject) d;
            String name = (String) ob.get("name");
            if(name.equals(deviceName)) {
                port = ((Number) ob.get("port")).intValue();
            }
        }
        return port;
    }

    public static int parseSwitchPortByName(JSONObject jsonData, String deviceName) {
        JSONArray arr = (JSONArray)jsonData.get("switches");
        int port = 0;
        for (Object d : arr) {
            JSONObject ob = (JSONObject) d;
            String name = (String) ob.get("name");
            if(name.equals(deviceName)) {
                port = ((Number) ob.get("port")).intValue();
            }
        }
        return port;
    }

    public static String parsePCIPByName(JSONObject jsonData, String deviceName) {
        JSONArray arr = (JSONArray)jsonData.get("devices");
        String ip = null;
        for (Object d : arr) {
            JSONObject ob = (JSONObject) d;
            String name = (String) ob.get("name");
            if(name.equals(deviceName)) {
                ip = (String) ob.get("ip");
            }
        }
        return ip;
    }

    public static String parseSwitchIPByName(JSONObject jsonData, String deviceName) {
        JSONArray arr = (JSONArray)jsonData.get("switches");
        String ip = null;
        for (Object d : arr) {
            JSONObject ob = (JSONObject) d;
            String name = (String) ob.get("name");
            if(name.equals(deviceName)) {
                ip = (String) ob.get("ip");
            }
        }
        return ip;
    }

    public static String parseRouterIPByName(JSONObject jsonData, String deviceName) {
        JSONArray arr = (JSONArray)jsonData.get("routers");
        String ip = null;
        for (Object d : arr) {
            JSONObject ob = (JSONObject) d;
            String name = (String) ob.get("name");
            if(name.equals(deviceName)) {
                ip = (String) ob.get("ip");
            }
        }
        return ip;
    }

    public static String getRouterDeviceByName(JSONObject jsonData, String deviceName) throws IOException {
        JSONArray arr = (JSONArray)jsonData.get("routers");
        for (Object o : arr) {
            JSONObject ob = (JSONObject) o;
            String name = (String) ((JSONObject) o).get("name");
            if(name.equals(deviceName)) {
                String ip = (String) ob.get("ip");
                int port = ((Number)ob.get("port")).intValue();
                return name+"/"+ip+":"+port;
            }
        }

        return null;
    }

    public static String getPCDeviceByName(JSONObject jsonData, String deviceName) throws IOException {
        JSONArray arr = (JSONArray)jsonData.get("devices");
        for (Object o : arr) {
            JSONObject ob = (JSONObject) o;
            String name = (String) ((JSONObject) o).get("name");
            if(name.equals(deviceName)) {
                String ip = (String) ob.get("ip");
                int port = ((Number)ob.get("port")).intValue();
                return name+"/"+ip+":"+port;
            }
        }

        return null;
    }

    public static String getSwitchDeviceByName(JSONObject jsonData, String deviceName) throws IOException {
        JSONArray arr = (JSONArray)jsonData.get("switches");
        for (Object o : arr) {
            JSONObject ob = (JSONObject) o;
            String name = (String) ((JSONObject) o).get("name");
            if(name.equals(deviceName)) {
                String ip = (String) ob.get("ip");
                int port = ((Number)ob.get("port")).intValue();
                return name+"/"+ip+":"+port;
            }
        }
        return null;
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

    public static String parseGetRouterIpByName(String routerName, JSONObject data) {
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
