import java.net.*;
import java.io.*;
public class UDPListen extends Thread{
    DatagramPacket datapacket, returnpacket;
    int port;
    int len = 1024;
    OrderHandler oh;

    public UDPListen(int port, OrderHandler oh){
        this.port = port;
        this.oh = oh;
    }

    @Override
    public void run() {
        try {
            DatagramSocket datasocket = new DatagramSocket(port);
            byte[] buf = new byte[len];
            //datapacket = new DatagramPacket(buf, buf.length);
            while (true) {
                String response;
                try {
                    datapacket = new DatagramPacket(buf, buf.length);
                    datasocket.receive(datapacket);
                    String clientCommand = new String(datapacket.getData(), 0, datapacket.getLength());

                    response = oh.handleCommand(clientCommand);

                    returnpacket = new DatagramPacket(
                            response.getBytes(),
                            response.getBytes().length,
                            datapacket.getAddress(),
                            datapacket.getPort());
                    datasocket.send(returnpacket);
                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        } catch (SocketException se) {
            System.err.println(se);
        }
    }
}
