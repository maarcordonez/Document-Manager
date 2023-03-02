package domini.classes;

import domini.excepcions.ExceptionConsultaIncorrecta;
import domini.utils.Pair;
import persistencia.excepcions.ExceptionAPersitencia;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

/**Aquesta Classe s'encarrega de resoldre una consulta booleana. Es guarda en forma d'arbre, per tant cada node te un punter als seus fills dret i esquerre.
 *
 * @author pere.carrillo
 */
public class Node implements Callable<ArrayList<Pair<Pair<String, String>, String>>> {
    /**Informacio del node. Sera o un operador o un literal. */
    String data;
    /**Fills dret i esquerre de l'expressio separada. */
    Node left, right;
    /**Boolea que indica si l'expressio esta negada o no. */
    boolean negat;
    /**Llista de tots els documents del sistema. */
    private static ArrayList<Pair<String, String>> allDocuments;
    /**Index del primer i ultim element sobre els que buscara aquesta instancia de node. */
    private int start, end;
    public static AtomicBoolean run = new AtomicBoolean(true);

    /**Funcio que es crida quan un thread executa aquesta classe.
     *
     * @return Retorna la llista de frases que compleixen l'expressio booleana.
     */
    public ArrayList<Pair<Pair<String, String>, String>> call() {
        //System.out.println("Em dic " + Thread.currentThread().getName() + " i vaig a fer coses");
        return getResultat();
    }

    /**Afegeix els indexs dels elements entre els que buscar.
     *
     * @param start Index del primer element.
     * @param end Index de l'ultim element.
     */
    public void setStartEnd(int start, int end) {
        this.start = start;
        this.end = end;
    }

    /**Setter de tots els documents del sistema.
     *
     * @param allDocuments Llista de (titol, autor) de tots els documents del sistema.
     */
    public static void setDocuments(ArrayList<Pair<String, String>> allDocuments) {
        Node.allDocuments = allDocuments;
    }

    /**Creadora: Crea un arbre d'expressions booleanes a partir d'una expressio donada.
     *
     * @param cons Expressio booleana com a string.
     * @param negat Si l'expressio booleana estava negada.
     * @throws ExceptionConsultaIncorrecta Si l'expressio {@code cons} no es correcta.
     */
    public Node(String cons, boolean negat) throws ExceptionConsultaIncorrecta {
        if (!esCorrecta(cons)) throw new ExceptionConsultaIncorrecta(cons);
        String data = simplifica(cons);
        this.negat = negat;
        if (esSimple(data)) {
            this.data = data;
            left = null;
            right = null;
        }
        else {
            String[] separada = separaEnDos(data);
            if (separada[1] == null) throw new ExceptionConsultaIncorrecta(data);
            this.data = String.valueOf(separada[0].charAt(separada[0].length() - 1));
            separada[0] = separada[0].substring(0, separada[0].length() - 1);
            left = new Node(separada[0], this.negat);
            right = new Node(separada[1], this.negat);
        }
    }

    /**Creadora: Crea un arbre d'expressions booleanes a partir d'una expressio donada.
     *
     * @param cons Expressio booleana com a string.
     * @param negat Si l'expressio booleana estava negada.
     * @param start Index del primer document desde el que mirar.
     * @param end Index de l'ultim element al que mirar.
     * @throws ExceptionConsultaIncorrecta Si l'expressio {@code cons} no es correcta.
     */
    public Node(String cons, boolean negat, int start, int end) throws ExceptionConsultaIncorrecta {
        if (!esCorrecta(cons)) throw new ExceptionConsultaIncorrecta(cons);
        /*try {
            if (controladorDomini == null) controladorDomini = ControladorDomini.getInstance();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }*/
        this.negat = negat;
        String data = simplifica(cons);
        //System.out.println("exp: " + this.data + " negat: " + this.negat);

        this.start = start;
        this.end = end;
        if (esSimple(data)) {
            this.data = data;
            left = null;
            right = null;
        }
        else {
            String[] separada = separaEnDos(data);
            if (separada[1] == null) throw new ExceptionConsultaIncorrecta(data);
            this.data = String.valueOf(separada[0].charAt(separada[0].length() - 1));
            separada[0] = separada[0].substring(0, separada[0].length() - 1);
            left = new Node(separada[0], this.negat, start, end);
            right = new Node(separada[1], this.negat, start, end);
        }
    }

    /**Creadora: Crea un node amb un operador i dos expressions booleanes filles.
     * No s'utilitza, nomes per testing.
     *
     * @param data l'operador.
     * @param left Una expressio.
     * @param right L'altra expressio.
     */
    public Node(String data, Node left, Node right) {
        this.left = left;
        this.right = right;
        this.data = data;
    }

