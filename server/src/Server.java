import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            //================================================
            // READING FROM THE CLIENT
            //================================================
            if(cmd_handler.getStoreState()) {

                String file_name = cmd_handler.getStoreFileName();
                int file_size = cmd_handler.getStoreSize();
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
                    // WRITE TO AN ASCII FILE
                    if(cmd_handler.getStoreType().equals("APP") && cmd_handler.checkFileExists(file_name)) {
                        Files.write(Paths.get(file_name), file_data.getBytes(), StandardOpenOption.APPEND);
                    }
                    else {
                        if(cmd_handler.getStoreType().equals("NEW")) {
                            if(cmd_handler.checkFileExists(file_name)) {
                                file_name = cmd_handler.getNewFileName(file_name);
                            }
                        }

                        BufferedWriter writer = new BufferedWriter(new FileWriter(file_name));
                        writer.write(file_data);
                        writer.close();
                    }
                }
                else {
//                  READ IT AS BINARY OR/AND CONTINUOUS
//                    DataInputStream dis = new DataInputStream(s.getInputStream());

                    if(cmd_handler.getStoreType().equals("NEW")) {
                        if(cmd_handler.checkFileExists(file_name)) {
                            file_name = cmd_handler.getNewFileName(file_name);
                        }
                    }

                    System.out.println("NEW NAME: " + file_name);

                    FileOutputStream fos; // = new FileOutputStream(file_name);

                    byte[] buffer;
                    int totalRead = 0;
                    int remaining = file_size;
                    int read = 0;

                    if(cmd_handler.getStoreType().equals("APP") && cmd_handler.checkFileExists(file_name)) {

                        int tmp_size = cmd_handler.getFileSize(file_name);
                        byte[] buffer_1 = new byte[tmp_size];            // Allocate more buffer space since we need to read both files

                        FileInputStream fis = new FileInputStream(file_name);
                        remaining = tmp_size;
                        while ((read = fis.read(buffer_1,0, Math.min(buffer_1.length, remaining))) > 0) {
                            totalRead += read;
                            remaining -= read;
                            System.out.println("read " + totalRead + " bytes.");
                        }
                        fis.close();

                        byte[] buffer_2 = new byte[file_size];
                        totalRead = 0;
                        read = 0;
                        remaining = file_size;

                        while((read = in.read(buffer_2, 0, Math.min(buffer_2.length, remaining))) > 0) {
                            totalRead += read;
                            remaining -= read;
                            System.out.println("read " + totalRead + " bytes.");
                        }

                        buffer = new byte[buffer_1.length + buffer_2.length];
                        System.arraycopy(buffer_1, 0, buffer, 0, buffer_1.length);
                        System.arraycopy(buffer_2, 0, buffer, buffer_1.length, buffer_2.length);

                        fos = new FileOutputStream(file_name);
                        fos.write(buffer, 0, buffer.length);
                        fos.close();
                    }
                    else {

                        fos = new FileOutputStream(file_name);

                        System.out.println("FILE SIZE: " + file_size);
                        buffer = new byte[file_size];

                        while((read = in.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                            totalRead += read;
                            remaining -= read;
                            System.out.println("read " + totalRead + " bytes.");
                        }

                        System.out.println("DONE READING THE FILE");

                        fos.write(buffer, 0, buffer.length);
                        fos.close();

                        System.out.println("DONE WRITING THE NEW FILE");
                    }
//                    out.close();
                }

                cmd_handler.resetStoreState();
                cmd_in = "NONE ";
            }
            else {
                cmd_in = in.readLine();
                System.out.println(("Received: " + cmd_in));
            }

            //*********************************************************



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