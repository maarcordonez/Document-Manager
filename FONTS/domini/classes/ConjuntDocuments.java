package domini.classes;

import domini.excepcions.*;
import domini.utils.Pair;
import persistencia.excepcions.ExceptionAPersitencia;

import java.time.LocalDateTime;
import java.util.*;

/**Classe ConjuntDocuments. S'encarrega de redirigir operacions i d'emmagtzemar les etiquetes
 * Es singleton.
 *
 * @author laura.hui.perez
 */

public class ConjuntDocuments {

    /**La instancia de idxObres */
    private IdxObres idxObres;
    /**La instancia de idxAutors */
    private IdxAutors idxAutors;
    /**La instancia d'espaiVectorial */
    private EspaiVectorial espaiVectorial;
    /**La instancia d'etiquetes, la key és el nom de l'etiqueta i el value és l'etiqueta sencera */
    private TreeMap<String, Etiqueta> etiquetes;
    /**La instancia singleton. */
    public static ConjuntDocuments singleton;


    /**Retorna la Conjunt Documents com a instancia singleton
     * @return La instancia de conjunt documents
     */
    public static ConjuntDocuments getInstance() {
        if (singleton == null) singleton = new ConjuntDocuments();
        return singleton;
    }

    /**Creadora buida. Creem la llista d'etiquetes. 
     * Inicialment nomes te l'etiqueta Favorits que esta sempre per defecte.
     */
    public ConjuntDocuments() {
        idxObres = new IdxObres();
        idxAutors = new IdxAutors();
        espaiVectorial = new EspaiVectorial();
        etiquetes = new TreeMap<>();
        Etiqueta etiqFavorits = new Etiqueta("Favorits");
        etiquetes.put(etiqFavorits.getNom(), etiqFavorits);
    }

    /**Creadora per parametres. Utilitzada en testos interns
     * @param idxObres Index obres
     * @param idxAutors Index Autors
     * @param espaiVectorial Espai Vectorial
     */
    public ConjuntDocuments(IdxObres idxObres, IdxAutors idxAutors, EspaiVectorial  espaiVectorial) {
        this.idxAutors = idxAutors;
        this.idxObres = idxObres;
        this.espaiVectorial = espaiVectorial;
    }

    /**Afegeix un document als indexs donat un autor i un titol 
     * @param autor Autor de l'obra
     * @param titol Titol de l'obra
     * @throws ExceptionObraJaExisteix Si ja exiteix una obra amb aquets identificadors
     */
    public void afegirDocument(String autor, String titol) throws ExceptionObraJaExisteix {
        idxObres.afegirObra(titol,autor);
        idxAutors.afegir(autor);
    }

    /** Es la segona part d’afegir un document, actualitzar les dades de l'espai vectorial
     * @param autor Autor de l'obra
     * @param titol Titol de l'obra
     * @param contingut Contingut del document
     */
    public void actualitzarEspaiVectorial(String autor, String titol, String contingut) {
        Docu d = new Docu(autor,titol,contingut);
        espaiVectorial.insertDocEV(titol,autor,d.getParaules(),idxObres.getNumObres());
    }

    /**Elimina un document donat el seu titol i autor de les estructures de dades
     * @param autor Autor de l'obra
     * @param titol Titol de l'obra
     * @throws ExceptionAutorNoExisteix Si l'autor no existeix
     * @throws ExceptionObraNoExisteix Si la obra amb el titol indicat no existeix
     */
    public void eliminarDocument(String autor, String titol) throws ExceptionAutorNoExisteix, ExceptionObraNoExisteix {
        boolean empty = idxObres.eliminarObraAutor(titol,autor);
        if (empty) idxAutors.eliminar(autor);
        espaiVectorial.eraseDocEV(titol, autor);
    }
    
