package domini.classes;

import domini.excepcions.*;
import domini.utils.Pair;
import persistencia.classes.ControladorPersistencia;
import persistencia.excepcions.ExceptionAPersitencia;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

/**Classe Controlador domini. S'encarrega de separar la capa de domini de presentacio i persistencia.
 * Es singleton.
 *
 * @author marc.ordonez
 */
public class ControladorDomini {
    /**La instancia de conjunt Documents. */
    ConjuntDocuments cjtDoc;
    /**La instancia de Controlador Consultes Booleanes. */
    ControladorConsultesBooleanes contrCons;
    /**La instancia de Controlador Persistencia. */
    ControladorPersistencia ctrPers;
    /**La instancia singleton. */
    private static ControladorDomini single_ctrDom = null;

    /**Creadora: Crea un conjunt documents, un controlador consultes i un controlador persistencia.
     *
     */
    public ControladorDomini() {
        cjtDoc = new ConjuntDocuments();
        contrCons = ControladorConsultesBooleanes.getInstance();
        ctrPers = ControladorPersistencia.getInstance();
    }

    /**Funcio que es crida per aconseguir la instancia singleton de Controlador Domini.
     *
     * @return Retorna la instancia de Controlador Domini.
     */
    public static ControladorDomini getInstance() {
        if (single_ctrDom == null) single_ctrDom = new ControladorDomini();
        return single_ctrDom;
    }

    /**Inicialitza totes les estructures que et guardades.
     *
     */
    public void inicialitzar() {
        ArrayList idxA = ctrPers.loadIdxAutors();
        TreeMap<String, TreeMap<String, LocalDateTime>> idxO = ctrPers.loadIdxObres();
        List<LinkedHashMap<String, Pair<Double,List<Pes>>>> weights = ctrPers.loadWeights();
        List<List<Double>> norms = ctrPers.loadNorms();
        List<Pair<String, String>> TA = ctrPers.loadTitolAutors();
        ArrayList<Pair<String, TreeMap<String, TreeMap<String, LocalDateTime>>>> etiq = ctrPers.loadEtiquetes();
        String stopwords;
        stopwords = ctrPers.llegirStopWords();
        ArrayList<Pair<String, String>> expBooleanesHist = ctrPers.loadExpressionsBooleanesHist();
        ArrayList<Pair<String, String>> expBooleanesGuardades = ctrPers.loadExpressionsBooleanesGuardades();
        cjtDoc.inicialitzarIdxAutors(idxA);
        cjtDoc.inicialitzarIdxObres(idxO);
        cjtDoc.inicialitzarWeights(weights);
        cjtDoc.inicialitzarNorms(norms);
        cjtDoc.inicialitzarTAS(TA);
        cjtDoc.inicialitzarStopWords(stopwords);
        cjtDoc.inicialitzarEtiquetes(etiq);
        contrCons.loadHist(expBooleanesHist);
        contrCons.loadGuardades(expBooleanesGuardades);
        actualitzarConsultesBooleanes();
    }

    /**Crea un document identificat amb autor i titol, amb el contingut que se li passa.
     *
     * @param autor El nom de l'autor.
     * @param titol El titol del document.
     * @param contingut El contingut del document.
     * @throws ExceptionObraJaExisteix Si ja existia un document amb titol {@code titol} de l'autor {@code autor}.
     * @throws ExceptionTitolBuit Si el titol es buit.
     * @throws ExceptionAutorBuit Si l'autor es buit.
     */
    public void creaDocument(String autor, String titol, String contingut) throws ExceptionObraJaExisteix, ExceptionTitolBuit, ExceptionAutorBuit {
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        if (titol == null || titol.equals("")) throw new ExceptionTitolBuit();
        else if (autor == null || autor.equals("")) throw new ExceptionAutorBuit();
        else {
            cjtDoc.afegirDocument(autor, titol);
            //contrCons.actualitzacioDocuments();
            ctrPers.guardarDocumentAMemoria(titol, autor, contingut);
            cjtDoc.actualitzarEspaiVectorial(autor, titol, contingut);
        }
    }

