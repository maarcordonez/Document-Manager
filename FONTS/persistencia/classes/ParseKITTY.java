package persistencia.classes;

import domini.utils.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**Guarda la informacio d'un document en el format propietari
 * @author marc.ordonez
 * Aquest format consta de seccions. Cada seccio comença amb una linia amb el nomb de la seccio entre hashtags. Per exemple:
 * ----------------------------
 * #seccio#
 * Lorem ipsum dolore
 * bla bla bla
 * el que vulguis
 * 
 * #seccio2#
 * bon dia
 * ---------------------------
 * La seccio s'acabara quan comeci la seguent, al contrari que passa en xml que pots tenir continguts dins d'altres continguts.
 * La darrera seccio conte tot el contingut des d'aquell punt fins al final (perque no te cap seccio darrere per indicar el
 * final del bloc). En aquest forma es indiferent l'ordre en que es guardi l'informacio, ja que la seccio indicara si el
 * que va a continuacio es el titol, l'autor o el contingut
 */
public class ParseKITTY extends Parse {
    /**
     * Donat un directori i el nom de l'arxiu recull l'informacio d'aquest arxiu en un ArrayList amb titol autor i contingut.
     * L'ordre en que estigues guardada l'informacio al document no importa. Es a dir, pot anar primer el titol o l'autor
     * o el contingut i igualment ho llegira be
     * @param filepath Path del directori on es troba l'arxiu que volem llegir incloent el nom de l'arxiu
     * @return Llista amb les dades del document
     */
    public ArrayList llegeixDocument(String filepath) throws FileNotFoundException {
        ArrayList<Object> llista = new ArrayList<>();
        File myObj = new File(filepath);
        Scanner myReader = new Scanner(myObj);
        String info = "";
        if (myReader.hasNextLine()) info = myReader.nextLine();
        while (myReader.hasNextLine()) {
            info += "\n" + myReader.nextLine();
        }
        info = info.replace("\n", " \n ");
        String[] fulltext = info.split(" ");
        myReader.close();
        String titol = null;
        String autor = null;
        String contingut = null;
        int i = 0;
        while (i < fulltext.length) {
            if (fulltext[i].equals("#titol#")) {
                Pair<String, Integer> p = llegirEtiqueta(fulltext, i + 2); // ens saltem salt de linia
                titol = p.left();
                i = p.right();
            }
            else if (fulltext[i].equals("#autor#")) {
                Pair<String, Integer> p = llegirEtiqueta(fulltext, i + 2); // ens saltem salt de linia
                autor = p.left();
                i = p.right();
            }
            else if (fulltext[i].equals("#contingut#")) {
                Pair<String, Integer> p = llegirEtiqueta(fulltext, i + 2); // ens saltem salt de linia
                contingut = p.left();
                i = p.right();
            }
            else ++i;
        }
        llista.add(titol);
        llista.add(autor);
        llista.add(contingut);
        return llista;
    }

    /**
     * Llegeix el contingut de qualsevol etiqueta fins que s'acaba el document o fins que troba una altra etiqueta
     * @param info text complert a parssejar
     * @param i posició a partir de la que es parsseja (posició següent a l'etiqueta del bloc)
     * @return retorna Pair del contingut del bloc i de l'index del final de bloc
     */
    public Pair<String, Integer> llegirEtiqueta(String[] info, int i) {
        boolean primer = true;
        String text = null;
        while (i < info.length) {
            int index = info[i].indexOf("#");
            boolean esEtiqueta = index != -1 && index != info[i].lastIndexOf("#");
            if (!esEtiqueta) { // si no és etiqueta...
                if (primer) {
                    primer = false;
                    text = info[i];
                } else text += " " + info[i];
                ++i;
            }
            else break;

        }
        return new Pair<>(text, i);
    }

    /**
     * Guarda el document al filepath en el format .kitty
     * @param filepath Es el directori incloent el nom de l'arxiu que es vol llegir
     * @param titol Titol del document
     * @param autor Autor del document
     * @param contingut Contingut del document
     */
    public void guardaDocument(String filepath , String titol, String autor, String contingut){
        // Creating an instance of file
        Path path = Paths.get(filepath + "/" + titol + "_" + autor + ".kitty");

        // Custom string as an input
        String str = "#titol#\n" + titol + "\n" +
                    "#autor#\n" + autor + "\n" +
                    "#contingut#\n" + contingut;

        // Try block to check for exceptions
        try {
            Files.writeString(path, str, StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            System.out.print("Invalid Path");
        }
    }
}