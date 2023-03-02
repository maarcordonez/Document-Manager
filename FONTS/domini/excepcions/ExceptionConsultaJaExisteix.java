package domini.excepcions;

public class ExceptionConsultaJaExisteix extends Exception{
    public ExceptionConsultaJaExisteix(String consulta) {
        super("Ja existeix la consulta " + consulta);
    }

    public ExceptionConsultaJaExisteix() {
        super();
    }
}
