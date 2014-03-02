import java.util.ArrayList;

public class OtherServerList {
    private final ArrayList<OtherServer> list;

    public OtherServerList(String[] serverList) {
        this.list = new ArrayList<OtherServer>();
        for (int i=0; i < serverList.length; i++ ) {
            try {
                this.list.add(new OtherServer(i, serverList[i]));
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
