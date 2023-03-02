package domini.excepcions;

public class ExceptionObraNoExisteix extends Exception {
    public ExceptionObraNoExisteix(String autor, String obra) {
        super("L'autor '" + autor + "' no té cap obra amb el nom '" + obra + "'");
    }

    public ExceptionObraNoExisteix() {
        super();
    }
}
