import java.io.*;

public class HelperFunctions {

    public HelperFunctions() {}

    public String getUserAccount(String current_user) {

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
        String rd_ln = "/";
        try {
            while ((rd_ln = br.readLine()) != null) {
                // Get the password for the specified user by looking at the value between the 2 '|' symbols
                String tmp_user = rd_ln.substring(0, rd_ln.indexOf("|"));
                if(tmp_user.equals(current_user)) {
                    String account;
                    account = rd_ln.substring(rd_ln.indexOf("|") + 1);
                    account = account.substring(0, account.indexOf("|"));
                    return account;
                }
            }
        } catch (IOException e) {
            return "-";
        }
        // If you do not find the matching user or cannot access account then return error code
        return "-";
    }
}
