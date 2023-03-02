package domini.classes;

import domini.excepcions.ExceptionObraJaExisteix;
import domini.excepcions.ExceptionObraNoExisteix;
import domini.utils.Pair;
import java.util.*;

/**Classe EspaiVectorial
 * Guarda tota la informacio necesaria per resoldre consultes de k documents similars/rellevants
 * @author Pol Garrido
 *
 */
public class EspaiVectorial {
    /**Llista de cada document afegit amb el seu vector de [Paraula,[PesBool,Pesidf]]*/
    private List<LinkedHashMap<String,Pair<Double,List<Pes>>>> Weights;
    /**Norma de cada vector de pesos de cada document*/
    private List<List<Double>> Norms;
    /**Llista de [Títol,Autor], per identificar els documents*/
    private List<Pair<String,String>> TAS;
    /**Llista d'stopwords*/
    private List<String> Stopwords;

    //Constructores

    /**Creadora buida
     */
    public EspaiVectorial (){
        Weights = new ArrayList<LinkedHashMap<String,Pair<Double,List<Pes>>>>(0);
        Norms = new ArrayList<List<Double>>(0);
        TAS = new ArrayList<Pair<String,String>>(0);
        Stopwords = new ArrayList<String>(0);
    }

    //Getters i setters
    /**Inicialitza els pesos de l'espai vectorial
     * @param weights Els pesos a inicialitzar
     */
    public void setWeights(List<LinkedHashMap<String,Pair<Double,List<Pes>>>> weights){
        Weights=weights;
    }
    /**Inicialitza les normes de l'espai vectorial
     * @param norms Les normes a inicialitzar
     */
    public void setNorms(List<List<Double>> norms){
        Norms=norms;
    }
    /**Inicialitza els identificators de les obres registrades a l'espai vectorial
     * @param tas Els identificadors per inicialitzar
     */
    public void setTAS(List<Pair<String,String>> tas){
        TAS=tas;
    }
    /**Inicialitza els conjunt d'stopwords
     * @param stop El conjut d'stopwords
     */
    public void setStopwords(String stop){
        List<String> Stop = new ArrayList<String>(Arrays.asList(stop.split("\n")));
        Stopwords=Stop;
    }
    /**
     * @return Retorna les llistes de pesos del documents registrats
     */
    public List<LinkedHashMap<String,Pair<Double,List<Pes>>>> getWeights(){
        return Weights;
    }
    /**
     * @return Retorna les normes dels vectors de pesos dels documents registrats
     */
    public List<List<Double>> getNorms(){
        return Norms;        
    }
    /**
     * @return Retorna el titol i l'autor dels documents registrats
     */
    public List<Pair<String,String>> getTAS(){
        return TAS;
    }

    //Operadores
    /**Calcula la norma d'un vector i ho guarda a l'estructura Norms, per no haver de recalcular a cada crida
     * Funcio a cridar quan es vulgui carregar un document al l'espai vectorial i calculem els pesos, aixi tambe calculem la norma
     */
    private void addNorm(){
        //Set de "key's" de l'ultim document afegit
        LinkedHashMap<String,Pair<Double,List<Pes>>> w = Weights.get(Weights.size()-1);
        Set<String> keys = w.keySet();
        List<Double> normes = new ArrayList<Double>();
        boolean primer = true;
        for (String key : keys) {
            double norm=0.0;
            for (int i=0 ; primer && i<(w.get(key).right()).size() ; ++i) {
                List<Pes> pesos = w.get(key).right();
                norm = Math.pow(pesos.get(i).getValor(),2);
                normes.add(norm);
            }
            for (int i=0 ; !primer && i<(w.get(key).right()).size() ; ++i) {
                List<Pes> pesos = w.get(key).right();
                norm = Math.pow(pesos.get(i).getValor(),2);
                normes.set(i, norm+normes.get(i));
            }
            primer = false;
        }

        for (int i=0 ; i<normes.size() ; ++i){
            normes.set(i,Math.sqrt(normes.get(i)));
        }
        Norms.add(normes);
    }

