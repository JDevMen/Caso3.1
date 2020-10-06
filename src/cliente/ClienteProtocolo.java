package cliente;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
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
			System.out.println("Entro a enviando archivo");
			try {
				File file = new File("./data/prueba.mp4");
				file.createNewFile();
				out = new FileOutputStream(file);
				
			} catch (FileNotFoundException ex) {
				System.out.println("File not found. ");
			}
			
			DataInputStream dis = new DataInputStream(pIn);
			long len = dis.readLong();
			System.out.println("Tamaño archivo "+len);
			int read = 0;
			byte[] bytes = new byte[16*1024];
			long contador = 0;
			System.out.println("entro a leer");
			while (len>0)
			{	
				read = dis.read(bytes);
				out.write(bytes);
//				contador+=bytes.length;
				len-=read;
			}
			System.out.println("Salió de leer");
//			long tamArchivo = Long.parseLong(nuevo.readLine());
//			
//			System.out.println("tamArchivo "+tamArchivo );
//			
//			byte[] bytes = new byte[16*1024];
//			long contador =0;
//			int count;
//			while ((count = pIn.read(bytes)) > 0) {
//				System.out.println("" + count);
//				out.write(bytes, 0, bytes.length);
//				contador+= count;
//				System.out.println("contador "+contador);
//				if (contador >= tamArchivo ) {
//					break;
//				}
//			}
			
//			while (contador< tamArchivo) {
//				count = pIn.read(bytes); 
//				System.out.println("count " + count);
//				out.write(bytes, 0, count);
//				contador+= count;
//			}	
			
			System.out.println("Hola");
//			pIn.close();
//			out.close();
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
