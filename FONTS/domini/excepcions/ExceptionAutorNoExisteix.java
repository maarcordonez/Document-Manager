package domini.excepcions;

public class ExceptionAutorNoExisteix extends Exception {
    public ExceptionAutorNoExisteix(String autor) {
        super("L'autor '" + autor + "' no existeix");
    }

    public ExceptionAutorNoExisteix() {
        super();
    }
}