    /**Modifica un document donat el seu titol i autor de les estructures de dades i el
     * substitueix per un document amb nouTitol, nouAutor
     * @param autor Autor de l'obra
     * @param titol Titol de l'obra
     * @param nouAutor Nou titol de l'obra
     * @param nouTitol NOu autor de l'obra
     * @throws ExceptionAutorNoExisteix Si l'autor no existeix
     * @throws ExceptionObraNoExisteix Si la obra amb el titol indicat no existeix
     * @throws ExceptionObraJaExisteix Si ja existeix una obra amb nouTitol,nouAutor
     */
    public void modificarDocument(String autor, String titol, String nouAutor, String nouTitol) throws ExceptionObraJaExisteix, ExceptionAutorNoExisteix, ExceptionObraNoExisteix {
        boolean empty = idxObres.modificaDocument(autor,titol,nouAutor,nouTitol);
        if(empty) idxAutors.eliminar(autor);
        idxAutors.afegir(nouAutor);
    }

    /** Actualitza les dades de l'espai vectorial quan es modifica un document, perque els calculs
     * es fagin amb la informacio adient
     * @param autor Autor de l'obra
     * @param titol Titol de l'obra
     * @param nouAutor Nou titol de l'obra
     * @param nouTitol Nou autor de l'obra
     * @param nouContingut Nou contingut de l'obra
     */
    public void modificarEspaiVectorial(String autor, String titol, String nouAutor, String nouTitol, String nouContingut) {
        Docu d = new Docu(nouAutor, nouTitol, nouContingut);
        espaiVectorial.actDocEV(titol, autor, nouTitol, nouAutor, d.getParaules(), idxObres.getNumObres());
    }

    /*
    public String getContingut(String autor, String titol) throws ExceptionAutorNoExisteix, ExceptionObraNoExisteix, ParserConfigurationException, IOException, SAXException {
        ArrayList<String> info = ctrDom.getInfoDocument(titol, autor);
        return info.get(2); // el contingut es el tercer element de l'ArrayList
    }

     */


    /**Llista els titols donat un autor
     * @param autor Nom de l'autor
     * @return Retorna una llista amb el titols de totes les obres de l'autor indicat
     * @throws ExceptionAutorNoExisteix Si l'autor no existeix
     */
    public ArrayList<String> llistaTitols(String autor) throws ExceptionAutorNoExisteix {
        return idxObres.consultaObresAutor(autor);
    }

    /**Llista els titols donat un prefix del nom d'un d'autor
     * @param prefix Prefix d'un posible nom d'autor
     * @return Retorna una llista amb el titols de totes les obres dels autors que satisfacin la condicio del prefix
     */
    public ArrayList<String> llistaAutorsPrefix(String prefix) {
        return idxAutors.llistarPrefix(prefix);
    }

    /**Retorna una llista ordenada per semblança respecte al document amb l'autor i titol pasats com a parametre
     * @param tipus Indica si es tracta d'una consulta de semblança o rellevancia
     * @param k Indica de quants documents es compondra la llista. Si hi ha més que resgitrats es fara el min(resgitrats,k)
     * @param autor Indica l'autor del document
     * @param titol Indica el titol del document
     * @return Retorna una llista amb el titol i l'autor dels documents
     * @throws ExceptionAPersitencia Si el document no es pot identificar
     */
    public ArrayList<Pair<String,String>> kDocumentsSemblants(String tipus, int k, String autor, String titol) throws ExceptionAPersitencia {
        ControladorDomini ctrDom = ControladorDomini.getInstance();
        ArrayList<String> info = ctrDom.getInfoDocument(titol, autor);
        String contingut = info.get(2);
        Docu d = new Docu(autor, titol, contingut);
        if (Objects.equals(tipus, "Bool")){
            return espaiVectorial.cosineSimilarityBool(0,k,d.getParaules());
        }
        else return espaiVectorial.cosineSimilarityTf_Idf(0,k,d.getParaules());
    }

