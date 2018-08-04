import java.awt.*;
import java.io.*;

public class CmdHandler {

    HelperFunctions helper;
    StatusEnum status;
    String current_user;

    public CmdHandler() {
        status = StatusEnum.LOGGEDOUT;
        current_user = " ";
        helper = new HelperFunctions();
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
                return authoriseAccount(arg);
            case "PASS":
                return "hdh";
            case "DONE":
                return "+";
            default:
                return "fe";
        }
    }

//    Authorise the enetered user-id
    private String authoriseUser(String user) {

        if(status.equals(StatusEnum.LOGGEDOUT)) {

            // If user logs in as a root then there is no password associated with it.
            // So set status as SIGNED-IN immediately.
            String root = "root";
            if(user.equals(root)) {
                status = StatusEnum.LOGGEDIN;
                current_user = user;
                return "!"+ root + " logged in";
            }

            // Read a text file with all the user-ids in it
            File file = new File("admin/users.txt");
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException e) {
                return "-";
            }

            // loop through the list of users and see if any matches the one sent from the client.
            // If there is a match then send success response
            String rd_ln = "";
            try {
                while ((rd_ln = br.readLine()) != null) {
                    String tmp_user = rd_ln.substring(0, rd_ln.indexOf("|"));
                    if(user.endsWith(tmp_user)) {
                        status = StatusEnum.USERTRUE;
                        current_user = user;
                        return "+User-id valid, send account and password";
                    }
                }
            } catch (IOException e) {
                return "-";
            }

            // Otherwise, this means that the user-id is not valid.
            return "-Invalid user-id, try again";
        }
        else {
            // If someone is already signed in then USER command should not be valid.
            return "-" + current_user + " is currently signed in, please logout first";
        }
    }

    private String authoriseAccount(String account) {

        // If we verified the user-id then we can check for the account
        if(status.equals(StatusEnum.USERTRUE)) {

            if (account.equals(helper.getUserAccount(current_user))) {
                status = StatusEnum.ACCTTRUE;
                return  "+Account valid, send password";
            }
            else if(helper.getUserAccount(current_user).equals(" ")) {         // No need for the password
                status = StatusEnum.LOGGEDIN;
                return  "! Account valid, logged-in";
            }
        }
        else if (current_user.equals(" ")) {
            return "-Please specify user first";
        }
        else {
            return "-" + current_user + " is currently signed in, please logout first";
        }

        return "-Invalid account, try again";
    }



}
