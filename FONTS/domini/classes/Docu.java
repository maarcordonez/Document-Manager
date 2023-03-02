package domini.classes;

import java.util.*;

/**Guarda la informacio d'un document
 * @author laura.hui.perez
 */
public class Docu {
    /** Nom de l'autor del document */
    protected String autor;
    /** Nom del titol del document*/
    private String titol;
    /** Contingut del document tal com es va introduir */
    private String contingut;
    /** Paraules pertanyents al document i les seves aparicions*/
    private LinkedHashMap<String, Integer> paraules;
    /** Frases del document per separat*/
    private ArrayList<String> frases;
    /** Nombre de frases pertanyents al document*/
    private int numFrases;

    //Constructores

    /**Creadora buida
     */
    public Docu() {

        autor = "";
        titol = "";
        contingut = "";
        paraules = new LinkedHashMap<>();
        frases = new ArrayList<>();
        numFrases = 0;

    }

    /**Creadora
     *
     * @param autor nom de l'autor (String)
     * @param titol nom del titol (String)
     * @param contingut contingut del document
     */
    public Docu(String autor, String titol, String contingut) {
        this.autor = autor;
        this.titol = titol;
        this.contingut = contingut;

        contaParaules();
        separaFrases();
    }

    /**Conta el nombre d'aparicions d'una paraula
     *
     */
    protected void contaParaules(){
        paraules = new LinkedHashMap<String, Integer>();
        ArrayList<String> pars = new ArrayList<>();
        StringTokenizer t = new StringTokenizer(contingut);
        String p = "";
        while(t.hasMoreTokens())
        {
            p = t.nextToken();
            //p = p.replace("\n", "");
            //p = p.replace("\r", "");
            p = p.replace("¿","");
            p = p.replace("¡","");
            p = p.replace("?","");
            p = p.replace("!","");
            p = p.replace("¡","");
            p = p.replace(".","");
            p = p.replace(",","");
            p = p.replace(";","");

            if(p.length() > 2 && p.charAt(1) == '\'') p = p.substring(2);
            pars.add(p.toLowerCase());
        }

        for(int i = 0; i < pars.size(); ++i){
            p = pars.get(i);
            if(paraules.containsKey(p)){
                Integer num = paraules.get(p);
                paraules.replace(p,num+1);
            }
            else {
                paraules.put(p,1);
            }

        }
    }

    /**Conta i separa el nombre de frases del contingut
     */
    protected void separaFrases(){

        if(!contingut.equals("")) {
            String testString = contingut;
            testString = testString.replace("¿","").replace("¡","");
            String[] sentences = testString.split("[\\.\\!\\?\\n]");
            ArrayList<String> f = new ArrayList<>();
            for (int i = 0; i < sentences.length; i++) {
                //System.out.println(i);
                String aux = sentences[i].trim();
                f.add(aux);
            }
            frases = f;
            numFrases = frases.size();
        }
        else{
            frases = new ArrayList<>();
            numFrases = 0;
        }
    }

    //Consultores
    /**Retorna cert si el document conté la paraula p
     * @param p paraula a buscar
     * @return cert si la paraula p pertany al document
     */
    public boolean teParaula(String p){
        return paraules.containsKey(p);
    }

    /**Retorna el nombre d'aparicions de la paraula p
     * Pre: la paraula p existeix
     * @param p paraula
     * @return nombre d'aparicions de la paraula p
     *
     */
    public int aparicionsParaula(String p){
        return paraules.get(p);
    }

    /**Transforma la classe en un string llegible
     *
     * @return Retorna un string amb el contingut del document
     */
    @Override
    public String toString() {
        return "Document\nAutor: " + autor + "\nTítol: " + titol + "\nContingut: " + contingut +
                "\nParaules: " + paraules + "\nNúmero Frases: " + numFrases + "\nFrases: " + frases;
    }

    //Getters i setters

    /**Frases del document
     *
     * @return Retorna les frases del document
     */
    public ArrayList<String> getFrases() {
        return frases;
    }

    /**Nom de l'autor document
     *
     * @return Retorna el nom de l'autor del document
     */
    public String getAutor() {
        return autor;
    }

    /**Setter autor
     *
     * @param autor Nom autor del document
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**Nom del titol del document
     *
     * @return Retorna el titol del document
     */
    public String getTitol() {
        return titol;
    }

    /**Setter titol
     *
     * @param titol Nom titol del document
     */
    public void setTitol(String titol) {
        this.titol = titol;
    }

    /**Contingut
     *
     * @return Retorna el contingut tal com es va introduir
     */
    public String getContingut() {
        return contingut;
    }

    /**Setter contingut
     *
     * Atualitza el nombre de frases, les frases i les paraules
     *
     * @param contingut Contingut del document tal com s'ha introduit
     */
    public void setContingut(String contingut) {
        this.contingut = contingut;

        separaFrases();
        contaParaules();
    }

    /**Nombre de frases
     *
     * @return Retorna el nombre de frases que pertanyen al document
     */
    public int getNumFrases() {
        return numFrases;
    }

    /**Frequencia de les paraules
     *
     * @return Retorna les paraules del document i la seva frequencia
     */
    public LinkedHashMap<String, Integer> getParaules() {
        return paraules;
    }

}