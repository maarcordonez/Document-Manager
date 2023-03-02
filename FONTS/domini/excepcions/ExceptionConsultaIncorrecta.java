package domini.excepcions;

public class ExceptionConsultaIncorrecta extends Exception {
    public ExceptionConsultaIncorrecta(String consulta) {
        super("La consulta: " + consulta + " es incorrecta");
    }

    public ExceptionConsultaIncorrecta() {
        super();
    }
}
