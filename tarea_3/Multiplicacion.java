import java.net.Socket;
import java.net.ServerSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

class Multiplicacion{
  static final int PUERTO = 5000;
  static final String HOST = "localhost";
  static long CHECKSUM = 0;
  static final int N = 1000;
  static final int N_2 = N/2;

  static int[][]A = new int[N][N];
  static int[][]B = new int[N][N];
  static int[][]C = new int[N][N];

  static class Worker extends Thread{
    private Socket cliente = null;
    private int nodo;
    
    public Worker( Socket cliente, int nodo ){
      this.cliente = cliente;
      this.nodo = nodo;
    }

    public void run(){
      try{
	  DataInputStream entrada = new  DataInputStream( cliente.getInputStream() );
	  DataOutputStream salida = new DataOutputStream( cliente.getOutputStream() );

	  int inicioA = nodo < 3 ? 0: N_2;
	  int finA = inicioA + N_2;
	  int inicioB = nodo % 2 == 0? N_2: 0;
	  int finB = inicioB + N_2;

	  ByteBuffer ma = ByteBuffer.allocate(N_2*N*4);
	  ByteBuffer mb = ByteBuffer.allocate(N_2*N*4);

	  for( int i = inicioA; i < finA; i++ )
	    for( int j = 0; j < N; j++ )
	      ma.putInt( A[i][j] );    
	  
	  for( int i = inicioB; i < finB; i++ )
	    for( int j = 0; j < N; j++ )
	      mb.putInt( B[i][j] );  

	  byte[] a = ma.array();
	  byte[] b = mb.array();
	  salida.write( a );
	  salida.write( b );
	  
	  byte[] c = new byte[N_2*N_2*4];
	  read( entrada, c, 0, N_2*N_2*4);
	  ByteBuffer mc = ByteBuffer.wrap( c );

	  int inicioX = nodo < 3? 0: N_2;
	  int finX = inicioX + N_2;
	  int inicioY = nodo % 2 == 0? N_2: 0;  
	  int finY = inicioY + N_2;
      
	  for( int i = inicioX; i < finX; i++ )
	    for( int j = inicioY; j < finY; j++ )
	      C[i][j] = mc.getInt();

	  entrada.close();
	  salida.close();
	  cliente.close();
      }catch(Exception e){
	System.out.println("Error al procesas nodo: " + nodo);
      }
    }
  }

  public static void main(String[] args){
    int nodo = -1;
    try{
      nodo = Integer.parseInt( args[0] );
      
      if( nodo == 0 ){
	procesoServidor();
      }else if( nodo > 0 && nodo < 5 ){
	procesoCliente();
      }else{
	System.out.println("El numero de nodo: "+ nodo + " no es valido");
	System.exit(0);
      }

    }catch( NumberFormatException e){
      System.out.println("El numero de nodo: "+ nodo + " no es valido");
      System.out.println("Ejemplo de uso: java Multiplicacion <nodo>, nodo = {0, 1, 2, 3, 4}");
      System.exit(0);
    }
  }

  static void procesoServidor(){
    try{
      inicializaMatrices();
      ServerSocket servidor = new ServerSocket( PUERTO );
      Worker[] w = new Worker[4];
      Socket cliente;

      System.out.println("Servidor iniciado, esperando clientes.");
      for( int i = 0; i < 4; i++ ){
	cliente = servidor.accept();
	w[i] = new Worker( cliente, i + 1 );
	w[i].start();
      }
    
      for( int i = 0; i < 4; i++ )
	  w[i].join();

      if( N == 4 ){
	desplegarMatriz(A, "A");
	desplegarMatriz(B, "B^T");
	desplegarMatriz( C, "C" );
      }

      calcularSumaDeComprobacion();
      System.out.println("\nLa suma de comprobacion es: " + CHECKSUM);

      servidor.close();

    }catch( IOException ioex ){
      System.out.println("Error: "+ ioex.getMessage());
      System.exit(0);
    }catch( InterruptedException iex ){
      System.out.println("Error: "+ iex.getMessage());
      System.exit(0);
    }
  }

  static void procesoCliente(){
    try{
      Socket servidor = new Socket( HOST, PUERTO );	
      DataOutputStream salida = new DataOutputStream( servidor.getOutputStream() ); 
      DataInputStream entrada = new DataInputStream( servidor.getInputStream() );

      System.out.println("Conexion establecida con el servidor");
      int[][] matrizA = new int[N_2][N];
      int[][] matrizB = new int[N_2][N];

      byte[] a = new byte[N_2*N*4];
      byte[] b = new byte[N_2*N*4];
      
      System.out.println("Recibiendo Matriz A");
      read( entrada, a, 0, N_2*N*4);
      System.out.println("Recibiendo Matriz B");
      read( entrada, b, 0, N_2*N*4);
      
      ByteBuffer ma = ByteBuffer.wrap( a );
      ByteBuffer mb = ByteBuffer.wrap( b );
      
      for( int i = 0; i < N_2; i++ )
	for( int j = 0; j < N; j++ )
	  matrizA[i][j] = ma.getInt();
      
      for( int i = 0; i < N_2; i++ )
	for( int j = 0; j < N; j++ )
	  matrizB[i][j] = mb.getInt();

      ByteBuffer mc = ByteBuffer.allocate((N_2 * N_2)*4);
      int tmp = 0;
      for( int i = 0; i < N/2; i++ ){
	for( int j = 0; j < N/2; j++ ){
	  tmp = 0;
	  for( int k = 0; k < N; k++){
	    tmp += matrizA[i][k] * matrizB[j][k];
	  }
	  mc.putInt( tmp );
	}
      }
      
      System.out.println("Enviando resultado");
      byte[] c = mc.array();
      salida.write( c );

      entrada.close();
      salida.close();
      servidor.close();
    }catch( IOException ioex ){
      System.out.println("Error: "+ ioex.getMessage());
      System.exit(0);
    }  
  }

  static void read( DataInputStream f, byte[] b, int posicion, int longitud ) throws IOException {
    while( longitud > 0 ){
      int n = f.read( b, posicion, longitud );
      posicion += n;
      longitud -= n;
    }
  }

  static void inicializaMatrices(){
    for(int i = 0; i < N; i++ )
      for(int j = 0; j < N; j++ ){
	A[i][j] = i - 2 * j;
	B[i][j] = i + 2 * j;
	C[i][j] = 0;
      }

    int tmp;
    for(int i = 0; i < N; i++ )
      for(int j = 0; j < i; j++ ){
	tmp = B[i][j];
	B[i][j] = B[j][i]; 
	B[j][i] = tmp;
      }
  }

  static void calcularSumaDeComprobacion(){
    for(int i = 0; i < N; i++ )
      for( int j = 0; j < N; j++ )
	CHECKSUM += C[i][j]; 
  }

  static void desplegarMatriz( int matriz[][], String m ){
    System.out.println("\n"+ m + " = " );
    for( int i = 0; i < matriz.length; i++ ){
      System.out.printf("\t| ");
      for( int j = 0; j < matriz[0].length; j++ ){
	System.out.printf("%4d ", matriz[i][j]);
      }
      System.out.println("|");
    }
  }
}
