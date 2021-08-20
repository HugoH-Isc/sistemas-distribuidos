import java.net.Socket;
import java.net.ServerSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

class Token{
  static DataInputStream entrada;
  static DataOutputStream salida;
  static boolean primeraVez = true;
  static String ip;
  static int nodo;
  static int token;
  static int contador = 0;

  static class Worker extends Thread{
    public void run(){
      try{
	ServerSocket server = new ServerSocket( 50000 );
	Socket conexion = server.accept();
        entrada = new DataInputStream( conexion.getInputStream() );
      }catch(Exception e){
	System.out.println("Error al iniciar servidor");
      }
    }
  }

  public static void main(String[] args){
    if( args.length != 2){
      System.out.println( "Se debe pasar como parametro el numero de nodo y la ip del siguiente nodo" );
      System.exit(1);
    } 

    nodo = Integer.valueOf( args[0] );
    ip = args[1];

    Worker w = new Worker(); 
    w.start(); 
    Socket conexion = null; 
    
    for(;;){ 
      try{ 
	conexion = new Socket( ip, 50000 );
	break; 
      }catch(Exception e){
	try{
	  Thread.sleep( 500 );
	}catch(Exception s){}
      } 
    }
  
    try{
      salida = new DataOutputStream( conexion.getOutputStream() );
      w.join();
    }catch(Exception e){}

    for(;;){
      try{
	if( nodo == 0 ){
	  if( primeraVez ){
	    primeraVez = false;
	    token = 1;
	  }else{
	    token = entrada.readInt();
	    contador += 1;
	    System.out.println("Nodo: " + nodo + "\tContador: " + contador + "\tToken: " + token );
	  }
	}else{
	  token = entrada.readInt();
	  contador += 1 ;
	  System.out.println("Nodo: " + nodo + "\tContador: " + contador + "\tToken: " + token );
	}

	if ( nodo == 0 && contador == 1000 )
	    break;

	salida.writeInt( token );
      }catch(Exception e){
	break;
      }
    }

    try{
      salida.close();
      entrada.close();
      conexion.close();
    }catch(Exception e){
      System.exit(0);
    }
  } 
}
