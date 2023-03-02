package persistencia.tests;


import org.junit.Assert;
import org.junit.Test;
import persistencia.classes.ParseXML;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

public class ParseXMLTest {
    @Test
    public void guardarDocument() {
        ParseXML p = new ParseXML();
        p.guardaDocument("./documentsTest", "mojon", "lluis", "això es una \n altra prova");
    }

    @Test
    public void obrirDocument() throws ParserConfigurationException, IOException {
        ParseXML p = new ParseXML();
        ArrayList rebut = p.llegeixDocument("./documentsTest/mojon_lluis.xml");
        ArrayList expected = new ArrayList();
        expected.add("mojon");
        expected.add("lluis");
        expected.add("això es una \n altra prova");
        Assert.assertEquals(expected, rebut);
    }
}
