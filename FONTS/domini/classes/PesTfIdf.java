package domini.classes;

import java.util.LinkedHashMap;
import java.util.List;

import domini.utils.Pair;


/**Classe PesTfIdf
 * Clase que hereda de Pes, per definir el pes "tfidf" que te en compte la frequencia local d'una paraula
 * i la seva frequancia entre els documents registrats
 * @author Pol Garrido
 *
 */
public class PesTfIdf extends Pes{

    /**Creadora buida
     */
    public PesTfIdf() {
        super();
    }

    /**Creadora amb parametre per definir el valor del Pes
     * @param v Valor del Pes
     */
    public PesTfIdf(double v) {
        super(v);
    }

    /**Metode reimplementat de la clase pare Pes. Es defineix el calcul del tf-idf
     * @param Weights Pesos de l'espai vectorial
     * @param word La paraula de la que volem calcular el pes
     * @param tf La frequencia local de la paraula
     * @param numobres El nombre d'obres registrades al sistema
     * @return El pes de la paraula
     */
    @Override
    public Double calcPes(List<LinkedHashMap<String,Pair<Double,List<Pes>>>> Weights, String word, Double tf, int numobres) {
        double n = 0.0;
        for (LinkedHashMap<String, Pair<Double,List<Pes>>> paraules_del_doc : Weights) {
            if (paraules_del_doc.containsKey(word)) ++n;
        }
        return tf*(numobres/(n+1));
    }
}
