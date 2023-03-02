package domini.classes;

import domini.excepcions.ExceptionAutorNoExisteix;
import domini.excepcions.ExceptionObraJaExisteix;
import domini.excepcions.ExceptionObraNoExisteix;
import domini.utils.Pair;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.TreeMap;


/**Classe Etiqueta. S'utilitza per poder identificar diferents categories de documents
 * que l'usuari pot definir i eliminar
 *
 * @author marc.ordonez
 */
public class Etiqueta {
    /**Nom de l'etiqueta*/
    private String nomEtiq;
    /**Index d'obres per poder associar les etiquetes*/
    private IdxObres obres;

    /**Creadora d'etiqueta, es definex el seu nom pel parametre
     * @param nom Nom de l'etiqueta
     */
    public Etiqueta (String nom) {
        nomEtiq = nom;
        obres = new IdxObres();
    }

    /**Afegeix una obra a l'index, identificada per titol autor
     * @param titol Titol de l'obra
     * @param autor Autor de l'obra
     */
    public void afegirObra(String titol, String autor) {
        try {
            obres.afegirObra(titol, autor);
        } catch (ExceptionObraJaExisteix e) {
            //no fem res i ja està
        }
    }

    /**Elimina una obra de l'index, identificada per titol autor
     * @param titol Titol de l'obra
     * @param autor Autor de l'obra
     */
    public void eliminarObra(String titol, String autor) {
        try {
            obres.eliminarObraAutor(titol, autor);
        } catch (ExceptionAutorNoExisteix | ExceptionObraNoExisteix e) {
            //no fem res i ja està
        }
    }

    /**Llista les obres registrades al index
     * @return Llista amb els titols i autors de les obres
     */
    public ArrayList<Pair<String, String>> llistarObres() {
        return obres.getTitolsAutors();
    }

    /**
     * @return Nom de l'etiqueta
     */
    public String getNom() {
        return nomEtiq;
    }

    /**Inicialitza l'index d'obres
     * @param nomObres Dades de l'index amb les que s'inicialitzara
     */
    public void inicialitzarObres(TreeMap<String, TreeMap<String, LocalDateTime>> nomObres) {
        obres.inicialitzarIdxObres(nomObres);
    }

    /**Obte les obres registrades mantenint l'estructura de dades de l'index
     * @return Estructura amb les obres registrades
     */
    public TreeMap<String, TreeMap<String, LocalDateTime>> getObres() {
        return obres.getIdxObres();
    }

    /**Comprova si l'obra esta registarada a l'index
     * @param titol Titol de l'obra
     * @param autor Autor de l'obra
     * @return Cert si existeix o fals si no
     */
    public boolean conteObra (String titol, String autor) {
        return obres.existeixDocument(titol, autor);
    }
}
