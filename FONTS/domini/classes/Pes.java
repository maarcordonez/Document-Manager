package domini.classes;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

import domini.utils.Pair;

/**Classe Pes
 * Clase abstracta per definir el concepte de pes de la que heredar per definir diferents tipus de pesos
 * @author Pol Garrido
 *
 */
public abstract class Pes implements Serializable {
    /**Valor del Pes*/
    private Double valor;

    /**Creadora buida
     */
    public Pes(){}

    /**Creadora amb parametre per definir el valor del Pes
     * @param v Valor del Pes
     */
    public Pes(double v){
        valor = v;
    }

    /**Obte el valor del Pes
     * @return Valor del Pes
     */
    public Double getValor(){
        return valor;
    }

    /**Estableix el valor del Pes
     * @param v Valor del Pes
     */
    public void setValor(double v){
        valor = v;
    }

    /**Metode abstracte a reimplementar a les clases que hereden de Pes per definir com es calcula
     * @param Weights Pesos de l'espai vectorial
     * @param word La paraula de la que volem calcular el pes
     * @param tf La frequencia local de la paraula
     * @param numobres El nombre d'obres registrades al sistema
     * @return El pes de la paraula
     */
    public abstract Double calcPes(List<LinkedHashMap<String,Pair<Double,List<Pes>>>> Weights, String word, Double tf, int numobres);
}
