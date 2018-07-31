public class CmdHandler {

    public CmdHandler() {

    }

    public String handleCommand(String cmd) {

        switch(cmd) {
            case "USER":
                return authoriseUser(cmd);
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
        return "-Invalid user-id, try again";
    }
}
