package persistencia.classes;

import domini.classes.Pes;
import domini.utils.Pair;
import persistencia.excepcions.ExceptionAPersitencia;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

/**Classe ControladorPersistencia. S'encarrega de la comunicacio entre domini i persistencia
 * Es singleton.
 *
 * @author Marc Ordonez
 */
public class ControladorPersistencia {
    /**La instancia singleton. */
    private static ControladorPersistencia single_ctrPers = null;

    /**Creadora buida
     */
    public ControladorPersistencia() {
    }

    /**
     * @return Retorna la instancia singleton de controlador persistencia
     */
    public static ControladorPersistencia getInstance() {
        if (single_ctrPers == null) single_ctrPers = new ControladorPersistencia();
        return single_ctrPers;
    }

    /**Obte la informacio de disc del document demanat
     * @param path Path relatiu al document incloent el nom de l'arxiu i el format
     * @throws ExceptionAPersitencia Si el document no existeix
     * @return ArrayList de String amb 3 paràmetres: titol, autor i contingut
     */
    public ArrayList<String> getInfoDocument(String path) throws ExceptionAPersitencia {
        try {
            if (path.endsWith(".txt")) {
                Parse p = new ParseTXT();
                return p.llegeixDocument(path);
            } else if (path.endsWith(".xml")) {
                Parse p = new ParseXML();
                return p.llegeixDocument(path);
            } else if (path.endsWith(".kitty")) {
                Parse p = new ParseKITTY();
                return p.llegeixDocument(path);
            }
        }
        catch (IOException e){
            throw new ExceptionAPersitencia();
        }
        return new ArrayList<>();
    }

    /**Es descarrega el document identificat per titol i autor a un path concret en el format indicat
     * @param titol Titol del document
     * @param autor Autor del document
     * @param path Path de la ubicacio on volem guardar el document nomes nom carpeta (sense incloure nom arxiu)
     * @param format Format en el que es vol descarregar el document
     * @throws ExceptionAPersitencia Si el document no existeix
     */
    public void descarregarDocument(String titol, String autor, String path,  String format) throws ExceptionAPersitencia {
        try {
            Parse doc = new ParseTXT();
            String contingut = (String) doc.llegeixDocument("./documents/" + titol + "_" + autor + ".txt").get(2);
            if (format.equals(".txt")) {
                Parse p = new ParseTXT();
                p.guardaDocument(path, titol, autor, contingut);
            } else if (format.equals(".xml")) {
                Parse p = new ParseXML();
                p.guardaDocument(path, titol, autor, contingut);
            } else if (format.equals(".kitty")) {
                Parse p = new ParseKITTY();
                p.guardaDocument(path, titol, autor, contingut);
            }
        } catch (IOException e) {
            throw new ExceptionAPersitencia();
        }
    }

    /**Es guarda el document a una carpeta especifica dins de l'aplicacio per poder recuperar-lo per a la següent sessio
     * @param titol Titol del document
     * @param autor Autor del document
     * @param contingut Contingut del document
     */
    public void guardarDocumentAMemoria(String titol, String autor, String contingut) {
        Parse p = new ParseTXT();
        p.guardaDocument("./documents", titol, autor, contingut);
    }

    /**S'elimina el document de la carpeta amb els documents que es va guardant l'aplicació entre sessió i sessió
     * @param titol Titol del document
     * @param autor Autor del document
     * @throws IOException Si el path no es pot resoldre
     */
    public void eliminarDocument(String titol, String autor) throws IOException {
        Path path = FileSystems.getDefault().getPath("./documents/" + titol + "_" + autor + ".txt");
        Files.delete(path);
    }

    /**Guarda un objecte a disc
     * @param o Objecte a guardar
     * @param path Path fins al objecte
     * @throws ExceptionAPersitencia Si el path no es pot resoldre
     */
    public void saveObject (Object o, String path) throws ExceptionAPersitencia {
        try {
            FileOutputStream myFileOutStream = new FileOutputStream(path);
            ObjectOutputStream myObjectOutStream = new ObjectOutputStream(myFileOutStream);
            myObjectOutStream.writeObject(o);
            myObjectOutStream.close();
            myFileOutStream.close();
        } catch (IOException e) {
            throw new ExceptionAPersitencia();
        }
    }

