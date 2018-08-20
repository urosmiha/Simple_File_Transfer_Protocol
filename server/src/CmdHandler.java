import javafx.stage.Stage;

import java.io.*;

public class CmdHandler {

    private HelperFunctions helper;
    private StatusEnum status;
    private String s_user;
    private String s_account;
    private String s_password;

    private String s_dir;
    private String tmp_dir = "";
    private Type s_type;

    private boolean wait_new_name = false;
    private String old_name;

    private boolean wait_next_retr = false;
    private String file_name = "";

    private boolean allow_gen = true;
    private boolean store_file = false;
    private boolean wait_store = false;
    private String store_file_name = "default.txt";
    private int store_size = 0;
    private int max_store_size = 8192;

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
        s_type = Type.ASCII;    // Set type as ascii by default
    }

    protected String handleCommand(String cmd) {

        String cdm_id;

        // Special case where command can be only 4 characters (i.e. for RETR)
        if(cmd.length() == 4) {
            if(cmd.equals("SEND") || cmd.equals("STOP")) {
                cdm_id = cmd;
            }
            else {
                return "-invalid command";
            }
        }   // First check if the command is at least 5 characters long and if there is a space after the 4 letter command. e.g. 'USER ' is valid, but 'USER' or 'USERx' will return error.
        else if(cmd.length() < 5 || cmd.charAt(4) != ' ') {
            return "-invalid command";
        }
//        else if(cmd.length() < 6) {     // Add an empty character if user specified 4 char command but no arguments and let argument checkers take care of it.
//            cmd += " ";
//        }

        // Split user command so we can get the 4 character command and argument
        // We know that the command always starts with 4 characters and rest is arguments.
        cdm_id = cmd.substring(0, Math.min(cmd.length(), 4));

        String arg;
        if(cmd.length() > 4) {
            arg = cmd.substring(5,cmd.length());
        }
        else {
            arg = "";
        }

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
            case "NAME":
                return checkName(arg);
            case "TOBE":
                return renameFile(arg);
            case "RETR":
                return checkFileSize(arg);
            case "STOP":
                return stopFile();
            case "SEND":
                return sendFile();
            case "STOR":
                return checkStoreFile(arg);
            case "SIZE":
                return checkStoreSize(arg);
            case "NONE":
                return "Stored File";
            default:
                return "NONE";
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
                    s_type = Type.ASCII;
                    break;
                case "B":
                    s_type = Type.BINARY;
                    break;
                case "C":
                    s_type = Type.CONTINUOUS;
                    break;
                default:
                    return "-Type not valid";
            }
            return "+Using " + s_type + " mode";
        }
        else {
            return "-Access denied, please login";
        }
    }

    public Type getType() {
        return s_type;
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
        if(status.equals(StatusEnum.LOGGEDIN)) {
            try {
                File file = new File(s_dir + File.separator + arg);
                if (!file.isDirectory()) {
                    if (file.exists()) {
                        if (file.delete()) {
                            return ("+" + arg + " deleted");
                        }
                    } else {
                        return "-Not deleted because: the file you specified does not exists";
                    }
                } else {
                    return "-Not deleted because: the file you specified is directory, please specify file";
                }
            } catch (Exception e) {
                return "-Not deleted because: " + e;
            }
            return "-Not deleted because: Unexpected Error occurred";
        }
        else {
            return "-Access denied, please login";
        }
    }

    private String renameFile(String name) {
        if(status.equals(StatusEnum.LOGGEDIN)) {
            if (wait_new_name) {
                wait_new_name = false;
                try {
                    File file = new File(s_dir + File.separator + old_name);
                    File file2 = new File(s_dir + File.separator + name);

                    if (!file2.exists()) {
                        if (file.renameTo(file2)) {
                            return "+" + old_name + " renamed to " + name;
                        } else {
                            return "-File wasn't renamed because of unexpected error";
                        }
                    } else {
                        return "-File wasn't renamed because the file with same name already exists";
                    }
                } catch (Exception e) {
                    return "-File wasn't renamed because: " + e;
                }
            } else {
                return "-File wasn't renamed because you have to specify the name of the file first";
            }
        }
        else {
            return "-Access denied, please login";
        }
    }

    private String checkName(String name) {
        if(status.equals(StatusEnum.LOGGEDIN)) {
            try {
                File file = new File(s_dir + File.separator + name);
                if (!file.isDirectory() && file.exists()) {
                    wait_new_name = true;
                    old_name = name;
                    return "+File exists";
                } else {
                    return ("-Can't find " + name);
                }
            } catch (Exception e) {
                return ("-Error: " + e);
            }
        }
        else {
            return "-Access denied, please login";
        }
    }

    private String checkFileSize(String name) {
        if(status.equals(StatusEnum.LOGGEDIN)) {
            try {
                File file = new File(s_dir + File.separator + name);
                if (!file.isDirectory() && file.exists()) {

                    Type file_type = getFileType(name);

                    if(file_type == s_type || (file_type == Type.BINARY && s_type == Type.CONTINUOUS)) {
                        wait_next_retr = true;
                        file_name = name;
                        return ("" + file.length());
                    }
                    else {
                        return ("-Can't send " + file_type + " file as " + s_type);
                    }
                } else {
                    return "-File doesn't exists";
                }
            } catch (Exception e) {
                wait_next_retr = false;
                return ("Error: " + e);
            }
        }
        else {
            return "-Access denied, please login";
        }
    }

    private String sendFile() {
        if(status.equals(StatusEnum.LOGGEDIN)) {
            if(wait_next_retr) {
                wait_next_retr = false;
                // Use this only if the current mode is ascii
                if(s_type == Type.ASCII) {
                    StringBuffer contents = new StringBuffer();
                    BufferedReader input = null;
                    try {
                        input = new BufferedReader(new FileReader(file_name), 1);
                        String line = null; //not declared within while loop
                        while ((line = input.readLine()) != null) {
                            contents.append(line);
                            contents.append(System.getProperty("line.separator"));
                        }
                        return contents.toString();
                    } catch (IOException ex) {
                        return "-Bad request: " + ex;
                    }
                }
            }
        }
        else {
            return "-Access denied, please login";
        }
        return "-Please specify the file first";
    }

    public void sendBinary(DataOutputStream out) {
        String response = "-";
        if(status.equals(StatusEnum.LOGGEDIN)) {
            if(wait_next_retr) {
                wait_next_retr = false;
                try {
                    File file = new File(s_dir + File.separator + file_name);
                    long file_size = file.length();
                    System.out.println(file_size);
                    // Use this for reading the data.

                    FileInputStream fis = new FileInputStream(file_name);
                    byte[] buffer = new byte[(int)file_size];

                    while (fis.read(buffer) > 0) {
                        out.write(buffer);
                    }


                } catch (FileNotFoundException ex) {
                    System.out.println("Unable to open file '" + file_name + "'");
                    response = "-Unable to open file " + file_name;
                } catch (IOException ex) {
                    System.out.println("Error reading file '" + file_name + "'");
                    response = "Error getting file: " + file_name;
                }
            }
            else {
                response = "\"-Please specify the file first";
            }
        }
        else {
            response = "-Access denied, please login";
        }

        try {
            response = response + '\0' + '\n';
            out.writeBytes(response);
        } catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }

    private String stopFile() {
        if(status.equals(StatusEnum.LOGGEDIN)) {
            if(wait_next_retr) {
                wait_next_retr = false;
                return "+ok, RETR aborted";
            }
        }
        else {
            return "-Access denied, please login";
        }
        return "-Please specify the file first";
    }

    private String checkStoreFile(String arg) {

        String sub_cmd = "";
        String file_name = "";

        if(status.equals(StatusEnum.LOGGEDIN)) {
            // Check if the second part of the command is valid (i.e. only accept NEW, OLD, APP)
            System.out.println(arg.length());
            if (arg.length() < 5) {
                return "-Bad request";
            } else {
                try {
                    sub_cmd = arg.substring(0, Math.min(arg.length(), 3));
                    file_name = arg.substring(4);

                    Type file_type = getFileType(file_name);
                    if(file_type == s_type || (file_type == Type.BINARY && s_type == Type.CONTINUOUS)) {

                        File file = new File(s_dir + File.separator + file_name);
                        store_size = (int) file.length();
                        store_file = true;
                        store_file_name = file_name;
                        switch (sub_cmd) {
                            case "NEW":
                                if (file.exists()) {
                                    if (allow_gen) {
                                        return "+File exists, will create new generation of file";
                                    } else {
                                        store_file = false;
                                        return "-File exists, but system doesn't support generations";
                                    }
                                } else {
                                    return "+File does not exists, will create new file";
                                }
                            case "OLD":
                                if (file.exists()) {
                                    return "+Will write over old file";
                                } else {
                                    return "+Will create new file";
                                }
                            case "APP":
                                if (file.exists()) {
                                    return "+Will append to file";
                                } else {
                                    return "+Will create file";
                                }
                        }
                    }
                    else {
                        return ("-Can't send " + file_type + " file as " + s_type);
                    }
                } catch (Exception e) {
                    return "-Bad request: " + e;
                }
            }
            return "-Bad request. Try Again";
        }
        else {
            return "-Access denied, please login";
        }
    }

    private String checkStoreSize(String size) {
        if(store_file) {
            String tmp = size.replaceAll("[^0-9]","");     // Remove everything that is not a

            try {
                int store_size = Integer.parseInt(tmp);
            }
            catch (Exception e) {
                return ("-Bad request: " + e);
            }

            if(store_size > max_store_size) {
                store_file = false;
                return "-Not enough room, don't send it";
            }
            else {
                store_file = false;
                wait_store = true;
                return "+ok, waiting for file";
            }
        }
        else {
            return "-Please specify the file first";
        }
    }

    protected void storeFile(String arg) {

    }

    protected boolean getStoreState() {
        return wait_store;
    }

    protected void resetStoreState() {
        store_file = false;
        wait_store = false;
    }

