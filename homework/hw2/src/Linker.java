import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class Linker {
    ServerSocket listener;
    Socket[] link;
    PrintWriter[] dataOut;
    BufferedReader[] dataIn;
    BufferedReader dIn;
    ServerList servers;
    private final int myId;
    private final int numProc;
    public IntLinkedList neighbors = new IntLinkedList();
    public Linker(String basename, int id, ServerList serverList) throws Exception {
        myId = id;
        servers = serverList;
        numProc = serverList.getServerList().size();
        dataIn = new BufferedReader[numProc];
        dataOut = new PrintWriter[numProc];

        // Replacement for Topology - assume all processes are neighbors
        for (int j = 0; j < numProc; j++) {
            if (j != myId) {
                neighbors.add(j);
            }
        }
        link = new Socket[numProc];
        connect(basename, dataIn, dataOut);
    }
    public void sendMsg(int destId, String tag, String msg) {
        dataOut[destId].println(myId + " " + destId + " " + tag + " " + msg + "#");
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
        //System.out.println(" received message " + getline);
        StringTokenizer st = new StringTokenizer(getline);
        int srcId = Integer.parseInt(st.nextToken());
        int destId = Integer.parseInt(st.nextToken());
        String tag = st.nextToken();
        String msg = st.nextToken("#");
        return new Msg(srcId, destId, tag, msg);
    }
    public int getMyId() { return myId; }
    public int getNumProc() { return numProc; }

    public void connect(String basename, BufferedReader[] dataIn, PrintWriter[] dataOut) throws IOException {
        int localport = getLocalPort(myId, servers);
        listener = new ServerSocket(localport);

        // TODO: need an independent list of ACTIVE processes - like the nameserver

        // Accept connections from all smaller processes
        for(int i=1; i <= myId; i++){
            Socket s = listener.accept();
            BufferedReader dIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String getline = dIn.readLine();
            StringTokenizer st = new StringTokenizer(getline);
            int hisId = Integer.parseInt(st.nextToken());
            int destId = Integer.parseInt(st.nextToken());
            String tag = st.nextToken();
            if (tag.equals("hello")){
                link[hisId] = s;
                dataIn[hisId] = dIn;
                dataOut[hisId] = new PrintWriter(s.getOutputStream());
            }
        }

        // Contact all the bigger processes
        for (OtherServer server : servers.getServerList()) {
            // OtherServer os = servers.searchId(i); // TODO: What happens if the destination server is not found?  Can it be missing?
            // ^^ this should retry until the other servers have started - do while loop with a pause: Thread.sleep(100)
            // Should use the above-mentioned list of ACTIVE servers
            int serverIndex = server.getId() - 1;
            while (link[serverIndex] == null) {
                try {
                    link[serverIndex] = new Socket(server.getAddress(), server.getPort());
                }
                catch(IOException e) {
                    try {
                        System.out.println("Trying to connect connection to " + server.getAddress() + ":" + server.getPort()); // e1.printStackTrace();
                        Thread.sleep(250);
                    } catch (InterruptedException ignore) {}
                }
            }
            dataOut[serverIndex] = new PrintWriter(link[serverIndex].getOutputStream());
            dataIn[serverIndex] = new BufferedReader(new InputStreamReader(link[serverIndex].getInputStream()));

            // Send a hello message to P_i
            dataOut[serverIndex].println(myId + " " + serverIndex + " " + "hello" + " " + "null");
            dataOut[serverIndex].flush();
        }
    }

    int getLocalPort(int id, ServerList servers){
        return servers.searchId(id).getPort();
    }

    public void closeSockets(){
        try {
            listener.close();
            for(Socket sock : link) {
                sock.close();
            }
        }
        catch(Exception e) {
            System.err.println(e);
        }
    }

}