    /**Retorna una llista ordenada dels docuemnts mes rellevants per les paraules de la consulta
     * @param tipus Indica si es tracta d'una consulta de semblança o rellevancia
     * @param k Indica de quants documents es compondra la llista. Si hi ha més que resgitrats es fara el min(resgitrats,k)
     * @param paraules Les paraules de la consulta
     * @return Retorna una llista amb el titol i l'autor dels documents 
     */
    public ArrayList<Pair<String,String>> kDocumentsRellevants(String tipus, int k, ArrayList<String> paraules) {
        LinkedHashMap<String,Integer> pars = new LinkedHashMap<>();
        for(int i = 0; i < paraules.size(); ++i){
            String p =paraules.get(i);
            if(p.charAt(p.length()-1) == '.' || p.charAt(p.length()-1) == ',' || p.charAt(p.length()-1) == '?' || p.charAt(p.length()-1) == ';' || p.charAt(p.length()-1) == '!')p = p.substring(0,p.length()-1);
            if(p.length() > 2 && p.charAt(1) == '\'') p = p.substring(2,p.length());
            if(pars.containsKey(p)){
                Integer num = pars.get(p);
                pars.replace(p,num+1);
            }
            else {
                pars.put(p,1);
            }
        }
        if (tipus=="Bool"){
            return espaiVectorial.cosineSimilarityBool(1,k,pars);
        }
        else return espaiVectorial.cosineSimilarityTf_Idf(1,k,pars);
    }

    /**Retorna una llista de totes les parelles titol autor del sistema
     * @return Retorna una llista amb el titol i l'autor dels documents 
     */
    public ArrayList<Pair<String,String>> getAllTitolsAutors(){
        return idxObres.getTitolsAutors();
    }

    /*
    public ArrayList<String> getFrasesDocument(String titol, String autor) throws ExceptionAutorNoExisteix, ExceptionObraNoExisteix, ParserConfigurationException, IOException, SAXException, ClassNotFoundException, ExceptionConsultaIncorrecta {
        ControladorDomini ctrDom = ControladorDomini.getInstance();
        ArrayList<String> info = ctrDom.getInfoDocument(titol, autor);
        String contingut = info.get(2);
        Docu d = new Docu(autor, titol, contingut);
        return d.getFrases();
    }

     */

    /**
     * @return Retorna l'index d'obres
     */
    public TreeMap<String, TreeMap<String, LocalDateTime>> getIdxObres() {
        return idxObres.getIdxObres();
    }

    /**Inicialitza l'index d'obres
     * @param idxO Les dades de l'index a inicialitzar
     */
    public void inicialitzarIdxObres(TreeMap<String, TreeMap<String, LocalDateTime>> idxO){
        idxObres.inicialitzarIdxObres(idxO);
    }

    /**Inicialitza l'index d'autors
     * @param idxA Les dades de l'index a inicialitzar
     */
    public void inicialitzarIdxAutors(ArrayList idxA){
        idxAutors.inicialitzarIdxAutors(idxA);
    }

    /**
     * @return Retorna les llistes de pesos del documents registrats
     */
    public List<LinkedHashMap<String, Pair<Double,List<Pes>>>> getWeights() {
        return espaiVectorial.getWeights();
    }

    /**
     * @return Retorna les normes dels vectors de pesos dels documents registrats
     */
    public List<List<Double>> getNorms() {
        return espaiVectorial.getNorms();
    }

    /**
    * @return Retorna el titol i l'autor dels documents registrats
    */
    public List<Pair<String,String>> getTitolAutors() {
        return espaiVectorial.getTAS();
    }

    /**Inicialitza els pesos de l'espai vectorial
     * @param weights Els pesos a inicialitzar
     */
    public void inicialitzarWeights(List<LinkedHashMap<String, Pair<Double,List<Pes>>>> weights) {
        espaiVectorial.setWeights(weights);
    }

