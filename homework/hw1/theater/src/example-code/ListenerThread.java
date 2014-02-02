import java.io.IOException;

/**
 * Created by douglasgunter on 1/25/14.
 */
public class ListenerThread extends Thread {
    int channel;
    MsgHandler process;
    public ListenerThread(int channel, MsgHandler process){
        this.channel = channel;
        this.process = process;
    }

    @Override
    public void run() {
        while(true){
            try{
                Msg m = process.receiveMsg(channel);
                process.handleMsg(m, m.getSrcId(), m.getTag());
            } catch (IOException e){
                System.err.println(e);
            }
        }
    }
}
