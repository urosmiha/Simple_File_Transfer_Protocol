import java.io.*;

public class CmdHandler {

    StatusEnum status;
    String current_user;

    public CmdHandler() {
        status = StatusEnum.SIGNEDOUT;
    }

    public String handleCommand(String cmd) {

        // First check if the command is at least 5 characters long and if there is a space after the 4 letter command
        // e.g. 'USER ' is valid, but 'USER' or 'USERx' will return error.
        if(cmd.length() < 5 || cmd.charAt(4) != ' ') {
            return "-invalid command";
        } // Add an empty character if user specified 4 char command but no arguments and let argument checkers take care of it.
        else if(cmd.length() < 6) {
            cmd += " ";
        }

        // Split user command so we can get the 4 character command and argument
        // We know that the command always starts with 4 characters and rest is arguments.
        String cdm_id = cmd.substring(0, Math.min(cmd.length(), 4));
        String arg = cmd.substring(5,cmd.length());

        switch(cdm_id) {
            case "USER":
                return authoriseUser(arg);
            case "ACCT":
                return "acct";
            case "PASS":
                return "hdh";
            case "DONE":
                return "+";
            default:
                return "fe";
        }
    }

//    CHANGE BACK TO PRIVATE ONCE FINISH TESTING
    public String authoriseUser(String user) {

        if(status.equals(StatusEnum.SIGNEDOUT)) {

            // If user logs in as a root then there is no password associated with it.
            // So set status as SIGNED-IN immediately.
            String root = "root";
            if(user.equals(root)) {
                status = StatusEnum.SIGNEDIN;
                current_user = user;
                return "!"+ root + " logged in";
            }

            // Read a text file with all the user-ids in it
            File file = new File("admin/users.txt");
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                return "-Failed to access user-ids";
            }

            // loop through the list of users and see if any matches the one sent from the client.
            // If there is a match then send success response
            String tmp_user;
            try {
                while ((tmp_user = br.readLine()) != null) {
                    if(user.endsWith(tmp_user)) {
                        status = StatusEnum.USERTRUE;
                        current_user = user;
                        return "+User-id valid, send account and password";
                    }
                }
            } catch (IOException e) {
                return "-Failed verify the user";
            }

            // Otherwise, this means that the user-id is not valid.
            return "-Invalid user-id, try again";
        }
        else {
            // If someone is already signed in then USER command should not be valid.
            return "-" + current_user + " already signed in, please logout first";
        }
    }
}