    /**Inicialitza les normes de l'espai vectorial
     * @param norms Les normes a inicialitzar
     */
    public void inicialitzarNorms(List<List<Double>> norms) {
        espaiVectorial.setNorms(norms);
    }

    /**Inicialitza els identificators de les obres registrades a l'espai vectorial
     * @param TAS Els identificadors per inicialitzar
     */
    public void inicialitzarTAS(List<Pair<String, String>> TAS) {
        espaiVectorial.setTAS(TAS);
    }

    /**Inicialitza les etiquetes
     * @param etiq Llista de pairs nomEtiqueta i obres etiqueta. Les obres estan en un treemap de nom autor i treemap de nom obres autor i dates
     */
    public void inicialitzarEtiquetes(ArrayList<Pair<String, TreeMap<String, TreeMap<String, LocalDateTime>>>> etiq) {
        etiquetes = new TreeMap<>();
        for (int i = 0; i < etiq.size(); ++i) {
            String nomEtiq = etiq.get(i).left();
            Etiqueta e = new Etiqueta(nomEtiq);
            e.inicialitzarObres(etiq.get(i).right());
            etiquetes.put(e.getNom(), e);
        }
        // Per si no existís la etiqueta Favorits la creem al final, si ja existia no es sobreescriurà la informació
        if (!etiquetes.containsKey("Favorits")) {
            Etiqueta e = new Etiqueta("Favorits");
            etiquetes.put(e.getNom(), e);
        }
    }

    /**Inicialitza els conjunt d'stopwords
     * @param stopwords El conjut d'stopwords
     */
    public void inicialitzarStopWords(String stopwords) {
        espaiVectorial.setStopwords(stopwords);
    }

    /**Comprova si l'obra identificada pel titol i l'autor existeix
     * @param titol El titol del document
     * @param autor L'autor del document
     * @return Cert si existiex o fals si no
     */
    public boolean existeixDocument(String titol, String autor) {
       return idxObres.existeixDocument(titol, autor);
    }

    /**Llista les etiquetes definides al sistema
     * @return Una llista amb el nom de cada etiqueta
     */
    public ArrayList<String> llistarEtiquetes() {
        ArrayList<String> etiq = new ArrayList<>();
        etiq.add("Favorits"); // Fem que sigui la primera perquè així sempre aparegui a dalt, ja que és la important
        for (Map.Entry<String, Etiqueta> entry : etiquetes.entrySet()) {
            String nom = entry.getKey();
            if (!nom.equals("Favorits")) etiq.add(entry.getKey()); // Si no és la de Favorits l'afegim.
        }
        return etiq;
    }

    /**Si no existeix una etiqueta amb aquest nom, crea una nova etiqueta amb el nom que rep com a parametre i l’afegeix al TreeMap
     * @param nomEtiq El nom de l'etiqueta
     */
    public void afegirEtiqueta(String nomEtiq) {
        if (!etiquetes.containsKey(nomEtiq)) {
            Etiqueta etiq = new Etiqueta(nomEtiq);
            etiquetes.put(nomEtiq, etiq);
        }
    }

    
    /**Si existeix una etiqueta amb el nom que rep com a parametre, l’elimina del TreeMap
     * @param nomEtiq El nom de l'etiqueta
     */
    public void eliminarEtiqueta(String nomEtiq) {
        if(!nomEtiq.equals("Favorits")) etiquetes.remove(nomEtiq); //Favorits mai s'ha d'eliminar
    }

    /**Si existeix una etiqueta amb el nom que rep com a parametre, l'associa al document identificat per titol i autor
     * @param nomEtiq El nom de l'etiqueta
     * @param titol Titol del document
     * @param autor Autor del document
     */
    public void afegirEtiquetaObra(String nomEtiq, String titol, String autor) {
        if (etiquetes.containsKey(nomEtiq)) {
            etiquetes.get(nomEtiq).afegirObra(titol, autor);
        }
    }

