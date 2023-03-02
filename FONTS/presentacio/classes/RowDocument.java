package presentacio.classes;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**Classe RowDocument. Classe per poder carregar els documents a la taula interactiva 
 * @author laura.hui.perez
 */
public class RowDocument {
    /** Titol del document */
    private String titol = null;
    /** Autor del document */
    private String autor = null;
    /** Data del document */
    private String data = null;

    /** Creadora de la classe 
     * @param strings Cadena d'strings amb els parametres
    */
    public RowDocument(ArrayList<String> strings) {
    }

    /** Creadora de la classe amb els parametres de titol,autor i data
     * @param titol Titol del document
     * @param autor Autor del document
     * @param data Data del document
    */
    public RowDocument(String titol, String autor, String data) {
        this.titol = titol;
        this.autor = autor;
        this.data = data;
    }

    /** 
     * @return Retorna el titol del document
    */
    public String getTitol() {
        return titol;
    }

    /** Assigna el titol passat com a parametre al element de la clase
     * @param titol Titol del document
    */
    public void setTitol(String titol) {
        this.titol = titol;
    }

    /** 
     * @return Retorna l'autor del document
    */
    public String getAutor() {
        return autor;
    }

    /** Assigna l'autor passat com a parametre al element de la clase
     * @param autor Autor del document
    */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /** 
     * @return Retorna la data del document
    */
    public String getData() {
        return data;
    }

    /** Assigna la data passada com a parametre al element de la clase
     * @param data Data del document
    */
    public void setData(String data) {
        this.data = data;
    }

    /** Pasa a string els atributs de la clase titol,autor i data
     * @return Els atributs passats a string
    */
    @Override
    public String toString(){
        return autor + " " + titol + " " + data;
    }
}