    /**Carrega un objecte de disc
     * @param path Path fins al objecte
     * @throws ExceptionAPersitencia Si el path no es pot resoldre
     * @return L'objecte carregat
     */
    public Object loadObject(String path) throws ExceptionAPersitencia {
        try {
            FileInputStream fileInput = new FileInputStream(path);
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            Object info = (Object) objectInput.readObject();
            objectInput.close();
            fileInput.close();
            return info;
        } catch (IOException | ClassNotFoundException e) {
            throw new ExceptionAPersitencia();
        }

    }

    /**Guarda l'index d'obres
     * @param info Informacio de l'index a guardar
     * @throws ExceptionAPersitencia Si el path no es pot resoldre
     */
    public void saveIdxObres(TreeMap<String, TreeMap<String, LocalDateTime>> info) throws ExceptionAPersitencia {
        saveObject(info, "./infoSessio/idxObres.txt");
    }

    /**Carrega l'index d'obres
     * @return L'estructura d'index obres
     */
    public TreeMap<String, TreeMap<String, LocalDateTime>> loadIdxObres() {
        TreeMap<String, TreeMap<String, LocalDateTime>> idx;
        try {
            idx = (TreeMap<String, TreeMap<String, LocalDateTime>>) loadObject("./infoSessio/idxObres.txt");
        } catch (ExceptionAPersitencia e) {
            idx = new TreeMap<>();
        }
        return idx;
    }

    /**Guarda l'estructura d'index autors
     * @param autors Llista d'autors a guardar
     * @throws ExceptionAPersitencia Si no es pot resoldre el path
     */
    public void saveIdxAutors (ArrayList<String> autors) throws ExceptionAPersitencia {
        saveObject(autors, "./infoSessio/idxAutors.txt");
    }

    /**Carrega l'estructura d'index autors de disc
     * @return La llista d'autors
     */
    public ArrayList loadIdxAutors () {
        comprovaQueExisteixenCarpetes();
        ArrayList<String> idx;
        try {
            idx = (ArrayList<String>) loadObject("./infoSessio/idxAutors.txt");
        } catch (ExceptionAPersitencia e) {
            idx = new ArrayList<>();
        }
        return idx;
    }

    /**Funcio privada per comprovar l'existencia de les carpetes infoSesio i documents
     * necessaries per al bon funcionament del programa
     */
    private void comprovaQueExisteixenCarpetes() {
        try {
            Files.createDirectories(Paths.get("./infoSessio"));
            Files.createDirectories(Paths.get("./documents"));
        } catch (IOException ignored) {
        }
    }

    /**Guarda l'estructura d'historial de consutes booleanes
     * @param info Estructura de l'historial de consultes
     * @throws ExceptionAPersitencia Si no es pot resoldre el path
     */
    public void saveExpressionsBooleanesHist(ArrayList<Pair<String, String>> info) throws ExceptionAPersitencia {
        saveObject(info, "./infoSessio/expressionsBooleanesHist.txt");
    }

    /**Guarda l'estructura de consutes booleanes guardades
     * @param info Estructura de consultes guardades
     * @throws ExceptionAPersitencia Si no es pot resoldre el path
     */
    public void saveExpressionsBooleanesGuardades(ArrayList<Pair<String, String>> info) throws ExceptionAPersitencia {
        saveObject(info, "./infoSessio/expressionsBooleanesGuardades.txt");
    }

    /**Carrega l'estructura d'historial de consutes booleanes de disc
     * @return L'estructura d'historial de consultes booleanes
     */
    public ArrayList<Pair<String, String>> loadExpressionsBooleanesHist() {
        ArrayList<Pair<String, String>> expr;
        try {
            expr = (ArrayList<Pair<String, String>>) loadObject("./infoSessio/expressionsBooleanesHist.txt");
        } catch (ExceptionAPersitencia e) {
            expr = new ArrayList<>();
        }
        return expr;
    }

    /**Carrega l'estructura de consultes booleanes guardades de disc
     * @return L'estructura de consultes booleanes guardades a disc
     */
    public ArrayList<Pair<String, String>> loadExpressionsBooleanesGuardades() {
        ArrayList<Pair<String, String>> expr;
        try {
            expr = (ArrayList<Pair<String, String>>) loadObject("./infoSessio/expressionsBooleanesGuardades.txt");
        } catch (ExceptionAPersitencia e) {
            expr = new ArrayList<>();
        }
        return expr;
    }

    /**Guarda l'estructura de pesos de l'espai vectorial a disc
     * @param weights Estructura de pesos de l'espai vectorial
     * @throws ExceptionAPersitencia Si no es pot resoldre el path
     */
    public void saveWeights(List<LinkedHashMap<String, Pair<Double,List<Pes>>>> weights) throws ExceptionAPersitencia {
        saveObject(weights, "./infoSessio/EV_weights.txt");
    }  

