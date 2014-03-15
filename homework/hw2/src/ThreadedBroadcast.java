/**
 * Created by douglasgunter on 3/15/14.
 */
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
        String[] response = comm.sendMsg(i, msg).split(" ");  // returns the acks
        if (response.length < 3) return;
        int ts = Integer.parseInt(response[2]);
        int senderIdx = Integer.parseInt(response[0]);
        String tag = response[1];

        lm.handleMsg(ts, senderIdx, tag);
        return;
    }
    public String toString(){ return "";}
}
