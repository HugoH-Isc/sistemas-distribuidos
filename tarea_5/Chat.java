import java.net.*;
import java.io.*;
import java.util.*;

public class Chat{

  private static final String HOST = "230.0.0.0";
  private static final int PUERTO = 5000;
  private static final int MAX_LEN = 1024;
  private static String nombre;
  private static final String FIN_DE_TEXTO = "$FIN";
  private static int maxima_longitud;

  static class Worker extends Thread{
    public void run(){
      try{
	InetAddress grupo = InetAddress.getByName( HOST );
	MulticastSocket socket = new MulticastSocket( PUERTO );
	socket.joinGroup( grupo );
	String mensaje;
	int indice;
	while( true ){
	      mensaje = new String( recibe_mensaje_multicast( socket, MAX_LEN ));
	      indice = mensaje.indexOf( FIN_DE_TEXTO );
	      mensaje = mensaje.substring(0, indice);
	      System.out.println("\r" + mensaje + "                   \n" );
              System.out.print("\r"+ "Ingresa el mensaje a enviar"+ " > ");
	}
      }catch( IOException ioex ){
	  System.out.println( "Error al recibir mensaje" );
      }
    }
    
    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException{
      byte[] buffer = new byte[ longitud_mensaje ];
      DatagramPacket paquete = new DatagramPacket( buffer, buffer.length );
      socket.receive( paquete );
      return paquete.getData();
    }
  }

  public static void main( String[] args ){
    if( args.length == 0 ){
      System.out.println("Debes pasar como argumento el nombre del usuario");
      System.out.println("Ejemplode uso: java Chat <nombre-usuario>");
      System.exit(0);
    }else{
      try{
	Worker w = new Worker();
	w.start();

	nombre = args[0];
	maxima_longitud = MAX_LEN + nombre.length() + FIN_DE_TEXTO.length() - 2;
	Scanner sc = new Scanner(System.in, "UTF-16");
	String mensaje;
	while( true ){
	    System.out.print("\r" + "Ingresa el mensaje a enviar" + " > ");
	    mensaje = System.console().readLine();
	    if ( mensaje.length() > maxima_longitud ){
		System.out.println("El mensaje no puede exceder los " + maxima_longitud + "caracteres" );
	    }else{
		mensaje = nombre + ": " + mensaje + FIN_DE_TEXTO;
	    	envia_mensaje_multicast( mensaje.getBytes(), HOST, PUERTO );
	    }
	}
      }catch( IOException ioex ){
	  System.out.println("Error al enviar mensaje");
      }
    }
  }

  static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException{
    DatagramSocket socket = new DatagramSocket();
    socket.send( new DatagramPacket( buffer, buffer.length, InetAddress.getByName( ip ), puerto ));
    socket.close();
  } 
}
