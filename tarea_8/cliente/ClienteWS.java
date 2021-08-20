import java.io.*;
import java.net.*;
import java.util.Scanner;

import com.google.gson.*;

public class ClienteWS{

  private static final String URL_BASE = "http://13.90.47.149:8080/Servicio/rest/ws";
  private static final Scanner sc = new Scanner( System.in );

  private static final Gson j = new GsonBuilder()
    .registerTypeAdapter(byte[].class,new AdaptadorGsonBase64())
    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
    .create();

  public static void main( String[] args ){
    try{
      desplegarMenu();
      char opcion = sc.nextLine().charAt(0); 
      while( opcion != 'd' ){
	switch( opcion ){
	  case 'a':
	    altaUsuario();
	    break;
	  case 'b':
	    consultaUsuario();
	    break;
	  case 'c':
	    borrarUsuario();
	    break;
	  default:
	    System.out.println("Opcion no valida");
	    break;
	}
	desplegarMenu();
	opcion = sc.nextLine().charAt(0); 
      }
    }catch(Exception e){
      e.printStackTrace();
    }  
  }
  static void desplegarMenu(){
    System.out.printf("\n******* Cliente WS  *******\n"+
		       "** Selecciona una opcion **\n"+
		       "a. Alta usuario\n"+
		       "b. Consulta usuario\n"+
		       "c. Borrar usuario\n"+
		       "d. Salir\n\n"+
		       "Opcion: ");
  }

  static void altaUsuario() throws Exception { 
    try{
      System.out.print("\nEmail: ");
      String email = sc.nextLine();

      if( !Validador.validarCorreo( email ) ){
	System.out.println("El email no es valido");
	return;
      }

      System.out.printf("Nombre: ");
      String nombre = sc.nextLine();   

      if( nombre.length() == 0 ){
	System.out.println("El nombre no es valido");
	return;
      }

      System.out.printf("Apellido paterno: ");
      String apellido_paterno = sc.nextLine();

      if( apellido_paterno.length() == 0 ){
	System.out.println("El apellido paterno no es valido");
	return;
      }

      System.out.printf("Apellido materno: ");
      String apellido_materno = sc.nextLine();    
    
      System.out.printf("Fecha de nacimiento (AAAA-MM-DD): ");
      String fecha_nacimiento = sc.nextLine();
    
      if( !Validador.validarFecha( fecha_nacimiento ) ){
	System.out.println("El la fecha de nacimiento no es valida");
	return;
      }

      System.out.printf("Telefono: ");
      String telefono = sc.nextLine();    
      
      System.out.printf("Genero (M/F): ");
      String genero = sc.nextLine();    

      if( !genero.equals("M") && !genero.equals("F") ){
	System.out.println("El genero no es valido");
	return;
      }

      URL url = new URL( URL_BASE + "/alta_usuario" );
      HttpURLConnection conexion = (HttpURLConnection)url.openConnection();

      conexion.setDoOutput( true );
      conexion.setRequestMethod("POST");
      conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

      Usuario r = new Usuario();
      r.email = email;
      r.nombre = nombre;
      r.apellido_paterno = apellido_paterno;
      r.apellido_materno = apellido_materno;
      r.fecha_nacimiento = fecha_nacimiento;
      r.telefono = telefono;
      r.genero = genero;

      String parametros = j.toJson( r );
      parametros = "usuario=" + URLEncoder.encode( parametros, "UTF-8");
  
      OutputStream os = conexion.getOutputStream();
      os.write( parametros.getBytes() );
      os.flush();

      if( conexion.getResponseCode() == 200){
	BufferedReader br = new BufferedReader( new InputStreamReader( conexion.getInputStream() ));
	System.out.println("\nId del usuario creado: " + br.readLine() );
      }else{
	BufferedReader br = new BufferedReader( new InputStreamReader( conexion.getErrorStream() ));
	String respuesta = null;
	while( (respuesta = br.readLine()) != null )
	  System.out.println( respuesta );  
      }

      conexion.disconnect();
    }catch(Exception e){
      System.out.println("Error al dar de alta usuario");
    }
  }

  static void consultaUsuario(){
    try{
      System.out.printf("Ingresa el id del usuario que deseas consultar: ");
      String id_usuario = sc.nextLine();
    
      URL url = new URL( URL_BASE + "/consulta_usuario" );
      HttpURLConnection conexion = (HttpURLConnection)url.openConnection();

      conexion.setDoOutput( true );
      conexion.setRequestMethod("POST");
      conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

      String parametros = "id_usuario=" + URLEncoder.encode( id_usuario, "UTF-8" );	

      OutputStream os = conexion.getOutputStream();
      os.write( parametros.getBytes() );
      os.flush();

      if( conexion.getResponseCode() == 200){
	BufferedReader br = new BufferedReader( new InputStreamReader( conexion.getInputStream() ));

	String respuesta = null;
	String informacion = "";
	while( (respuesta = br.readLine()) != null )
	  informacion += respuesta;
  
	Usuario r = Usuario.valueOf( informacion );
	System.out.println("\n\tCliente: "+
			   "\n\tNombre: "+r.nombre+
			   "\n\tApellido paterno: "+ r.apellido_paterno+
			   "\n\tApellido materno: "+ r.apellido_materno+
			   "\n\tFecha de nacimiento: "+ r.fecha_nacimiento+
			   "\n\tTelefono: "+ r.telefono+
			   "\n\tGenero: "+r.genero);

	
      }else{
	BufferedReader br = new BufferedReader( new InputStreamReader( conexion.getErrorStream() ));
	String respuesta = null;
	while( (respuesta = br.readLine()) != null )
	  System.out.println( respuesta );  
      }

      conexion.disconnect();   
    }catch(Exception e){
      System.out.println("Error al consultar usuario");
    }
  }

  static void borrarUsuario(){
    try{
      System.out.printf("Ingresa el id del usuario que deseas borrar: ");
      String id_usuario = sc.nextLine();
    
      URL url = new URL( URL_BASE + "/borra_usuario" );
      HttpURLConnection conexion = (HttpURLConnection)url.openConnection();

      conexion.setDoOutput( true );
      conexion.setRequestMethod("POST");
      conexion.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

      String parametros = "id_usuario=" + URLEncoder.encode( id_usuario, "UTF-8" );	

      OutputStream os = conexion.getOutputStream();
      os.write( parametros.getBytes() );
      os.flush();

      if( conexion.getResponseCode() == 200){	
	  System.out.println( "\nEl usuario ha sido borrado" );
      }else{
	BufferedReader br = new BufferedReader( new InputStreamReader( conexion.getErrorStream() ));
	String respuesta = null;
	while( (respuesta = br.readLine()) != null )
	  System.out.println( respuesta );  
      }

      conexion.disconnect();   
    }catch(Exception e){
      System.out.println("Error al borrar usuario");
    } 
  }
}
