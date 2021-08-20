import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Validador{
  
  private static final String REGEX_CORREO = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
  private static final String REGEX_FECHA  = "\\d{4}-\\d{2}-\\d{2}";

  public static boolean validarCorreo(String correo){
    Pattern p = Pattern.compile( REGEX_CORREO );
    Matcher m = p.matcher( correo );
    return m.find();
  }

  public static boolean validarFecha(String fecha){
    Pattern p = Pattern.compile( REGEX_FECHA );
    Matcher m = p.matcher( fecha );
    return m.find();
  }
}
