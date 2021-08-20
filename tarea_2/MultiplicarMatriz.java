class MultiplicarMatriz{
  static int N = 1000;
  static int[][] A = new int[N][N];
  static int[][] B = new int[N][N];
  static int[][] C = new int[N][N];

  public static void main(String[] args){

    long t1 = System.currentTimeMillis();
    for( int i = 0; i < N; i++ )
      for( int j = 0; j < N; j++ ){
	A[i][j] = 2*i - j;
	B[i][j] = i + 2*j;
	C[i][j] = 0;
      }
    
    for( int i = 0; i < N; i++ )
      for( int j = 0; j < N; j++ )
	for( int k = 0; k < N; k++ )
	  C[i][j] = C[i][j] + A[i][k]*B[k][j];

     long t2 = System.currentTimeMillis();
     System.out.println("Tiempo: "+(t2-t1));
  }
}
