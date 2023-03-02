package domini.classes;

import domini.excepcions.ExceptionConsultaIncorrecta;
import domini.utils.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**Aquesta classe s'encarrega de guardar una expressio booleana.
 * Tambe s'encarrega de gestionar la creacio de threads per resoldre les consultes booleanes.
 * @author pere.carrillo
 */
public class NouExpressioBooleana {

    //private Node expressio;
    /**El nom de l'expressio. */
    private String nom;
    /**L'expressio com a String. */
    private String consulta;

    /**El resultat de la consulta. Es null si no esta resolta encara. */
    private ArrayList<Pair<Pair<String, String>, String>> resultat;
    /**El numero maxim de threads que es crearan. */
    private static final int NUM_WORKERS_MAX = 10;

    //private static int numDocuments;

    /**Creadora.
     *
     * @param nom El nom de la consulta.
     * @param consulta L'expressio com a string.
     * @throws ExceptionConsultaIncorrecta Si l'expressio {@code consulta} no existeix.
     */
    public NouExpressioBooleana(String nom, String consulta) throws ExceptionConsultaIncorrecta {
        this.nom = nom;
        this.consulta = consulta;
        //No es fa servir per res, només perquè comprovi que l'expressió és correcta
        new Node(consulta, false);
    }

    /*public static void setNumDocuments(int n) {
        numDocuments = n;
    }*/

    /*public void escriuPreordre() {
        expressio.escriuPreordre();
    }

    public void escriuInordre() {
        expressio.escriuInordre();
    }*/

    /**Setter nom.
     *
     * @param nom El nou nom.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**Getter nom.
     *
     * @return Retorna el nom de la consulta.
     */
    public String getNom() {
        return nom;
    }

    /**Getter consulta.
     *
     * @return Retorna l'expressio com a string.
     */
    public String getConsulta() {
        return consulta;
    }

    /**Setter consulta.
     *
     * @param consulta La nova expressio.
     * @throws ExceptionConsultaIncorrecta Si la nova expressio es incorrecta.
     */
    public void setConsulta(String consulta) throws ExceptionConsultaIncorrecta{
        new Node(consulta, false);
        this.consulta = consulta;
    }

    /**Retorna el resultat d'una consulta.
     * Tambe se'l guarda per si el tornen a demanar mes endavant.
     * Crea 10 threads i assigna a cadascun una part dels documents guardats al sistema. D'aquesta manera aconseguim dividir la part mes lenta entre tots per fer-la 10 vegades mes rapida.
     *
     * @return Retorna el resultat de la consulta.
     */
    public ArrayList<Pair<Pair<String, String>, String>> getResultat() {
        if (resultat == null) {
            ControladorDomini controladorDomini = null;
            controladorDomini = ControladorDomini.getInstance();
            ArrayList<Pair<String, String>> allDocuments = controladorDomini.getAllDocuments();
            //System.out.println("Tots els documents: " + allDocuments);
            Node.setDocuments(allDocuments);

            //List<Node> nodes = new ArrayList<>();
            double midaUn = allDocuments.size()/(double)NUM_WORKERS_MAX;
            int NUM_WORKERS = NUM_WORKERS_MAX;
            /*if (allDocuments.size() < 10) {
                NUM_WORKERS = allDocuments.size();
                midaUn = 1;
            }*/

            ExecutorService executorService = Executors.newFixedThreadPool(NUM_WORKERS);
            List<Future<ArrayList<Pair<Pair<String, String>, String>>>> resultats = new ArrayList<>(NUM_WORKERS);
            for (int i = 0; i < NUM_WORKERS; ++i) {
                int start = (int) ((i) * midaUn);
                //if (i == 0) start = 0;
                int end = (int) ((i+1) * midaUn);
                if (i == NUM_WORKERS-1) end = allDocuments.size();
                Node node = null;
                try {
                    node = new Node(consulta, false, start, end);
                } catch (ExceptionConsultaIncorrecta ignored) {} //Mai passara pq ja hem creat la consulta abans.
                //System.out.println("Thread nº" + i + " inici: " + start + " final: " + end);
                resultats.add(i, executorService.submit(node));
                //nodes.add(node);
            }
            try {
                resultat = resultats.get(0).get();
                resultats.remove(0);
            } catch (InterruptedException | ExecutionException ignored) {
            }
            for (Future<ArrayList<Pair<Pair<String, String>, String>>> future : resultats) {
                try {
                    //System.out.println("Resultat parcial " + resultats.indexOf(future)+": " + future.get());
                    resultat = unio(resultat, future.get());
                } catch (InterruptedException | ExecutionException ignored) {
                }
            }
            executorService.shutdown();
        }
        return resultat;
    }

    /**Fa la unio de les dues llistes (Hauria de ser privat)
     * @param docs Llista 1 a unir
     * @param altres Llista 2 a unir
     * @return Retorna la unio de les dues llistes
     */
    private ArrayList<Pair<Pair<String, String>, String>> unio(ArrayList<Pair<Pair<String, String>, String>> docs, ArrayList<Pair<Pair<String, String>, String>> altres) {
        //System.out.println("Unio: " + "'" + docs + "'" + " amb " + "'" + altres + "'");
        ArrayList<Pair<Pair<String, String>, String>> result = (ArrayList<Pair<Pair<String, String>, String>>) docs.clone();
        for (Pair<Pair<String, String>, String> altre : altres) {
            if (!result.contains(altre)) result.add(altre);
        }
        return result;
    }

    /**Setter resultat.
     * Es fa servir per posar-lo a null quan canvien els documents.
     *
     * @param resultat El nou resultat.
     */
    public void setResultat(ArrayList<Pair<Pair<String, String>, String>> resultat) {
        this.resultat = resultat;
    }

    /**Actualitza la llista de tots els documents del sistema de Node.
     *
     * @param allDocuments Una llista de (titol, autor) amb tots els documents del sistema.
     */
    public static void setDocuments(ArrayList<Pair<String, String>> allDocuments) {
        Node.setDocuments(allDocuments);
        System.out.println(allDocuments);
    }
}