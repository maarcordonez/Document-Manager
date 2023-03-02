package domini.excepcions;

public class ExceptionAutorJaExisteix extends Exception {
    public ExceptionAutorJaExisteix(String autor) {
        super("L'autor '" + autor + "' ja existeix");
    }

    public ExceptionAutorJaExisteix() {
        super();
    }
}

