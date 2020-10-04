import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * 
 */

/**
 * @author Juli�n Mendoza
 *
 */
public class Servidor {
	private static ServerSocket ss;	
	private static int puerto = 147; 
	private static int cantServidores =1;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ss = new ServerSocket(puerto);
		System.out.println("Iniciado el servidor en el puerto: "+puerto);
		
		ThreadPoolExecutor deadpools = (ThreadPoolExecutor) Executors.newFixedThreadPool(cantServidores);
		
		/*
		 * Aqu� estoy poniendo el ciclo para aceptar conexiones a los servidores delegados
		 */
		for(int i= 0 ; true; i++)
		{
			try { 
				Socket sc = ss.accept();
				DSinSeguridad dingo = new DSinSeguridad(sc,i);
				deadpools.execute(dingo);
				System.out.println("Cliente aceptado.");

			} catch (IOException e) {
				System.out.println("Error creando el socket cliente.");
				e.printStackTrace();
			}
		}
	}

}
