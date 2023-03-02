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

/**Guarda la informacio d'un document en format .txt
 * @author marc.ordonez
 */
public class ParseTXT extends Parse{
    /**
     * Llegeix un document que es troba al filepath i que esta en format .txt
     * @param filepath Es el directori incloent el nom de l'arxiu que es vol llegir
     * @return Llista amb les dades del document
     */
    public ArrayList llegeixDocument(String filepath) throws FileNotFoundException {
        ArrayList llista = new ArrayList<>();
        File myObj = new File(filepath);
        Scanner myReader = new Scanner(myObj);
        String autor = null;
        String titol = null;
        String contingut = null;
        if (myReader.hasNextLine()) autor = myReader.nextLine();
        if (myReader.hasNextLine())  titol = myReader.nextLine();
        if (myReader.hasNextLine())  contingut = myReader.nextLine();
        while (myReader.hasNextLine()) {
            contingut += "\n" + myReader.nextLine();
        }

        myReader.close();

        llista.add(titol);
        llista.add(autor);
        llista.add(contingut);
        return llista;
    }

    /**
     * Guarda el document al filepath en el format .txt
     * @param filepath Nom del document que volem guardar
     * @param titol Titol del document
     * @param autor Autor del document
     * @param contingut Contingut del document
     */
    public void guardaDocument(String filepath , String titol, String autor, String contingut){
        // Creating an instance of file
        Path path = Paths.get( filepath + "/" + titol + "_" + autor + ".txt");

        // Custom string as an input
        String str = autor + "\n" + titol + "\n" + contingut;

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
