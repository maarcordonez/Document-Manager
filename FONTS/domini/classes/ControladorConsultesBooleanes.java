package domini.classes;

import domini.excepcions.*;
import domini.utils.Pair;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.Thread.sleep;

/**Aquesta classe s'encarrega de controlar tot el que te a veure amb consultes booleanes i crear el thread principal.
 * Es singleton.
 * @author pere.carrillo
 */
public class ControladorConsultesBooleanes {
    /**Instancia de Nou Conjunt Expressions Booleanes. */
    private NouConjuntExpressionsBooleanes conjuntConsultesBooleanes;
    /**Instancia del singleton. */
    private static ControladorConsultesBooleanes singleton = null;
    /**Thread principal. S'utilitzara per actualitzar les consultes en segon pla. */
    private Thread t;

    /**Creadora: Crea el Nou Conjunt Consultes Booleanes.
     *
     */
    public ControladorConsultesBooleanes() {
        //System.out.println("new ControladorConsultesBooleanes");
        conjuntConsultesBooleanes = new NouConjuntExpressionsBooleanes();
        t = null;
    }

    /**Funcio que retorna la instancia de Controlador Consultes Booleanes. Si no existia la crea.
     *
     * @return Retorna la instancia de Controlador Consultes Booleanes.
     */
    public static ControladorConsultesBooleanes getInstance() {
        if (singleton == null) singleton = new ControladorConsultesBooleanes();
        return singleton;
    }

    public void interrompThread() {
        //Aquestes linies no haurien d'existir, l'unic que ha de fer aixo es finalitzar tots els threads que estaven corrent. Pero java no permet fer-ho
        //aixi que es fara aixi. Realment ControladorConsultesBooleanes no coneix Node excepte per aquestes dues crides.
        Node.run.set(false);
        esperaThread();
        Node.run.set(true);
    }
    /**Funcio que es crida cada cop que s'afegeix / elimina / modifica un document.
     * Crea un thread que actualitza en segon pla totes les consultes booleanes que teniem guardades i l'historial.
     * Primer posa tots els resultats a {@code null} i despres els recalcula.
     */
    public void actualitzacioDocuments() {
        //System.out.println("actualitzacioDocuments");
        //t.interrupt();
        //System.out.println("Esperant thread");
        interrompThread();
        conjuntConsultesBooleanes.actualitzacioDocuments();
        t = new Thread(conjuntConsultesBooleanes);
        t.setName("ActualitzadorCjtExpressionsBooleanes");
        t.start();
        //System.out.println("Ha acabat d'actualitzar");
    }

    /**Funcio que criden totes les altres funcions que necessiten accedir a Nou Conjunt Expressions Booleanes.
     * Espera que el thread {@code T} hagi acabat i retorna.
     */
    public void esperaThread() {
        try {
            //System.out.println("Intentem ajuntar threads");
            if (t != null) {
                t.join();
                //System.out.println("RIP thread actualitzador");
            }
            //else System.out.println("No existia cap thread");
        } catch (InterruptedException ignored) {
        }
    }

    /**Fa una consulta booleana
     *
     * @param nom El nom amb el que es vol guardar la consulta.
     * @param exp L'expressio de la consulta.
     * @return Retorna un ArrayList(Titol, Autor) amb tots els documents que satisfan l'expressio.
     * @throws ExceptionConsultaIncorrecta Si la consulta booleana introduida no es correcta.
     */
    public ArrayList<Pair<String, String> > ferConsulta(String nom, String exp) throws ExceptionConsultaIncorrecta {
        //System.out.println("ferConsulta");
        esperaThread();
        return conjuntConsultesBooleanes.ferConsulta(nom, exp);
    }

    /**Resol la consulta booleana ja guardada amb un nom.
     *
     * @param nom El nom de la consulta a resoldre.
     * @return Retorna un ArrayList(Titol, Autor) amb tots els documents que satisfan l'expressio guardada amb nom {@code nom}.
     * @throws ExceptionConsultaNoExisteix Si no existeix cap consulta amb nom {@code nom}.
     */
    public ArrayList<Pair<String, String>> ferConsultaPerNom(String nom) throws ExceptionConsultaNoExisteix {
        //System.out.println("ferConsultaPerNom");
        esperaThread();
        return conjuntConsultesBooleanes.ferConsultaPerNom(nom);
    }

    /**Resol una consulta booleana.
     *
     * @param exp L'expressio booleana.
     * @return Retorna la llista de (Titol, Autor) de tots els documents que satisfan l'expressio {@code exp}.
     * @throws ExceptionConsultaIncorrecta Si l'expressio no es correcta
     */
    public ArrayList<Pair<String, String>> ferConsultaPerExp(String exp) throws ExceptionConsultaIncorrecta {
        //System.out.println("ferConsultaPerExp");
        esperaThread();
        return conjuntConsultesBooleanes.ferConsultaPerConsulta(exp);
    }

