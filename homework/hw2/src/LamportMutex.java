public class LamportMutex {
    DirectClock v;
    int[] q;  // request queue
    Linker comm;

    public LamportMutex(Linker initComm, CommandHandler commandHandler) {
        comm = initComm;
        v = new DirectClock(comm.numProc, comm.myIdx);
        q = new int[comm.numProc];
        for(int j=0; j < comm.numProc; j++){
            q[j] = -1;
        }
        commandHandler.registerMutex(this);
    }

    public synchronized void myWait(){
        try{
            wait();
        }
        catch (InterruptedException e){ System.err.println(e); }
    }

    public synchronized void requestCS() {
        v.tick();
        q[comm.myIdx] = v.getValue(comm.myIdx);
        broadcastMsg("request", q[comm.myIdx]);
        while(!okayCS()){
            myWait();
        }
    }

    public synchronized void releaseCS() {
        q[comm.myIdx] = -1;
        // TODO: NEED TO SEND BOOK STATUSES HERE - static fields won't work across multiple instances of the application
        broadcastMsg("release", v.getValue(comm.myIdx));
    }

    boolean okayCS() {
        return true;
//        for(int j=0; j<comm.numProc; j++){
//            if(isGreater(q[comm.myIdx], comm.myIdx, q[j], j))
//                return false;
//            if(isGreater(q[comm.myIdx], comm.myIdx, v.getValue(j), j))
//                return false;
//        }
//        return true;
    }

    boolean isGreater(int entry1, int pid1, int entry2, int pid2){
        if (entry2 == -1) {
            return false;
        }
        return ((entry1 > entry2) || ((entry1 == entry2) && (pid1 > pid2)));
    }

    public synchronized String handleMsg(Msg m, int src, String tag){
        String response = null;
        LibraryCLI.safePrintln("LamportMutex: message received" + m);
        int timestamp = m.getMessageInt();
        v.receiveAction(src, timestamp);
        if(tag.equals("request")) {
            q[src] = timestamp;
            response = src + " ack " + v.getValue(comm.myIdx);
        }
        else if (tag.equals("release")){
            q[src] = -1;
        }
        notify();  // okayCS() may be true now
        return response;
    }

    public void broadcastMsg(String tag, int msg){
        for(int i=0; i < comm.numProc; i++){
            if (i != comm.myIdx){
                comm.sendMsg(i, tag + " " + msg);
            }
        }
    }

}
