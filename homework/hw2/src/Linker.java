import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;

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

        // Replacement for Topology - assume all processes are neighbors
        for (int j = 0; j < numProc; j++) {
            if (j+1 != myId) {
                neighbors.add(j+1);
            }
        }
        link = new Socket[numProc];
        connect(basename, dataIn, dataOut);
    }
    public void sendMsg(int destId, String tag, String msg) {
        dataOut[destId].println(myId + " " + destId + " " + tag + " " + msg + "#");
        LibraryCLI.safePrintln("Linker sending message: " + myIdx + " " + destId + " " + tag + " " + msg + "#");
        dataOut[destId].flush();
    }
    public void sendMsg(int destId, String tag) {
        sendMsg(destId, tag, " 0 ");
    }
    public void multicast(IntLinkedList destIds, String tag, String msg) {
        for (int i=0; i<destIds.size(); i++) {
            sendMsg(destIds.getEntry(i), tag, msg);
        }
    }
    public Msg receiveMsg(int fromId) throws IOException {
        String getline = dataIn[fromId].readLine();
        LibraryCLI.safePrintln("Linker: received message: " + getline);
        StringTokenizer st = new StringTokenizer(getline);
        int srcId = Integer.parseInt(st.nextToken());
        int destId = Integer.parseInt(st.nextToken());
        String tag = st.nextToken();
        String msg = st.nextToken("#");
        return new Msg(srcId, destId, tag, msg);
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

                LibraryCLI.safePrintln(dataIn[server.idx].readLine());
                // dataOut[server.idx].close();
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