    /**Calcula la norma d'un vector i ho guarda a l'estructura Norms, per no haver de recalcular a cada crida
     * Funcio a cridar quan es vulgui modificar un document al l'espai vectorial i calculem els pesos, aixi recalculem les normes
     * @param idx Index del document a recalcular
     */
    private void actNorm(int idx){
        LinkedHashMap<String,Pair<Double,List<Pes>>> w = Weights.get(idx);
        Set<String> keys = w.keySet();
        List<Double> normes = new ArrayList<Double>();
        boolean primer = true;
        for (String key : keys) {
            double norm=0.0;
            for (int i=0 ; primer && i<(w.get(key).right()).size() ; ++i) {
                List<Pes> pesos = w.get(key).right();
                norm = Math.pow(pesos.get(i).getValor(),2);
                normes.add(norm);
            }
            for (int i=0 ; !primer && i<(w.get(key).right()).size() ; ++i) {
                List<Pes> pesos = w.get(key).right();
                norm = Math.pow(pesos.get(i).getValor(),2);
                normes.set(i, norm+normes.get(i));
            }
            primer = false;
        }

        for (int i=0 ; i<normes.size() ; ++i){
            normes.set(i,Math.sqrt(normes.get(i)));
        }
        Norms.set(idx,normes);
    }

    /**Afegir un nou document a l'espai vectorial
     * Calcular pes i anar afegint a l'ultim "LinkedHashMap" de la llista de documents: [Paraula,[PesBool,Pesidf]]
     * He fet el pes "bool", "tf-idf"
     *
     * @param titol Titol del document
     * @param autor Autor del document
     * @param contingut Paraules del document amb la seva frequencia local
     * @param numobres Indica el nombre d'obres registrades
     */
    public void insertDocEV(String titol, String autor, LinkedHashMap<String, Integer> contingut, int numobres)  {
        int idx = TAS.indexOf(new Pair<String,String>(titol,autor));
        if (idx!=-1){
            System.out.println("Aqui no hauria d'arribar mai espai vectorial insert. El document es correcte pq ho ha comprovat idxObres");
            return;
        }

        LinkedHashMap<String,Pair<Double,List<Pes>>> w = new LinkedHashMap<String,Pair<Double,List<Pes>>>();

        for (String word : contingut.keySet()) {
            double tf = contingut.get(word);
            List<Pes> pesos = new ArrayList<Pes>();
            if (!Stopwords.contains(word)){
                pesos.add(new PesBool(1.0));
                PesTfIdf tfidf = new PesTfIdf();
                tfidf.setValor(tfidf.calcPes(Weights,word,tf,numobres));
                pesos.add(tfidf);
                w.put(word, new Pair<Double,List<Pes>>(tf, pesos));
            }
        }

        Weights.add(w);
        TAS.add(new Pair<String,String>(titol,autor));
        addNorm();
    }

    /**Actualitza els pesos d'un document ja existent
     * Calcular pes i anar modificant "idx" - "LinkedHashMap" de la llista de documents: [Paraula,[PesBool,Pesidf]]
     * He fet el pes "bool", "tf-idf"
     *
     * @param titol Titol antic del document
     * @param autor Autor antic del document
     * @param noutitol Nou titol del document
     * @param nouautor Nou autor del document
     * @param contingut Paraules del document amb la seva frequencia local
     * @param numobres Inidica el numero d'obres registrades
     */
    public void actDocEV(String titol, String autor, String noutitol, String nouautor, LinkedHashMap<String, Integer> contingut, int numobres) {
        int idx = TAS.indexOf(new Pair<String,String>(titol,autor));
        if (idx==-1){
            System.out.println("Aqui no hauria d'arribar mai actDOc EV");
        }

        Set<String> contingutantic = Weights.get(idx).keySet();
        Set<String> contingutnou = contingut.keySet();
        List<LinkedHashMap<String, Pair<Double,List<Pes>>>> copia=Weights;

        eraseDocEV(titol, autor);
        insertDocEV(noutitol, nouautor, contingut, numobres);
        
        Set<String> total = new HashSet<String>();
        total.addAll(contingutantic);
        total.addAll(contingutnou);

        
        for (String word : total) {
            for (int i=0 ; i<Weights.size()-1 ; ++i){
                LinkedHashMap<String, Pair<Double,List<Pes>>> paraules_del_doc=Weights.get(i);
                if (!Stopwords.contains(word)){
                    if ((contingutantic.contains(word) && !contingutnou.contains(word)) || !(contingutantic.contains(word) && contingutnou.contains(word))){
                        Pair<Double,List<Pes>> aux = paraules_del_doc.get(word);
                        PesTfIdf tfidf = new PesTfIdf();
                        if (copia.get(i).get(word)!=null){
                            tfidf.setValor(tfidf.calcPes(Weights,word,copia.get(i).get(word).left(),numobres));
                            aux.right().set(1, tfidf);
                            paraules_del_doc.put(word, aux);
                            Weights.set(i, paraules_del_doc);
                        }
                    }
                }
                actNorm(i);
            }
        }
    }

