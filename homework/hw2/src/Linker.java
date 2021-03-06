import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Linker {
    Socket[] link;
    PrintWriter[] dataOut;
    BufferedReader[] dataIn;
    BufferedReader dIn;
    ServerList allServers;
    public final int myId;
    public final int myIdx;
    public final int numProc;
    public IntLinkedList neighbors = new IntLinkedList();
    public Linker(String basename, int id, ServerList serverList) throws IOException {
        myId = id;
        myIdx = id - 1;
        allServers = serverList;
        numProc = serverList.getServerList().size();
        dataIn = new BufferedReader[numProc];
        dataOut = new PrintWriter[numProc];

        link = new Socket[numProc];
        //connect(basename, dataIn, dataOut);
    }

    public String sendMsg(int destId, String message) {
        String response = null;
        long sendTime = System.currentTimeMillis();
        try {
            if (!link[destId].isConnected()) {
                link[destId].connect(link[destId].getRemoteSocketAddress());
            }
            dataOut[destId] = new PrintWriter(link[destId].getOutputStream());
            //dataIn[destId] = new BufferedReader(new InputStreamReader(link[destId].getInputStream()));
            dataOut[destId].println(myIdx + " " + destId + " " + message + "#");
            LibraryCLI.safePrintln("Linker sending message: " + myIdx + " " + destId + " " + message + "#");
            dataOut[destId].flush();

            // SHOULD THIS MOVE ON TO THE NEXT SERVER IF RESPONSE == NULL?
            while (System.currentTimeMillis() - sendTime < 1000) {
                if (dataIn[destId].ready()) {
                    response = dataIn[destId].readLine();
                    return response;
                }
            }
           return response;
        }
        catch (IOException e) {
            LibraryCLI.safePrintln("SendMsg exception: " + e.getMessage());
            e.printStackTrace();
        }
        LibraryCLI.safePrintln("Received response: " + response);
        return response;
    }

    public void connect(String basename, BufferedReader[] dataIn, PrintWriter[] dataOut) throws IOException {
        // Contact all the bigger processes
        for (OtherServer server : allServers.serverList) {
            if (server.id != myId) {
                LibraryCLI.safePrintln("Trying to connect to " + server.address + ":" + server.port); // e1.printStackTrace();
                while (link[server.idx] == null) try {
                    link[server.idx] = new Socket(server.address, server.port);
                }
                catch (IOException e) {
                    try {
                        LibraryCLI.safePrintln("Waiting for server " + server.id + " to initialize."); // e1.printStackTrace();
                        Thread.sleep(250);
                    }
                    catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                dataOut[server.idx] = new PrintWriter(link[server.idx].getOutputStream());
                dataIn[server.idx] = new BufferedReader(new InputStreamReader(link[server.idx].getInputStream()));

                // Send a hello message to P_i
                dataOut[server.idx].println("initConnection " + myIdx + " " + server.idx + " " + "hello" + " " + "null");
                dataOut[server.idx].flush();

                String response = dataIn[server.idx].readLine();
            }
        }
    }

    public void closeSockets(){
        try {
            for(Socket sock : link) {
                sock.close();
            }
        }
        catch(Exception e) {
            System.err.println(e);
        }
    }

}
