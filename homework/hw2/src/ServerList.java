import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ServerList {
    private final ArrayList<OtherServer> serverList;

    public ServerList(String[] serverList) {
        this.serverList = new ArrayList<OtherServer>();
        for (int i=0; i < serverList.length; i++ ) {
            try {
                this.serverList.add(new OtherServer(i, serverList[i]));
            }
            catch (IllegalArgumentException e) {
                System.out.println("line ignored by serverlist");
            }
        }
    }

    public String clientQuery(String[] request) {
        while (true) {
            for (OtherServer server : this.serverList) {

                // TODO: IMPLEMENT ME!
                // if server can connect
                    // issue command to server
                // if response
                    // return response
                // if no response before timeout
                    // stop listening for response and continue the loop

                try {
                    Socket connectToServer = new Socket(server.address, server.port);
                } catch (IOException e) {
                    System.out.println("Trying to connect connection to " + server.address + ":" + server.port); // e1.printStackTrace();
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }

            }
        }
    }

    /**
     * Returns an available port from the serverList of local allServers
     *
     */
    public int getAvailableLocalPort() {
        while (true) {
            for (OtherServer server : this.serverList) {
                if (server.local) {
                    if (server.portAvailable()) {
                        server.me = true;
                        return server.port;
                    }
                }
            }
        }
    }

    public ArrayList<OtherServer> getServerList(){
        return serverList;
    }

    public OtherServer getServer(int id){
        return serverList.get(id-1);
    }
}