    /**Comprova si l'expressio booleana donada es correcta.
     *
     * @param expressio L'expressio a comprovar.
     * @return Retorna cert si l'expressio es correcta, fals si no.
     */
    private boolean esCorrecta(String expressio) {
        if (expressio == null) return false;
        String noSpaces = expressio.replaceAll(" ", "");
        if (noSpaces.equals("&")) return true;
        if (noSpaces.equals("|")) return true;
        String noPar = expressio.replaceAll("[{}() ]", "");
        if (noPar.equals("")) return false;

        int par = 0;
        boolean operador = false;
        boolean inClaudator = false;
        boolean inDouble = false;
        for (int i = 0; i < expressio.length(); ++i) {
            char c = expressio.charAt(i);
            //System.out.println("Char: " + c + " op: " + operador + " inCl: " + inClaudator);
            if (c != ' ') {
                if (operador) {
                    if (c == ')') --par;
                    else if (c != '&' && c != '|') return false;
                    else operador = false;
                }
                else if (!inDouble && (c == '&' || c == '|')) return false;
                else if (!inDouble && c == '(') {
                    ++par;
                    operador = false;
                }
                else if (!inDouble && c == ')') {
                    --par;
                    operador = true;
                }
                else if (!inDouble && c == '{') {
                    if (inClaudator) return false;
                    inClaudator = true;
                    operador = false;
                }
                else if (!inDouble && c == '}') {
                    if (!inClaudator) return false;
                    inClaudator = false;
                    operador = true;
                }
                else if (c == '"') {
                    inDouble = !inDouble;
                    if (inDouble) operador = false;
                    else operador = true;
                }
                else if (c == '!');
                else if (!inDouble && !inClaudator && (i == expressio.length()-1 || !charNormal(expressio, i+1))) {
                    //System.out.println("hahaha");
                    operador = true;
                }
            }

        }
        if (!operador) return false;
        if (par != 0) return false;
        return true;
    }

    /**Mira si el seguent caracter es un caracter especial o no.
     *
     * @param i seguent caracter a mirar, si es ' ' iterar fins trobar el seguent caracter.
     * @return Retorna cert si el seguent caracter es un parentesis tancant o no es un caracter especial.
     */
    private boolean charNormal(String expressio, int i) {
        char c = expressio.charAt(i);
        if (c ==  ' ') {
            for (int j = i; j < expressio.length(); ++j) {
                if (expressio.charAt(j) == ')') return true;
                else if (expressio.charAt(j) != ' ') return false;
            }
        }
        if (c == '&' || c == '|' || c == '!' || c == ' ' || c == '"' || c == '(' /*|| c == ')'*/ || c == '{' || c == '}') return false;
        return true;
    }

    /**Elimina els espais del principi i del final i treu un ! del principi de l'expressio (si n'hi ha).
     *
     * @param expressio L'expressio a comprovar.
     * @return Retorna una parella de {@code Bool i String}, El boolea es cert si ha eliminat un !, fals altrament i el string es l'expressio sense parentesis o null si no s'ha tret res.
     */
    private Pair<Boolean, String> treureNot(String expressio) {
        //System.out.println("Treure not : " + expressio);
        expressio = expressio.trim();
        if (expressio.length() == 0) return new Pair<>(false, null);
        if (expressio.charAt(0) != '!') return new Pair<>(false, null);
        int i = 1;
        char nextChar = expressio.charAt(i);
        while(nextChar == ' ' || nextChar == '!') {
            ++i;
            nextChar = expressio.charAt(i);
        }
        if (esSimple(expressio)) {
            expressio = expressio.substring(1);
            negat = !negat;
            return new Pair<>(true, expressio);
        }
        if (nextChar == '(') {
            int par = 1;
            for (int j = 0; j < expressio.length(); ++j) {
                char c = expressio.charAt(j);
                if (c == '(') ++par;
                else if (c == ')') {
                    --par;
                    if (par == 0) {
                        return new Pair<>(false, null);
                    }
                }
            }
            expressio = expressio.substring(1);
            negat = !negat;
            return new Pair<>(true, expressio);
        }
        return new Pair<>(false, null);
    }

