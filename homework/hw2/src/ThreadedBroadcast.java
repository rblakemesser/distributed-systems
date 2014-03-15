class ThreadedBroadcast extends Thread implements Runnable{
    Linker comm;
    int i;
    String msg;
    LamportMutex lm;

    ThreadedBroadcast(Linker comm, int i, String msg, LamportMutex lm){
        this.comm = comm;
        this.i = i;
        this.msg = msg;
        this.lm = lm;
    }
    @Override
    public void run() {
        String response = comm.sendMsg(i, msg);
        if (response == null || response.equals("null")) return;
        String[] splitResponse = response.split(" ");  // returns the acks
        if (splitResponse.length < 3) return;
        int ts = Integer.parseInt(splitResponse[2]);
        int senderIdx = Integer.parseInt(splitResponse[0]);
        String tag = splitResponse[1];

        lm.handleMsg(ts, senderIdx, tag);
    }
    public String toString(){ return "";}
}
