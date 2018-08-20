import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws Exception {
        String user_msg = "";
        String server_msg = "";

        String IP = "localhost";
        int port = 8085;

        boolean init = true;
        boolean wait_file = false;
        boolean send_file = false;
        String send_file_name = "default.txt";
        String file_name = "default.txt";
        String file_data = "";
        int file_size = 0;

        Type c_type = Type.ASCII;

        for(;;) {

            Socket s = new Socket(IP, port);

            BufferedReader inUser = new BufferedReader(new InputStreamReader(System.in));
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            BufferedReader inServer = new BufferedReader(new InputStreamReader(s.getInputStream()));

            System.out.println("Enter command: ");  // Get user input
            user_msg = inUser.readLine();
            out.writeBytes(user_msg + '\n');

            if(wait_file && user_msg.equals("SEND") && c_type != Type.ASCII) {
                // READ IT AS BINARY OR/AND CONTINUOUS
                DataInputStream dis = new DataInputStream(s.getInputStream());
                FileOutputStream fos = new FileOutputStream(file_name);
                byte[] buffer = new byte[file_size];

                int totalRead = 0;
                int remaining = file_size;
                int read = 0;
                while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                    totalRead += read;
                    remaining -= read;
                    System.out.println("read " + totalRead + " bytes.");
                    fos.write(buffer, 0, read);
                }
                fos.close();
                dis.close();
                wait_file = false;
            }
            else {
                server_msg = inServer.readLine();
                System.out.println("From server: " + server_msg);
                file_data = server_msg;

                while (server_msg.charAt(server_msg.length() - 1) != '\0') {
                    server_msg = inServer.readLine();
                    file_data += server_msg + System.lineSeparator();
                    System.out.println(server_msg);
                }

                if (wait_file && user_msg.equals("SEND") && c_type == Type.ASCII) {
                    // WRITE TO A ASCII FILE
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file_name));
                    writer.write(file_data);
                    writer.close();
                    wait_file = false;
                }

                s.close();
            }

            if(user_msg.contains("TYPE")) {
                if(server_msg.contains("+Using")) {
                    String type = user_msg.substring(5);
                    c_type = changeType(type, c_type);
                }
            }
            else if(user_msg.contains("STOR") && server_msg.contains("+")) {
                send_file = true;
                send_file_name = user_msg.substring(9);
                File file = new File(send_file_name);
                file_size = (int)file.length();
                System.out.println("File name: " + send_file_name + " | Size: " + file_size);
            }
            else if(send_file && user_msg.contains("SIZE") && server_msg.contains("+ok")) {
                if(c_type == Type.ASCII) {
                    StringBuffer contents = new StringBuffer();
                    BufferedReader input = null;
                    try {
                        input = new BufferedReader(new FileReader(send_file_name), 1);
                        String line = null; //not declared within while loop
                        while ((line = input.readLine()) != null) {
                            contents.append(line);
                            contents.append(System.getProperty("line.separator"));
                        }
                    } catch (IOException ex) {
                        System.out.println("-Bad request: " + ex);
                    }
                    out.writeBytes(contents.toString());
                }
                else {
                    try {
                        FileInputStream fis = new FileInputStream(send_file_name);
                        byte[] buffer = new byte[(int)file_size];

                        while (fis.read(buffer) > 0) {
                            out.write(buffer);
                        }
                    } catch (FileNotFoundException ex) {
                        System.out.println("Unable to open file " + send_file_name + " : " + ex);
                    } catch (IOException ex) {
                        System.out.println("Error reading file " + send_file_name + " : " + ex);
                    }
                }
                s.close();
            }
            // If command is RETR and response is not + or - (meaning we can send file)
            else if(!wait_file && user_msg.contains("RETR") && !server_msg.contains("+") && !server_msg.contains("-")) {
                wait_file = true;
                file_name = user_msg.substring(5);
                String size = server_msg.replaceAll("[^0-9]","");     // Remove everything that is not a number
                file_size = Integer.parseInt(size);
            }
            else {
                wait_file = false;
                send_file = false;
            }

            if(user_msg.equals("DONE")) {
                System.out.println("Bye...");
                break;
            }
        }


    }

    private static Type changeType(String type, Type c_type) {
        switch (type) {
            case "A":
                System.out.println("ASCII");
                return Type.ASCII;
            case "B":
                System.out.println("BINARY");
                return Type.BINARY;
            case "C":
                System.out.println("CONTINUOUS");
                return Type.CONTINUOUS;
            default:
                return c_type;
        }
    }
}
