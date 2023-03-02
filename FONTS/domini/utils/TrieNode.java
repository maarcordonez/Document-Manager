package domini.utils;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;

/**Classe TrieNode. Definicio de la clase per poder representar els indexs en forma d'arbre
 *
 * @author Marc Ordonez
 */
public class TrieNode implements Serializable {
    /**TreeMap de l'index*/
    private final SortedMap<Character, TrieNode> children = new TreeMap<>();
    /**Boolea per indicar endOfWord*/
    private boolean endOfWord;

    /**
     * @return Retorna el filld'un node
     */
    public SortedMap<Character, TrieNode> getChildren() {
        return children;
    }

    /**
     * @return Cert si endOfWord i fals altrament
     */
    public boolean isEndOfWord() {
        return endOfWord;
    }

    /** Asigna valor al atribut endOfWord
     * @param endOfWord Valor a assignar
     */
    public void setEndOfWord(boolean endOfWord) {
        this.endOfWord = endOfWord;
    }
}
