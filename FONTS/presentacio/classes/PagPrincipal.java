package presentacio.classes;

import domini.excepcions.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import persistencia.excepcions.ExceptionAPersitencia;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**Classe PaginaPrincipal. Vista principal per presentar les opcions
 * principals a l'usuari
 * @author laura.hui.perez
 */
public class PagPrincipal {

    /**Obte la instancia singleton de controlador presentacio
     */
    private static ControladorPresentacio controladorPresentacio;
    /** Identificador del label de benvinguda */
    @FXML
    Label benvingudaLabel;
    /** Identificador del boto nou document */
    @FXML
    Button nouDocButton;
    /** Identificador del boto importa document */
    @FXML
    Button obreDocButton;
    /** Identificador del boto per carregar el gestor de documents */
    @FXML
    Button gestorDocButton;

    /** Contenidor de la vista */
    private Stage stage;
    
    /**Obte la instancia singleton de controlador presentacio
     */
    {
        controladorPresentacio = ControladorPresentacio.getSingletonInstance();
    }

    /**Creadora buida
     */
    public PagPrincipal() {

    }

    /**Carrega la pagina principal
     */
    public void carregaPaginaPrincipal(){
        benvingudaLabel.setText("Ens alegrem de veure't " + System.getProperty("user.name") + "!");
    }

    /**Carrega de la vista de nou document
     * @param event Event per controlar l'accio
     */
    public void nouDoc(ActionEvent event){

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        controladorPresentacio.carregaNouDocument(stage);

    }

    /**Carrega de la vista gestor documents
     * @param event Event per controlar l'accio
     */
    public void gestorDoc(ActionEvent event) {

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        controladorPresentacio.carregaVistaGestorDoc(stage);

    }

    /**Obre un document
     * @param event Event per controlar l'accio
     */
    public void obreDoc(ActionEvent event) {

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Kitty files (*.txt, *.xml, *.kitty)", "*.txt", "*.xml", "*.kitty");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open Resource File");
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        List<File> selectedFile = fileChooser.showOpenMultipleDialog(stage);

        boolean sobreescrit = true;
        boolean error = false;

        if(selectedFile != null) {

            for (File f : selectedFile) {

                String docPath = f.getAbsolutePath();
                ArrayList<String> res = null;

                try {
                    res = controladorPresentacio.carregarDocument(docPath);
                } catch (ExceptionAPersitencia e) {
                    errorPersistencia();
                }

                if (res != null) {

                    String titol = res.get(0);
                    String autor = res.get(1);
                    String contingut = res.get(2);

                    if(contingut == null) contingut = "";

                    try {
                        controladorPresentacio.creaDocument(titol, autor, contingut);
                    } catch (ExceptionObraJaExisteix e) {

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("El document ja existeix");
                        alert.setHeaderText("El document amb el títol " + titol + " i autor " + autor + " ja existeix,\nVols sobreescriure el document?");

                        ButtonType sobreescriu = new ButtonType("Sobreescriu");
                        ButtonType buttonTypeCancel = new ButtonType("Cancel·la", ButtonBar.ButtonData.CANCEL_CLOSE);

                        alert.getButtonTypes().setAll(sobreescriu, buttonTypeCancel);

                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.get() == sobreescriu) {
                            try {
                                controladorPresentacio.modificaDocument(titol, autor, titol, autor, contingut);
                            } catch (ExceptionObraJaExisteix ex) {
                                errorObraJaExisteix();
                                error = true;
                            } catch (ExceptionAutorNoExisteix ex) {
                                errorAutorNoExisteix();
                                error = true;
                            } catch (ExceptionObraNoExisteix ex) {
                                errorObraNoExisteix();
                                error = true;
                            }
                        }
                        else if (result.get() == buttonTypeCancel){
                            sobreescrit = false;
                        }

                    } catch (ExceptionAutorBuit e) {
                        errorAutorBuit();
                        error = true;
                    } catch (ExceptionTitolBuit e) {
                        errorTitolBuit();
                        error = true;
                    }

                    if (selectedFile.size() == 1 && sobreescrit && !error) {

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Modificar Document");
                        alert.setHeaderText("Document carregat al sistema, vols editar el document?");

                        ButtonType sobreescriu = new ButtonType("Edita");
                        ButtonType buttonTypeCancel = new ButtonType("Cancel·la", ButtonBar.ButtonData.CANCEL_CLOSE);

                        alert.getButtonTypes().setAll(sobreescriu, buttonTypeCancel);

                        Optional<ButtonType> result = alert.showAndWait();

                        if (result.get() == sobreescriu) {

                            controladorPresentacio.actualitzarConsultesBooleanes();
                            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            controladorPresentacio.carregaVistaModifDoc(titol, autor, stage);

                        }

                    }

                }
            }
            if(selectedFile.size() > 1 && !error){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("S'han carregat tots els documents correctament!");
                alert.setTitle("Carregat");
                alert.showAndWait();

            }
        }

        controladorPresentacio.actualitzarConsultesBooleanes();

    }


        //----------------- errors -------------


    /** Avisa de si s'ha produit un error a persistencia
     */
    public void errorPersistencia() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Hi ha hagut un error al trobar el document, consulta amb els desenvolupadors per arreglar-lo");
        alert.setTitle("Error");
        alert.showAndWait();
    }

    /** Alerta, l'obra ja existeix
     */
    public void errorObraJaExisteix() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Aquest autor ja te una obra amb aquest titol");
        alert.setTitle("Error obra ja existeix");
        alert.showAndWait();
    }

    /** Alerta, l'autor no existeix
     */
    public void errorAutorNoExisteix() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("No existeix aquest autor");
        alert.setTitle("Error autor no existeix");
        alert.showAndWait();
    }

    /** Alerta, l'obra no existeix
     */
    public void errorObraNoExisteix() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("No existeix aquesta obra");
        alert.setTitle("Error obra no existeix");
        alert.showAndWait();
    }

    /** Alerta, l'obra no existeix
     */
    public void errorAutorBuit() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Error autor buit");
        alert.setTitle("Error autor buit");
        alert.showAndWait();
    }
    /** Alerta, el titol del document és buit
     */
    public void errorTitolBuit(){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText("Error titol buit");
        alert.setTitle("Error titol buit");
        alert.showAndWait();
    }


    }