    /**Elimina el document identificat per autor i titol dels índexs i de persistencia.
     *
     * @param autor L'autor del document a eliminar.
     * @param titol El titol del document.
     * @throws ExceptionAutorNoExisteix Si l'autor no existeix.
     * @throws ExceptionObraNoExisteix Si l'autor {@code autor} no te una obra titulada {@code titol}.
     * @throws IOException Exepcio de Java
     */
    public void eliminarDocument(String autor, String titol) throws ExceptionAutorNoExisteix, ExceptionObraNoExisteix, IOException {
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        cjtDoc.eliminarDocument(autor,titol);
        contrCons.actualitzacioDocuments();
        ctrPers.eliminarDocument(titol, autor);
    }

    public void actualitzarConsultesBooleanes() {
        contrCons.actualitzacioDocuments();
    }


    /**Modifica el document identificat amb autor i titol i li assignem el nou autor, el nou titol i el nou contingut.
     *
     * @param autor L'autor del document a modificar.
     * @param titol El titol del document a modificar.
     * @param nouAutor El nou autor del document.
     * @param nouTitol El nou titol del document.
     * @param nouContingut El nou contingut del document.
     * @throws ExceptionObraJaExisteix Si ja existia una obra titulada {@code nouTitol} de l'autor {@code nouAutor}.
     * @throws ExceptionAutorNoExisteix Si l'autor {@code autor} no existeix.
     * @throws ExceptionObraNoExisteix Si l'autor {@code autor} no te una obra titulada {@code titol}.
     */
    public void modificarDocument(String autor, String titol, String nouAutor, String nouTitol, String nouContingut) throws ExceptionObraJaExisteix, ExceptionAutorNoExisteix, ExceptionObraNoExisteix {;
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        cjtDoc.modificarDocument(autor,titol, nouAutor, nouTitol); // es comprova que no existeix la nova obra i que l'antiga
        String contingut;                                                        // sí que existia
        //guardem contingut antic per si cal desfer el canvi
        try {
            contingut = ctrPers.getInfoDocument("./documents/" + titol + "_" + autor + ".txt").get(2);
        } catch (ExceptionAPersitencia e) {
            contingut = "";
        }
        // intenta eliminar el document de persitencia i crear-ne el nou
        try {
            cjtDoc.modificarEspaiVectorial(autor, titol, nouAutor, nouTitol, nouContingut);
            ctrPers.eliminarDocument(titol, autor);
            ctrPers.guardarDocumentAMemoria(nouTitol, nouAutor, nouContingut);
        }
        // si no ha pogut fer el canvi a persistencia desfem els canvis anteriors per deixar-ho com al principi
        catch (IOException e) {
            try {
                creaDocument(autor, titol, contingut);
            } catch (ExceptionAutorBuit | ExceptionTitolBuit ignored) {
            }
        }
        //contrCons.actualitzacioDocuments();

    }

    /**Entrega el contingut del document consultat
     *
     * @param autor L'autor del document a modificar.
     * @param titol El titol del document a modificar.
     * @return Retorna String amb el contingut del document identificat amb autor titol
     * @throws ExceptionObraNoExisteix Si l'obra {@code titol} no existeix
     * @throws ExceptionAutorNoExisteix Si l'autor {@code autor} no existeix.
     * @throws ExceptionAPersitencia Si es produeix un error de comunicacio amb persistencia
     */
    public String mostraContingut(String autor, String titol) throws ExceptionAutorNoExisteix, ExceptionObraNoExisteix, ExceptionAPersitencia {
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        if (existeixDocument(titol, autor)) {
            ArrayList info = ctrPers.getInfoDocument("./documents/" + titol + "_" + autor + ".txt");
            return (String) info.get(2);
        }
        else throw new ExceptionObraNoExisteix();
    }