    /**Esborrar fila: Weights[Document], esborrar TAS[titol,autor] i la norma
     *
     * @param titol Titol del document
     * @param autor Autor del document
     */
    public void eraseDocEV(String titol, String autor) {
        int idx = TAS.indexOf(new Pair<String,String>(titol,autor));
        if (idx==-1){
            System.out.println("Aqui no hauria d'arribar mai EV eraseDoc");
        }
        Weights.remove(idx);
        TAS.remove(idx);
        Norms.remove(idx);
    }


    /**Calcula els pesos d'una consulta, fem servir el pes boolea, es a dir, si no es tracta 
     * d'una stopword afegim la paraula amb pes 1.0
     *
     * @param consulta Representa cada paraula de la cosulta, en el cas d'un document, totes les seves paraules
     * @return Retorna una llista amb el pes de cada paraula de la consulta
     */
    private List<Pair<String,Double>> calcPesBoolConsulta(LinkedHashMap<String, Integer> consulta){
        List<Pair<String,Double>> weights = new ArrayList<>();
        for (String word : consulta.keySet()) {
            if (!Stopwords.contains(word)){
                weights.add(new Pair<String,Double>(word,1.0));
            }
        }
        return weights;
    }

    /**Calcula el cosinus del dotProduct de la consulta respecte els documents, en els dos casos de cosine(Bool,tf-idf) la consulta es codifica amb bool
     *
     * @param t 0 si la consulta es un document, !=0 si es un conjunt de paraules
     * @param k Nombre de documents similars a retornar, si hi ha menys definits al sistema, o no hi ha res en comu en retornara el maxim de similars
     * @param consulta Cadena d'Strings representant cada paraula de la cosulta, en el cas d'un document, totes les seves paraules
     * @return Retorna el vector de cosinus ordenat de mes similaritat a menys. Utilitzem els pesos bool dels documents
     */
    public ArrayList<Pair<String,String>> cosineSimilarityBool(int t, int k, LinkedHashMap<String, Integer> consulta) {
        ArrayList<Pair<String,String>> kout = new ArrayList<Pair<String,String>>();
        List<Pair<String,Double>> consulta_pw = calcPesBoolConsulta(consulta);
        List<Pair<Integer,Double>> out = new ArrayList<Pair<Integer,Double>>(Weights.size());
        double normCON = 0.0;
        
        normCON = Math.sqrt(consulta_pw.size());

        for (int i = 0 ; i<Weights.size() ; ++i){
            double dotProduct = 0.0;
            if (Norms.get(i).size()!=0) {
                double normDOC = Norms.get(i).get(0);
                LinkedHashMap<String, Pair<Double, List<Pes>>> w = Weights.get(i);
                for (int j = 0; j < consulta_pw.size(); ++j) {
                    Boolean test = w.containsKey(consulta_pw.get(j).left());
                    if (test) {
                        Double docword = (w.get(consulta_pw.get(j).left()).right().get(0)).getValor();
                        dotProduct += docword * consulta_pw.get(j).right();
                    }
                }
                out.add(new Pair<Integer, Double>(i, dotProduct / (normCON * normDOC)));
            }
        }

        Collections.sort(out, new Comparator<Pair<Integer,Double>>() {
            @Override
            public int compare(Pair<Integer,Double> o1, Pair<Integer,Double> o2) {
                return (o1.right()).compareTo(o2.right());
            }
        });
;
        if (t==0){
            for (int i = out.size()-2 ; i>=0 && k!=0 ; i=i-1){
                if (!out.get(i).right().isNaN() && out.get(i).right()!=0.0){
                    kout.add(new Pair<String,String>(TAS.get(out.get(i).left()).left(), TAS.get(out.get(i).left()).right()));
                }
                k=k-1;
            }
        }
        else{
            for (int i = out.size()-1 ; i>=0 && k!=0 ; i=i-1){
                if(!out.get(i).right().isNaN() && out.get(i).right()!=0.0){
                    kout.add(new Pair<String,String>(TAS.get(out.get(i).left()).left(), TAS.get(out.get(i).left()).right()));
                }
                k=k-1;
            }
        }
        return kout;
    }

