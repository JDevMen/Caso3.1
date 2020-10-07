package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Cliente extends Thread {
	
	public static final int PUERTO = 2048;
	public static final String SERVIDOR = "192.168.0.16";
	private ArrayList<Socket> lista = new ArrayList<Socket>();
	
	public void run (){
		Socket socket = null;
		PrintWriter escritor = null;
		InputStream lector = null;
		
		System.out.println("Cliente...");
		try{
			socket = new Socket(SERVIDOR, PUERTO);
			lista.add(socket);
			escritor = new PrintWriter(socket.getOutputStream(), true);
			lector = socket.getInputStream();
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
