package domini.utils;

/** Clase Pair de C++, per simplicar algunes estructures de dades, segueix el mateix funcionament que a C++
 * @author Pol Garrido
 *
 */
import java.io.Serializable;
import java.util.Objects;

public class Pair<left,right> implements Serializable {

    /** Atribut esquerre */
    private left left;
    /** Atribut dret */
    private right right;

    /**Creadora per parametres
     * @param o1 Objecte per la posicio esquerra
     * @param o2 Objecte per la posicio drtea
     */
    public Pair(left o1, right o2){
        left = o1;
        right = o2;
    }

    /**
     * @return Retorna l'objecte de l'esquerra
     */
    public left left()  {return left;}

    /**
     * @return Retorna l'objecte de la dreta
     */
    public right right() {return right;}

    /**
     * @return Converteix a string els dos atributs de la clase
     */
    @Override
    public String toString() {
        return "Pair{" + left + ", " + right + '}';
    }

    /**Redefinicio de la funcio igual per Pairsç
     * @param o Pair a comparar amb el passat implicitament
     * @return Cert si son iguals i fals si no
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<left,right> pair = (Pair<left,right>) o;
        return Objects.equals(left, pair.left) && Objects.equals(right, pair.right);
    }

    /**Redefinicio de la funcio hashCode
     * @return El hash code de l'objecte
     */
    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
