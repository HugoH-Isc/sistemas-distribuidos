import java.rmi.Naming;

public class ClienteRMI{
   
  private static final int N = 8;
  private static final String NODO1_URL = "rmi://localhost/multiplica_matrices";
  private static final String NODO2_URL = "rmi://localhost/multiplica_matrices";
  
  private static float[][] A = new float[N][N];
  private static float[][] B = new float[N][N];
  private static float[][] C = new float[N][N];
  
  private static double checksum = 0.0;

  public static void main( String[] args ){
    try{

      inicializarMatrices();
      transponerMatriz( B );

      float[][] A1 = separar_matriz( A, 0 );
      float[][] A2 = separar_matriz( A, N/2 );
      float[][] B1 = separar_matriz( B, 0 );
      float[][] B2 = separar_matriz( B, N/2 );

      InterfaceRMI rmi_nodo1 = (InterfaceRMI)Naming.lookup( NODO1_URL );
      InterfaceRMI rmi_nodo2 = (InterfaceRMI)Naming.lookup( NODO1_URL );

      float[][] C1 = rmi_nodo1.multiplica_matrices( A1, B1 );
      float[][] C2 = rmi_nodo1.multiplica_matrices( A1, B2 );
      float[][] C3 = rmi_nodo2.multiplica_matrices( A2, B1 );
      float[][] C4 = rmi_nodo2.multiplica_matrices( A2, B2 );

      acomoda_matriz( C, C1, 0, 0 );
      acomoda_matriz( C, C2, 0, N/2 );
      acomoda_matriz( C, C3, N/2, 0 );
      acomoda_matriz( C, C4, N/2, N/2 );

      calcularChecksum();
      if( N == 8 ){
	imprimirMatriz( A, "Matriz A" );
	imprimirMatriz( B, "Matriz B^T" );
	imprimirMatriz( C, "Matriz C" );
      }
      System.out.println( "\nLa suma de comprobacion es: " + checksum );
    }catch( Exception ex ){
      System.out.println("Algo salio mal");
    }
  }


  static void calcularChecksum (){
    for( int i = 0; i < N; i++)
      for( int j = 0; j < N; j++ ) 
	checksum += C[i][j];
  }
  
  static void imprimirMatriz( float[][] matriz, String nombre ){
    System.out.println( "\n" + nombre + "\n" );
    for( int i = 0; i < N; i++){
      System.out.print("\t|");
      for( int j = 0; j < N; j++ ) {
	System.out.printf(" %10.2f", matriz[i][j]);
      }
      System.out.println(" |");
    }
  }

  static void acomoda_matriz( float[][] C, float[][] M, int renglon, int columna ){
    for( int i = 0; i < N/2; i++ ) 
      for( int j = 0; j < N/2; j++ )
	C[i + renglon][j + columna] = M[i][j]; 
  }

  static float[][] separar_matriz( float[][] A, int inicio ){
    float[][] m = new float[N/2][N];
    for( int i = 0; i < N/2; i++ )
      for( int j = 0; j < N; j++ )
	m[i][j] = A[i+inicio][j];

    return m;
  }

  static void transponerMatriz( float[][] M ){
    float tmp = 0f;
    for( int i = 0; i < N; i++ ){
      for( int j = 0; j < i; j++ ){	
	tmp =  M[i][j];
	M[i][j] = M[j][i];
	M[j][i] = tmp;
      }
    }
  }

  static void inicializarMatrices(){
    for( int i = 0; i < N; i++ ){
      for( int j = 0; j < N; j++ ){
	A[i][j] = i - 2 * j;
	B[i][j] = i + 2 * j;
      }
    } 
  }
}
