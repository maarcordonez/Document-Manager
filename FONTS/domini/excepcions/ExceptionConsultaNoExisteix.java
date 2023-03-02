package domini.excepcions;

public class ExceptionConsultaNoExisteix extends Exception{
    public ExceptionConsultaNoExisteix(String consulta) {
        super("La consulta " + consulta + " no existeix");
    }

    public ExceptionConsultaNoExisteix() {
        super();
    }
}
