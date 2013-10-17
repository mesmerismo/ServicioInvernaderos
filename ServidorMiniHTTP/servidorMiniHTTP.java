import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


/**
 *
 * @author mesmerismo
 */
public class servidorMiniHTTP {
    public static void main(String[] args) throws IOException{
        int puerto = 8080;
        
            ServerSocket skServidor = new ServerSocket(puerto);
            Socket skControlador = new Socket("localhost",puerto);
            System.out.println("Servidor activo.");
            System.out.println("Escuchando puerto " + puerto);
        try{
            for(;;){
                Socket skCliente = skServidor.accept();
                System.out.println("Sirviendo cliente...");
                Thread hilo = new hiloServidor(skCliente,skControlador);
                hilo.start();
            }
        } catch (Exception ex) {
            System.err.println("Error: " + ex.getMessage());
        }
    }
}