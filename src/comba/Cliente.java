package comba;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Cliente {
	public final static int PUERTO = 80;
	public final static String SERVIDOR = "localhost";
	public static void main(String[] args) throws Exception {
		byte b[] = new byte[1024];
		Socket nuevo = new Socket(SERVIDOR, PUERTO);
		OutputStream out = null;
		InputStream in = null;
		
		
		try {
            in = nuevo.getInputStream();
        } catch (IOException ex) {
            System.out.println("Can't get socket input stream. ");
        }

        try {
            out = new FileOutputStream("./data/prueba.mp4");
        } catch (FileNotFoundException ex) {
            System.out.println("File not found. ");
        }
        
        byte[] bytes = new byte[16*1024];
        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, bytes.length);
        }
		

        out.close();
        in.close();
        nuevo.close();
		
	}

}
