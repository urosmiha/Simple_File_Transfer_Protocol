import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws Exception {

        String Lmessage;
        String Umessage;
        int port = 8085;

        ServerSocket ss = new ServerSocket(port);

        while(true) {
            Socket s = ss.accept();
            System.out.println("Connected to server");
            System.out.println("Listening on port " + port);

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            Lmessage = in.readLine();
            System.out.println(("Recieved: " + Lmessage));

            Umessage = Lmessage.toUpperCase() + "\n";
            out.writeBytes(Umessage);
        }
    }
}
