import java.io.IOException;
import java.net.*;

public class OtherServer {
    public InetAddress address;
    public int id;
    public int idx;
    public final int port;
    public final boolean local;
    public boolean me = false;

    public OtherServer(int idx, String configString) {
        this.idx = idx;
        this.id = this.idx + 1;
        String[] addressComponents = configString.split(":");
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
