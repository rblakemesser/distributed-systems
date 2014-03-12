import java.io.DataOutputStream;
import java.net.Socket;

public class MessageTrace {
    public String cmd;
    public DataOutputStream addy;
    Socket sock;

    public MessageTrace(String cmd, DataOutputStream addy, Socket connectionSocket) {
        this.cmd = cmd;
        this.addy = addy;
        this.sock = connectionSocket;
    }
}
