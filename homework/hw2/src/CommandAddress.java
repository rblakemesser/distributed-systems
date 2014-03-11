import java.io.DataOutputStream;

public class CommandAddress {
    public String cmd;
    public DataOutputStream addy;

    public CommandAddress(String cmd, DataOutputStream addy) {
        this.cmd = cmd;
        this.addy = addy;
    }
}
