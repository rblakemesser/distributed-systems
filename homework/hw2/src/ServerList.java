import java.util.ArrayList;

public class ServerList {
    private final ArrayList<OtherServer> list;

    public ServerList(String[] serverList) {
        this.list = new ArrayList<OtherServer>();
        for (int i=0; i < serverList.length; i++ ) {
            try {
                this.list.add(new OtherServer(i, serverList[i]));
            }
            catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public String clientQuery(String[] request) {
        while (true) {
            for (OtherServer server : this.list) {
                // TODO: IMPLEMENT ME!
                // if server can connect
                    // issue command to server
                // if response
                    // return response
                // if no response before timeout
                    // stop listening for response and continue the loop
            }
        }
    }

    /**
     * Returns an available port from the list of local servers
     *
     */
    public int getAvailableLocalPort() {
        while (true) {
            for (OtherServer server : this.list) {
                if (server.isLocal()) {
                    if (server.portAvailable()) {
                        server.me = true;
                        return server.getPort();
                    }
                }
            }
        }
    }

    public ArrayList<OtherServer> getList(){
        return list;
    }

    public OtherServer searchId(int id){
        for(OtherServer os : list){
            if (os.getId() == id){
                return os;
            }
        }
        return null; // TODO: handle an "ID is not in the list" type error
    }

}
