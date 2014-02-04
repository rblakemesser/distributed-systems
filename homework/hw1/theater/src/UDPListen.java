import java.net.*;
import java.io.*;
public class UDPListen {
    DatagramPacket datapacket, returnpacket;
    int port = 2018;
    int len = 1024;
    public UDPListen(OrderHandler oh) {
        //OrderHandler orderHandler = new OrderHandler(100);

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

                    String[] splitCommand = clientCommand.split(" ");
                    if (splitCommand[0].equals("reserve") && splitCommand.length == 2) {
                        response = oh.reserve(splitCommand[1]);
                    } else if(splitCommand[0].equals("bookSeat") && splitCommand.length == 3) {
                        response = oh.bookSeat(splitCommand[1], Integer.parseInt(splitCommand[2]));
                    } else if(splitCommand[0].equals("search") && splitCommand.length == 2) {
                        response = oh.search(splitCommand[1]);
                    } else if(splitCommand[0].equals("delete") && splitCommand.length == 2) {
                        response = oh.delete(splitCommand[1]);
                    } else {
                        response = "ERROR"; // reply "I don't understand the command"
                    }

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