    /**Demana de llegir un document del sistema
     *
     * @param path Identificador del document
     * @return Retorna arrayList amb titol, autor i contingut del document que es troba en aquest path sempre i quan sigui .txt, .xml o .kitty
     * @throws ExceptionAPersitencia Si es produeix un error de comunicacio amb persistencia
     */
    public ArrayList<String> llegirDocumentSistema(String path) throws ExceptionAPersitencia {
        ArrayList<String> llista = ctrPers.getInfoDocument(path);
        String titol = llista.get(0);
        String autor = llista.get(1);
        String contingut = llista.get(2);
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        llista.clear();
        llista.add(titol);
        llista.add(autor);
        llista.add(contingut);
        return llista;
    }

    /**Demana de descarregar un document en el format seleccionat
     *
     * @param titol Titol del document
     * @param autor Autor del document
     * @param path Identificador del document
     * @param format Format en que es vol descarregar el document
     * @throws ExceptionAPersitencia Si es produeix un error de comunicacio amb persistencia
     */
    public void descarregarDocumentASistema(String titol, String autor, String path, String format) throws ExceptionAPersitencia {
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        ctrPers.descarregarDocument(titol, autor, path, format);
    }

    /**Llista els títols de l'autor identificar per el parametre autor
     * @param autor Autor del document
     * @throws ExceptionAutorNoExisteix Si l'autor no existeix
     * @return Una llista amb els titols dels seus documents
     */
    public ArrayList<String> llistaTitols(String autor) throws ExceptionAutorNoExisteix {
        autor = depurarNom(autor);
        return cjtDoc.llistaTitols(autor);
    }

    /**Llista autors que comencen amb el prefix passat com a parametre
     * @param prefix Prefix a evaluar
     * @return Una llista amb els noms dels autors que satisfan la consulta
     */
    public ArrayList<String> llistaAutorsPrefix(String prefix){
        return cjtDoc.llistaAutorsPrefix(prefix);
    }

    /**Retorna llista de k parelles titol i autor semblants al document identificat per autor i titol
     * @param tipus Determina si la consulta es per rellevancia o semblança
     * @param k Indica el nombre de documents a retornar
     * @param autor Autor del document
     * @param titol Titol del document
     * @return Llista de k parelles titol i autor semblants al document identificat per autor i titol
     * @throws ExceptionAPersitencia Si es produeix un error al identificar el fitxer
     */
    public ArrayList<ArrayList<String>> kDocumentsSemblants(String tipus, int k, String autor, String titol) throws ExceptionAPersitencia {
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        ArrayList<Pair<String, String>> llista =  cjtDoc.kDocumentsSemblants(tipus, k, autor, titol);
        return afegirDataAObra(llista);
    }

    /**Retorna llista de k parelles titol i autor rellevants per les paraules consultades
     * @param tipus Determina si la consulta es per rellevancia o semblança
     * @param k Indica el nombre de documents a retornar
     * @param paraules Conjunt de paraules a consultar
     * @return Llista de k parelles titol i autor dels documents rellevants
     */
    public ArrayList<ArrayList<String>> kDocumentsRellevants(String tipus, int k, ArrayList<String> paraules){
        ArrayList<Pair<String, String>> llista = cjtDoc.kDocumentsRellevants(tipus,k, paraules);
        return afegirDataAObra(llista);
    }

    /**Retorna llista de parelles titol i autor que compleixen l'expressió indentificada pel nom
     * @param nom Nom identificatiu de la consulta
     * @throws ExceptionConsultaNoExisteix Si no hi ha cap consulta guardada amb aquell nom
     * @return Llista de parelles titol i autor que compleixen l'expressió indentificada pel nom
     */
    public ArrayList<Pair<String, String>> ferConsultaBooleanaPerNom(String nom) throws ExceptionConsultaNoExisteix {
        return contrCons.ferConsultaPerNom(nom);
    }

    /**Retorna llista de parelles titol i autor que compleixen l'expressió exp
     * @param exp Expresio booleana
     * @throws ExceptionConsultaIncorrecta Si el format de la consuta es incorrecte
     * @return Llista de parelles titol i autor que compleixen l'expressió exp
     */
    public ArrayList<Pair<String, String>> ferConsultaBooleanaPerExp(String exp) throws ExceptionConsultaIncorrecta {
        return contrCons.ferConsultaPerExp(exp);
    }

