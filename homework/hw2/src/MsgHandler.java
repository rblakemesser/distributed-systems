import java.io.IOException;

public interface MsgHandler {
    public String handleMsg(Msg m, int srcId, String tag);
    public Msg receiveMsg(int fromId) throws IOException;
}
