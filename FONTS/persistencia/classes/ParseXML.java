package persistencia.classes;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**Guarda la informacio d'un document en format .xml
 * @author marc.ordonez
 */
public class ParseXML extends Parse {

    /**
     * Llegeix un document que es troba al filepath i que esta en format .xml
     * @param filepath Es el directori incloent el nom de l'arxiu que es vol llegir
     * @return Llista amb les dades del document
     */
    public ArrayList llegeixDocument(String filepath) {
        Scanner myReader;
        ArrayList<String> info = new ArrayList<>();
        try {
            File myObj = new File(filepath);
            myReader = new Scanner(myObj);
            String text = "";
            int cont = 0;
            String titol = null;
            String autor = null;
            String contingut = null;
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                text += line + "\n";
            }
            myReader.close();
            // Afegim un espai davant i darrere de les etiquetes per poder separar el text per paraules.
            // si tinguessim <titol>hola</titol> seria una unica paraula i no ho detectaria be
            // hauria de ser <titol> hola </titol> per que detecti be l'etiqueta i la paraula
            text = text.replaceAll("<titol>", " <titol> ");
            text = text.replaceAll("</titol>", " </titol> ");
            text = text.replaceAll("<autor>", " <autor> ");
            text = text.replaceAll("</autor>", " </autor> ");
            text = text.replaceAll("<contingut>", " <contingut> ");
            text = text.replaceAll("</contingut>", " </contingut> ");
            String[] fulltext = text.split(" ");
            int i = 0;
            while(i < fulltext.length && cont < 3) {
                String word = fulltext[i];
                if (word.equals("<titol>")) {
                    ++cont;
                    ++i;
                    if (i < fulltext.length && !fulltext[i].equals("</titol>")) {
                        titol = fulltext[i];
                        word = fulltext[i];
                    }
                    ++i;
                    // si estem dins d'una etiqueta mentres no trobem l'etiqueta de tancament tot serà text d'aquella etiqueta
                    // i l'anirem afegint amb un espai entre mig.
                    while (i < fulltext.length && !fulltext[i].equals("</titol>")) {
                        titol += " " + fulltext[i];
                        ++i;
                    }
                }
                if (word.equals("<autor>")) {
                    ++cont;
                    ++i;
                    if (i < fulltext.length && !fulltext[i].equals("</autor>")) {
                        autor = fulltext[i];
                        word = fulltext[i];
                    }
                    ++i;
                    boolean primer = true;
                    while (i < fulltext.length && !fulltext[i].equals("</autor>")) {
                        if (primer )
                        autor += " " + fulltext[i];
                        ++i;
                    }
                }
                if (word.equals("<contingut>")) {
                    ++cont;
                    ++i;
                    if (i < fulltext.length && !fulltext[i].equals("</contingut>")) {
                        contingut = fulltext[i];
                        word = fulltext[i];
                    }
                    ++i;
                    while (i < fulltext.length && !fulltext[i].equals("</contingut>")) {
                        contingut += " " + fulltext[i];
                        ++i;
                    }
                }
                ++i;
            }
            // if (cont != 3) throw exception no s'ha pogut llegir el document
            // fem un strip per eliminar espais que hagin pogut quedar a principi o final de les etiquetes
            //info.add(titol.strip());
            //info.add(autor.strip());
            //info.add(contingut.strip());
            info.add(titol);
            info.add(autor);
            info.add(contingut);
        } catch (FileNotFoundException e) {
        }
        return info;
    }

    /**
     * Guarda el document al filepath en el format .xml
     * @param filepath Es el directori sense inlcloure nom de l'arxiu (nomes nom carpeta)
     * @param titol Titol del document
     * @param autor Autor del document
     * @param contingut Contingut del document
     */
    public void guardaDocument(String filepath, String titol, String autor, String contingut){
        // Creating an instance of file
        Path path = Paths.get(filepath + "/" + titol + "_" + autor + ".xml");

        // Custom string as an input
        String str = "<document>\n" +
                "<titol>" + titol + "</titol>\n" +
                "<autor>" + autor + "</autor>\n" +
                "<contingut>" + contingut + "</contingut>\n" +
                "</document>";

        // Try block to check for exceptions
        try {
            // Now calling Files.writeString() method
            // with path , content & standard charsets
            Files.writeString(path, str, StandardCharsets.UTF_8);
        }

        // Catch block to handle the exception
        catch (IOException ex) {
            // Print message exception occurred as
            // invalid. directory local path is passed
            System.out.print("Invalid Path");
        }
    }
}
