import java.net.InetAddress;

public class OtherServer {
    private final InetAddress address;
    private final int id;
    private final int port;
    private final boolean local;

    public OtherServer(int id, String configString) throws Exception {
        this.id = id;
        String[] addressComponents = configString.split(":");
        if (!(addressComponents.length == 2)) {
            throw new Exception("No server detected on config line: " + configString);
        }
        this.address = InetAddress.getByName(addressComponents[0]);
        this.port = Integer.getInteger(addressComponents[1]);
        this.local = this.address.getHostAddress().equals("127.0.0.1");
    }
}
