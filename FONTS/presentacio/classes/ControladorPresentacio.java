package presentacio.classes;

import domini.classes.ControladorDomini;
import domini.excepcions.*;
import domini.utils.Pair;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import persistencia.excepcions.ExceptionAPersitencia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

/**Classe ControladorPresentacio. S'encarrega de redirigir operacions i comunicar la capa de
 * presentacio amb domini
 * @author laura.hui.perez
 */
public class ControladorPresentacio {

    /** Instancia controlador presentacio */
    private static ControladorPresentacio controladorPresentacio;
    /** Instancia controlador domini */
    private static ControladorDomini controladorDomini;
    /** Pila per saber la vista anterior */
    private final Stack<String> pilaEnrere;
    /** Contenidor auxiliar per la vista anterior */
    private Stage auxStage;

    //----------------------METODES---------------------//
    /**Creadora buida
     */
    private ControladorPresentacio() {

        controladorDomini = ControladorDomini.getInstance();
        controladorDomini.inicialitzar();

        pilaEnrere = new Stack<>();
        pilaEnrere.add("menu");
    }

    /**
     * @return Retorna la instancia singleton de controlador presentacio
     */
    public static ControladorPresentacio getSingletonInstance() {

        if(controladorPresentacio == null) {
            controladorPresentacio = new ControladorPresentacio();
        }

        return controladorPresentacio;
    }


    //-----------------Aplicacio i vistes------------------------//

    /**Envia la peticio de tancar l'aplicacio
     * @throws ExceptionAPersitencia Si es produeix un error de comunicacio
     */
    public void tancarAplicacio() throws ExceptionAPersitencia {
        controladorDomini.tancarAplicacio();
    }

