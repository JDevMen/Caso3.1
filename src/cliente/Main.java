/**
 * 
 */
package cliente;

/**
 * @author Juli�n Mendoza
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Thread t = new Cliente();
		
		t.start();
	}

}