    /**Calcula el cosinus del dotProduct de la consulta respecte els documents, en els dos casos de cosine(Bool,tf-idf) la consulta es codifica amb bool
     *
     * @param t 0 si la consulta es un document, !=0 si es un conjunt de paraules
     * @param k Nombre de documents similars a retornar, si hi ha menys definits al sistema, o no hi ha res en comu en retornara el maxim de similars
     * @param consulta Cadena d'Strings representant cada paraula de la cosulta, en el cas d'un document, totes les seves paraules
     * @return Retorna el vector de cosinus ordenat de mes similaritat a menys. Utilitzem els pesos tf-idf dels documents
     */
    public ArrayList<Pair<String,String>> cosineSimilarityTf_Idf(int t, int k, LinkedHashMap<String, Integer> consulta) {

        ArrayList<Pair<String,String>> kout = new ArrayList<Pair<String,String>>();
        List<Pair<String,Double>> consulta_pw = calcPesBoolConsulta(consulta);
        List<Pair<Integer,Double>> out = new ArrayList<Pair<Integer,Double>>(Weights.size());
        double normCON = 0.0;
        
        normCON = Math.sqrt(consulta_pw.size());

        for (int i = 0 ; i<Weights.size() ; ++i){
            double dotProduct = 0.0;
            if (Norms.get(i).size()!=0) {
                double normDOC = Norms.get(i).get(1);
                LinkedHashMap<String, Pair<Double, List<Pes>>> w = Weights.get(i);
                for (int j = 0; j < consulta_pw.size(); ++j) {
                    Boolean test = w.containsKey(consulta_pw.get(j).left());
                    if (test) {
                        Double docword = (w.get(consulta_pw.get(j).left()).right().get(1)).getValor();
                        dotProduct += docword * consulta_pw.get(j).right();
                    }
                }
                out.add(new Pair<Integer, Double>(i, dotProduct / (normCON * normDOC)));
            }
        }

        Collections.sort(out, new Comparator<Pair<Integer,Double>>() {
            @Override
            public int compare(Pair<Integer,Double> o1, Pair<Integer,Double> o2) {
                return (o1.right()).compareTo(o2.right());
            }
        });

        if (t==0){
            for (int i = out.size()-2 ; i>=0 && k!=0 ; i=i-1){
                if (!out.get(i).right().isNaN() && out.get(i).right()!=0.0){
                    kout.add(new Pair<String,String>(TAS.get(out.get(i).left()).left(), TAS.get(out.get(i).left()).right()));
                }
                k=k-1;
            }
        }
        else{
            for (int i = out.size()-1 ; i>=0 && k!=0 ; i=i-1){
                if (!out.get(i).right().isNaN() && out.get(i).right()!=0.0){
                    kout.add(new Pair<String,String>(TAS.get(out.get(i).left()).left(), TAS.get(out.get(i).left()).right()));
                }
                k=k-1;
            }
        }
        return kout;
    }
}