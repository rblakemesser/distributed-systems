import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerList {
    public final ArrayList<OtherServer> serverList;

    public ServerList(ArrayList<String> serverConfigLines) {
        this.serverList = new ArrayList<OtherServer>();
        for (int i=0; i < serverConfigLines.size(); i++) {
            this.serverList.add(new OtherServer(i, serverConfigLines.get(i)));
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
                    Socket connectionToServer = new Socket(server.address, server.port);
                    PrintWriter dataOut = new PrintWriter(connectionToServer.getOutputStream());
                    BufferedReader dataIn = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));
                    dataOut.println(request[0] + " " + request[1] + " " + request[2]);
                    dataOut.flush();

                    // Wait for a reply for <timeout>
                    // Loop for <timeout> time - check to see if dataIn is not null.  If it's NOT null, return the value
                    // and you're done with this loop

                } catch (IOException e) {
                    System.out.println("Trying to connection to " + server.address + ":" + server.port); // e1.printStackTrace();
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
