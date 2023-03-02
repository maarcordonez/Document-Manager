package persistencia.classes;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;

/**Classe ControladorPersistencia. S'encarrega de la comunicacio entre domini i persistencia
 * Es singleton.
 *
 * @author Marc Ordonez
 */
public abstract class Parse {
    /**
     * Polimorfisme per llegir un document segons el format
     * @param filepath Es el directori incloent el nom de l'arxiu que es vol llegir
     * @return Llista amb les dades del document
     * @throws IOException Exepcio de Java
     */
    public ArrayList llegeixDocument(String filepath) throws IOException {
        return new ArrayList<>();
    }

    /**
     * Polimorfisme per guardar un document en un path determinat
     * @param filepath Es el directori sense inlcloure nom de l'arxiu (nomes nom carpeta)
     * @param titol Titol del document
     * @param autor Autor del document
     * @param contingut Contingut del document
     */
    public void guardaDocument(String filepath, String titol, String autor, String contingut) {}
}
