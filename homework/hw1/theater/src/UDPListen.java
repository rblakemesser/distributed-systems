import java.awt.*;
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
            datapacket = new DatagramPacket(buf, buf.length);
            while (true) {
                try {
                    datasocket.receive(datapacket);
                    String clientCommand = datapacket.getData().toString();
                    String[] splitCommand = clientCommand.split(" ");

                    if(splitCommand[0].equals("reserve") && splitCommand.length == 2){
                        // call OrderHandler.reserve(splitCommand[1]);
                        oh.reserve(splitCommand[1]);
                    }else if(splitCommand[0].equals("bookSeat") && splitCommand.length == 3){
                        // call OrderHandler.bookSeat(splitCommand[1], splitCommand[2]);
                        oh.bookSeat(splitCommand[1], Integer.parseInt(splitCommand[2]));
                    }else if(splitCommand[0].equals("search") && splitCommand.length == 2){
                        // call OrderHandler.search(splitCommand[1]);
                        oh.search(splitCommand[1]);
                    }else if(splitCommand[0].equals("delete") && splitCommand.length == 2){
                        // call OrderHandler.delete(splitCommand[1]);
                        oh.delete(splitCommand[1]);
                    }else {
                        // reply "I don't understand the command"
                    }



                } catch (IOException e) {
                    System.err.println(e);
                }
            }
        } catch (SocketException se) {
            System.err.println(se);
        }
    }
}