    /**Carrega la vista d'Expressions Booleanes
     * @param stage Contenidor de la vista
     */
    public void carregaVistaBool(Stage stage) {

        auxStage = stage;

        pilaEnrere.add("bool");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("GestorConsultesBooleanes.fxml"));

        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GestorConsultesBooleanes gd = loader.getController();
        gd.carregaConsultesBooleanes();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**Carrega la vista de Pagina Principal
     * @param stage Contenidor de la vista
     */
    public void carregaVistaPagPrincipal(Stage stage){
        auxStage = stage;
        pilaEnrere.clear();
        pilaEnrere.add("menu");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("PagPrincipal.fxml"));

        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PagPrincipal pp = loader.getController();
        pp.carregaPaginaPrincipal();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

    }

    /**Carrega la vista de ModificarDocument a l'apartat de nou document
     * @param stage Contenidor de la vista
     */
    public void carregaNouDocument(Stage stage) {

        auxStage = stage;
        pilaEnrere.add("modifDoc");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ModificarDocument.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ModificarDocument md = loader.getController();

        md.carregaDoc("","","");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**Carrega la vista de ModificarDocument
     * @param titol Titol del document
     * @param autor Autor del document
     * @param stage Contenidor de la vista
     */
    public void carregaVistaModifDoc(String titol, String autor, Stage stage){

        auxStage = stage;
        pilaEnrere.add("modifDoc");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("ModificarDocument.fxml"));

        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String contingut = "";
        try {
            contingut = mostraContingut(titol,autor);
        } catch (ExceptionAutorNoExisteix | ExceptionObraNoExisteix | ExceptionAPersitencia e) {
            //throw new RuntimeException(e);
        }

        ModificarDocument md = loader.getController();
        md.carregaDoc(titol,autor,contingut);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**Carrega la vista de GestorDocuments
     * @param stage Contenidor de la vista
     */
    public void carregaVistaGestorDoc(Stage stage) {

        auxStage = stage;

        pilaEnrere.add("gestor");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("GestorDocuments.fxml"));

        Parent root;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GestorDocuments gd = loader.getController();
        gd.carregaGestorDocuments();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**Funcio per tornar a la vista anterior
     */
    public void tornaEnrere() {

        pilaEnrere.pop();

        while(pilaEnrere.peek().equals("modifDoc")) pilaEnrere.pop();
        String darreraVista = pilaEnrere.pop();

        switch (darreraVista) {
            case "bool":
                carregaVistaBool(auxStage);
                break;
            case "gestor":
                carregaVistaGestorDoc(auxStage);
                break;
            case "modifDoc":
                carregaNouDocument(auxStage);
                break;
            default:
                carregaVistaPagPrincipal(auxStage);
                break;
        }
    }


    //-------------Gestio de documents------------------//
    /**Crea un document amb autor, titol i contingut pasats com a parametre
     * @param autor Autor del document
     * @param titol Titol del document
     * @param contingut Contingut del document
     * @throws ExceptionObraJaExisteix Si un document amb els mateixos identificadors ja existeix
     * @throws ExceptionAutorBuit Si el camp de document esta buit
     * @throws ExceptionTitolBuit Si el camp de titol esta buit
     */
    public void creaDocument(String titol, String autor, String contingut) throws ExceptionObraJaExisteix, ExceptionAutorBuit, ExceptionTitolBuit {
        controladorDomini.creaDocument(autor, titol, contingut);
    }

    /**Modifica un document amb autor, titol pasats com a parametres que l'identifiquen
     * i els modifica per els nous
     * @param autor Autor del document
     * @param titol Titol del document
     * @param nouAutor Nou autor del document
     * @param nouTitol Nou titol del document
     * @param nouContingut Nou contingut del document
     * @throws ExceptionObraJaExisteix Si el document amb els nous parametres ja existia
     * @throws ExceptionAutorNoExisteix Si l'autor original no existia
     * @throws ExceptionObraNoExisteix Si l'obra original no existia
     */
    public void modificaDocument(String titol, String autor, String nouTitol, String nouAutor, String nouContingut) throws ExceptionObraJaExisteix, ExceptionAutorNoExisteix, ExceptionObraNoExisteix {
        controladorDomini.modificarDocument(autor,titol,nouAutor,nouTitol, nouContingut);
    }

    /**Elimina un document amb autor, titol com parametre que l'identifica
     * i els modifica per els nous
     * @param autor Autor del document
     * @param titol Titol del document
     * @throws ExceptionAutorNoExisteix Si l'autor original no existia
     * @throws ExceptionObraNoExisteix Si l'obra original no existia
     * @throws IOException Exepcio de Java
     */
    public void eliminaDocument(String titol, String autor) throws ExceptionAutorNoExisteix, ExceptionObraNoExisteix, IOException {
        controladorDomini.eliminarDocument(autor, titol);
    }


    //-------------------Consultes----------------------//
    /**Llista els titols de l'autor passat com a parametre
     * @param autor Autor del document
     * @return La llista dels titols de l'autor consultat
     * @throws ExceptionAutorNoExisteix Si l'autor original no existia
     */
    public ArrayList<String> llistarTitols(String autor) throws ExceptionAutorNoExisteix {
        return controladorDomini.llistaTitols(autor);
    }

    /**Llista els autors que el seu con comença pel prefix passat com a parametre
     * @param prefix Prefix a evaluar
     * @return Llista dels autors que satisfan la consulta
     */
    public ArrayList<String> llistarAutors(String prefix){
        return controladorDomini.llistaAutorsPrefix(prefix);
    }

    /**Retorna el contingut del document demanat, amb autor i titol com a identificadors
     * @param autor Autor del document
     * @param titol Titol del document
     * @throws ExceptionAPersitencia Si el document no existeix
     * @throws ExceptionAutorNoExisteix Si l'autor passat com a parametre no existeix
     * @throws ExceptionObraNoExisteix Si el titol de l'obra no esta registrat
     * @return El contingut del document
     */
    public String mostraContingut(String titol, String autor) throws ExceptionAPersitencia, ExceptionAutorNoExisteix, ExceptionObraNoExisteix {
        return controladorDomini.mostraContingut(autor, titol);
    }

    /**Realitza la consulta de documents semblants respecte al document identificat pels
     * parametres autor i titol
     * @param tipus Determina si la consulta es per similaritat o rellevancia
     * @param k Indica el nombre de documents a retornar
     * @param autor Autor del document
     * @param titol Titol del document
     * @throws ExceptionAPersitencia Si el document no existeix
     * @return El titol i l'autor dels documents resultat de la consulta en ordre de semblança
     */
    public ArrayList<ArrayList<String>> kDocumentsSemblants(String tipus, int k, String titol, String autor) throws ExceptionAPersitencia {
        return controladorDomini.kDocumentsSemblants(tipus, k, autor, titol);
    }

    /**Realitza la consulta de documents rellevants respecte al conjunt de paraules identificat pels
     * parametres autor i titol
     * @param tipus Determina si la consulta es per similaritat o rellevancia
     * @param k Indica el nombre de documents a retornar
     * @param paraules Conjunt de paraules sobre la que fer la cerca
     * @return El titol i l'autor dels documents resultat de la consulta en ordre de rellevancia
     */
    public ArrayList<ArrayList<String>> kDocumentsRellevants(String tipus, int k, ArrayList<String> paraules){
        return controladorDomini.kDocumentsRellevants(tipus, k, paraules);
    }

    /**Retorna ArrayList de títol, autor i data
     * @return ArrayList de títol, autor i data
     */
    public ArrayList<ArrayList<String>> getAllDocumentsData(){
        return controladorDomini.getAllDocumentsData();
    }


    //----------------------Persistencia--------------------------------//

    /**Pasa la peticio a domini de carregar un document, que es pasara a persistencia posteriorment
     * @param path Identificador del document
     * @return Llista amb els parametres del document
     * @throws ExceptionAPersitencia Si es produeix un error de comunicacio
     */
    public ArrayList<String> carregarDocument(String path) throws ExceptionAPersitencia {
        return controladorDomini.llegirDocumentSistema(path);
    }

    /**Demana descarregar el document en format .txt
     * @param titol Titol del document
     * @param autor Autor del document
     * @param path Identificador del document
     * @throws ExceptionAPersitencia Si es produeix un error de comunicacio
     */
    public void baixaDocumentTxt(String titol, String autor, String path) throws ExceptionAPersitencia {
        controladorDomini.descarregarDocumentASistema(titol,autor,path,".txt");
    }

    /**Demana descarregar el document en format .xml
     * @param titol Titol del document
     * @param autor Autor del document
     * @param path Identificador del document
     * @throws ExceptionAPersitencia Si es produeix un error de comunicacio
     */
    public void baixaDocumentXML(String titol, String autor, String path) throws ExceptionAPersitencia {
        controladorDomini.descarregarDocumentASistema(titol,autor,path,".xml");
    }

    /**Demana descarregar el document en format .kitty
     * @param titol Titol del document
     * @param autor Autor del document
     * @param path Identificador del document
     * @throws ExceptionAPersitencia Si es produeix un error de comunicacio
     */
    public void baixaDocumentKitty(String titol, String autor, String path) throws ExceptionAPersitencia {
        controladorDomini.descarregarDocumentASistema(titol,autor,path,".kitty");
    }


    //----------------------Etiquetes--------------------------------//


    /**Afegeix una etiqueta al sistema
     * @param et Nom de la nova etiqueta
     */
    public void afegirEtiqueta(String et){
        controladorDomini.afegirEtiqueta(et);
    }

    /**Elimina l'etiqueta amb nom nomEtiqueta del sistema
     * @param nomEtiqueta Nom de la etiqueta
     */
    public void eliminaEtiqueta(String nomEtiqueta){
        controladorDomini.eliminarEtiqueta(nomEtiqueta);
    }

    /**Obte les etiquetes associades d'una obra identificada pels parametres titol, autor
     * @param titol Titol del document
     * @param autor Autor del document
     * @return Noms de les etiquetes associades
     */
    public ArrayList<String> getEtiquetesDocument(String titol, String autor){
        return controladorDomini.getEtiquetesObra(titol, autor);
    }

    /**Obte les etiquetes que se li poden afegir al document identificat amb titol i autor
     *
     * @param titol el titol del document
     * @param autor el nom de l'autor del document
     * @return Les etiquetes afegibles
     */
    public ArrayList<String> etiquetesAfegibles(String titol, String autor){
        return controladorDomini.etiquetesAfegibles(titol,autor);
    }

    /**Associa l'etiqueta pasada com a parametre al document identificat per titol i autor
     * @param titol Titol del document
     * @param autor Autor del document
     * @param etiqueta Etiqueta a associar
     */
    public void afegeixEtiquetaObra(String titol, String autor, String etiqueta){
        controladorDomini.afegirEtiquetaObra(etiqueta,titol,autor);
    }

    /**Elimina l'etiqueta d'una obra identificada pels parametres titol, autor
     * @param etiqueta Nom de l'etiqueta
     * @param titol Titol del document
     * @param autor Autor del document
     */
    public void eliminarEtiquetaObra(String etiqueta, String titol, String autor){
        controladorDomini.eliminarEtiquetaObra(etiqueta,titol,autor);
    }

    /**Llista les etiquetes registrades al sistema
     * @return Els noms de les etiquetes registrades al sistema
     */
    public ArrayList<String> llistarEtiquetes(){
        return controladorDomini.llistarEtiquetes();
    }

    /**Llista les obres que tenen associada alguna de les etiquetes pasades com a parametre
     * @param ets Etiquetes a consultar
     * @return Obres que satisfan la consulta
     */
    public ArrayList<ArrayList<String>> llistarObresEtiquetes(ArrayList<String> ets){
        return controladorDomini.llistarObresEtiquetes(ets);
    }




    //----------------------Consultes Booleanes--------------------------------//

    /**Retorna l'historial de consultes booleanes
     * @return Historial de consultes booleanes
     */
    public ArrayList<Pair<String, String>> getHistorial(){
        ArrayList<Pair<String, String>> historial;
        historial = controladorDomini.getHistorialConsultesBooleanes();
        Collections.reverse(historial);
        return historial;
    }

    /**Retorna les consultes booleanes guardades
     * @return Les consultes booleanes guardades amb el seu nom i expressio
     */
    public ArrayList<Pair<String, String>> getGuardades(){
        return controladorDomini.getConsultesGuardades();
    }

    /**Fa una consulta booleana indicant el nom que l'identifica
     * @param nom Nom de la consulta
     * @return Els titols i autors dels documents que satisfan la consulta
     * @throws ExceptionConsultaNoExisteix Si la consulta no esta emmagatzemada
     */
    public ArrayList<Pair<String, String>> consultaBooleanaNom(String nom) throws ExceptionConsultaNoExisteix {
        return controladorDomini.ferConsultaBooleanaPerNom(nom);
    }

    /**Fa una consulta booleana indicant l'expresio pel parametre
     * @param exp Expresio de la consulta
     * @return Els titols i autors dels documents que satisfan la consulta
     * @throws ExceptionConsultaIncorrecta Si la consulta te un format incorrecte
     */
    public ArrayList<Pair<String, String>> consultaBooleanaExp(String exp) throws ExceptionConsultaIncorrecta {
        return controladorDomini.ferConsultaBooleanaPerExp(exp);
    }

    /**Modifica el nom d'una consulta per un de nou
     * @param nouNom Nou nom de la consulta
     * @param anticNom Nom antic de la consulta
     * @throws ExceptionConsultaJaExisteix Si ja existeix una consulta amb el nouNom
     * @throws ExceptionConsultaNoExisteix Si no existeix cap consulta amb acticNom
     */
    public void modificaNomConsulta(String nouNom, String anticNom) throws ExceptionConsultaJaExisteix, ExceptionConsultaNoExisteix {
        controladorDomini.reanomenarConsultaBooleana(anticNom, nouNom);
    }

    /**Assigna un nom per poder guardar una consulta pasada com a parametre
     * @param nom Nom de la consulta
     * @param exp Expresio de la consulta
     * @throws ExceptionConsultaJaExisteix Si ja existeix una consulta amb el mateix nom
     * @throws ExceptionConsultaIncorrecta Si el format de la consulta es incorrecte
     */
    public void posarNomConsulta(String nom, String exp) throws ExceptionConsultaJaExisteix, ExceptionConsultaIncorrecta {
        controladorDomini.guardarConsultaBooleana(nom,exp);
    }

    /**Guarda la consulta booleana amb el nom indicat com a parametre
     * @param nom Nom de l'expresio booleana a guardar
     * @param exp Expressio a guardar
     * @throws ExceptionConsultaJaExisteix Si ja existeix la consulta a l'estructura de consultes guardades
     * @throws ExceptionConsultaIncorrecta Si el format de la consulta es incorrecte
     */
    public void guardarConsultaBooleana(String nom, String exp) throws ExceptionConsultaJaExisteix, ExceptionConsultaIncorrecta {
        controladorDomini.guardarConsultaBooleana(nom, exp);
    }

    /**Elimina la consulta booleana amb l'expresio indicada com a parametre
     * @param exp Expressio a borrar
     * @throws ExceptionConsultaNoExisteix Si la consulta no esta registrada al sistema
     */
    public void eliminarConsultaBooleana(String exp) throws ExceptionConsultaNoExisteix {
        controladorDomini.eliminaConsultaBooleana(exp);
    }

    /**Funcio que es crida cada cop que s'afegeix / elimina / modifica un document.
     * Crea un thread que actualitza en segon pla totes les consultes booleanes que teniem guardades i l'historial.
     * Primer posa tots els resultats a {@code null} i despres els recalcula.
     */
    public void actualitzarConsultesBooleanes() {
        controladorDomini.actualitzarConsultesBooleanes();
    }
}