    /**Retorna llista de parelles titol i autor que compleixen l'expressió indentificada pel nom i exp
     * @param nom Nom identificatiu de la consulta
     * @param exp Expresio booleana
     * @throws ExceptionConsultaIncorrecta Si el format de la consuta es incorrecte
     * @return Llista de parelles titol i autor que compleixen l'expressió exp
     */
    public ArrayList<Pair<String, String>> ferConsultaBooleana(String nom, String exp) throws ExceptionConsultaIncorrecta {
        return contrCons.ferConsulta(nom, exp);
    }

    /**Guarda la consulta booleana amb el nom i expresio indicades
     * @param nom Nom identificatiu de la consulta
     * @param expressio Expresio booleana
     * @throws ExceptionConsultaIncorrecta Si el format de la consuta es incorrecte
     * @throws ExceptionConsultaJaExisteix Si ja existeix una consult amb el mateix nom
     */
    public void guardarConsultaBooleana(String nom, String expressio) throws ExceptionConsultaJaExisteix, ExceptionConsultaIncorrecta {
        contrCons.guardarConsultaBooleana(nom, expressio);
    }

    /**Elimina la consulta booleana amb el nom indicat
     * @param nom Nom identificatiu de la consulta
     * @throws ExceptionConsultaNoExisteix Si no existeix cap consulta amb aquell nom
     */
    public void eliminaConsultaBooleana(String nom) throws ExceptionConsultaNoExisteix {
        contrCons.eliminarConsultaBooleana(nom);
    }

    /**Modifica la consulta identificada pel nom
     * @param nom Nom identificatiu de la consulta
     * @param novaExp Nova expresio
     * @throws ExceptionConsultaNoExisteix Si no existeix cap consulta amb aquell nom
     * @throws ExceptionConsultaIncorrecta Si el format de la consulta es incorrecte
     */
    public void modificaConsultaBooleana(String nom, String novaExp) throws ExceptionConsultaIncorrecta, ExceptionConsultaNoExisteix {
        contrCons.modificarConsultaBooleana(nom, novaExp);
    }

    /**Reanomena la consulta identificada pel nomAntic i el substitueix pel nou
     * @param nomAntic Nom antic de la consulta
     * @param nomNou Nom nou de la consulta
     * @throws ExceptionConsultaNoExisteix Si no existeix cap consulta amb nomAntic
     * @throws ExceptionConsultaJaExisteix Si ja existeix una consulta amb nomNou
     */
    public void reanomenarConsultaBooleana(String nomAntic, String nomNou) throws ExceptionConsultaNoExisteix, ExceptionConsultaJaExisteix {
        contrCons.reanomenarConsultaBooleana(nomAntic, nomNou);
    }

    /**Obte les consultes guardades
     * @return La llista de nom i expresio de les consultes guardades
     */
    public ArrayList<Pair<String, String>> getConsultesGuardades(){
        return contrCons.getConsultesGuardades();
    }

    /**Obte les consultes de l'historial
     * @return La llista de nom i expresio de les consultes de l'historial
     */
    public ArrayList<Pair<String, String>> getHistorialConsultesBooleanes() {
        return contrCons.getHistorial();
    }

