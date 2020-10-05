package comba;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Servidor {

	public static void main(String[] args) throws Exception {
		
		ServerSocket ss = new ServerSocket(80);
		Socket sr = ss.accept();
        File file = new File(".\\data\\esteessech.mp4");
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[16 * 1024];
        InputStream in = new FileInputStream(file);
        OutputStream out = sr.getOutputStream();

        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.close();
        in.close();
//		String rta ="./data/esteessech.mp4";
//		FileInputStream fi = new FileInputStream(rta);
//		
//		fi.read(b, 0, b.length);
//		OutputStream os = sr.getOutputStream();
//		os.write(b,0,b.length);
	}

}
