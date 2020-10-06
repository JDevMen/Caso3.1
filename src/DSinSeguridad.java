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
	 * M�todo principal para iniciar el thread, recibe el socket por el cual
	 * se va a comunidar y un id que lo identifique (asignado por el servidor)
	 * Abr� que hacer ajustes para que el log quede por bloques de cada delegado
	 */
	public DSinSeguridad (Socket csP, int idP) {
		sc = csP;
		System.out.println("LLEGUE UNA VEZ " + idP);
		this.id = idP;
		dlg = new String("delegado " + idP + ": ");
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
	 * - Es el Ãºnico metodo permitido para escribir en el log.
	 */
	//Tal cual, este m�todo lo �nico que hace es escribir sobre el log.
	private void escribirMensaje(String pCadena) {

		try {
			FileWriter fw = new FileWriter(file,true);
			fw.write(pCadena + "\n");
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	//M�todo para enviar el archivo que sea y la reputa madre que te repario
	public void sendFile(File file, DataOutputStream dos) throws IOException {
	    if(dos!=null&&file.exists()&&file.isFile())
	    {
	    	byte[] bytes = new byte[16*1024];
	        FileInputStream input = new FileInputStream(file);
	        dos.writeLong(file.length());
	        System.out.println(file.getAbsolutePath());
	        int read = 0;
	        System.out.println("Tama�o archivo "+file.length());
	        
	        while ((read = input.read(bytes)) != -1)
	        {
	            dos.write(bytes, 0, read);
//	            System.out.println(""+read);
	        }
	        dos.flush();
	        input.close();
	        System.out.println("File successfully sent!");
	    }
	}

	/*
	 * M�todo run, tu deber�as acordarte pero sino lo que tiene es el hilo de 
	 * ejecuci�n del thread. B�sicamente como va a ejecutar los dem�s m�todos
	 * (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		System.out.println(dlg + "Empezando atenci�n.");

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
			ac.println("Enviando archivo");
			
			File file = new File(".\\data\\esteessech.mp4");
			DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
			sendFile(file, dos);
//			long tamArchivo = file.length();
//			
//			System.out.println("tama�o archivo: "+tamArchivo);
//			
//			ac.println(tamArchivo);
//			
//	        byte[] bytes = new byte[16*1024];
//	        InputStream in = new FileInputStream(file);
//	        OutputStream out = sc.getOutputStream();
//	        long contador=0;
//	        int count;
//	        while ((count = in.read(bytes)) > 0) {
//	        	System.out.println("" + count);
//	            out.write(bytes, 0, count);
//	            contador+= count;
//	        }
//			System.out.println("Contador "+contador);
	        
			
//	        System.out.println("Mando el video");
////	        ac.println();
	        System.out.println("Mando FINALIZADO");
	        
			lineaCoca = dc.readLine();
			if(!lineaCoca.equals("RECIBIDO"))
			{
				System.out.println("Problema, no fue recibido el mensaje");
			}
			else {
				System.out.println("lleg� bien panita");
			}
			
//			in.close();
//	        out.close();
			
			dos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(dlg + "Terminando atenci�n.");

	}
}
