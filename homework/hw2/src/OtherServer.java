import java.io.IOException;
import java.net.*;

public class OtherServer {
    private InetAddress address;
    private final int id;
    private final int port;
    private final boolean local;
    public boolean me = false;

    public OtherServer(int id, String configString) throws IllegalArgumentException {
        this.id = id;
        String[] addressComponents = configString.split(":");
        if (!(addressComponents.length == 2)) {
            throw new IllegalArgumentException("No server on config line. Skipping: " + configString);
        }
        try {
            address = InetAddress.getByName(addressComponents[0]);
        }
        catch (UnknownHostException e) {
            address = null;
            System.out.println("OtherServer: UnknownHostException");
        }
        port = Integer.parseInt(addressComponents[1]);
        local = this.address.getHostAddress().equals("127.0.0.1");
    }

    public boolean isLocal() {
        return this.local;
    }

    public int getPort() {
        return this.port;
    }

    public int getId() {
        return this.id;
    }

    public InetAddress getAddress(){
        return this.address;
    }

    /**
     * Checks to see if a specific port is available.
     *
     */
    public boolean portAvailable() {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        }
        catch (IOException e) {
            System.out.println("Port was not available: " + port);
        }
        finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                }
                catch (IOException e) {
                    System.out.println("IMPOSSIBRE!");
                }
            }
        }
        return false;
    }
}
