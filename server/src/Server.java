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

            if(cmd_handler.getStoreState()) {
                String file_data = "";
                if(cmd_handler.getType() == Type.ASCII) {
                    System.out.println("Waiting for file");
                    cmd_in = in.readLine();
                    System.out.println("Received: " + cmd_in);
                    file_data = cmd_in + System.lineSeparator();

                    while (cmd_in.charAt(cmd_in.length() - 1) != '\0') {
                        cmd_in = in.readLine();
                        file_data += cmd_in + System.lineSeparator();
                        System.out.println(cmd_in);
                    }
                    // WRITE TO A ASCII FILE
                    BufferedWriter writer = new BufferedWriter(new FileWriter(cmd_handler.getStoreFileName()));
                    writer.write(file_data);
                    writer.close();
                }
                else {
                    // READ IT AS BINARY OR/AND CONTINUOUS
//                    DataInputStream dis = new DataInputStream(s.getInputStream());
//                    FileOutputStream fos = new FileOutputStream(file_name);
//                    byte[] buffer = new byte[file_size];
//
//                    int totalRead = 0;
//                    int remaining = file_size;
//                    int read = 0;
//                    while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
//                        totalRead += read;
//                        remaining -= read;
//                        System.out.println("read " + totalRead + " bytes.");
//                        fos.write(buffer, 0, read);
//                    }
//                    fos.close();
//                    dis.close();
                }

                cmd_handler.resetStoreState();
                cmd_in = "NONE ";
            }
            else {
                cmd_in = in.readLine();
                System.out.println(("Received: " + cmd_in));
            }



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