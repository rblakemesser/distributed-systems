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
                boolean serverMode = true;
                int numSeats = Integer.parseInt(cmd.getOptionValue("s"));
                System.out.println("Running in server mode! " + numSeats + " seats selected for theater.");
            }

            // CLIENT
            else {
                System.out.println("Running in client mode!");
            }

        }
        catch (ParseException parseExcept) {
            System.out.println(parseExcept.getMessage());
        }
    }
}
