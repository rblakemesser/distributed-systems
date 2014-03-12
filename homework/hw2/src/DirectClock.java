public class DirectClock {
    public int[] clock;
    int myId;
    int myIndex;
    public DirectClock(int numProc, int idx) {
        myIndex = idx;
        myId = myIndex + 1;
        clock = new int[numProc];
        for(int i=0;i<numProc;i++) {
            clock[i] = 0;
        }
        clock[myIndex] = 1;
    }
    public int getValue(int i){
        return clock[i];
    }
    public void tick(){
        clock[myIndex]++;
    }
    public void sendAction(){
        // sentValue = clock[myId];
        tick();
    }
    public void receiveAction(int sender, int sentValue){
        clock[sender] = Math.max(clock[sender], sentValue);
        clock[myIndex] = Math.max(clock[myIndex], sentValue) + 1;
    }
}