    /**Guarda una consulta booleana amb nom {@code nom} i expressio {@code exp}.
     *
     * @param nom El nom de l'expressio a guardar.
     * @param exp L'expressio a guardar.
     * @throws ExceptionConsultaJaExisteix Si ja existia una consulta amb aquest nom.
     * @throws ExceptionConsultaIncorrecta Si la nova consulta es incorrecte.
     */
    public void guardarConsultaBooleana(String nom, String exp) throws ExceptionConsultaJaExisteix, ExceptionConsultaIncorrecta {
        //System.out.println("guardarConsulta");
        esperaThread();
        conjuntConsultesBooleanes.guardarConsultaBooleana(nom, exp);
    }

    /**Elimina una consulta booleana guardada.
     *
     * @param nom El nom de la consulta a eliminar.
     * @throws ExceptionConsultaNoExisteix Si la consulta amb nom {@code nom} no existeix.
     */
    public void eliminarConsultaBooleana(String nom) throws ExceptionConsultaNoExisteix {
        //System.out.println("eliminarConsulta");
        esperaThread();
        conjuntConsultesBooleanes.eliminarConsultaBooleana(nom);
    }

    /**Modifica una consulta booleana.
     *
     * @param nom El nom de la consulta.
     * @param newExp La nova expressio que es guardara a la consulta.
     * @throws ExceptionConsultaIncorrecta Si la nova expressio no es correcta.
     * @throws ExceptionConsultaNoExisteix Si no existeix la consulta a modificar.
     */
    public void modificarConsultaBooleana(String nom, String newExp) throws ExceptionConsultaIncorrecta, ExceptionConsultaNoExisteix {
        //System.out.println("modificarConsulta");
        esperaThread();
        conjuntConsultesBooleanes.modificarConsultaBooleana(nom, newExp);
    }

    /**Canvia el nom a una consulta booleana.
     *
     * @param nomAntic El nom antic de la consulta a reanomenar.
     * @param nomNou El nou nom.
     * @throws ExceptionConsultaNoExisteix Si no existeix la consulta a reanomenar.
     * @throws ExceptionConsultaJaExisteix Si ja existeix una consulta amb nom {@code nomNou}.
     */
    public void reanomenarConsultaBooleana(String nomAntic, String nomNou) throws ExceptionConsultaNoExisteix, ExceptionConsultaJaExisteix {
        //System.out.println("reanomenarConsulta");
        esperaThread();
        conjuntConsultesBooleanes.canviarNomConsultaBooleana(nomAntic, nomNou);
    }

    /**Getter Consultes guardades.
     *
     * @return Les consultes guardades en una llista de (nom, expressio).
     */
    public ArrayList<Pair<String, String>> getConsultesGuardades() {
        //System.out.println("getConsultesGuardades");
        esperaThread();
        return conjuntConsultesBooleanes.getConsultesGuardades();
    }

    /**Getter Historial.
     *
     * @return L'historial de consultes booleanes en una llista de (nom, expressio).
     */
    public ArrayList<Pair<String, String>> getHistorial() {
        //System.out.println("getHistorial");
        esperaThread();
        return conjuntConsultesBooleanes.getHistorial();
    }

    /**Fa el mateix que getHistorial pero s'utilitza al tancar l'aplicacio juntament amb {@code saveGuardades()}.
     * Els hem posat dos noms diferents per entendre-les com dues funcions diferents, pero realment son la mateixa.
     *
     * @return L'historial de consultes booleanes en una llista de (nom, expressio).
     */
    public ArrayList<Pair<String, String>> saveHist() {
        //System.out.println("save");
        esperaThread();
        return conjuntConsultesBooleanes.saveHist();
    }

    /**Fa el mateix que getConsultesGuardades pero s'utilitza al tancar l'aplicacio juntament amb {@code saveHist()}.
     * Els hem posat dos noms diferents per entendre-les com dues funcions diferents, pero realment son la mateixa.
     *
     * @return Les consultes booleanes guardades al sistema.
     */
    public ArrayList<Pair<String, String>> saveGuardades() {
        //System.out.println("save");
        esperaThread();
        return conjuntConsultesBooleanes.saveGuardades();
    }

    /**Carrega l'historial del sistema amb la llista de consultes que se li passen com a parametre. S'utilitza a l'iniciar l'aplicacio juntament amb {@code loadGuardades()}.
     *
     * @param l La llista de consultes a carregar a l'historial.
     */
    public void loadHist(ArrayList<Pair<String, String>> l) {
        //System.out.println("load");
        //esperaThread();
        conjuntConsultesBooleanes.loadHist(l);
        //actualitzacioDocuments();
    }

    /**Carrega les consultes guardades del sistema amb la llista de consultes que se li passen com a parametre. S'utilitza a l'iniciar l'aplicacio juntament amb {@code loadHist()}.
     *
     * @param l La llista de consultes a carregar a consultes guardades.
     */
    public void loadGuardades(ArrayList<Pair<String, String>> l) {
        //System.out.println("load");
        //esperaThread();
        conjuntConsultesBooleanes.loadGuardades(l);
        //actualitzacioDocuments();
    }
}
