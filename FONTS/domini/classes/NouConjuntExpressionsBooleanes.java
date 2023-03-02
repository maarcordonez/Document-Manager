package domini.classes;

import domini.excepcions.ExceptionConsultaIncorrecta;
import domini.excepcions.ExceptionConsultaJaExisteix;
import domini.excepcions.ExceptionConsultaNoExisteix;
import domini.utils.Pair;

import java.util.*;

/**Classe que guarda el conjunt d'expressions booleanes.
 * Implementa la classe Runnable per poder executar-la des d'un thread diferent.
 * @author pere.carrillo
 */
public class NouConjuntExpressionsBooleanes implements Runnable{

    //Historial de consultes fetes, mida maxima = MIDA_HISTORIAL
    /**La mida maxima de l'historial de consultes. */
    private static final int MIDA_HISTORIAL = 10;
    /**L'historial de consultes booleanes. Es un LinkedHashMap per guardar els elements en ordre d'entrada i permetre insercions, cerques i eliminacions rapides O(1).
     * La clau es el nom de la consulta i el valor la instancia de NouExpressioBooleana.
     */
    private LinkedHashMap<String, NouExpressioBooleana> historial;      //Key: nom consulta. Value: exp bool
    /**Les consultes guardades per l'usuari. Es un HashMap per permetre insercions, cerques i eliminacions rapides.
     * La clau es el nom de la consulta i el valor la instancia de NouExpressioBooleana.
     */
    private HashMap<String, NouExpressioBooleana> consultesGuardades;   //Key: nom consulta. Value: exp bool

    /**Creadora: inicialitza l'historial i les consultes booleanes i a l'historial sobrecarreca la funcio removeEldestEntry(), que fa que quan es supera {@code MIDA_HISTORIAL} i s'afegeix un nou element s'elimina el que s'havia afegit primer.
     * Funciona com si fos una cache de consultes.
     *
     */
    public NouConjuntExpressionsBooleanes() {
        //System.out.println("Creat conjunt consultes bool");
        historial = new LinkedHashMap<>() {
            protected boolean removeEldestEntry(Map.Entry<String, NouExpressioBooleana> eldest) {
                return size() > MIDA_HISTORIAL;
            }
        };
        consultesGuardades = new HashMap<>();
    }

    /**Es la funcio que es cridara desde el Thread. El que volem que faci es que calculi els resultats de totes les consultes guardades tant a l'historial com a les guardades per l'usuari.
     * Crida a {@code getResultat()} per cada expressio pero no se'l guarda enlloc. Es nomes perque es quedi calculat a NouExpressioBooleana.
     */
    public void run() {
        //System.out.println("Soc un thread. Em dic " + Thread.currentThread().getName());
        for (Map.Entry<String,NouExpressioBooleana> entry : consultesGuardades.entrySet()) {
            //if (Thread.currentThread().isInterrupted()) {System.out.println("Interrupted"); return;}
            entry.getValue().getResultat();  //Unica linia important
        }

        for (Map.Entry<String,NouExpressioBooleana> entry : historial.entrySet()) {
            //if (Thread.currentThread().isInterrupted()) {System.out.println("Interrupted"); return;}
            entry.getValue().getResultat();  //Unica linia important
        }
        //System.out.println("He acabat");
    }

    /**Busca la consulta a consultes guardades i si la troba crida a {@code ferConsulta(nom, expressioGuardada)}.
     *
     * @param nom El nom de la consulta a buscar i fer.
     * @return Retorna una llista (titol, autor) dels documents que compleixen l'expressio guardada amb nom {@code nom}.
     * @throws ExceptionConsultaNoExisteix Si no existia cap expressio amb nom {@code nom}
     */
    public ArrayList<Pair<String, String>> ferConsultaPerNom(String nom) throws ExceptionConsultaNoExisteix {
        NouExpressioBooleana n = consultesGuardades.getOrDefault(nom, null);
        if (n == null) throw new ExceptionConsultaNoExisteix(nom);
        ArrayList<Pair<String, String>> result = null;
        try {
            result = ferConsulta(nom, n.getConsulta());
        } catch (ExceptionConsultaIncorrecta ignored) { //mai sera incorrecta la consulta
        }
        return result;
    }

