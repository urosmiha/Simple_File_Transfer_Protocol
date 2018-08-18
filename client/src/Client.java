import java.net.*;
import java.io.*;

public class Client {
        public static void main(String[] args) throws Exception {
            String Lmessage = "";
            String Umessage = "";

            String IP = "localhost";
            int port = 8085;

            boolean init = true;
            boolean wait_file = false;
            String file_name = "default.txt";
            String file_data = "";

            Type c_type = Type.ASCII;

            for(;;) {

                Socket s = new Socket(IP, port);

                BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in));
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                BufferedReader inServer = new BufferedReader(new InputStreamReader(s.getInputStream()));

                System.out.println("Enter command: ");  // Get user input
                Lmessage = inUser.readLine();
                out.writeBytes(Lmessage + '\n');

                if(wait_file && Lmessage.equals("SEND") && c_type != Type.ASCII) {
                    // READ IT AS BINARY OR/AND CONTINUOUS
                    DataInputStream dis = new DataInputStream(s.getInputStream());
                    FileOutputStream fos = new FileOutputStream("testfile.jpg");
                    byte[] buffer = new byte[30716];

                    int filesize = 30716; // Send file size in separate msg
                    int totalRead = 0;
                    int remaining = filesize;
                    int read = 0;
                    while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                        totalRead += read;
                        remaining -= read;
                        System.out.println("read " + totalRead + " bytes.");
                        fos.write(buffer, 0, read);
                    }
                    fos.close();
                    dis.close();
                }
                else {
                    Umessage = inServer.readLine();
                    System.out.println("From server: " + Umessage);
                    file_data = Umessage;

                    while (Umessage.charAt(Umessage.length() - 1) != '\0') {
                        Umessage = inServer.readLine();
                        file_data += Umessage + System.lineSeparator();
                        System.out.println(Umessage);
                    }
                    s.close();
                }

                if(Lmessage.contains("TYPE")) {
                    if(Umessage.contains("+Using")) {
                        String type = Lmessage.substring(5);
                        switch (type){
                            case "A":
                                System.out.println("ASCII");
                                c_type = Type.ASCII;
                                break;
                            case "B":
                                System.out.println("BINARY");
                                c_type = Type.BINARY;
                                break;
                            case "C":
                                System.out.println("CONTINUOUS");
                                c_type = Type.CONTINUOUS;
                                break;
                            default:
                                break;
                        }
                    }
                }

                // If command is RETR and response is not + or - (meaning we can send file)
                if(!wait_file && Lmessage.contains("RETR") && !Umessage.contains("+") && !Umessage.contains("+")) {
                    wait_file = true;
                    file_name = Lmessage.substring(5);
                    System.out.println("Filename: " + file_name);
                }
                else if (wait_file && Lmessage.equals("SEND")) {
                    if(c_type == Type.ASCII) {
                        // WRITE TO A ASCII FILE
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file_name));
                        writer.write(file_data);
                        writer.close();
                        wait_file = false;
                    }
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
