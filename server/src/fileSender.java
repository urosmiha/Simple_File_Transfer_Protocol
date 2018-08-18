import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class fileSender {

    DataOutputStream out;

    public fileSender(DataOutputStream out) {
        this.out = out;
    }

    public void sendFile(Type type, String file_name) {

        // The name of the file to open.
        try {
            // Use this for reading the data.
            byte[] buffer = new byte[1000];

            FileInputStream inputStream = new FileInputStream(file_name);

            // read fills buffer with data and returns
            // the number of bytes read (which of course
            // may be less than the buffer size, but
            // it will never be more).
            int total = 0;
            int nRead = 0;
            while((nRead = inputStream.read(buffer)) != -1) {
                // Convert to String so we can display it.
                // Of course you wouldn't want to do this with
                // a 'real' binary file.
                System.out.println(new String(buffer));
                out.writeByte(nRead);
                total += nRead;
            }


            // Always close files.
            inputStream.close();

            System.out.println("Read " + total + " bytes");
        }
        catch(FileNotFoundException ex) {
            System.out.println(
                    "Unable to open file '" + file_name + "'");
        }
        catch(IOException ex) {
            System.out.println(
                    "Error reading file '" + file_name + "'");
            // Or we could just do this:
            // ex.printStackTrace();
        }
    }
}