    /**Crea una consulta amb nom {@code consulta} i crida a {@code ferConsulta(nom, consulta)}.
     *
     * @param consulta L'expressio a resoldre.
     * @return Retorna una llista (titol, autor) dels documents que compleixen l'expressio {@code consulta}.
     * @throws ExceptionConsultaIncorrecta Si l'expressio {@code consulta} es incorrecte.
     */
    public ArrayList<Pair<String, String>> ferConsultaPerConsulta(String consulta) throws ExceptionConsultaIncorrecta {
        //NouExpressioBooleana n = consultesGuardades.getOrDefault(consulta, null);
        //if (n == null) throw new ExceptionConsultaNoExisteix(consulta);
        return ferConsulta(consulta, consulta);
    }

    /**Crea una consulta amb nom {@code nom} i expressio {@code consultaString}.
     *
     * @param nom El nom de la consulta.
     * @param consultaString L'expressio a resoldre.
     * @return Retorna una llista (titol, autor) dels documents que compleixen l'expressio {@code consultaString}.
     * @throws ExceptionConsultaIncorrecta Si l'expressio {@code consultaString} es incorrecte.
     */
    public ArrayList<Pair<String, String>> ferConsulta(String nom, String consultaString) throws ExceptionConsultaIncorrecta {
        NouExpressioBooleana consultaExp = new NouExpressioBooleana(nom, consultaString);
        //if (!consultaExp.esCorrecta()) throw new ExceptionConsultaIncorrecta(consultaExp.toString());

        //Mirar si esta a l'historial
        NouExpressioBooleana resHist = historial.remove(nom);
        if (resHist != null) consultaExp = resHist;
        //Al haver reimplementat la funcio removeoldestentry de linkedhashmap, si la mida supera MIDA_HISTORIAL eliminara l'entrada mes antiga
        historial.put(nom, consultaExp);

        //Mirar si la teniem guardada
        consultaExp = consultesGuardades.getOrDefault(nom, consultaExp);

        HashSet<Pair<String, String>> documentsSet = new HashSet<>();
        for (Pair<Pair<String, String>, String> p : consultaExp.getResultat()) {
            documentsSet.add(new Pair<>(p.left().left(), p.left().right()));
        }
        return new ArrayList<>(documentsSet);
    }

    /**Funcio que invalida els resultats de totes les consultes.
     * Es crida cada cop que s'afegeix / modifica / elimina un document.
     *
     */
    public void actualitzacioDocuments(){
        //System.out.println("Actualitzacio documents");
        ControladorDomini controladorDomini = null;
        controladorDomini = ControladorDomini.getInstance();
        NouExpressioBooleana.setDocuments(controladorDomini.getAllDocuments());
        //Run thread
        for (Map.Entry<String, NouExpressioBooleana> entry : historial.entrySet()) {
            entry.getValue().setResultat(null);                                            //Unica linia important
        }
        for (Map.Entry<String, NouExpressioBooleana> entry : consultesGuardades.entrySet()) {
            entry.getValue().setResultat(null);                                            //Unica linia important
        }
    }

    /**Guarda una consulta booleana.
     *
     * @param nom El nom de la consulta a guardar.
     * @param consulta L'expressio de la consulta a guardar.
     * @throws ExceptionConsultaJaExisteix Si ja existia una consulta amb nom {@code nom}.
     * @throws ExceptionConsultaIncorrecta Si l'expressio {@code consulta} es incorrecta.
     */
    public void guardarConsultaBooleana(String nom, String consulta) throws ExceptionConsultaJaExisteix, ExceptionConsultaIncorrecta {
        if (consultesGuardades.containsKey(nom)) throw new ExceptionConsultaJaExisteix(nom);
        NouExpressioBooleana exp = new NouExpressioBooleana(nom, consulta);
        consultesGuardades.put(nom, exp);
    }

    /**Elimina una consulta booleana guardada al sistema.
     *
     * @param nom El nom de la consulta a eliminar.
     * @throws ExceptionConsultaNoExisteix Si la consulta amb nom {@code nom} no existia.
     */
    public void eliminarConsultaBooleana(String nom) throws ExceptionConsultaNoExisteix {
        if (!consultesGuardades.containsKey(nom)) throw new ExceptionConsultaNoExisteix(nom);
        consultesGuardades.remove(nom);
    }

    /**Modifica l'expressio d'una consulta booleana guardada.
     *
     * @param nom El nom de l'expressio a modificar.
     * @param consultaNova La nova expressio.
     * @throws ExceptionConsultaIncorrecta Si l'expressio {@code consultaNova} es incorrecta.
     * @throws ExceptionConsultaNoExisteix Si la consulta amb nom {@code nom} no existeix.
     */
    public void modificarConsultaBooleana(String nom, String consultaNova) throws ExceptionConsultaIncorrecta, ExceptionConsultaNoExisteix {
        eliminarConsultaBooleana(nom);
        try {
            guardarConsultaBooleana(nom, consultaNova);
        } catch (ExceptionConsultaJaExisteix ignored) { //No passara mai ja que l'acabem d'esborrar
        }
    }

