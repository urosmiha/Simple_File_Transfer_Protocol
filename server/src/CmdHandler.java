import javax.sql.rowset.spi.SyncResolver;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class CmdHandler {

    private HelperFunctions helper;
    private StatusEnum status;
    private String s_user;
    private String s_account;
    private String s_password;

    private String s_dir;
    private String tmp_dir = "";
    private String s_type;

    private CdirSatatus cdir_pass_acct = CdirSatatus.WAITNON;

//    DataOutputStream out;

    public CmdHandler() {

//        try {
//            out = new DataOutputStream(s.getOutputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        s_dir = System.getProperty("user.dir");
        status = StatusEnum.LOGGEDOUT;
        s_user = "";
        helper = new HelperFunctions();
        s_account = "";
        s_password = "";

        s_type = "";
    }

    protected String handleCommand(String cmd) {

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
            case "CDIR":
                return changeWorkingDir(arg);
            case "KILL":
                return DeleteFile(arg);
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

            if(cdir_pass_acct.equals(CdirSatatus.WAITBOTH)) {
                if(account.equals(helper.getUserAccount(s_user))) {
                    cdir_pass_acct = CdirSatatus.WAITPASS;
                    return "+account ok, send password";
                }
                else {
                    return "-invalid account";
                }
            }
            else if(cdir_pass_acct.equals(CdirSatatus.WAITACCT)) {
                if(account.equals(helper.getUserAccount(s_user))) {
                    cdir_pass_acct = CdirSatatus.WAITNON;
                    s_dir = tmp_dir;
                    return "!Changed working dir to " + tmp_dir;
                }
                else {
                    return "-invalid account";
                }
            }
            else {
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
        }
        else {
            return "-Please specify user first";
        }
    }

    private String authorisePassword(String password) {

        if(!s_user.isEmpty()) {
            if(cdir_pass_acct.equals(CdirSatatus.WAITBOTH)) {
                if(password.equals(helper.getUserPassword(s_user))) {
                    cdir_pass_acct = CdirSatatus.WAITACCT;
                    return "+account ok, send account";
                }
                else {
                    return "-invalid password";
                }
            }
            else if(cdir_pass_acct.equals(CdirSatatus.WAITPASS)) {
                if(password.equals(helper.getUserPassword(s_user))) {
                    cdir_pass_acct = CdirSatatus.WAITNON;
                    s_dir = tmp_dir;
                    return "!Changed working dir to " + tmp_dir;
                }
                else {
                    return "-invalid password";
                }
            }
            else {
                if (!s_password.isEmpty()) {
                    return "-Password is already set";
                }
                if (password.equals(helper.getUserPassword(s_user))) {
                    s_password = password;
                    // if current account is not required or it was already set then login
                    if (helper.getUserAccount(s_user).equals(" ") || !s_account.isEmpty()) {
                        status = StatusEnum.LOGGEDIN;
                        return "! Logged in";
                    }   // if the account was not set then ask for it
                    else {
                        return "+Send Account";
                    }
                } else {
                    return "-Wrong password, try again";
                }
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
                File folder = new File(s_dir);
                File[] listOfFiles = folder.listFiles();
                String response = "+" + s_dir + "\r\n";

                assert listOfFiles != null;
                for (File listOfFile : listOfFiles) {
                    // Append files
                    if (listOfFile.isFile()) {
                        switch (format) {
                            case "R":
                                response += listOfFile.getName() + "\r\n";
                                break;
                            case "V":
                                response += listOfFile.getName() + ": ";
                                response += "Size: " + listOfFile.length() + "; ";
                                response += "Protection: ";
                                if (listOfFile.canExecute()) {
                                    response += "can Execute,";
                                }
                                if (listOfFile.canRead()) {
                                    response += "can Read,";
                                }
                                if (listOfFile.canWrite()) {
                                    response += "can Write";
                                }
                                response += "; ";
                                String time = helper.msToDays(listOfFile.lastModified());
                                response += "Last modified " + time + " days ago; ";
                                response += "Last Author: ;";
                                response += "\r\n";
                                break;
                            default:
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

    private String changeWorkingDir(String dir) {

        System.out.println("Direcorty: " + dir);


        if(status.equals(StatusEnum.LOGGEDIN)) {

            try {
                File file = new File(dir);
                if (file.isDirectory()) {
                    file = file.getParentFile();
                    if (file.exists()) {
                        tmp_dir = dir;
                        if (helper.getUserAccount(s_user).equals(" ") && helper.getUserPassword(s_user).equals(" ")) {
                            s_dir = dir;
                            return "!Changed working dir to " + dir;
                        }
                        else {
                            if(!helper.getUserAccount(s_user).equals(" ") && !helper.getUserPassword(s_user).equals(" ")) {
                                cdir_pass_acct = CdirSatatus.WAITBOTH;
                            }
                            else if(!helper.getUserAccount(s_user).equals(" ")) {
                                cdir_pass_acct = CdirSatatus.WAITPASS;
                            }
                            else if(!helper.getUserPassword(s_user).equals(" ")) {
                                cdir_pass_acct = CdirSatatus.WAITACCT;
                            }
                            return "+directory ok, send account/password";
                        }
                    }
                    else {
                        return "-Can't connect to directory because: specified directory does not exists";
                    }
                }
                else {
                    return "-Can't connect to directory because: specified path is not a valid directory";
                }
            }
            catch (Exception e) {
                return "-Can't connect to directory because: " + e;
            }
        }
        else {
            return "-Access denied, please login";
        }
    }

    /* Delete specified sile*/
    private String DeleteFile(String arg) {

        try {
            File file = new File(s_dir + File.separator + arg);
            if (!file.isDirectory()) {
                System.out.println(file);
                if (file.exists()) {
                    if(file.delete())
                    {
                        return ("+" + arg + " deleted");
                    }
                }
                else {
                    return "-Not deleted because: the file you specified does not exists";
                }
            }
            else {
                return "-Not deleted because: the file you specified is directory, please specify file";
            }
        }
        catch (Exception e) {
            return "-Not deleted because: " + e;
        }
        return "-Not deleted because: Unexpected Error occurred";
    }

}
