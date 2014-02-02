/**
 * Created by douglasgunter on 1/25/14.
 */
import java.io.*;
public interface MsgHandler {
    public void handleMsg(Msg m, int srcId, String tag);
    public Msg receiveMsg(int fromId) throws IOException;
}
