package domini.excepcions;

public class ExceptionErrorEnConsulta extends Exception{
    public ExceptionErrorEnConsulta(String error) {
        super("Error inesperat a la consulta " + error + " intenta-ho de nou amb una altra consulta");
    }
    public ExceptionErrorEnConsulta() {
        super();
    }
}
