/*
  Servicio.java
  Servicio web tipo REST
  Carlos Pineda Guerrero 2021
*/
package negocio;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.QueryParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Response;

import java.sql.*;
import javax.sql.DataSource;
import javax.naming.Context;
import javax.naming.InitialContext;

import java.util.ArrayList;
import com.google.gson.*;

// la URL del servicio web es http://localhost:8080/Servicio/rest/ws
// donde:
//	"Servicio" es el dominio del servicio web (es decir, el nombre de archivo Servicio.war)
//	"rest" se define en la etiqueta <url-pattern> de <servlet-mapping> en el archivo WEB-INF\web.xml
//	"ws" se define en la siguiente anotacin @Path de la clase Servicio

@Path("ws")
public class Servicio{

  static DataSource pool = null;
  static{		
    try
    {
      Context ctx = new InitialContext();
      pool = (DataSource)ctx.lookup("java:comp/env/jdbc/datasource_Servicio");
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  static Gson j = new GsonBuilder()
		.registerTypeAdapter(byte[].class,new AdaptadorGsonBase64())
		.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
		.create();

  @POST
  @Path("alta_usuario")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response alta(@FormParam("usuario") Usuario usuario) throws Exception
  {
    Connection conexion = pool.getConnection();

    if (usuario.email == null || usuario.email.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el email"))).build();

    if (usuario.nombre == null || usuario.nombre.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el nombre"))).build();

    if (usuario.apellido_paterno == null || usuario.apellido_paterno.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar el apellido paterno"))).build();

    if (usuario.fecha_nacimiento == null || usuario.fecha_nacimiento.equals(""))
      return Response.status(400).entity(j.toJson(new Error("Se debe ingresar la fecha de nacimiento"))).build();

    try{
      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT id_usuario FROM usuarios WHERE email=?");
      try{
        stmt_1.setString(1,usuario.email);
        ResultSet rs = stmt_1.executeQuery();
        try{
          if (rs.next())
             return Response.status(400).entity(j.toJson(new Error("El email ya existe"))).build();
        }finally{
          rs.close();
        }
      }finally{
        stmt_1.close();
      }

      PreparedStatement stmt_2 = conexion.prepareStatement("INSERT INTO usuarios VALUES (0,?,?,?,?,?,?,?)");
      try{
        stmt_2.setString(1,usuario.email);
        stmt_2.setString(2,usuario.nombre);
        stmt_2.setString(3,usuario.apellido_paterno);
        stmt_2.setString(4,usuario.apellido_materno);
        stmt_2.setString(5,usuario.fecha_nacimiento);
        stmt_2.setString(6,usuario.telefono);
        stmt_2.setString(7,usuario.genero);
        stmt_2.executeUpdate();
      }finally{
        stmt_2.close();
      }

      PreparedStatement stmt_3 = conexion.prepareStatement("SELECT id_usuario FROM usuarios WHERE email=?");
      try{
        stmt_3.setString(1,usuario.email);
        ResultSet rs = stmt_3.executeQuery();
	if( rs.next() ){
	  usuario.id_usuario = rs.getInt(1);
	}
      }finally{
        stmt_3.close();
      }

    }catch (Exception e){
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }finally{
      conexion.close();
    }
    return Response.ok().entity(j.toJson( usuario.id_usuario )).build();
  }

  @POST
  @Path("consulta_usuario")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response consulta(@FormParam("id_usuario") int id_usuario) throws Exception{
    Connection conexion= pool.getConnection();

    try
    {
      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT id_usuario,email,nombre,apellido_paterno,apellido_materno,fecha_nacimiento,telefono,genero FROM usuarios WHERE id_usuario=?");
      try{
        stmt_1.setInt(1,id_usuario);
        ResultSet rs = stmt_1.executeQuery();
        try{
          if (rs.next()){
            Usuario r = new Usuario();
	    r.id_usuario = rs.getInt(1);
            r.email = rs.getString(2);
            r.nombre = rs.getString(3);
            r.apellido_paterno = rs.getString(4);
            r.apellido_materno = rs.getString(5);
            r.fecha_nacimiento = rs.getString(6);
            r.telefono = rs.getString(7);
            r.genero = rs.getString(8);
	    r.foto =null;
            return Response.ok().entity(j.toJson(r)).build();
          }
          return Response.status(400).entity(j.toJson(new Error("El id no existe"))).build();
        }
        finally{
          rs.close();
        }
      }
      finally
      {
        stmt_1.close();
      }
    }
    catch (Exception e)
    {
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }
    finally
    {
      conexion.close();
    }
  }
 
  @POST
  @Path("borra_usuario")
  @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
  @Produces(MediaType.APPLICATION_JSON)
  public Response borra(@FormParam("id_usuario") int id_usuario) throws Exception{
    Connection conexion= pool.getConnection();
    try{
      PreparedStatement stmt_1 = conexion.prepareStatement("SELECT 1 FROM usuarios WHERE id_usuario=?");
      try{
        stmt_1.setInt(1,id_usuario);
        ResultSet rs = stmt_1.executeQuery();
        try{
          if (!rs.next())
		return Response.status(400).entity(j.toJson(new Error("El id de usuario no existe"))).build();
        }
        finally{
          rs.close();
        }
      }finally{
        stmt_1.close();
      }
      PreparedStatement stmt_2 = conexion.prepareStatement("DELETE FROM fotos_usuarios WHERE id_usuario=?");
      try{
        stmt_2.setInt(1,id_usuario);
	stmt_2.executeUpdate();
      }finally{
        stmt_2.close();
      }
      PreparedStatement stmt_3 = conexion.prepareStatement("DELETE FROM usuarios WHERE id_usuario=?");
      try{
        stmt_3.setInt(1,id_usuario);
	stmt_3.executeUpdate();
      }finally{
        stmt_3.close();
      }
    }catch (Exception e){
      return Response.status(400).entity(j.toJson(new Error(e.getMessage()))).build();
    }finally{
      conexion.close();
    }
    return Response.ok().build();
  }
}
