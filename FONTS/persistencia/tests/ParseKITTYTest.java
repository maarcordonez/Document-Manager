package persistencia.tests;

import org.junit.Assert;
import org.junit.Test;
import persistencia.classes.ParseKITTY;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class ParseKITTYTest {
    @Test
    public void guardarDocument() {
        ParseKITTY p = new ParseKITTY();
        p.guardaDocument("./documentsTest", "el jefazo", "antonio", "un gran señor");
    }

    @Test
    public void obrirDocument() throws FileNotFoundException {
        ParseKITTY p = new ParseKITTY();
        ArrayList rebut = p.llegeixDocument("./documentsTest/prova8.kitty");
        ArrayList expected = new ArrayList<>();
        expected.add("el jefazo");
        expected.add("antonio");
        expected.add("un gran señor");
        Assert.assertEquals(expected, rebut);


        ParseKITTY p2 = new ParseKITTY();
        ArrayList rebut2 = p2.llegeixDocument("./documentsTest/primerContingut.kitty");
        ArrayList expected2 = new ArrayList<>();
        expected2.add("Prova Contingut");
        expected2.add("Jordi");
        expected2.add("Això és el contingut d'aquest \n"+"document. En diferents\n"+"línies.");
        Assert.assertEquals(expected2, rebut2);


        ParseKITTY p3 = new ParseKITTY();
        ArrayList rebut3 = p3.llegeixDocument("./documentsTest/primerAutor.kitty");
        Assert.assertEquals(expected2, rebut3);


        ParseKITTY p4 = new ParseKITTY();
        ArrayList rebut4 = p4.llegeixDocument("./documentsTest/alMigContingut.kitty");
        Assert.assertEquals(expected2, rebut4);


        ParseKITTY p5 = new ParseKITTY();
        ArrayList rebut5 = p5.llegeixDocument("./documentsTest/primerTitol.kitty");
        Assert.assertEquals(expected2, rebut5);
    }
}
