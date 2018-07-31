import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws Exception {

        String cmd_in;
        String response;
        int port = 8085;

        CmdHandler cmd_handler = new CmdHandler();
        ServerSocket ss = new ServerSocket(port);

        while(true) {
            Socket s = ss.accept();
            System.out.println("Connected to server");
            System.out.println("Listening on port " + port);

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            cmd_in = in.readLine();
            System.out.println(("Received: " + cmd_in));

            response = cmd_handler.handleCommand(cmd_in);
            response = response + '\n';
            System.out.println(("Response: " + response));
            out.writeBytes(response);

            if(cmd_in.equals("DONE")) {
                s.close();
                System.out.print("Bye...");
                break;
            }
        }
    }
}