    /**Tanca l'aplicacio
     * @throws ExceptionAPersitencia Si hi ha un error de comunicacio amb persistencia
     */
    public void tancarAplicacio() throws ExceptionAPersitencia {
        TreeMap<String, TreeMap<String, LocalDateTime>> idxO = cjtDoc.getIdxObres();
        ArrayList<String> idxA = cjtDoc.llistaAutorsPrefix("");
        List<LinkedHashMap<String, Pair<Double, List<Pes>>>> weights = cjtDoc.getWeights();
        List<List<Double>> norms = cjtDoc.getNorms();
        List<Pair<String,String>> titol_autors = cjtDoc.getTitolAutors();
        ArrayList<Pair<String, String>> expBooleanesHist = contrCons.saveHist();
        ArrayList<Pair<String, String>> expBooleanesGuardades = contrCons.saveGuardades();
        ArrayList<Pair<String, TreeMap<String, TreeMap<String, LocalDateTime>>>> etiq = cjtDoc.getEtiquetes();
        ctrPers.saveIdxObres(idxO);
        ctrPers.saveIdxAutors(idxA);
        ctrPers.saveWeights(weights);
        ctrPers.saveNorms(norms);
        ctrPers.saveTitolAutors(titol_autors);
        ctrPers.saveExpressionsBooleanesHist(expBooleanesHist);
        ctrPers.saveEtiquetes(etiq);
        ctrPers.saveExpressionsBooleanesHist(expBooleanesHist);
        ctrPers.saveExpressionsBooleanesGuardades(expBooleanesGuardades);
    }

    /**Obte el les dades del document identificat per titol, autor
     * @param titol Titol del document
     * @param autor Autor del document
     * @throws ExceptionAPersitencia Si hi ha un error de comunicacio amb persistencia
     * @return Llista amb les dades dels documents
     */
    public ArrayList<String> getInfoDocument(String titol, String autor) throws ExceptionAPersitencia {
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        return ctrPers.getInfoDocument("./documents/" + titol + "_" + autor + ".txt");
    }

    /**Obte els titols i autors de tots els documents registrats
     * @return Parelles titol autor de tots els documents
     */
    public ArrayList<Pair<String, String>> getAllDocuments() {
        return cjtDoc.getAllTitolsAutors();
    }

    /**Retorna ArrayList de titol, autor i data
     * @return ArrayList de titol, autor i data
     */
    public ArrayList<ArrayList<String>> getAllDocumentsData() {
        return cjtDoc.getAllTitolsAutorsData();
    }

    /**Borra tots els documents registrats de les estructures de dades
     */
    public void borrarDocuments() {
        ArrayList idxA = new ArrayList<>();
        TreeMap<String, TreeMap<String, LocalDateTime>> idxO = new TreeMap<>();
        List<LinkedHashMap<String, Pair<Double,List<Pes>>>> weights = new ArrayList<>();
        List<List<Double>> norms = new ArrayList<>();
        List<Pair<String, String>> TA = new ArrayList<>();
        ArrayList<Pair<String, String>> expBooleanesHist = new ArrayList<>();
        ArrayList<Pair<String, String>> expBooleanesGuardades = new ArrayList<>();
        ArrayList<Pair<String, TreeMap<String, TreeMap<String, LocalDateTime>>>> etiq = new ArrayList<Pair<String, TreeMap<String, TreeMap<String, LocalDateTime>>>>();
        cjtDoc.inicialitzarIdxAutors(idxA);
        cjtDoc.inicialitzarIdxObres(idxO);
        cjtDoc.inicialitzarWeights(weights);
        cjtDoc.inicialitzarNorms(norms);
        cjtDoc.inicialitzarTAS(TA);
        cjtDoc.inicialitzarEtiquetes(etiq);
        contrCons.loadHist(expBooleanesHist);
        contrCons.loadGuardades(expBooleanesGuardades);
    }

    /**Obte les frases del document identificat per titol i autor
     * @param titol Titol del document
     * @param autor Autor del document
     * @throws ExceptionAPersitencia Si hi ha un error de comunicacio amb persistencia
     * @return Les frases del document
     */
    public ArrayList<String> getFrasesDocument(String titol, String autor) throws ExceptionAPersitencia {
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        ArrayList<String> info = getInfoDocument(titol, autor);
        String contingut = info.get(2);
        Docu d = new Docu(autor, titol, contingut);
        return d.getFrases();
    }

    /**Comprova si el document identificat per titol, autor existeix
     * @param titol Titol del document
     * @param autor Autor del document
     * @return Cert si existeix o fals si no
     */
    public boolean existeixDocument(String titol, String autor) {
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        return cjtDoc.existeixDocument(titol, autor);
    }

