import org.apache.commons.cli.*;

public class TheaterCLI {
    public static void main(String[] args){
        Options options = new Options();
        options.addOption("s", true, "Number of seats");
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(options, args);

            // SERVER
            if (cmd.hasOption("s")) {
                System.out.println("Running in server mode!");
                int numSeats = Integer.parseInt(cmd.getOptionValue("s"));
                System.out.println("Number of seats selected: " + numSeats);
            }

            // CLIENT
            else if (cmd.hasOption("p")) {
                System.out.println("Running in client mode!");
            }

            else {
            }

        }
        catch (ParseException parseExcept) {
            System.out.println(parseExcept.getMessage());
        }
    }
}
