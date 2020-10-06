package cliente;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
				
			//Recibir nombre archivo xdddddddd
			String nombreArchivo = nuevo.readLine();
				
			//Creación de archivo a guardar
				File file = new File("./datosClientes/"+nombreArchivo);
				file.createNewFile();
				out = new FileOutputStream(file);
				
			
			//Inicio  dataInputStream para recibir archivo
			DataInputStream dis = new DataInputStream(pIn);
			
			//Recibir tamaño del archivo
			long len = dis.readLong();
			System.out.println("Tamaño archivo "+len);
			int read = 0;
			byte[] bytes = new byte[16*1024];
			System.out.println("entro a leer");
			while (len>0)
			{	
				read = dis.read(bytes);
				out.write(bytes);
				len-=read;
			}
			System.out.println("Salió de leer");
			
			//Envía señal FINALIZADO
			pOut.println("FINALIZADO");
			
			//recibir código de hash
			int hashArchivo;
			
			//Use SHA-1 algorithm
	        MessageDigest shaDigest = MessageDigest.getInstance("MD5");
	         
	        //SHA-1 checksum 
	        String shaChecksum = getFileChecksum(shaDigest, file);
	        
	      //see checksum
	        System.out.println("hash nuevo "+shaChecksum);
			
			hashArchivo = Integer.parseInt(nuevo.readLine());
			System.out.println("codigo hash "+hashArchivo);
			
			int hashCalculado = file.hashCode();
			
			System.out.println("Hash calculado"+hashCalculado);
			
			System.out.println("Enviando recibido");
			pOut.println("RECIBIDO");
			
			}catch (FileNotFoundException | NoSuchAlgorithmException ex) {
				System.out.println("File not found. ");
			}
		}
		else {
			pOut.println("NO RECIBIDO");
		}
		System.out.println("Protocolo terminado");
	}

	public static String toHexString(byte[] array) {
		return DatatypeConverter.printBase64Binary(array);
	}
	
	private static String getFileChecksum(MessageDigest digest, File file) throws IOException
	{
	    //Get file input stream for reading the file content
	    FileInputStream fis = new FileInputStream(file);
	     
	    //Create byte array to read data in chunks
	    byte[] byteArray = new byte[1024];
	    int bytesCount = 0; 
	      
	    //Read file data and update in message digest
	    while ((bytesCount = fis.read(byteArray)) != -1) {
	        digest.update(byteArray, 0, bytesCount);
	    };
	     
	    //close the stream; We don't need it now.
	    fis.close();
	     
	    //Get the hash's bytes
	    byte[] bytes = digest.digest();
	     
	    //This bytes[] has bytes in decimal format;
	    //Convert it to hexadecimal format
	    StringBuilder sb = new StringBuilder();
	    for(int i=0; i< bytes.length ;i++)
	    {
	        sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	    }
	     
	    //return complete hash
	   return sb.toString();
	}
}