    /**Canvia el nom a una consulta booleana.
     *
     * @param nomAntic El nom de la consulta a reanomenar.
     * @param nomNou El nou nom per la consulta.
     * @throws ExceptionConsultaNoExisteix Si la consulta amb nom {@code nomAntic} no existeix.
     * @throws ExceptionConsultaJaExisteix Si ja existeix la consulta amb nom {@code nomNou}.
     */
    public void canviarNomConsultaBooleana(String nomAntic, String nomNou) throws ExceptionConsultaNoExisteix, ExceptionConsultaJaExisteix {
        if (!consultesGuardades.containsKey(nomAntic)) throw new ExceptionConsultaNoExisteix(nomAntic);
        if (consultesGuardades.containsKey(nomNou)) throw new ExceptionConsultaJaExisteix(nomNou);
        NouExpressioBooleana n = consultesGuardades.remove(nomAntic);
        n.setNom(nomNou);
        consultesGuardades.put(nomNou, n);
    }

    /**Getter historial.
     *
     * @return L'historial de consultes booleanes en una llista de (nom, expressio).
     */
    public ArrayList<Pair<String, String>> getHistorial() {
        ArrayList<Pair<String, String>> resultat = new ArrayList<>();
        for (Map.Entry<String, NouExpressioBooleana> entry : historial.entrySet()) {
            resultat.add(new Pair<> (entry.getValue().getNom(), entry.getValue().getConsulta()));
        }
        return resultat;
    }

    /**Getter Consultes guardades.
     *
     * @return Les consultes guardades en una llista de (nom, expressio).
     */
    public ArrayList<Pair<String, String>> getConsultesGuardades() {
        ArrayList<Pair<String, String>> resultat = new ArrayList<>();
        for (Map.Entry<String, NouExpressioBooleana> entry : consultesGuardades.entrySet()) {
            resultat.add(new Pair<> (entry.getValue().getNom(), entry.getValue().getConsulta()));
        }
        return resultat;
    }

    /**Fa el mateix que getHistorial pero s'utilitza al tancar l'aplicacio juntament amb {@code saveGuardades()}.
     * Els hem posat dos noms diferents per entendre-les com dues funcions diferents, pero realment son la mateixa.
     *
     * @return L'historial de consultes booleanes en una llista de (nom, expressio).
     */
    public ArrayList<Pair<String, String>> saveHist() {
        return getHistorial();
    }

    /**Fa el mateix que getConsultesGuardades pero s'utilitza al tancar l'aplicacio juntament amb {@code saveHist()}.
     * Els hem posat dos noms diferents per entendre-les com dues funcions diferents, pero realment son la mateixa.
     *
     * @return Les consultes booleanes guardades al sistema.
     */
    public ArrayList<Pair<String, String>> saveGuardades() {
        return getConsultesGuardades();
    }

    /**Carrega l'historial del sistema amb la llista de consultes que se li passen com a parametre. S'utilitza a l'iniciar l'aplicacio juntament amb {@code loadGuardades()}.
     *
     * @param l La llista de consultes a carregar a l'historial.
     */
    public void loadHist(ArrayList<Pair<String, String>> l)  {
        historial.clear();
        for (Pair<String, String> p : l) {
            NouExpressioBooleana n = null;
            try {
                n = new NouExpressioBooleana(p.left(), p.right());
                historial.put(p.left(), n);
            } catch (ExceptionConsultaIncorrecta e) {
                System.out.println("Si passa aixo no afegim el document i ja");
            }
        }
    }

    /**Carrega les consultes guardades del sistema amb la llista de consultes que se li passen com a parametre. S'utilitza a l'iniciar l'aplicacio juntament amb {@code loadHist()}.
     *
     * @param l La llista de consultes a carregar a consultes guardades.
     */
    public void loadGuardades(ArrayList<Pair<String, String>> l)  {
        consultesGuardades.clear();
        for (Pair<String, String> p : l) {
            NouExpressioBooleana n = null;
            try {
                n = new NouExpressioBooleana(p.left(), p.right());
                consultesGuardades.put(p.left(), n);
            } catch (ExceptionConsultaIncorrecta e) {
                System.out.println("Si passa aixo no afegim el document i ja");
            }
        }
    }
}
