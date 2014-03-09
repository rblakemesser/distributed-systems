
public class LamportMutex extends Process implements Lock {
    DirectClock v;
    int[] q;  // request queue

    public LamportMutex(Linker initComm) {
        super(initComm);
        v = new DirectClock(N, myId);
        q = new int[N];
        for(int j=0; j < N; j++){
            q[j] = -1;
        }
    }

    @Override
    public synchronized void requestCS() {
        v.tick();
        q[myId] = v.getValue(myId);
        broadcastMsg("request", q[myId]);
        while(!okayCS()){
            myWait();
        }
    }

    @Override
    public synchronized void releaseCS() {
        q[myId] = -1;
        broadcastMsg("release", v.getValue(myId));
    }

    boolean okayCS() {
        for(int j=0; j<N; j++){
            if(isGreater(q[myId], myId, q[j], j))
                return false;
            if(isGreater(q[myId], myId, v.getValue(j), j))
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
            sendMsg(src, "ack", v.getValue(myId));
        }else if (tag.equals("release")){
            q[src] = -1;
        }
        notify();  // okayCS() may be true now
    }
}
