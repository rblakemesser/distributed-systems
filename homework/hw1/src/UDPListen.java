import java.net.*;
import java.io.*;
public class UDPListen {
    DatagramPacket datapacket, returnpacket;
    int port = 2018;
    int len = 1024;
    public UDPListen() {
        try {
            DatagramSocket datasocket = new DatagramSocket(port);
            byte[] buf = new byte[len];
            datapacket = new DatagramPacket(buf, buf.length);
            while (true) {
                try {
                    datasocket.receive(datapacket);
                    returnpacket = new DatagramPacket(
                            datapacket.getData(),
                            datapacket.getLength(),
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
