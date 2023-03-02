package presentacio.classes;

import domini.excepcions.*;
import domini.utils.Pair;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import persistencia.excepcions.ExceptionAPersitencia;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;

/**Classe GestorConsultesBooleanes. Vista dedicada a la gestio de consultes booleanes i a fer-les
 * @author laura.hui.perez
 */
public class GestorConsultesBooleanes {

    /**Obte la instancia singleton de controlador presentacio
     */
    ControladorPresentacio controladorPresentacio;

    {
        controladorPresentacio = ControladorPresentacio.getSingletonInstance();
    }
    /** Identificador barra de menu */
    @FXML
    MenuBar myMenuBar;
    /** Identificador label per l'input */
    @FXML
    TextField input;
    /** Identificador label per l'historial guardat de consultes booleanes */
    @FXML
    Label historialIGuardatLabel;
    /** Identificador llista de consultes per seleccionar */
    @FXML
    ListView llistaConsultes;
    /** Identificador llista dels documents com a resultat de consultes */
    @FXML
    ListView llistaDocuments;
    /** Indentificador tipus */
    @FXML
    Label tipusLabel;
    /** Identificador selector nom*/
    @FXML
    RadioMenuItem nomMode;
    /** Identificador selector exp*/
    @FXML
    RadioButton historialRadio;
    /** Identificador boto per desar*/
    @FXML
    Button desarButton;
    /** Identificador boto nombrar*/
    @FXML
    Button nombraButton;
    /** Identificador boto eliminar*/
    @FXML
    Button eliminaButton;
    /** Identificador boto desLater*/
    @FXML
    Button desaLateraButton;

    /** Contenidor de la vista */
    private Stage stage;

    /** Consulta seleccionada*/
    String consultaSeleccionada;
    /** Documents resultat d'una consulta*/
    ArrayList<Pair<String,String>> resultatDocuments;
    /** Resultat consulta*/
    ArrayList<Pair<String,String>> resultatConsultes;
    /** Listener de consultes */
    ChangeListener consultesListener;
    /** Listener de document*/
    ChangeListener documentsListener;

    /** Opcio consultes historial */
    Callback<ListView,ListCell> opcionsConsultesHistorial;
    /** Opcio consultes desades */
    Callback<ListView,ListCell> opcionsConsultesDesades;
    /** Opcio documents */
    Callback<ListView,ListCell> opcionsDocuments;

    /** Expresio selecionada */
    String expSeleccionada;
    /** Nom seleccionat */
    String nomSeleccionat;
    /** Autor seleccionat */
    String autorSeleccionat;
    /** Titol seleccionat */
    String titolSeleccionat;

    /** Event pel dobleclic en documents */
    EventHandler<MouseEvent> dobleClickDocs;
    /** Event pel dobleclic en consultes */
    EventHandler<MouseEvent> dobleClickConsultes;


    /** Funcio gestora dels elements de la interficie i la comunicacio amb les accions a realitzar
    */
    public GestorConsultesBooleanes() {

        consultesListener = (ChangeListener<String>) (arg0, arg1, arg2) -> {

            int idx = llistaConsultes.getSelectionModel().getSelectedIndex();

            if(idx >= 0) {

                Pair<String, String> cons = resultatConsultes.get(idx);
                nomSeleccionat = cons.left();
                expSeleccionada = cons.right();

                if(nomMode.isSelected()) input.setText(nomSeleccionat);
                else  input.setText(expSeleccionada);

                if(historialRadio.isSelected()) desarButton.setDisable(false);
                else {
                    eliminaButton.setDisable(false);
                    nombraButton.setDisable(false);
                }

            }
            else{
                desarButton.setDisable(true);
                eliminaButton.setDisable(true);
                nombraButton.setDisable(true);
            }
        };

        documentsListener = (ChangeListener<String>) (arg0, arg1, arg2) -> {

            int idx = llistaDocuments.getSelectionModel().getSelectedIndex();

            if(idx >= 0){

                Pair<String,String> doc = resultatDocuments.get(idx);
                titolSeleccionat = doc.left();
                autorSeleccionat = doc.right();

            }

        };

        opcionsDocuments = lv -> {

            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();


            MenuItem mostraContingut = new MenuItem();
            mostraContingut.textProperty().bind(Bindings.format("Mostra contingut"));
            mostraContingut.setOnAction(event -> {
                try{

                    String c  = controladorPresentacio.mostraContingut(titolSeleccionat, autorSeleccionat);

                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Contingut");
                    dialog.setResizable(true);

                    TextArea contingutArea = new TextArea();
                    contingutArea.setText(c);
                    contingutArea.setWrapText(true);
                    contingutArea.setEditable(false);

                    dialog.getDialogPane().setContent(contingutArea);

                    ButtonType type = new ButtonType("Surt", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().add(type);
                    dialog.showAndWait();

                } catch (ExceptionAutorNoExisteix e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Autor no existeix");
                    alert.setTitle("Autor no existeix");
                    alert.showAndWait();
                } catch (ExceptionObraNoExisteix e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Obra no existeix");
                    alert.setTitle("Obra no existeix");
                    alert.showAndWait();
                } catch (ExceptionAPersitencia e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Hi ha hagut un error al trobar el document, consulta amb els desenvolupadors per arreglar-lo");
                    alert.setTitle("Error");
                    alert.showAndWait();
                }
            });

            MenuItem kDocs = new MenuItem();
            kDocs.textProperty().bind(Bindings.format("K Documents Semblants"));
            kDocs.setOnAction(event -> {

            });

            MenuItem eliminaDocument = new MenuItem();
            eliminaDocument.textProperty().bind(Bindings.format("Elimina Document"));
            eliminaDocument.setOnAction(event -> {

                try {

                    controladorPresentacio.eliminaDocument(autorSeleccionat,titolSeleccionat);
                    resultatDocuments.remove(cell.getIndex());

                } catch (ExceptionAutorNoExisteix e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Autor no existeix");
                    alert.setTitle("Autor no existeix");
                    alert.showAndWait();
                } catch (ExceptionObraNoExisteix e) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Obra no existeix");
                    alert.setTitle("Obra no existeix");
                    alert.showAndWait();
                } catch (IOException ignored) {
                }

            });

            contextMenu.getItems().addAll(mostraContingut,kDocs,eliminaDocument);

            cell.textProperty().bind(cell.itemProperty());
            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });

            return cell ;
        };

        opcionsConsultesHistorial = lv -> {

            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem desarConsulta = new MenuItem();
            desarConsulta.textProperty().bind(Bindings.format("Desar Consulta"));
            desarConsulta.setOnAction(event -> desarConsulta());

            contextMenu.getItems().addAll(desarConsulta);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell ;
        };

        opcionsConsultesDesades = lv -> {

            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem modificarConsulta = new MenuItem();
            modificarConsulta.textProperty().bind(Bindings.format("Modificar Nom Consulta"));
            modificarConsulta.setOnAction(event -> {
                modificaNomConsulta();
                llistaConsultes.getItems().clear();
                carregaDesades();
            });

            MenuItem eliminaConsulta = new MenuItem();
            eliminaConsulta.textProperty().bind(Bindings.format("Elimina Consulta"));
            eliminaConsulta.setOnAction(event -> eliminarConsulta());

            contextMenu.getItems().addAll(modificarConsulta,eliminaConsulta);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell ;
        };
        dobleClickDocs = event -> {
            if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {

                stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                controladorPresentacio.carregaVistaModifDoc(titolSeleccionat,autorSeleccionat,stage);

            }
        };
        dobleClickConsultes = event -> {
            if(event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {

                cercaConsulta();
                llistaConsultes.getItems().clear();
                if(historialRadio.isSelected()){

                    carregaHistorial();
                    resultatConsultes = controladorPresentacio.getHistorial();
                }
                else {
                    carregaDesades();
                    resultatConsultes = controladorPresentacio.getGuardades();
                }

            }
        };

    }

    /** Carrega les consultes booleanes a les llistes
    */
    public void carregaConsultesBooleanes(){

        carregaHistorial();

        llistaConsultes.getSelectionModel().selectedItemProperty().addListener(consultesListener);
        llistaDocuments.getSelectionModel().selectedItemProperty().addListener(documentsListener);

        llistaDocuments.setCellFactory(opcionsDocuments);

        llistaDocuments.setOnMouseClicked(dobleClickDocs);
        llistaConsultes.setOnMouseClicked(dobleClickConsultes);

        desarButton.setDisable(true);

    }

    //------Historial-Desades-------//
    /** Carrega l'historial a la llista i activa i desactiva els elements d'interficie adients
    */
    public void carregaHistorial(){

        llistaConsultes.getItems().clear();
        resultatConsultes = controladorPresentacio.getHistorial();
        ArrayList<String> aux = new ArrayList<>();

        for (Pair<String, String> resultatConsulte : resultatConsultes) {
            aux.add("(" + resultatConsulte.right() + ")");
        }
        llistaConsultes.getItems().addAll(aux);

        llistaConsultes.setCellFactory(opcionsConsultesHistorial);

        eliminaButton.setDisable(true);
        nombraButton.setDisable(true);
    }

    /** Carrega les consultes desades a les llistes i desactiva els elements d'interficie adients
    */
    public void carregaDesades(){

        llistaConsultes.getItems().clear();
        resultatConsultes = controladorPresentacio.getGuardades();
        ArrayList<String> aux = new ArrayList<>();

        for (Pair<String, String> resultatConsulte : resultatConsultes) {
            aux.add(resultatConsulte.left() + " (" + resultatConsulte.right() + ")");
        }
        llistaConsultes.getItems().addAll(aux);
        llistaConsultes.setCellFactory(opcionsConsultesDesades);

        desarButton.setDisable(true);


    }

    /** Activa l'historial
     */
    public void activaHistorial() {
        historialIGuardatLabel.setText("Historial");
        carregaHistorial();
    }

    /** Activa consultes desades
     */
    public void activaConsultesDesades() {
        historialIGuardatLabel.setText("Consultes Guardades");
        carregaDesades();
    }

    //------Accions consultes-------//

    /** Desa una consulta comprovant els errors per notificar a l'usuari
    */
    public void desarConsulta() {

        TextInputDialog td = new TextInputDialog(expSeleccionada);
        td.setHeaderText("Introdueix el nom de la consulta:");

        Optional<String> result = td.showAndWait();

        if (result.isPresent()) {
            String nomConsulta = td.getEditor().getText();

            try {
                controladorPresentacio.posarNomConsulta(nomConsulta, expSeleccionada);
            } catch (ExceptionConsultaJaExisteix e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Consulta ja existeix");
                alert.setHeaderText("Ja existeix una consulta amb nom " + nomConsulta);
                alert.showAndWait();
            } catch (ExceptionConsultaIncorrecta ignored) {
            }
        }
    }

    /** Desa l'expresió booleana al camp d'expresio
     */
    public void desaConsultaLateral() {

        TextInputDialog td = new TextInputDialog(input.getText());
        td.setHeaderText("Introdueix el nom de la consulta:");

        Optional<String> result = td.showAndWait();
        if (result.isPresent()) {
            String nomConsulta = td.getEditor().getText();

            try{
                controladorPresentacio.posarNomConsulta(nomConsulta, input.getText());
            } catch (ExceptionConsultaJaExisteix e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Consulta ja existeix");
                alert.setHeaderText("Ja existeix una consulta amb aquest nom");
                alert.showAndWait();
            } catch (ExceptionConsultaIncorrecta e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Consulta incorrecta");
                alert.setHeaderText("La consulta introduida es incorrecta");
                alert.showAndWait();
            }
        }
    }

    /** Elimina una consulta comprovant els errors per notificar a l'usuari
    */
    public void eliminarConsulta() {
        try {
            controladorPresentacio.eliminarConsultaBooleana(nomSeleccionat);
        } catch (ExceptionConsultaNoExisteix e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("La consulta no existeix");
            alert.setTitle("La consulta no existeix");
            alert.showAndWait();
            return;
        }

        carregaDesades();

    }

    /** Modifica el nom d'una consulta
    */
    public void modificaNomConsulta() {


        try {
            TextInputDialog td = new TextInputDialog(nomSeleccionat);

            td.setHeaderText("Modifica el nom de la consulta");

            Optional<String> result = td.showAndWait();
            if (result.isPresent()) {
                String novaConsulta = td.getEditor().getText();

                controladorPresentacio.modificaNomConsulta(novaConsulta, nomSeleccionat);
            }

        } catch (ExceptionConsultaNoExisteix e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("La consulta no existeix");
            alert.setTitle("La consulta no existeix");
            alert.showAndWait();
        } catch (ExceptionConsultaJaExisteix e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Ja existeix una consulta amb aquest nom");
            alert.setTitle("Ja existeix una consulta amb aquest nom");
            alert.showAndWait();
        }


    }

    //-----Accions----//

    /** Busca una consulta a les consultes guardades
    */
    public void cercaConsulta() {
        llistaDocuments.getItems().clear();

        if(nomMode.isSelected()){
            try {

                String nom = input.getText();
                resultatDocuments = controladorPresentacio.consultaBooleanaNom(nom);

            } catch (ExceptionConsultaNoExisteix e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Info incorrecta");
                alert.setHeaderText("La consulta no existeix.");
                alert.showAndWait();
                return;
            }
        }
        else{
            try {
                resultatDocuments = controladorPresentacio.consultaBooleanaExp(input.getText());
            } catch (ExceptionConsultaIncorrecta e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Consulta incorrecta");
                alert.setHeaderText("La consulta es incorrecta");
                alert.showAndWait();
                return;
            }
        }

        ArrayList<String> documents = new ArrayList<>();

        for (Pair<String, String> doc : resultatDocuments) {
            documents.add("Autor: " + doc.right() + "\nTitol: " + doc.left());
        }

        llistaDocuments.getItems().addAll(documents);

        if(historialRadio.isSelected()) carregaHistorial();
        else carregaDesades();
    }

    //----Tipus input-----//

    /** Activa el camp per introduir el nom d'una expresio guardada
    */
    public void activaNom() {
        tipusLabel.setText("Nom de l'expressió booleana");
        desaLateraButton.setDisable(true);
        input.clear();
    }

    /** Activa el camp per introduir l'expressio booleana
    */
    public void activaExprBool() {
        tipusLabel.setText("Expressió booleana");
        desaLateraButton.setDisable(false);
        input.clear();
    }


    //--------Vistes-----------//

    /** Canvia la vista a la pagina principal
     */
    public void carregaMain() {
        stage = (Stage) myMenuBar.getScene().getWindow();
        controladorPresentacio.carregaVistaPagPrincipal(stage);

    }

    /** Torna a la vista anterior
    */
    public void tornaEnrere(){
        controladorPresentacio.tornaEnrere();
    }

    /** Carrega el link pel repositori del GitLab
     */
    public void carregaGitlab() {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            try {
                Desktop.getDesktop().browse(new URI("https://repo.fib.upc.es/grau-prop/subgrup-prop31.2"));
            } catch (IOException | URISyntaxException ignored) {
            }
        }
    }
}
