import java.net.*;
import java.io.*;

public class Client {
        public static void main(String[] args) throws Exception {
            String Lmessage;
            String Umessage;

            String IP = "localhost";
            int port = 8085;

            boolean init = true;

            for(;;) {

                Socket s = new Socket(IP, port);

                BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in));

                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                BufferedReader inServer = new BufferedReader(new InputStreamReader(s.getInputStream()));

//                if(init) {
//                    out.writeBytes("\n");
//                    init = false;
//                    Umessage = inServer.readLine();
//                    System.out.println("From server: " + Umessage);
//                }

                System.out.println("Enter command: ");  // Get user input
                Lmessage = inUser.readLine();
                out.writeBytes(Lmessage + '\n');

                Umessage = inServer.readLine();
                System.out.println("From server: " + Umessage);
//
                s.close();

                if(Lmessage.equals("DONE")) {
                    System.out.println("Bye...");
                    break;
                }
            }


        }
}