//    public int getStoreSize() {
//        return store_size;
//    }

    protected String getStoreFileName() {
       return store_file_name;
    }

    /* Look at the file extenstion and return the type of the file based on that
        Can checks specific files.
        ASCII extensions:
            Web standards: html, xml, css, svg, json
            Source code: c, cpp, h, cs, js, py, java, rb, pl, php, sh
            Documents: txt, tex, markdown, asciidoc, rtf, ps
            Configuration: ini, cfg, rc, reg
            Tabular data: csv, tsv
        BINARY extensions:
            Images: jpg, png, gif, bmp, tiff, psd
            Videos: mp4, mkv, avi, mov, mpg, vob
            Audio: mp3, aac, wav, flac, ogg, mka, wma
            Documents: pdf, doc, xls, ppt, docx, odt
            Archive: zip, rar, 7z, tar, iso
            Database: mdb, accde, frm, sqlite
            Executable: exe, dll, so, class
    */
    private Type getFileType(String file) {

        String ext = file.substring(file.indexOf('.')+1);

        if(ext.equals("html") || ext.equals("xml") || ext.equals("css") || ext.equals("json") ||  ext.equals("csv") ||  ext.equals("tsv")) {
            return Type.ASCII;
        }
        else if(ext.equals("c") || ext.equals("cpp") || ext.equals("h") || ext.equals("cs") ||  ext.equals("js") ||  ext.equals("py") || ext.equals("java") || ext.equals("rb") || ext.equals("pl") || ext.equals("php") ||  ext.equals("sh")) {
            return Type.ASCII;
        }
        else if(ext.equals("txt") || ext.equals("tex") || ext.equals("markdown") || ext.equals("asciidoc") ||  ext.equals("rtf") || ext.equals("ps")) {
            return Type.ASCII;
        }
        else if((ext.equals("jpg") || ext.equals("png") || ext.equals("gif") || ext.equals("bmp") ||  ext.equals("tiff") || ext.equals("psd"))) {
            return Type.BINARY;
        }
        else if((ext.equals("mp4") || ext.equals("mvk") || ext.equals("avi") || ext.equals("mov") ||  ext.equals("mpg") || ext.equals("vob"))) {
            return Type.BINARY;
        }
        else if((ext.equals("mp3") || ext.equals("aac") || ext.equals("wav") || ext.equals("flac") ||  ext.equals("ogg") || ext.equals("mka") || ext.equals("wma"))) {
            return Type.BINARY;
        }
        else if((ext.equals("pdf") || ext.equals("doc") || ext.equals("xls") || ext.equals("ppt") ||  ext.equals("docx") || ext.equals("odt"))) {
            return Type.BINARY;
        }
        else if((ext.equals("zip") || ext.equals("rar") || ext.equals("7z") || ext.equals("tar") ||  ext.equals("iso"))) {
            return Type.BINARY;
        }
        else if((ext.equals("mdb") || ext.equals("accde") || ext.equals("frm") || ext.equals("sqlite"))) {
            return Type.BINARY;
        }
        else if((ext.equals("exe") || ext.equals("dll") || ext.equals("so") || ext.equals("class") ||  ext.equals("jar"))) {
            return Type.BINARY;
        }
        else {
            return Type.NONE;
        }
    }
}
