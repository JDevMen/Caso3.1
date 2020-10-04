package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;


public class ClienteProtocolo {
	public static void procesar(BufferedReader stdIn, BufferedReader pIn, PrintWriter pOut) throws IOException {
		//Se manda HOLA para comenzar el protocolo
		System.out.println("Escriba 'HOLA'");
		String fromUser = "HOLA";
		pOut.println(fromUser);
		String fromServer = "";
		
		//Confirmación de conexión
		if((fromServer = pIn.readLine())!= null){
			System.out.println("Respuesta del servidor: " + fromServer);
		}
		
		System.out.println("Protocolo terminado");
	}

}