    /**Carrega l'estructura de pesos de l'espai vectorial a disc
     * @return L'estructura de pesos de l'espai vectorial
     */
    public List<LinkedHashMap<String, Pair<Double,List<Pes>>>> loadWeights() {
        List<LinkedHashMap<String, Pair<Double, List<Pes>>>> weights;
        try {
            weights = (List<LinkedHashMap<String, Pair<Double,List<Pes>>>>) loadObject("./infoSessio/EV_weights.txt");
        } catch (ExceptionAPersitencia e) {
            weights = new ArrayList<>();        }
        return weights;
    }

    /**Guarda l'estructura de normes a disc, part de l'espai vectorial
     * @param norms Estructura de normes de l'espai vectorial
     * @throws ExceptionAPersitencia Si no es pot resoldre el path
     */
    public void saveNorms(List<List<Double>> norms) throws ExceptionAPersitencia {
        saveObject(norms, "./infoSessio/EV_norms.txt");
    }

    /**Carrega l'estructura de normes de disc, part de l'espai vectorial
     * @return Estructura de normes de l'espai vectorial
     */
    public List<List<Double>> loadNorms() {
        List<List<Double>> norms;
        try {
            norms = (List<List<Double>>) loadObject("./infoSessio/EV_norms.txt");
        } catch (ExceptionAPersitencia e) {
            norms = new ArrayList<>();
        }
        return norms;
    }

    /**Guarda l'estructura de identificadors a disc, part de l'espai vectorial
     * @param titol_autors Titol i autor de les obres registrades a l'espai
     * @throws ExceptionAPersitencia Si no es pot resoldre el path
     */
    public void saveTitolAutors(List<Pair<String, String>> titol_autors) throws ExceptionAPersitencia {
        saveObject(titol_autors, "./infoSessio/EV_titol_autors.txt");
    }

    /**Carrega l'estructura de identificadors de disc, part de l'espai vectorial
     * @return titol_autors Titol i autor de les obres registrades a l'espai
     */
    public List<Pair<String, String>> loadTitolAutors() {
        List<Pair<String, String>> TA;
        try {
            TA = (List<Pair<String, String>>) loadObject("./infoSessio/EV_titol_autors.txt");
        } catch (ExceptionAPersitencia e) {
            TA = new ArrayList<>();
        }
        return TA;
    }

    /**Guarda l'estructura de etiquetes a disc
     * @param etiquetes ArrayList de Pairs. Cada Pair és nom de l'etiqueta (left) i obres que estan en aquesta etiqueta (right). Les obres
     * que tenen una etiqueta es guardaran com un TreeMap amb Key autor i Value totes les obres d'aquell autor que
     * tinguin l'etiqueta.
     * @throws ExceptionAPersitencia Si no es pot resoldre el path
     */
    public void saveEtiquetes(ArrayList<Pair<String, TreeMap<String, TreeMap<String, LocalDateTime>>>> etiquetes) throws ExceptionAPersitencia {
        saveObject(etiquetes, "./infoSessio/etiquetes.txt");
    }

    /**Carrega l'estructura d'etiquetes de disc
     * @return Estructura d'etiquetes de disc
     */
    public ArrayList<Pair<String, TreeMap<String, TreeMap<String, LocalDateTime>>>> loadEtiquetes() {
        ArrayList<Pair<String, TreeMap<String, TreeMap<String, LocalDateTime>>>> etiquetes;
        try {
            etiquetes = (ArrayList<Pair<String, TreeMap<String, TreeMap<String, LocalDateTime>>>>) loadObject("./infoSessio/etiquetes.txt");
        } catch (ExceptionAPersitencia e) {
            etiquetes = new ArrayList<>();
        }
        return etiquetes;
    }

    /**Llegeix el fitxer d'stopwords de disc
     * @return Un String amb totes les stopwords
     */
    public String llegirStopWords() {
        String llista = new String();
        File myObj = new File("./FONTS/domini/utils/stopwords.txt");
        File myObj2 = new File("./domini/utils/stopwords.txt");
        Scanner myReader = null;
        try {
            myReader = new Scanner(myObj);
        } catch (FileNotFoundException e) {
            try {
                myReader = new Scanner(myObj2);
            } catch (FileNotFoundException ex) {
                return "";
            }
        }
        while (myReader.hasNextLine()) {
            llista += "\n" + myReader.nextLine();
        }
        myReader.close();
        //System.out.println(llista);
        return llista;
    }
}