    /**Llista les etiquetes registrades al sistema
     * @return Llista dels noms de les etiquetes
     */
    public ArrayList<String> llistarEtiquetes() {
        return cjtDoc.llistarEtiquetes();
    }

    /**Afegeix una etiqueta al sistema
     * @param nomEtiq Nom de la etiqueta
     */
    public void afegirEtiqueta(String nomEtiq) {
        cjtDoc.afegirEtiqueta(nomEtiq);
    }

    /**Elimina una etiqueta del sistema
     * @param nomEtiq Nom de la etiqueta
     */
    public void eliminarEtiqueta(String nomEtiq) {
        cjtDoc.eliminarEtiqueta(nomEtiq);
    }

    /**Associa una etiqueta a un document
     * @param nomEtiq Nom de la etiqueta
     * @param titol Titol del document
     * @param autor Autor del document
     */
    public void afegirEtiquetaObra(String nomEtiq, String titol, String autor) {
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        cjtDoc.afegirEtiquetaObra(nomEtiq, titol, autor);
    }

    /**Elimina una etiqueta de un document
     * @param nomEtiq Nom de la etiqueta
     * @param titol Titol del document
     * @param autor Autor del document
     */
    public void eliminarEtiquetaObra(String nomEtiq, String titol, String autor) {
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        cjtDoc.eliminarEtiquetaObra(nomEtiq, titol, autor);
    }

    /**Llista les obres que tenen associada la etiqueta pasada per parametre
     * @param nomEtiq Nom de la etiqueta
     * @return Llista de les obres que satisfan la consulta
     */
    public ArrayList<ArrayList<String>> llistarObresEtiqueta(String nomEtiq) {
        ArrayList<Pair<String, String>> llista = cjtDoc.llistarObresEtiqueta(nomEtiq);
        return afegirDataAObra(llista);
    }

    /**Llista les obres que tenen associada alguna de les etiquetes pasades com a parametre
     * @param nomEtiqs Noms de les etiquetes
     * @return Llista de les obres que satisfan la consulta
     */
    public ArrayList<ArrayList<String>> llistarObresEtiquetes(ArrayList<String> nomEtiqs) {
        ArrayList<Pair<String, String>> llista = cjtDoc.llistarObresEtiquetes(nomEtiqs);
        return afegirDataAObra(llista);
    }

    /**Donat un ArrayList de pairs titols i autors retorna un arraylist d'arraylist de titol, autor i data
     * @param obres Identificadors de la obres
     * @return Arraylist d'arraylist de titol, autor i data
     */
    public ArrayList<ArrayList<String>> afegirDataAObra (ArrayList<Pair<String, String>> obres) {
        ArrayList<ArrayList<String>> llista = new ArrayList<>();
        for (Pair<String, String> obra : obres) {
            ArrayList<String> obraData = cjtDoc.afegirDataObra(obra);
            llista.add(obraData);
        }
        return llista;
    }

    /**Llista les etiquetes associades a una obra
     * @param titol Titol de l'obra
     * @param autor Autor de l'obra
     * @return Llista de les etiquetes associades a un document
     */
    public ArrayList<String> getEtiquetesObra (String titol, String autor) {
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        return cjtDoc.getEtiquetesObra(titol, autor);
    }

    /**Eliminem caracters que puguin influir al crear el path. Per exemple la / o :
     * @param nom Paraula a depurar
     * @return Paraula ja depurada
     */
    public String depurarNom (String nom) {
        if (nom != null) {
            nom = nom.replace(":", "");
            nom = nom.replace("/", "");
            nom = nom.replace("\\", "");
            nom = nom.replace("<", "");
            nom = nom.replace(">", "");
            nom = nom.replace("\n", "");
            nom = nom.trim();
        }
        return nom;

    }

    public ArrayList<String> etiquetesAfegibles(String titol, String autor) {
        titol = depurarNom(titol);
        autor = depurarNom(autor);
        return cjtDoc.etiquetesAfegibles(titol, autor);
    }
}
