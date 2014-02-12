import java.util.*;
public class RAMutexK extends Process implements Lock {  
    int k;
    public RAMutexK(Linker initComm) {
        super(initComm);
        k = k;
        // unchanged code removed
    }
    public synchronized void requestCS() {
        c.tick();
        myts = c.getValue();
        broadcastMsg("request", myts);
        numOkay = 0;
        while (numOkay - k < N-1)
            myWait(); 
    }
    // unchanged methods removed
}
