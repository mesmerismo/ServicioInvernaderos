import java.io.*;
import java.net.Socket;

/**
 *
 * @author mesmerismo
 */
class HiloServidor extends Thread {
    private Socket skCliente;
    private String host = "localhost";
    private int port = 9876;
    private BufferedReader entrada;
    private PrintWriter salida;
    
    public HiloServidor(Socket skCliente) {
        this.skCliente = skCliente;
    }
    
    @Override
    public void run(){
        String cadena = "";
        String[] peticion = null;
        String respuesta = "";
        String metodo = "";
        String url = "";
        String httpVerison = "";
        String codigoEstado = "200";
        String mensajeEstado = "OK";
        String estado = "";
        String cuerpo = "";
        String cabeceras = "";
        String conection = "Connection: close;\n";
        String contentLength = "Content-Lenght: ";
        String contentTipe = "Content-Type: text/html;\n";
        String server = "Server: Controlador/1.0";


        try {
            System.out.println("Establenciendo flujo de entrada con el cliente.");  
            establecerFlujoEntrada(skCliente);
            System.out.println("Leyendo peticion del cliente.");
            cadena = leeSocket();
            System.out.println(cadena);

            if (cadena != null) {
                peticion = cadena.split(" ");

                if (peticion.length == 3) {
                    metodo = peticion[0];
                    url = peticion[1];
                    httpVerison = peticion[2];
                    if(metodo.equals("GET")) {
                        System.out.println("Conectando con el controlador.");
                        Socket skControlador = new Socket(host,port);
                        establecerFlujos(skControlador);
                        System.out.println("Enviando petición al controlador.");
                        escribeSocket(url);
                        System.out.println("Esperando para recibir respuesta.");
                        respuesta = leeSocket();
                        System.out.println(respuesta);
                        skControlador.close();
                        System.out.println("Terminada conexion con el controlador.");
                        if (respuesta != "") {
                            if (respuesta != "ERROR") {
                                
                            } else {
                                codigoEstado = "500";
                                mensajeEstado = "Internal Server Error";
                            }
                        } else { 
                            codigoEstado = "404";
                            mensajeEstado = "Not Found";
                        }
                    } else {
                        codigoEstado = "400";
                        mensajeEstado = "Bad Request";
                    }
                } else { 
                    codigoEstado = "400";
                    mensajeEstado = "Bad Request";
                }
            } else {
                codigoEstado = "400";
                mensajeEstado = "Bad Request";
            }
            
            estado = httpVerison + codigoEstado + mensajeEstado;
            contentLength += respuesta.length()+";\n";
            cabeceras = conection + contentTipe + contentLength + server;
            cuerpo = respuesta;
        
            System.out.println("Devolviendo datos al cliente.");
            establecerFlujoSalida(skCliente);
            escribeSocket(estado);
            escribeSocket(cabeceras);
            escribeSocket("");
            escribeSocket(cuerpo);
            System.out.println("Mensaje enviado.");

            skCliente.close();
            System.out.println("Conexion cerrada con el cliente.");
        }catch (IOException ex){
            System.err.println(ex.getMessage());
        }
    }

    /**
    * Establece los flujos de entrada y salida con un socket.
    * \param socket Socket con el que se establecerá la conexión.
    **/
    private void establecerFlujos(Socket socket) throws IOException {
        establecerFlujoEntrada(socket);
        establecerFlujoSalida(socket);
    }

    /**
    * Establece el flujos de entrada con un socket.
    * \param socket Socket con el que se establecerá la conexión.
    **/
    private void establecerFlujoEntrada(Socket socket) throws IOException {
        InputStream flujoEntrada = socket.getInputStream();
        entrada = new BufferedReader(new InputStreamReader(flujoEntrada));
    }

    /**
    * Establece el flujos de salida con un socket.
    * \param socket Socket con el que se establecerá la conexión.
    **/
    private void establecerFlujoSalida(Socket socket) throws IOException {
        OutputStream flujoSalida = socket.getOutputStream();
        salida = new PrintWriter(new OutputStreamWriter(flujoSalida));
    }

    /**
    * Lee un mensaje del socket.
    * \return mensaje leído del socket.
    **/
    private String leeSocket() throws IOException {
        return entrada.readLine();
    }

    /**
    * Escribre un mensaje en el socket.
    * \param cadena Mensaje que se escribirá en el socket
    **/
    private void escribeSocket(String cadena) throws IOException {
       salida.println(cadena);
       salida.flush();
    }
}