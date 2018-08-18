import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws Exception {

        String cmd_in;
        String response;
        int port = 8085;

        HelperFunctions helper = new HelperFunctions();
        CmdHandler cmd_handler = new CmdHandler();
        ServerSocket ss = new ServerSocket(port);
        System.out.println("Server started");

        while(true) {
            Socket s = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            cmd_in = in.readLine();
            System.out.println(("Received: " + cmd_in));

            if(cmd_handler.getType() != Type.ASCII && cmd_in.equals("SEND")) {
                cmd_handler.sendBinary(out);
            }
            else {
                response = cmd_handler.handleCommand(cmd_in);
                System.out.println(response);

                if (response.contains("\n")) {
                    int count = helper.countChar(response, '\n');
                    System.out.println(count);
                    String tmp_msg = response;
                    String tmp_response = response;
                    System.out.print(response);
                    for (int i = 0; i < count; i++) {
                        tmp_msg = tmp_response.substring(0, tmp_response.indexOf("\n"));
                        tmp_response = tmp_response.substring(tmp_response.indexOf("\n") + 1);
                        System.out.println(tmp_msg);
                        out.writeBytes(tmp_msg);

                    }
                    out.writeBytes(tmp_response + '\0' + '\n');
                } else {
                    response = response + '\0' + '\n';
                    out.writeBytes(response);
                }
            }

            if(cmd_in.equals("DONE")) {
                s.close();
                System.out.print("Bye...");
                break;
            }
        }
    }
}