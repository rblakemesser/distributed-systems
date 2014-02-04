import CommonsCLI.DefaultParser;
import CommonsCLI.Options;
import CommonsCLI.CommandLineParser;
import CommonsCLI.CommandLine;
import CommonsCLI.ParseException;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
            int port = Integer.parseInt(cmd.getOptionValue("p"));

            // SERVER
            if (cmd.hasOption("s")) {
                int numSeats = Integer.parseInt(cmd.getOptionValue("s"));
                OrderHandler orderHandler = new OrderHandler(numSeats);
                System.out.println("Number of seats selected: " + numSeats);
                new UDPListen(orderHandler);
            }

            // CLIENT
            else if (cmd.hasOption("c")) {
                String hostname;
                String message;
                String response;
                if (cmd.hasOption("ip")) {
                    hostname = cmd.getOptionValue("ip");
                }
                else {
                    hostname = "localhost";
                }
                BufferedReader stdinp = new BufferedReader(new InputStreamReader(System.in));

                // UDP MODE
                if (cmd.hasOption("u")) {
                    DatagramPacket sPacket, rPacket;
                    try {
                        InetAddress ia = InetAddress.getByName(hostname);
                        DatagramSocket datasocket = new DatagramSocket();
                        while (true) {
                            try {
                                String echoline = new String(stdinp.readLine());
                                int inputLength = echoline.length();
                                if (echoline.equals("done")) break;
                                byte[] buffer = new byte[echoline.length()];
                                buffer = echoline.getBytes();
                                sPacket = new DatagramPacket(buffer, inputLength, ia, port);
                                datasocket.send(sPacket);
                                byte[] rbuffer = new byte[1024];
                                rPacket = new DatagramPacket(rbuffer, rbuffer.length);
                                datasocket.receive(rPacket);
                                response = new String(rPacket.getData(), 0, rPacket.getLength());
                                System.out.println(response);
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
                    while (true){
                        BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
                        Socket clientSocket = new Socket(hostname, port);
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        message = inFromUser.readLine();
                        outToServer.writeBytes(message + '\n');
                        response = inFromServer.readLine();
                        System.out.println(response);
                        clientSocket.close();
                    }
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
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
