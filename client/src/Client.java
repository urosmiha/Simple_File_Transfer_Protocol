import java.net.*;
import java.io.*;

public class Client {
        public static void main(String[] args) throws Exception {
            String Lmessage;
            String Umessage;

            String IP = "localhost";
            int port = 8085;

            boolean init = true;
            boolean wait_file = false;
            String file_name = "default.txt";
            String file_data = "";

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
                file_data = Umessage;

                while(Umessage.charAt(Umessage.length()-1) != '\0') {
                    Umessage = inServer.readLine();
                    file_data += Umessage + System.lineSeparator();
                    System.out.println(Umessage);
                }
                s.close();

                // If command is RETR and response is not + or - (meaning we can send file)
                if(Lmessage.contains("RETR") && !Umessage.contains("+") && !Umessage.contains("+")) {
                    wait_file = true;
                    file_name = Lmessage.substring(5);
                    System.out.println("Filename: " + file_name);
                }
                else if (wait_file && Lmessage.equals("SEND")) {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file_name));

                    writer.write(file_data);
                    writer.close();
                }
                else {
                    wait_file = false;
                }

                if(Lmessage.equals("DONE")) {
                    System.out.println("Bye...");
                    break;
                }
            }


        }
}
