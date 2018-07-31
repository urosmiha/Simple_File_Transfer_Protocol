public class CmdHandler {

    public CmdHandler() {

    }

    public String handleCommand(String cmd) {

        // Split user command so we can get the 4 character command and argument
        // We know that the command always starts with 4 characters and rest is arguments.
        String cdm_id = cmd.substring(0, Math.min(cmd.length(), 4));
        String arg = cmd.substring(5,cmd.length());

        // Check for the validity of the argument
        if(cmd.charAt(4) != ' ') {
            return "-invalid command";
        }

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

    private String authoriseUser(String user) {
        return user;
    }
}
