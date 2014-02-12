public class LamportMutexK extends LamportMutex implements Lock {
    DirectClock v;
    int[] q;
    int k;  // number of concurrent processes
    public LamportMutexK(Linker initComm, int k) {
        super(initComm);
        v = new DirectClock(N, myId);
        q = new int[N];
        k = k; // k gets passed in then instantiated as attr
        for (int j = 0; j < N; j++) 
            q[j] = Symbols.Infinity;
    }

    // other methods removed for readability
    
    boolean okayCS() {
        int numInCS = 0;
        for (int j = 0; j < N; j++){
            if (isGreater(q[myId], myId, q[j], j) | isGreater(q[myId], myId, v.getValue(j), j)) {
                numInCS++;
                        }
        }
        if numInCS >= k {
            return false;
        }
        return true;
    }

    // other methods removed for readability
}
