
package ServidorChat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;


public class ServidorChat extends Thread implements Observer{
    
    private Socket socket; 
    private MensajesChat mensajes;
    private DataInputStream entradaDatos;
    private DataOutputStream salidaDatos;
    private static  int cont=0;
    
    public ServidorChat (Socket socket, MensajesChat mensajes){
        this.socket = socket;
        this.mensajes = mensajes;
        
        try {
            entradaDatos = new DataInputStream(socket.getInputStream());
            salidaDatos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException ex) {
        	System.err.println("Error al crear los stream de entrada y salida : " + ex.getMessage());
        }
    }
    
    @Override
    public void run(){
        String mensajeRecibido;
        boolean conectado = true;
        // Se apunta a la lista de observadores de mensajes
        mensajes.addObserver(this);
        
        while (conectado) {
            try {
                // Lee un mensaje enviado por el cliente
                mensajeRecibido = entradaDatos.readUTF();
                // Pone el mensaje recibido en mensajes para que se notifique 
                // a sus observadores que hay un nuevo mensaje.
                mensajes.setMensaje(mensajeRecibido);
            } catch (IOException ex) {
            	System.err.println("Cliente con la IP " + socket.getInetAddress().getHostName() + " desconectado.");
            	cont--;
            	System.out.println("Hay " + cont + " personas conectadas");
                conectado = false; 
                // Si se ha producido un error al recibir datos del cliente se cierra la conexion con el.
                try {
                    entradaDatos.close();
                    salidaDatos.close();
                } catch (IOException ex2) {
                	System.err.println("Error al cerrar los stream de entrada y salida :" + ex2.getMessage());
                }
            }
        }   
    }
    
    @Override
    public void update(Observable o, Object arg) {
        try {
            // Envia el mensaje al cliente
            salidaDatos.writeUTF(arg.toString());
        } catch (IOException ex) {
        	System.err.println("Error al enviar mensaje al cliente (" + ex.getMessage() + ").");
        }
    }
public static void main(String[] args) {
        
        int puerto = 1234;
        int maximoConexiones = 10; // Maximo de conexiones simultaneas
        ServerSocket servidor = null; 
        Socket socket = null;
        MensajesChat mensajes = new MensajesChat();
        
        
        try {
            // Se crea el serverSocket
            servidor = new ServerSocket(puerto, maximoConexiones);
            
            // Bucle infinito para esperar conexiones
            while (true) {
            	System.err.println("Servidor a la espera de conexiones.");
                socket = servidor.accept();
                System.err.println("Cliente con la IP " + socket.getInetAddress().getHostName() + " conectado.");
                
                ServidorChat cc = new ServidorChat(socket, mensajes);
                cc.start();
                cont++;
                System.out.println("Hay " + cont + " personas conectadas");
            }
            
        } catch (IOException ex) {
        	System.err.println("Error: " + ex.getMessage());
        } finally{
            try {
                socket.close();
                servidor.close();
            } catch (IOException ex) {
            	System.err.println("Error al cerrar el servidor: " + ex.getMessage());
            }
        }
    }
}
