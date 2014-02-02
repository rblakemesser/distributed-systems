/**
 * Created by douglasgunter on 1/25/14.
 */
public class CentMutex extends Process implements Lock {
    // assumes that P_0 coordinates and does not request locks
    boolean haveToken;
    final int leader = 0;
    IntLinkedList pendingQ = new IntLinkedList();

    public CentMutex(Linker initComm){
        super(initComm);
        haveToken = (myId == leader);
    }

    @Override
    public synchronized void requestCS() {
        sendMsg(leader, "request");
        while(!haveToken) myWait();
    }

    @Override
    public synchronized void releaseCS() {
        sendMsg(leader, "release");
        haveToken = false;
    }

    public synchronized void handleMsg(Msg m, int src, String tag){
        if (tag.equals("requset")){
            if(haveToken){
                sendMsg(src, "okay");
                haveToken = false;
            }else pendingQ.add(src);
        }else if (tag.equals("release")){
            if (!pendingQ.isEmpty()){
                int pid = pendingQ.removeHead();
                sendMsg(pid, "okay");
            } else haveToken = true;
        }else if (tag.equals("okay")){
            haveToken = true;
            notify();
        }
    }
}
