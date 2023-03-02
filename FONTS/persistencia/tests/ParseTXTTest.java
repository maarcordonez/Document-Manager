package persistencia.tests;

import org.junit.Test;
import persistencia.classes.ParseTXT;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ParseTXTTest {
    @Test
    public void guardarDocument() {
        ParseTXT p = new ParseTXT();
        p.guardaDocument("./documentsTest", "hola", "es igual", "how are you");
    }

    @Test
    public void obrirDocument() throws FileNotFoundException {
        ParseTXT p = new ParseTXT();
        ArrayList rebut = p.llegeixDocument("./documentsTest/hola_es igual.txt");
        ArrayList expected = new ArrayList();
        expected.add("es igual");
        expected.add("hola");
        expected.add("how are you");
    }
}
