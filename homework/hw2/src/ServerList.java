import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerList {
    public final ArrayList<OtherServer> serverList;
    private int lastServerAccessed = 0;
    private int maxServerId;

    public ServerList(ArrayList<String> serverConfigLines) {
        this.serverList = new ArrayList<OtherServer>();
        for (int i=0; i < serverConfigLines.size(); i++) {
            this.serverList.add(new OtherServer(i, serverConfigLines.get(i)));
        }
        maxServerId = serverList.size();
    }

    public String clientQuery(String[] request) {

        while (true) {
            for (OtherServer server : this.serverList) {
                if (server.id <= lastServerAccessed){
                    continue;
                }
                lastServerAccessed = server.id;
                try {
                    long startTime = System.currentTimeMillis();
                    Socket connectionToServer = new Socket(server.address, server.port);
                    PrintWriter dataOut = new PrintWriter(connectionToServer.getOutputStream());
                    BufferedReader dataIn = new BufferedReader(new InputStreamReader(connectionToServer.getInputStream()));
                    LibraryCLI.safePrintln("ServerList: sending " + request[0] + " " + request[1] +" "+ request[2] + " to " + server.address + ":" + server.port);
                    dataOut.println(request[0] + " " + request[1] + " " + request[2]);
                    dataOut.flush();
                    long nowTime = System.currentTimeMillis();
                    while (nowTime - startTime < 1000) {
                        String response;
                        // We break down here if nothing gets sent at all!
                        if (dataIn.ready() && (response = dataIn.readLine()) != null) {
                            LibraryCLI.safePrintln("ServerList: response received from " + server.address + ":" + server.port + " - " + response);
                            if (lastServerAccessed == maxServerId){
                                lastServerAccessed = 0;
                            }
                            dataOut.close();
                            dataIn.close();
                            connectionToServer.close();
                            return response;
                        }
                        nowTime = System.currentTimeMillis();
                    }

                    if (lastServerAccessed == maxServerId) lastServerAccessed = 0;
                    // Wait for a reply for <timeout>
                    // Loop for <timeout> time - check to see if we have something from dataIn

                } catch (IOException e) {
                    LibraryCLI.safePrintln("Serverlist: Exception message: " + e.getMessage());
                    LibraryCLI.safePrintln("ServerList: trying to connection to " + server.address + ":" + server.port); // e1.printStackTrace();
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
