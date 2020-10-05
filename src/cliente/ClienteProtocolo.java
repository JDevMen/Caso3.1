package cliente;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.xml.bind.DatatypeConverter;


public class ClienteProtocolo {
	public static void procesar(BufferedReader stdIn, InputStream pIn, PrintWriter pOut) throws IOException {
		//Se manda HOLA para comenzar el protocolo
		BufferedReader nuevo = new BufferedReader(new InputStreamReader(pIn));
		System.out.println("Escriba 'HOLA'");
		String fromUser = "HOLA";
		pOut.println(fromUser);
		String fromServer = "";

		//Confirmación de conexión
		if((fromServer = nuevo.readLine())!= null){
			System.out.println("Respuesta del servidor: " + fromServer);
		}
		if((fromServer.equals("OK")))
		{
			System.out.println("Preparado para recibir archivos");
		}

		//Confirmación de conexión
		if((fromServer = nuevo.readLine()).equals("Enviando archivo")){
			OutputStream out = null;
			InputStream in2 = pIn;
			try {
				out = new FileOutputStream("./data/prueba.mp4");
			} catch (FileNotFoundException ex) {
				System.out.println("File not found. ");
			}
			byte[] bytes = new byte[16*1024];
			int count;
			while ((count = in2.read(bytes)) > 0) {
				System.out.println("" + count);
			}			
			System.out.println("Hola");
			in2.close();
			out.close();
			pOut.println("RECIBIDO");

		}
		else {
			pOut.println("NO RECIBIDO");
		}
		System.out.println("Protocolo terminado");
	}

	public static String toHexString(byte[] array) {
		return DatatypeConverter.printBase64Binary(array);
	}

}
