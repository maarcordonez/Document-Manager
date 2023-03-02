package domini.excepcions;

public class ExceptionObraJaExisteix extends Exception {
    public ExceptionObraJaExisteix(String autor, String obra) {
        super("L'autor '" + autor + "' ja té una obra amb el nom '" + obra + "'");
    }

    public ExceptionObraJaExisteix() {
        super();
    }
}