    /**Si existeix una etiqueta amb el nom que rep com a parametre, la elimina del document identificat per titol i autor
     * @param nomEtiq El nom de l'etiqueta
     * @param titol Titol del document
     * @param autor Autor del document
     */
    public void eliminarEtiquetaObra(String nomEtiq, String titol, String autor) {
        if (etiquetes.containsKey(nomEtiq)) {
            etiquetes.get(nomEtiq).eliminarObra(titol, autor);
        }
    }

    /**Llista les obres que tenen l'etiqueta associada que es passa com a parametre
     * @param nomEtiq El nom de l'etiqueta
     * @return Una llista amb els titols i autors de cada obra marcada amb l'etiqueta
     */
    public ArrayList<Pair<String, String>> llistarObresEtiqueta(String nomEtiq) {
        if (etiquetes.containsKey(nomEtiq)) {
            return etiquetes.get(nomEtiq).llistarObres();
        }
        return new ArrayList<>();
    }

    /**Obte les etiquetes registrades al sistema
     * @return Llista de pairs nomEtiqueta i obres etiqueta. Les obres estan en un treemap de nom autor i treemap de nom obres autor i dates
     */
    public ArrayList<Pair<String, TreeMap<String, TreeMap<String, LocalDateTime>>>> getEtiquetes() {
        ArrayList<Pair<String, TreeMap<String, TreeMap<String, LocalDateTime>>>> etiq = new ArrayList<>();
        for (Map.Entry<String, Etiqueta> entry : etiquetes.entrySet()) {
            Pair p = new Pair<>(entry.getKey(), entry.getValue().getObres());
            etiq.add(p);
        }
        return etiq;
    }

    /**Llista les obres que tenen les etiquetes associades que es passen com a parametre
     * @param nomEtiqs El noms de les etiquetes
     * @return Una llista amb els titols i autors de cada obra marcada amb alguna de les etiquetes
     */
    public ArrayList<Pair<String, String>> llistarObresEtiquetes(ArrayList<String> nomEtiqs) {
        Set<Pair<String, String>> llista = new HashSet<>();
        for (String etiq : nomEtiqs) {
            if (etiquetes.containsKey(etiq)) {
                ArrayList<Pair<String, String>> resposta = etiquetes.get(etiq).llistarObres();
                llista.addAll(resposta);
            }
        }
        return new ArrayList<> (llista);
    }

    /**Retorna una llista de titol, autor i data
     * @return ArrayList de titol, autor i data
     */
    public ArrayList<ArrayList<String>> getAllTitolsAutorsData() {
        return idxObres.getTitolsAutorsData();
    }

    /**Rebem un pair titol autor i retornem una llista de titol autor i data
     * @param obra Indentificador de la obra
     * @return ArrayList de titol, autor i data
     */
    public ArrayList<String> afegirDataObra(Pair<String, String> obra) {
        String data = idxObres.getData(obra.left(), obra.right());
        ArrayList<String> obraData = new ArrayList<>();
        obraData.add(obra.left());
        obraData.add(obra.right());
        obraData.add(data);
        return obraData;
    }

    /**Obte les etiquetes associades a una obra
     * @param titol Titol de l'obra
     * @param autor Autor de l'obra
     * @return Llista de les etiquetes associades
     */
    public ArrayList<String> getEtiquetesObra(String titol, String autor) {
        ArrayList<String> llista = new ArrayList<>();
        for (Map.Entry<String, Etiqueta> entry : etiquetes.entrySet()) {
            if (entry.getValue().conteObra(titol, autor)) llista.add(entry.getKey());
        }
        return llista;
    }

    public ArrayList<String> etiquetesAfegibles(String titol, String autor) {
        ArrayList<String> llista = new ArrayList<>();
        for (Map.Entry<String, Etiqueta> entry : etiquetes.entrySet()) {
            if (!entry.getValue().conteObra(titol, autor)) llista.add(entry.getKey());
        }
        return llista;
    }
}