    /**Elimina els possibles parentesis i espais del principi i final de l'expressio donada.
     *
     * @param expressio L'expressio a comprovar.
     * @return Retorna una parella de {@code Bool i String}, El boolea es cert si ha eliminat algun parentesis i fals altrament i el string es l'expressio sense parentesis o null si no s'ha tret res.
     */
    private Pair<Boolean, String> intentaTreureParentesis(String expressio) {
        expressio = expressio.trim();
        if (expressio.length() == 0) return new Pair<>(false, null);
        if (expressio.charAt(0) != '(') return new Pair<>(false, null);

        String intent = expressio.substring(1, expressio.length() - 1);
        int par = 0;
        for (int i = 0; i < intent.length(); ++i) {
            char c = intent.charAt(i);
            if (c == '(') ++par;
            else if (c == ')') --par;
        }
        if (par == 0) {
            expressio = intent.trim();
            return new Pair<>(true, expressio);
        }
        return new Pair<>(false, null);
    }

    /**Simplifica una expressio donada treient els possibles parentesis i NOTs que afectin a tota l'expressio.
     *
     * @param cons L'expressio a simplificar.
     * @return Retorna l'expressio simplificada.
     */
    private String simplifica(String cons) {
        boolean parentesis = true;
        boolean nots = true;
        String exp = cons.trim();
        while (parentesis || nots) {
            //System.out.println("simplificant...");
            Pair<Boolean, String> res = intentaTreureParentesis(exp);
            parentesis = res.left();
            if (parentesis) exp = res.right();

            res = treureNot(exp);
            nots = res.left();
            if (nots) exp = res.right();
            //System.out.println(nots + exp);
        }
        return exp;
    }

    /**Mira si una expressio booleana es simple o no.
     * Una expressio booleana es simple si no conte cap operador fora de dobles cometes.
     *
     * @param expressio L'expressio a comprovar.
     * @return Retorna cert si l'expressio booleana es simple, fals si no.
     */

    private boolean esSimple(String expressio) {
        boolean inDouble = false;
        for (int i = 0; i < expressio.length(); ++i) {
            char c = expressio.charAt(i);
            if (c == '"') inDouble = !inDouble;
            if (!inDouble) {
                if (c == '&' || c == '|') return false;
            }
        }
        return true;
    }

    /**Separa una expressio booleana en 2, si pot respecte la primera or fora de parentesis, sino respecte la primera and fora de parentesis.
     * Si no pot separar-la retorna l'expressio al primer element i null al segon.
     *
     * @param expressio l'expressio a separar.
     * @return Retorna les dues expressions booleanes resultants com a string o l'expressio original a la primera posicio i null a la segona.
     */

    private String[] separaEnDos(String expressio) {
        //System.out.println("Separa " + this);
        String e1 = null;
        String e2 = null;
        int par = 0;
        boolean inDouble = false;
        for (int i = 0; i < expressio.length(); ++i) {
            char c = expressio.charAt(i);
            if (c == '"') inDouble = !inDouble;
            else if (c == '(') ++par;
            else if (c == ')') --par;
            else if (!inDouble && par == 0 && c == '|') {
                e1 = expressio.substring(0, i+1);
                e2 = expressio.substring(i+1);
                return new String[] {e1, e2};
            }
        }
        inDouble = false;
        par = 0;
        for (int i = 0; i < expressio.length(); ++i) {
            char c = expressio.charAt(i);
            if (c == '"') inDouble = !inDouble;
            else if (c == '(') ++par;
            else if (c == ')') --par;
            else if (!inDouble && par == 0 && c == '&') {
                e1 = expressio.substring(0, i+1);
                e2 = expressio.substring(i+1);
                return new String[] {e1, e2};
            }
        }
        return new String[] {expressio, null};
    }

    /**Funcio per testing. Escriu l'arbre d'expressions booleanes en Inordre.
     *
     */
    public void escriuInordre() {
        if (left != null) left.escriuInordre();
        System.out.print(data);
        if (right != null) right.escriuInordre();
    }

    /**Funcio per testing. Escriu l'arbre d'expressions booleanes en Preordre.
     *
     */
    public void escriuPreordre() {
        System.out.print(data);
        if (left != null) left.escriuPreordre();
        if (right != null) right.escriuPreordre();
    }

