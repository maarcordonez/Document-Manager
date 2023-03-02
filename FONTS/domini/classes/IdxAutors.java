package domini.classes;

import domini.excepcions.ExceptionAutorNoExisteix;
import domini.utils.TrieNode;

import java.util.ArrayList;
import java.util.SortedMap;

/**
 * Estructua de dades que emmagatzema els noms dels autors en un Trie per facilitar el llistat d'autors donat un prefix.
 * @author Marc Ordonez
 */
public class IdxAutors {
    private TrieNode root;

    /**
     * Constructora de IdxAutors.
     */
    public IdxAutors() {
        root = new TrieNode();
    }

    /**
     * Afegeix l'autor a l'estructura del Trie.
     * @param autor Autor que volem afegir.
     */
    public void afegir(String autor) {
        TrieNode current = root;

        for (char l : autor.toCharArray()) {
            current = current.getChildren().computeIfAbsent(l, c -> new TrieNode());
        }
        if (current.isEndOfWord());
        current.setEndOfWord(true);
    }

    /**
     * Elimina a l'autor en cas de que existeixi.
     * @param autor Autor que volem eliminar.
     * @throws ExceptionAutorNoExisteix Saltara l'excecpcio si l'autor que volem eliminar no existeix.
     */
    public void eliminar(String autor) throws ExceptionAutorNoExisteix {
        eliminar(root, autor, 0);
    }

    /**
     * Comprova si l'autor es un dels elements del IdxAutors.
     * @param autor Nom de l'autor que volem veure si esta dins de l'estructura de IdxAutors.
     * @return Retorna cert si IdxAutors conte el nom de l'autor i fals en cas contrari.
     */
    public boolean conteNom(String autor) {
        TrieNode current = root;

        for (int i = 0; i < autor.length(); i++) {
            char ch = autor.charAt(i);
            TrieNode node = current.getChildren().get(ch);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.isEndOfWord();
    }

    /**
     * Indica si no hi ha cap autor guardat.
     * @return Retorna true si l'index és buit i fals altrament.
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Funcio recursiva per eliminar una paraula donat un node, una paraula i un index per indicar la lletra de la
     * paraula que volem eliminar. Si el node actual conte la lletra de la paraula word que esta en la posicio index,
     * es mira si conte un fill amb el seguent caràcter de la paraula i fa una crida recursiva a aquest node fins que
     * arriba a l'ultima lletra. Si pel cami arriba a un node que cap dels seus fills no es el seguent caracter de la
     * paraula vol dir que la paraula no existeix i llençara l'excepcio. Si a l'eliminar la paraula ja no penja cap paraula
     * d'aquell node, s'elimina el node sencer.
     * @param current Node actual a partir del qual es busca la paraula.
     * @param word Paraula que volem buscar per eliminar.
     * @param index Posicio del caràcter de la paraula que volem tractar.
     * @return Retorna cert si un node no té cap fill i tampoc es final de cap paraula.
     * @throws ExceptionAutorNoExisteix Llença l'excepcio si no s'ha pogut eliminar la paraula perque no formava part del
     * IdxAutors.
     */
    private boolean eliminar(TrieNode current, String word, int index) throws ExceptionAutorNoExisteix {
        if (index == word.length()) {
            if (!current.isEndOfWord()) {
                throw new ExceptionAutorNoExisteix(word);
            }
            current.setEndOfWord(false);
            return current.getChildren().isEmpty();
        }
        char ch = word.charAt(index);
        TrieNode node = current.getChildren().get(ch);
        if (node == null) {
            throw new ExceptionAutorNoExisteix(word);
        }
        boolean shouldDeleteCurrentNode = eliminar(node, word, index + 1) && !node.isEndOfWord();

        if (shouldDeleteCurrentNode) {
            current.getChildren().remove(ch);
            return current.getChildren().isEmpty();
        }
        return false;
    }

    /**
     * Llista tots els autors en ordre alfabetic.
     * @return Retorna un ArrayList de Strings amb tots els autors ordenats alfabeticament.
     */
    public ArrayList llistar() {
        ArrayList<String> output = new ArrayList<String>();
        if (root.isEndOfWord()) return output;
        else return llistar(root, "");
    }

    /**
     * Llistar tots els autors que es troben a partir d'un node.
     * @param current Node actual a partir del qual es llista.
     * @param prefix String de caracters que van des del node arrel fins al node actual (prefix de totes les paraules
     *               que es generin a partir d'aquell node).
     * @return Retorna un ArrayList de Strings amb tots els autors ordenats alfabeticament que es troben a partir del current node.
     */
    private ArrayList llistar(TrieNode current, String prefix) {
        ArrayList<String> output = new ArrayList<String>();
        if (current.isEndOfWord()) {
            output.add(prefix);
        }

        for (SortedMap.Entry<Character, TrieNode> entry : current.getChildren().entrySet()) {
            ArrayList<String> sufix = llistar(entry.getValue(), prefix + entry.getKey());
            output.addAll(sufix);
        }

        /*
        char ch = 'a';
        for (int i = 0; i < 26; ++i) {
            int nextValue = (int)ch + i; // find the int value plus 1
            char c = (char)nextValue;
            TrieNode child = current.getChildren().get(c);
            if (child != null) {
                ArrayList<String> sufix = llistar(child, prefix + c);
                output.addAll(sufix);
            }
        }

         */

        return output;
    }

    /**
     * Llista tots els autors que comencen pel prefix ordenats alfabeticament.
     * @param prefix Prefix que han de tenir els autors perquè apareguin llistats.
     * @return Retorna un ArrayList de Strings amb tots els autors ordenats alfabeticament que comencen pel prefix.
     */
    public ArrayList llistarPrefix(String prefix) {
        ArrayList<String> output = new ArrayList<String>();
        if (root.isEndOfWord()) return output;
        else return llistarPrefix(prefix, 0, root);
    }

    /**
     * Funcio per arribar fins al node que conte tot el prefix per llistar a partir d'aquell punt.
     * @param prefix Prefix que han de tenir els autors per aparèixer al resultat.
     * @param index Posicio del prefix en la que ens trobem en la crida actual.
     * @param current Node actual a partir del qual anirem baixant fins tenir tot el prefix.
     * @return Retorna un ArrayList de Strings amb tots els autors ordenats alfabeticament que comencen pel prefix.
     */
    private ArrayList llistarPrefix(String prefix, int index, TrieNode current) {
        if (index == prefix.length()) return llistar(current, prefix);
        char ch = prefix.charAt(index);
        TrieNode node = current.getChildren().get(ch);
        if (node == null) {
            ArrayList<String> output = new ArrayList<String>();
            return output;
        }
        return llistarPrefix(prefix, index + 1, node);
    }

    /**
     * Funcio per arribar inicialitzar l'index d'autors
     * @param idxA Llista d'autors
     */
    public void inicialitzarIdxAutors(ArrayList idxA) {
        root = new TrieNode();
        for (int i = 0; i < idxA.size(); ++i) {
            afegir((String) idxA.get(i));
        }
    }
}
