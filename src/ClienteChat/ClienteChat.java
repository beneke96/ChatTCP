package ClienteChat;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.*;

public class ClienteChat extends JFrame {

	private JTextArea mensajesChat;
	private Socket socket;

	private int puerto;
	private String host;
	private String usuario;

	public ClienteChat() {
		super("Cliente Chat");

		
		mensajesChat = new JTextArea();
		JScrollPane scrollMensajesChat = new JScrollPane(mensajesChat);
		JTextField tfMensaje = new JTextField("");
		JButton btEnviar = new JButton("Enviar");

		
		Container c = this.getContentPane();
		c.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();

		gbc.insets = new Insets(20, 20, 20, 20);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		gbc.weightx = 1;
		gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		c.add(scrollMensajesChat, gbc);
		
		gbc.gridwidth = 1;
		gbc.weighty = 0;

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0, 20, 20, 20);

		gbc.gridx = 0;
		gbc.gridy = 1;
		c.add(tfMensaje, gbc);
		
		gbc.weightx = 0;

		gbc.gridx = 1;
		gbc.gridy = 1;
		c.add(btEnviar, gbc);

		this.setBounds(400, 100, 400, 500);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		VentanaConfiguracion vc = new VentanaConfiguracion(this);
		host = vc.getHost();
		puerto = vc.getPuerto();
		usuario = vc.getUsuario();

		System.err.println("Quieres conectarte a " + host + " en el puerto " + puerto + " con el nombre de ususario: "
				+ usuario + ".");

		// Se crea el socket para conectar con el Sevidor del Chat
		try {
			socket = new Socket(host, puerto);
		} catch (UnknownHostException ex) {
			System.err.println("No se ha podido conectar con el servidor (" + ex.getMessage() + ").");
		} catch (IOException ex) {
			System.err.println("No se ha podido conectar con el servidor (" + ex.getMessage() + ").");
		}

		// Accion para el boton enviar
		ConexionServidor conex = new ConexionServidor(socket, tfMensaje, usuario);
		btEnviar.addActionListener(conex);

	}
	class ConexionServidor implements ActionListener {
	    
	    private Socket socket; 
	    private JTextField tfMensaje;
	    private String usuario;
	    private DataOutputStream salidaDatos;
	    
	    public ConexionServidor(Socket socket, JTextField tfMensaje, String usuario) {
	        this.socket = socket;
	        this.tfMensaje = tfMensaje;
	        this.usuario = usuario;
	        try {
	            this.salidaDatos = new DataOutputStream(socket.getOutputStream());
	        } catch (IOException ex) {
	        	System.err.println("Error al crear el stream de salida : " + ex.getMessage());
	        } catch (NullPointerException ex) {
	        	System.err.println("El socket no se creo correctamente. ");
	        }
	    }

	    @Override
	    public void actionPerformed(ActionEvent e) {
	        try {
	            salidaDatos.writeUTF(usuario + ": " + tfMensaje.getText() );
	            tfMensaje.setText("");
	        } catch (IOException ex) {
	        	System.err.println("Error al intentar enviar un mensaje: " + ex.getMessage());
	        }
	    }

	}
	// Recibe los mensajes del chat reenviados por el servidor

	public void recibirMensajesServidor() {
		
		DataInputStream entradaDatos = null;
		String mensaje;
		try {
			entradaDatos = new DataInputStream(socket.getInputStream());
		} catch (IOException ex) {
			System.err.println("Error al crear el stream de entrada: " + ex.getMessage());
		} catch (NullPointerException ex) {
			System.err.println("El socket no se creo correctamente. ");
		}

		// Bucle infinito que recibe mensajes del servidor
		boolean conectado = true;
		while (conectado) {
			try {
				mensaje = entradaDatos.readUTF();
				mensajesChat.append("\n" + mensaje);
			} catch (IOException ex) {
				System.err.println("Error al leer del stream de entrada: " + ex.getMessage());
				conectado = false;
			} catch (NullPointerException ex) {
				System.err.println("El socket no se creo correctamente. ");
				conectado = false;
			}
		}
	}

	public static void main(String[] args) {

		ClienteChat c = new ClienteChat();
		c.recibirMensajesServidor();
	}

}