    /** Implementa l'algorisme que resol la consulta booleana. Cada node te dos fills, si son null es un node fulla i per tant es resol l'expressio booleana directament.
     * Si no son null agafa els resultats dels dos fills i els uneix en funcio de l'operador i de la negacio fins al moment.
     *
     * @return Retorna el resultat de l'expressio booleana a partir d'aquest node en forma de ((Autor, Titol), Frase).
     */
    private ArrayList<Pair<Pair<String, String>, String>> getResultat() {
        //System.out.println("Soc el thread " + Thread.currentThread().getName() + "i busco entre els documents " + start + " " + end);
        if (!run.get()) {/*System.out.println("Interromput");*/ return new ArrayList<>();}
        ArrayList<Pair<Pair<String, String>, String>> resultat = null;
        if (esSimple(data)) {
            //System.out.println("Exp simple: " + data);
            ArrayList<Pair<Pair<String, String>, String>> solucio = new ArrayList<>();
            if (data.length() == 0) return solucio;
            ArrayList<Pair<String, String>> documents = allDocuments;
            ControladorDomini controladorDomini = null;
            controladorDomini = ControladorDomini.getInstance();
            for (int i = start; i < end; ++i) {
                Pair<String, String> doc = documents.get(i);
                //System.out.println("Document " + doc);
                if (!run.get()) {/*System.out.println("Interromput");*/ return new ArrayList<>();}
                try {
                    for (String frase : controladorDomini.getFrasesDocument(doc.left(), doc.right())) {
                        //System.out.print("Document " + doc + " frase: " + frase);
                        if (!run.get()) {/*System.out.println("Interromput");*/ return new ArrayList<>();}
                        frase = frase.replaceAll("[,.;:_'!?¿¡()=+\"*…–-]", " ");
                        if (compleix(frase)) {
                            //System.out.println("Compleix " + data);
                            solucio.add(new Pair<>(doc, frase));
                        }
                        //else System.out.println("no compleix");
                    }
                } catch (ExceptionAPersitencia ignored) {}
            }
            resultat = solucio;
            return resultat;
        }
        //Estaria be implementar un algoritme que resolgues primer totes les fulles i anes pujant. Per paralelitzarho seria molt mes facil
        if (!run.get()) {/*System.out.println("Interromput");*/ return new ArrayList<>();}
        if (negat) {
            if (Objects.equals(data, "&")) {
                resultat = unio(left.getResultat(), right.getResultat());
            } else if (Objects.equals(data, "|")) {
                resultat = interseccio(left.getResultat(), right.getResultat());
            }
        }
        if (!run.get()) {/*System.out.println("Interromput");*/ return new ArrayList<>();}
        else {
            if (Objects.equals(data, "|")) {
                resultat = unio(left.getResultat(), right.getResultat());
            } else if (Objects.equals(data, "&")) {
                resultat = interseccio(left.getResultat(), right.getResultat());
            }
        }
        return resultat;
    }

    /**Fa la interseccio de les dues llistes (Hauria de ser privat)
     * @param docs Llista 1
     * @param altres Llista 2
     * @return Retorna la interseccio de les dues llistes
     */

    private ArrayList<Pair<Pair<String, String>, String>> interseccio(ArrayList<Pair<Pair<String, String>, String>> docs, ArrayList<Pair<Pair<String, String>, String>> altres) {
        //System.out.println("Interseccio: " + "'" + docs + "'" + " amb " + "'" + altres + "'");
        ArrayList<Pair<Pair<String, String>, String>> result = new ArrayList<>();
        if (docs.size() > altres.size()) {
            for (Pair<Pair<String, String>, String> altre : altres)
                if (docs.contains(altre)) result.add(altre);
        }
        else {
            for (Pair<Pair<String, String>, String> doc : docs)
                if (altres.contains(doc)) result.add(doc);
        }

        return result;
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

    /**Mira si una frase compleix una expressio booleana (Hauria de ser privat)
     * Tipus d'expressions acceptades:
     *          {p1 p2 ... pn} retorna cert si la frase conte totes les paraules p1, p2, ..., pn
     *          "p1 p2" retorna cert si la frase conte la sequencia de paraules p1 p2
     *          par retorna cert si la frase conte la paraula par
     * @param frase Frase a comprovar
     * @return Retorna cert si la frase compleix l'expressio booleana
     */

    private boolean compleix(String frase) {
        String[] pars = frase.split(" ");
        boolean resultat;
        if (frase.length() == 0) return false;

        if (data.charAt(0) == '{') {
            String[] paraules = data.substring(1, data.length()-1).split(" ");
            resultat = true;
            for (String s : paraules)
                if (!contains(pars, s)) {
                    resultat = false;
                    break;
                }
        }
        else if (data.charAt(0) == '"') {
            resultat = frase.contains(data.substring(1, data.length()-1));
        }
        else resultat = contains(pars, data);
        if (negat) return !resultat;
        else return resultat;
    }

    /**Comprova si una paraula esta dintre d'un array de paraules.
     *
     * @param pars Array de paraules.
     * @param s Paraula a buscar.
     * @return Cert si {@code s} pertany a {@code pars}, fals altrament.
     */
    private boolean contains(String[] pars, String s) {
        for (String par : pars) {
            if (par.equalsIgnoreCase(s)) return true;
        }
        return false;
    }

    /*public void setResultat(ArrayList<Pair<Pair<String, String>, String>> resultat) {
        this.resultat = resultat;
    }*/
}
