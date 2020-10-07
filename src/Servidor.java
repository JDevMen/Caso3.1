import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * 
 */

/**
 * @author Julian Mendoza
 * @author Juan Res
 * @author Luismigoles
 *
 */
public class Servidor {
	private static ServerSocket ss;	
	private static int puerto = 2048; 
	private static int cantServidores =1;
	private static ArrayList<DSinSeguridad> dingos = new ArrayList<DSinSeguridad>();
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		ss = new ServerSocket(puerto);
		Object semaforo = new Object(); 
		System.out.println("Iniciado el servidor en el puerto: "+puerto);
		
		ThreadPoolExecutor deadpools = (ThreadPoolExecutor) Executors.newFixedThreadPool(cantServidores);
		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
		/*
		 * Aqui estoy poniendo el ciclo para aceptar conexiones a los servidores delegados
		 */
		System.out.println("Diga a cuantos clientes quiere enviar los archivos:");
		int cuantos =Integer.parseInt(stdIn.readLine());
		System.out.println("Diga cual de los siguientes archivos quiere ver:");
		System.out.println("Presione la tecla 1 para ver al Sech");
		System.out.println("Presione la tecla 2 para ver la actualidad de la virtualidad");
		String cual = stdIn.readLine();
		for(int i= 0 ; i<cuantos ; i++)
		{
			try { 
				Socket sc = ss.accept();
				DSinSeguridad dingo = new DSinSeguridad(sc,i,stdIn, cuantos, cual);
				System.out.println("Antes del dingoman");
				dingos.add(dingo);
				System.out.println("Cliente aceptado.");

			} catch (IOException e) {
				System.out.println("Error creando el socket cliente.");
				e.printStackTrace();
			}
		}
		for (DSinSeguridad actual : dingos) {
			deadpools.execute(actual);
		}
	}

}
