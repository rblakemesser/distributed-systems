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
    int myId, N;
    public IntLinkedList neighbors = new IntLinkedList();
    public Linker(String basename, int id, int numProc, ServerList servers) throws Exception {
        myId = id;
        N = numProc;
        dataIn = new BufferedReader[numProc];
        dataOut = new PrintWriter[numProc];
        // Replacement for Topology - assume all processes are neighbors
        for (int j = 0; j < N; j++)
            if (j != myId) neighbors.add(j);
        connect(basename, myId, numProc, dataIn, dataOut, servers);
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
    public int getNumProc() { return N; }

    public void connect(String basename, int myId, int numProc,
                        BufferedReader[] dataIn, PrintWriter[] dataOut,
                        ServerList servers) throws Exception {

        link = new Socket[numProc];
        int localport = getLocalPort(myId, servers);
        listener = new ServerSocket(localport);

        // TODO: need an independent list of ACTIVE processes - like the nameserver

        // Accept connections from all smaller processes
        for(int i=1; i<myId; i++){
            Socket s = listener.accept();
            BufferedReader dIn = new BufferedReader(
                    new InputStreamReader(s.getInputStream()));
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
        for (int i=myId; i < numProc; i++) {
            OtherServer os = servers.searchId(i); // TODO: What happens if the destination server is not found?  Can it be missing?
            // ^^ this should retry until the other servers have started - do while loop with a pause: Thread.sleep(100)
            // Should use the above-mentioned list of ACTIVE servers

            link[i] = new Socket(os.getAddress(), os.getPort());
            dataOut[i] = new PrintWriter(link[i].getOutputStream());
            dataIn[i] = new BufferedReader(new InputStreamReader(link[i].getInputStream()));

            // Send a hello message to P_i
            dataOut[i].println(myId + " " + i + " " + "hello" + " " + "null");
            dataOut[i].flush();
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

