package domini.excepcions;

public class ExceptionDocumentJaExisteix extends Exception {
    public ExceptionDocumentJaExisteix(String titol, String autor) {
        super("L'autor '" + autor + "' ja te una obra amb el titol '" + titol + "'");
    }

    public ExceptionDocumentJaExisteix() {
        super();
    }
}
