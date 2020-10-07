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
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
	private int cuantosConectados = 0;
	private static int cantUsCon = 0;
	private String cualVideo = "";

	// Cambiar socket del servidor delegado
	public void cambiarSocket(Socket pSc) {
		sc = pSc;
	}

	public boolean noHaySocket() {
		return sc == null;
	}

	/*
	 * Mitodo principal para iniciar el thread, recibe el socket por el cual se va a
	 * comunidar y un id que lo identifique (asignado por el servidor) Abri que
	 * hacer ajustes para que el log quede por bloques de cada delegado
	 */
	public DSinSeguridad(Socket csP, int idP, BufferedReader pConsola, int pCuantos, String pCual) {
		System.out.println("Entri al constructor");

		sc = csP;
		System.out.println("LLEGUE UNA VEZ " + idP);
		this.id = idP;
		dlg = new String("delegado " + idP + ": ");
		consola = pConsola;
		cuantosConectados = pCuantos;
		cualVideo = pCual;
		System.out.println("Salii del contructor");
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/*
	 * Generacion del archivo log. Nota: - Debe conservar el metodo . - Es el unico
	 * metodo permitido para escribir en el log.
	 */
	// Tal cual, este mitodo lo inico que hace es escribir sobre el log.
	private void escribirMensaje(String pCadena) {

		try {
			FileWriter fw = new FileWriter(file, true);
			fw.write(pCadena + "\n");
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String getFileChecksum(MessageDigest digest, File file) throws IOException {
		// Get file input stream for reading the file content
		FileInputStream fis = new FileInputStream(file);

		// Create byte array to read data in chunks
		byte[] byteArray = new byte[16 * 1024];
		int bytesCount = 0;

		// Read file data and update in message digest
		while ((bytesCount = fis.read(byteArray)) != -1) {
			digest.update(byteArray, 0, bytesCount);
		}
		;

		// close the stream; We don't need it now.
		fis.close();

		// Get the hash's bytes
		byte[] bytes = digest.digest();

		// This bytes[] has bytes in decimal format;
		// Convert it to hexadecimal format
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
		}

		// return complete hash
		return sb.toString();
	}

	// Mitodo para enviar el archivo que sea y la reputa madre que te repario
	public int sendFile(File file, DataOutputStream dos) throws IOException {
		int cuantos =0;
		if (dos != null && file.exists() && file.isFile()) {
			byte[] bytes = new byte[16 * 1024];
			FileInputStream input = new FileInputStream(file);
			dos.writeLong(file.length());
			int read = 0;
			System.out.println("Tamaio archivo " + file.length());

			// To do: timestamp para medir tiempo de transferencia

			// Ciclo para enviar el archivo
			while ((read = input.read(bytes)) != -1) {
				dos.write(bytes, 0, read);
				cuantos ++;
			}
			dos.flush();
			input.close();
			System.out.println("File successfully sent!");
		}
		return cuantos;
	}

	/*
	 * Mitodo run, tu deberias acordarte pero sino lo que tiene es el hilo de
	 * ejecuciin del thread. Bisicamente como va a ejecutar los demis mitodos
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {

		System.out.println(dlg + "Empezando atenciin.");
		String lineaCoca;
		try {
			PrintWriter ac = new PrintWriter(sc.getOutputStream(), true);
			BufferedReader dc = new BufferedReader(new InputStreamReader(sc.getInputStream()));

			// Creamos los archivos del log
			// --------------------------------------
			// LOG SETUP
			// --------------------------------------
			File myObj = new File("pruebas.txt");
			if (myObj.createNewFile()) {
				System.out.println("File created: " + myObj.getName());
			} else {
				System.out.println("File already exists.");
			}

			FileWriter w = new FileWriter(myObj.getName(), true);

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
			LocalDateTime now = LocalDateTime.now();
			// --------------------------------------
			
			lineaCoca = dc.readLine();
			if (!lineaCoca.equals(HOLA)) {
				ac.println(ERROR);
				sc.close();
				throw new Exception(dlg + ERROR + REC + lineaCoca + "-terminando.");
			} else {
				ac.println(OK);
			}
			String resultadoConsola = cualVideo;

			String archivoElegido = "";

			if (resultadoConsola.equals("1")) {
				archivoElegido = "./data/esteessech.mp4";
			} else {
				archivoElegido = "./data/actualidad.mp4";
			}
			
			// Revisa si quiere el video de Sech o el de Drake y Josh
			// Enviar la confirmaciin de empezar a enviar el archivo
			ac.println("Enviando archivo");

			// Archivo a enviar
			File file = new File(archivoElegido);
			
			//DATOS DEL ARCHIVO ENVIADO
			String nombreArchivoEnviado = file.getName();
			long tamanioArchivoEnviado = file.length();
			//DATOS SOCKET
			String nombreCliente = sc.getInetAddress().getHostName();

			// Enviando nombre archivo xdddddd
			ac.println(archivoElegido.split("/")[2]);

			// Enviar el archivo al cliente
			DataOutputStream dos = new DataOutputStream(sc.getOutputStream());
			//Instant start = Instant.now();
			int cantidadPaquetesEnviados =sendFile(file, dos);
			//Instant end = Instant.now();
			System.out.println("Termina de enviar");

			//TODO tiempos de envio entre sendFile
			//long ts = Duration.between(start, end).toMillis();
			
			
			//Recibir cantidad Paquetes
			lineaCoca = dc.readLine();
			int cantidadPaquetesRecibidos = Integer.parseInt(lineaCoca);
			
			
			// Recibir FINALIZADO
			lineaCoca = dc.readLine();
			System.out.println(lineaCoca);

			// To do: Time stamp para saber cuando termini la transferencia

			// Enviar cidigo hash del archivo

			// Use SHA-1 algorithm
			MessageDigest shaDigest = MessageDigest.getInstance("MD5");

			// SHA-1 checksum
			String shaChecksum = getFileChecksum(shaDigest, file);

			// see checksum
			System.out.println("hash nuevo " + shaChecksum);

			ac.println(shaChecksum);

			String exitoso;
			
			// Recibiendo mensaje de recibido
			lineaCoca = dc.readLine();
			if (!lineaCoca.equals("RECIBIDO")) {
				System.out.println("Problema, no fue recibido el mensaje");
				exitoso = "EXITOSO";
			} else {
				System.out.println("Mensaje Recibido Exitosamente");
				exitoso = "NO EXITOSO";
			}
			
			w.write("---------------------"+ "\n");
			w.write(dtf.format(now)+ "\n"); 
			w.write("Archivo Enviado: " + nombreArchivoEnviado+ "\n");
			w.write("Tamanio: " + tamanioArchivoEnviado+ "\n");
			w.write("Nombre Cliente: " + nombreCliente+ "\n");
			//w.write("Tiempo de Transferencia: " + ts + " ms"+ "\n");
			//TODO CON NUMERO DE BYTES
			w.write("# Paquetes Enviados: "+ cantidadPaquetesEnviados +"\n");
			
			//TODO CON NUNMERO DE BYTES
			w.write("# Paquetes Recibidos: "+ cantidadPaquetesRecibidos+ "\n");
			w.close();
			dos.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(dlg + "Terminando atenciin.");

	}

}
