import java.io.IOException;

public class Process implements MsgHandler {
    Linker comm;

    public Process(Linker initComm) {
        comm = initComm;
    }
    public synchronized void handleMsg(Msg m, int src, String tag){}
    public void sendMsg(int destId, String tag, String msg){
        comm.sendMsg(destId,tag,msg);
    }
    public void sendMsg(int destId, String tag, int msg){
        comm.sendMsg(destId, tag, String.valueOf(msg));
    }
    public void sendMsg(int destId, String tag, int msg1, int msg2){
        comm.sendMsg(destId, tag, String.valueOf(msg1) + " " + String.valueOf(msg2) + " ");
    }
    public void sendMsg(int destId, String tag){
        comm.sendMsg(destId, tag, " 0 ");
    }
    public void broadcastMsg(String tag, int msg){
        for(int i=0; i < comm.numProc; i++){
            if (i != comm.myIdx){
                sendMsg(i, tag, msg);
            }
        }
    }
    public void sendToNeighbors(String tag, int msg){
        for (int i=0; i<comm.numProc;i++){
            if(isNeighbor(i)) sendMsg(i, tag, msg);
        }
    }

    public boolean isNeighbor(int i){
        return comm.neighbors.contains(i);
    }

    @Override
    public Msg receiveMsg(int fromId) {
        LibraryCLI.safePrintln("Process: message received");
        try {
            return comm.receiveMsg(fromId);
        } catch (IOException e) {
            LibraryCLI.safePrintln(e.getMessage());
            comm.closeSockets();
            return null;
        }
    }

    public synchronized void myWait(){
        try{
            wait();
        }
        catch (InterruptedException e){ System.err.println(e); }
    }
}
