package domini.classes;

import java.util.LinkedHashMap;
import java.util.List;

import domini.utils.Pair;

/**Classe PesBool
 * Clase que hereda de Pes, per definir el pes "bool" que indica si una paraula apareix a un document
 * @author Pol Garrido
 *
 */
public class PesBool extends Pes{

    /**Creadora buida
     */
    public PesBool() {
        super();
    }

    /**Creadora amb parametre per definir el valor del Pes
     * @param v Valor del Pes
     */
    public PesBool(double v) {
        super(v);
    }
    
    /**Metode reimplementat de la clase pare Pes, en aquest cas no fa res ja que es molt simple
     * @param Weights Pesos de l'espai vectorial
     * @param word La paraula de la que volem calcular el pes
     * @param tf La frequencia local de la paraula
     * @param numobres El nombre d'obres registrades al sistema
     * @return El pes de la paraula
     */
    @Override
    public Double calcPes(List<LinkedHashMap<String, Pair<Double,List<Pes>>>> Weights, String word, Double tf, int numobres) {
        return 0.0;
    }
}
