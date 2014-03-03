import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;

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
        this.port = Integer.parseInt(addressComponents[1]);
        this.local = this.address.getHostAddress().equals("127.0.0.1");
    }

    public boolean isLocal() {
        return this.local;
    }

    public int getPort() {
        return this.port;
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
            System.out.println("IOEXCEPTION!");
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
