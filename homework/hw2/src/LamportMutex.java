
public class LamportMutex extends Process implements Lock {
    DirectClock v;
    int[] q;  // request queue

    public LamportMutex(Linker initComm, CommandHandler commandHandler) {
        super(initComm);
        v = new DirectClock(comm.numProc, comm.myIdx);
        q = new int[comm.numProc];
        for(int j=0; j < comm.numProc; j++){
            q[j] = -1;
        }
        commandHandler.registerMutex(this);
    }

    @Override
    public synchronized void requestCS() {
        v.tick();
        q[comm.myIdx] = v.getValue(comm.myIdx);
        broadcastMsg("request", q[comm.myIdx]);
        while(!okayCS()){
            myWait();
        }
    }

    @Override
    public synchronized void releaseCS() {
        q[comm.myIdx] = -1;
        broadcastMsg("release", v.getValue(comm.myIdx));
    }

    boolean okayCS() {
        for(int j=0; j<comm.numProc; j++){
            if(isGreater(q[comm.myIdx], comm.myIdx, q[j], j))
                return false;
            if(isGreater(q[comm.myIdx], comm.myIdx, v.getValue(j), j))
                return false;
        }
        return true;
    }

    boolean isGreater(int entry1, int pid1, int entry2, int pid2){
        if (entry2 == -1) {
            return false;
        }
        return ((entry1 > entry2) || ((entry1 == entry2) && (pid1 > pid2)));
    }

    public synchronized void handleMsg(Msg m, int src, String tag){
        int timestamp = m.getMessageInt();
        v.receiveAction(src, timestamp);
        if(tag.equals("request")) {
            q[src] = timestamp;
            sendMsg(src, "ack", v.getValue(comm.myIdx));
        }else if (tag.equals("release")){
            q[src] = -1;
        }
        notify();  // okayCS() may be true now
    }
}
