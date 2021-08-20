import java.rmi.Naming;

public class ServidorRMI{
  
  private static final String URL = "rmi://localhost/multiplica_matrices"; 

  public static void main( String[] args ){
    try{
      ClaseRMI obj = new ClaseRMI();
      Naming.rebind( URL, obj );
    }catch( Exception ex ){
      System.out.println("Error en el servidor RMI");
      System.out.println( ex.getMessage() );
    }
  }
}
