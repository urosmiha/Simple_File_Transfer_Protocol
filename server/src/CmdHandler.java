import java.awt.*;
import java.io.*;
import java.net.Socket;

public class CmdHandler {

    private HelperFunctions helper;
    private StatusEnum status;
    private String s_user;
    private String s_account;
    private String s_password;

    private String s_type;

//    DataOutputStream out;

    public CmdHandler() {

//        try {
//            out = new DataOutputStream(s.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        status = StatusEnum.LOGGEDOUT;
        s_user = "";
        helper = new HelperFunctions();
        s_account = "";
        s_password = "";

        s_type = "";
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
                return authorisePassword(arg);
            case "DONE":
                return "+";
            case "TYPE":
                return changeType(arg);
            case "LIST":
                return listFiles(arg);
            default:
                return "fe";
        }
    }

//    Authorise the enetered user-id
    private String authoriseUser(String user) {

        if(status.equals(StatusEnum.LOGGEDOUT)) {

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
                        s_user = user;
                        // If user-id does NOT have an associated account and password then immediately login user
                        if(helper.getUserAccount(user).equals(" ") && helper.getUserPassword(user).equals(" ")) {
                            status = StatusEnum.LOGGEDIN;
                            return "!"+ user + " logged in";
                        } // If user-id does NOT have an associated account but it needs a password
                        else if(helper.getUserAccount(user).equals(" ")){
                            return "+User-id valid, send password";
                        }   // any other way go to next step in login process
                        else {
                            return "+User-id valid, send account and password";
                        }
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
            return "-" + s_user + " is currently signed in";
        }
    }

    private String authoriseAccount(String account) {

        // If user-id was set to some value
        if(!s_user.isEmpty()) {
            if(!s_account.isEmpty()) {
                return "-Account is already set";
            }
            // Check if entered account matches the currently logged in user account
            if (account.equals(helper.getUserAccount(s_user))) {
                s_account = account;
                // If user-id does not require a password then just login or password was already set
                if(helper.getUserPassword(s_user).equals(" ") || !s_password.isEmpty()) {
                    status = StatusEnum.LOGGEDIN;
                    return  "! Account valid, logged-in";
                } // If account does not have a password then just login user immediately
                else {
                    return  "+Account valid, send password";
                }
            }
            else {
                return "-Invalid account, try again";
            }
        }
        else {
            return "-Please specify user first";
        }
    }

    private String authorisePassword(String password) {

        if(!s_user.isEmpty()) {
            if(!s_password.isEmpty()) {
                return "-Password is already set";
            }
            if(password.equals(helper.getUserPassword(s_user))) {
                s_password = password;
                // if current account is not required or it was already set then login
                if(helper.getUserAccount(s_user).equals(" ") || !s_account.isEmpty()) {
                    status = StatusEnum.LOGGEDIN;
                    return "! Logged in";
                }   // if the account was not set then ask for it
                else {
                    return "+Send Account";
                }
            }
            else {
                return "-Wrong password, try again";
            }
        }
        else {
            return "-Please specify user first";
        }
    }

//  ----------------------------------------------------
    /// Only valid if user is logged in \\\

    private String changeType(String type) {
        if(status.equals(StatusEnum.LOGGEDIN)) {

            switch (type) {
                case "A":
                    s_type = "Ascii";
                    break;
                case "B":
                    s_type = "Binary";
                    break;
                case "C":
                    s_type = "Continuous";
                    break;
                default:
                    return "-Type not valid";
            }
            return "+Using " + s_type + "mode";
        }
        else {
            return "-Access denied, please login";
        }
    }

    private String listFiles(String format) {
        System.out.println(status);
        if(status.equals(StatusEnum.LOGGEDIN)) {
            try {
                File folder = new File(System.getProperty("user.dir"));
                File[] listOfFiles = folder.listFiles();
                String response = "+" + System.getProperty("user.dir") + "\r\n";

                assert listOfFiles != null;
                for (int i = 0; i < listOfFiles.length; i++) {
                    // Append files
                    if (listOfFiles[i].isFile()) {
                        if(format.equals("R")) {
                            response += listOfFiles[i].getName() + "\r\n";
                        }
                        else if (format.equals("V")) {
                            response += listOfFiles[i].getName() + ": ";
                            response += "Size: " + listOfFiles[i].length() + "; ";
                            response += "Protection: ";
                            if(listOfFiles[i].canExecute()) {
                                response += "can Execute,";
                            }
                            if(listOfFiles[i].canRead()) {
                                response += "can Read,";
                            }
                            if(listOfFiles[i].canWrite()) {
                                response += "can Write";
                            }
                            response += "; ";
                            String time = helper.msToDays(listOfFiles[i].lastModified());
                            response += "Last modified " + time + " days ago; ";
                            response += "Last Author: ;";
                            response += "\r\n";
                        }
                        else {
                            return "-Invalid format specified, please try again";
                        }
                    }
                }
                return response;

            } catch (Exception e) {
                return "-" + e;
            }
        }
        else {
            return "-Access denied, please login";
        }
    }

}
