package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Cliente extends Thread {
	
	public static final int PUERTO = 147;
	public static final String SERVIDOR = "localhost";
	
	public void run (){
		Socket socket = null;
		PrintWriter escritor = null;
		BufferedReader lector = null;
		
		System.out.println("Cliente...");
		try{
			socket = new Socket(SERVIDOR, PUERTO);
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch (IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		try {
			ClienteProtocolo.procesar(stdIn, lector, escritor);
			stdIn.close();
			escritor.close();
			lector.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
}
