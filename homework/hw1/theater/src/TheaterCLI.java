import org.apache.commons.cli.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class TheaterCLI {
    public static void main(String[] args){
        Options options = new Options();
        options.addOption("p", true, "Port number (required all) For server: where to listen. For client: where to send.");
        options.addOption("s", true, "Number of seats (required server only)");
        options.addOption("c", false, "Running in client mode? (required client only)");
        options.addOption("t", false, "TCP (optional client only)");
        options.addOption("u", false, "UDP (optional client only)");
        options.addOption("ip", true, "Server IP location (optional client only)");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);
            int port = Integer.getInteger(cmd.getOptionValue("p"));

            // SERVER
            if (cmd.hasOption("s")) {
                OrderHandler orderHandler = new OrderHandler(Integer.valueOf(cmd.getOptionValue("s")));
                int numSeats = Integer.parseInt(cmd.getOptionValue("s"));
                System.out.println("Number of seats selected: " + numSeats);
            }

            // CLIENT
            else if (cmd.hasOption("c")) {
                BufferedReader stdinp = new BufferedReader(new InputStreamReader(System.in));

                // UPD MODE
                if (cmd.hasOption("u")) {
                    String hostname;
                    DatagramPacket sPacket, rPacket;
                    if (cmd.hasOption("ip")) {
                        hostname = cmd.getOptionValue("ip");
                    }
                    else {
                        hostname = "localhost";
                    }
                    try {
                        InetAddress ia = InetAddress.getByName(hostname);
                        DatagramSocket datasocket = new DatagramSocket();
                        while (true) {
                            try {
                                String echoline = stdinp.readLine();
                                if (echoline.equals("done")) break;
                                byte[] buffer = echoline.getBytes();
                                sPacket = new DatagramPacket(buffer, buffer.length, ia, port);
                                datasocket.send(sPacket);
                                byte[] rbuffer = new byte[1024];
                                rPacket = new DatagramPacket(rbuffer, rbuffer.length);
                                datasocket.receive(rPacket);
                                String retstring = new String(rPacket.getData());
                                System.out.println(retstring);
                            } catch (IOException e) {
                                System.err.println(e);
                            }
                        } // while
                    } catch (UnknownHostException e) {
                        System.err.println(e);
                    } catch (SocketException se) {
                        System.err.println(se);
                    }
                }

                // TCP MODE
                else if (cmd.hasOption("t")) {

                }

                else {
                    System.out.println("Invalid arguments-- you must select TCP or UDP");
                }

            }

            else {
            }

        }
        catch (ParseException parseExcept) {
            System.out.println(parseExcept.getMessage());
        }
    }
}
