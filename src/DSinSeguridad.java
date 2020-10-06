import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.MessageDigest;


/*
 * En esta clase se define el protocolo con el que se comunica el servidor 
 * con el cliente. 
 * Ante todo es la estructura del thread delegado.
 */
public class DSinSeguridad implements Runnable {
	// Constantes de respuesta 
	public static final String OK = "OK";
	public static final String ALGORITMOS = "ALGORITMOS";
	public static final String CERTSRV = "CERTSRV";
	public static final String CERCLNT = "CERCLNT";
	public static final String SEPARADOR = ":";
	public static final String HOLA = "HOLA";
	public static final String INICIO = "INICIO";
	public static final String ERROR = "ERROR";
	public static final String REC = "recibio-";
	public static final String ENVIO = "envio-";

	// Atributos
	private Socket sc = null;
	private String dlg;
	private static File file;
	public static final int numCadenas = 13;
	private long id = 0;
	private BufferedReader consola;

	//Cambiar socket del servidor delegado
	public void cambiarSocket(Socket pSc)
	{
		sc = pSc;
	}

	public boolean noHaySocket()
	{
		return sc ==null;
	}


	/*
	 * Método principal para iniciar el thread, recibe el socket por el cual
	 * se va a comunidar y un id que lo identifique (asignado por el servidor)
	 * Abrá que hacer ajustes para que el log quede por bloques de cada delegado
	 */
	public DSinSeguridad (Socket csP, int idP, BufferedReader pConsola) {
		sc = csP;
		System.out.println("LLEGUE UNA VEZ " + idP);
		this.id = idP;
		dlg = new String("delegado " + idP + ": ");
		consola = pConsola;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/*
	 * Generacion del archivo log. 
	 * Nota: 
	 * - Debe conservar el metodo . 
	 * - Es el ÃƒÂºnico metodo permitido para escribir en el log.
	 */
	//Tal cual, este método lo único que hace es escribir sobre el log.
	private void escribirMensaje(String pCadena) {

		try {
			FileWriter fw = new FileWriter(file,true);
			fw.write(pCadena + "\n");
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

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
	
	
	//Método para enviar el archivo que sea y la reputa madre que te repario
	public void sendFile(File file, DataOutputStream dos) throws IOException {
	    if(dos!=null&&file.exists()&&file.isFile())
	    {
	    	byte[] bytes = new byte[16*1024];
	        FileInputStream input = new FileInputStream(file);
	        dos.writeLong(file.length());
	        int read = 0;
	        System.out.println("Tamaño archivo "+file.length());
	        
	        //To do: timestamp para medir tiempo de transferencia
	        
	        
	        //Ciclo para enviar el archivo
	        while ((read = input.read(bytes)) != -1)
	        {
	            dos.write(bytes, 0, read);
	        }
	        dos.flush();
	        input.close();
	        System.out.println("File successfully sent!");
	    }
	}

	/*
	 * Método run, tu deberías acordarte pero sino lo que tiene es el hilo de 
	 * ejecución del thread. Básicamente como va a ejecutar los demás métodos
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		System.out.println(dlg + "Empezando atención.");

		String lineaCoca;
		try {
			PrintWriter ac = new PrintWriter(sc.getOutputStream() , true);
			BufferedReader dc = new BufferedReader(new InputStreamReader(sc.getInputStream()));
		
			lineaCoca = dc.readLine();
			if (!lineaCoca.equals(HOLA)) {
				ac.println(ERROR);
				sc.close();
				throw new Exception(dlg + ERROR + REC + lineaCoca +"-terminando.");
			} else {
				ac.println(OK);
			}
			System.out.println("Diga cuál de los siguientes archivos quiere ver:");
			System.out.println("Presione la tecla 1 para ver al Sech");
			System.out.println("Presione la tecla 2 para ver la actualidad de la virtualidad");
			
			String resultadoConsola = consola.readLine();
			System.out.println("Mensaje ingresado por consola "+resultadoConsola);
			//Revisa si quiere el video de Sech o el de Drake y Josh
			if(resultadoConsola.equals("1"))
			{
				//Enviar la confirmación de empezar a enviar el archivo
				ac.println("Enviando archivo");
				
				//Archivo a enviar
				File file = new File(".\\data\\esteessech.mp4");
				
				//Enviando nombre archivo xdddddd
				ac.println("esteessech.mp4");
				
				//Enviar el archivo al cliente
				DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
				sendFile(file, dos);
		        System.out.println("Termina de enviar");
		        
		      //Recibir FINALIZADO
		        lineaCoca = dc.readLine();
		        System.out.println(lineaCoca);
		        
		        
		      //To do: Time stamp para saber cuando terminó la transferencia
		        
		        
		      //Enviar código hash del archivo
		        
		      //Use SHA-1 algorithm
		        MessageDigest shaDigest = MessageDigest.getInstance("MD5");
		         
		        //SHA-1 checksum 
		        String shaChecksum = getFileChecksum(shaDigest, file);
		        
		      //see checksum
		        System.out.println("hash nuevo "+shaChecksum);
		        
				int hashArchivo = file.hashCode();
				
				ac.println(hashArchivo);
				System.out.println("codigo hash "+hashArchivo);
		        
		        //Recibiendo mensaje de recibido 
				lineaCoca = dc.readLine();
				if(!lineaCoca.equals("RECIBIDO"))
				{
					System.out.println("Problema, no fue recibido el mensaje");
				}
				else {
					System.out.println("llegó bien panita");
				}
				
				
				dos.close();
		        
			}
			//Aquí se manda el video de Drake y Josh y no el del Sech
			else {
				//Enviar la confirmación de empezar a enviar el archivo
				ac.println("Enviando archivo");
				
				//Archivo a enviar
				File file = new File(".\\data\\actualidad.mp4");
				
				//Enviando nombre archivo xdddddd
				ac.println("actualidad.mp4");
				
				//Enviar el archivo al cliente
				DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
				sendFile(file, dos);
		        System.out.println("Termina de enviar");
		        
		      //Recibir FINALIZADO
		        lineaCoca = dc.readLine();
		        System.out.println(lineaCoca);
		        
		        
		      //To do: Time stamp para saber cuando terminó la transferencia
		        
		        
		      //Enviar código hash del archivo
		        
		      //Use SHA-1 algorithm
		        MessageDigest shaDigest = MessageDigest.getInstance("MD5");
		         
		        //SHA-1 checksum 
		        String shaChecksum = getFileChecksum(shaDigest, file);
		        
		      //see checksum
		        System.out.println("hash nuevo "+shaChecksum);
		        
				int hashArchivo = file.hashCode();
				
				ac.println(hashArchivo);
				System.out.println("codigo hash "+hashArchivo);
		        
		        //Recibiendo mensaje de recibido 
				lineaCoca = dc.readLine();
				if(!lineaCoca.equals("RECIBIDO"))
				{
					System.out.println("Problema, no fue recibido el mensaje");
				}
				else {
					System.out.println("llegó bien panita");
				}
				
				
				dos.close();
		        
			}
			
	        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(dlg + "Terminando atención.");

	}
}
