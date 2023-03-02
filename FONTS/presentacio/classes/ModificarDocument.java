package presentacio.classes;

import domini.excepcions.*;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import persistencia.excepcions.ExceptionAPersitencia;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Classe ModificarDocument. Vista dedicada a la modificacio de documents
 *
 * @author laura.hui.perez
 */
public class ModificarDocument {

    /**
     * Obte la instancia singleton de controlador presentacio
     */
    ControladorPresentacio controladorPresentacio = ControladorPresentacio.getSingletonInstance();
    /**
     * Identificador barra de menu
     */
    @FXML
    MenuBar myMenuBar;
    /**
     * Indentificador camp de text nou autor
     */
    @FXML
    TextField nouAutor;
    /**
     * Indentificador camp de text nou titol
     */
    @FXML
    TextField nouTitol;
    /**
     * Indentificador camp de text nou nouContingut
     */
    @FXML
    TextArea nouContingut;
    /**
     * Indentificador boto guardar
     */
    @FXML
    Button guardarButton;
    /**
     * Menu de missatge desat
     */
    @FXML
    Menu desatMessage;

    /**
     * Contenidor de la vista
     */
    private Stage stage;

    /**
     * Autor guardat
     */
    String autorGuardat = "";
    /**
     * Titol guardat
     */
    String titolGuardat = "";
    /**
     * Contingut guardat
     */
    String contingutGuardat = "";

    /**
     * Creadora buida
     */
    public ModificarDocument() {

    }


    /**
     * Carrega document el document que li passen
     *
     * @param autor     Autor del document
     * @param titol     Titol del document
     * @param contingut Contingut del document
     */
    public void carregaDoc(String titol, String autor, String contingut) {

        autorGuardat = autor;
        titolGuardat = titol;
        contingutGuardat = contingut;

        nouAutor.setText(autor);
        nouTitol.setText(titol);
        nouContingut.setText(contingut);
        nouContingut.setWrapText(true);

        guardarButton.setDisable(true);

        nouAutor.textProperty().addListener((observable, oldValue, newValue) -> guardarButton.setDisable(false));

        nouTitol.textProperty().addListener((observable, oldValue, newValue) -> guardarButton.setDisable(false));

        nouContingut.textProperty().addListener((observable, oldValue, newValue) -> guardarButton.setDisable(false));

    }

    //---------Funcions auxiliars-------//

    /**
     * Verifica que es guarda el document
     *
     * @return Cert si es guarda fals si no
     */
    private boolean docGuardat() {

        return nouAutor.getText().equals(autorGuardat) &&
                nouTitol.getText().equals(titolGuardat) &&
                ((nouContingut.getText() == (null) && nouContingut.getText() == (null)) || nouContingut.getText().equals(contingutGuardat));
    }

    /**
     * Pop up per confirmar que es vol desar
     *
     * @return Cert si es guarda fals si no
     */
    private boolean comprovaGuardat() {

        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Document modificat");
        alert.setHeaderText("Document no desat");
        alert.setContentText("Vols desar?");

        ButtonType desar = new ButtonType("Desa");
        ButtonType noDesar = new ButtonType("No desis");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(desar, noDesar, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == desar) {

            if (nouAutor.getText().length() + nouTitol.getText().length() > 200) {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Posa un títol o un autor més curt");
                alert.setTitle("Error");
                alert.showAndWait();
                return false;
            }
            if (nouTitol.getText().equals("") || nouAutor.getText().equals("")) {
                alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Introdueix Títol i Autor");
                alert.setTitle("Error");
                alert.showAndWait();
                return false;
            }
            guardaDoc();
            return true;
        } else return result.get() == noDesar;
    }

    /**
     * Mostra el missatge de document desat correctament
     */
    private void missatgeGuardat() {

        desatMessage.setVisible(true);

        PauseTransition delay = new PauseTransition(Duration.seconds(2));
        delay.setOnFinished(e -> desatMessage.setVisible(false));

        delay.play();


    }

    //------Accions------//

    /**
     * Comprova que el document es guardi
     *
     * @return Cert si s'ha guardat correctament o fals si no
     */
    public boolean guardaDoc() {

        if (nouAutor.getText().length() + nouTitol.getText().length() > 200) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Posa un títol o un autor més curt");
            alert.setTitle("Error");
            alert.showAndWait();

            return false;

        } else {
            if (nouTitol.getText().equals("") || nouAutor.getText().equals("")) {

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Introdueix Títol i Autor");
                alert.setTitle("Error");
                alert.showAndWait();

                return false;

            } else {
                if (autorGuardat.equals("") && titolGuardat.equals("")) { //Nou document

                    try {

                        controladorPresentacio.creaDocument(nouTitol.getText(), nouAutor.getText(), nouContingut.getText());
                        controladorPresentacio.actualitzarConsultesBooleanes();

                        missatgeGuardat();

                        autorGuardat = nouAutor.getText();
                        titolGuardat = nouTitol.getText();
                        contingutGuardat = nouContingut.getText();

                        guardarButton.setDisable(true);

                        return true;

                    } catch (ExceptionObraJaExisteix e) { //El titol i autor introduits ja existien

                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Document ja existeix");
                        alert.setHeaderText("Segur que vols sobreescriure el document?");

                        ButtonType modifica = new ButtonType("Sobreescriure");
                        ButtonType buttonTypeCancel = new ButtonType("Cancel·la", ButtonBar.ButtonData.CANCEL_CLOSE);

                        alert.getButtonTypes().setAll(modifica, buttonTypeCancel);

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.get() == modifica) {
                            try {

                                controladorPresentacio.modificaDocument(nouTitol.getText(), nouAutor.getText(), nouTitol.getText(), nouAutor.getText(), nouContingut.getText());
                                controladorPresentacio.actualitzarConsultesBooleanes();

                                missatgeGuardat();

                                autorGuardat = nouAutor.getText();
                                titolGuardat = nouTitol.getText();
                                contingutGuardat = nouContingut.getText();

                                guardarButton.setDisable(true);

                                return true;

                            } catch (ExceptionObraJaExisteix ex) {
                                errorObraJaExisteix();
                                return false;
                            } catch (ExceptionAutorNoExisteix ex) {
                                errorAutorNoExisteix();
                                return false;
                            } catch (ExceptionObraNoExisteix ex) {
                                errorObraNoExisteix();
                                return false;
                            }

                        }
                    } catch (ExceptionAutorBuit e) {

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Introdueix un autor");
                        alert.setTitle("Error autor buit");
                        alert.showAndWait();

                        return false;

                    } catch (ExceptionTitolBuit e) {

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Introdueix un titol");
                        alert.setTitle("Error titol buit");
                        alert.showAndWait();

                        return false;

                    }
                } else {
                    if (autorGuardat.equals(nouAutor.getText()) && titolGuardat.equals(nouTitol.getText())) { //Document ja existent al sistema el titol i autor desats coincideixen
                        try {
                            controladorPresentacio.modificaDocument(titolGuardat, autorGuardat, nouTitol.getText(), nouAutor.getText(), nouContingut.getText());
                            controladorPresentacio.actualitzarConsultesBooleanes();
                        } catch (ExceptionObraJaExisteix ex) {
                            errorObraJaExisteix();
                            return false;
                        } catch (ExceptionAutorNoExisteix ex) {
                            errorAutorNoExisteix();
                            return false;
                        } catch (ExceptionObraNoExisteix ex) {
                            errorObraNoExisteix();
                            return false;
                        }

                        missatgeGuardat();

                        autorGuardat = nouAutor.getText();
                        titolGuardat = nouTitol.getText();
                        contingutGuardat = nouContingut.getText();

                        guardarButton.setDisable(true);

                        return true;

                    } else {//Document ja existent al sistema el titol i autor desats no coincideixen

                        try {

                            controladorPresentacio.modificaDocument(titolGuardat, autorGuardat, nouTitol.getText(), nouAutor.getText(), nouContingut.getText());
                            controladorPresentacio.actualitzarConsultesBooleanes();

                        } catch (ExceptionObraJaExisteix ex) { //El document amb el nou titol i autor ja existeix, es pregunta si es vol sobreescriure

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Document ja existeix");
                            alert.setHeaderText("Segur que vols sobreescriure el document?");

                            ButtonType modifica = new ButtonType("Sobreescriure");
                            ButtonType buttonTypeCancel = new ButtonType("Cancel·la", ButtonBar.ButtonData.CANCEL_CLOSE);

                            alert.getButtonTypes().setAll(modifica, buttonTypeCancel);

                            Optional<ButtonType> result = alert.showAndWait();

                            if (result.get() == modifica) {

                                try {
                                    controladorPresentacio.modificaDocument(nouTitol.getText(), nouAutor.getText(), nouTitol.getText(), nouAutor.getText(), nouContingut.getText());
                                    controladorPresentacio.actualitzarConsultesBooleanes();
                                } catch (ExceptionObraJaExisteix e) {
                                    errorObraJaExisteix();
                                    return false;
                                } catch (ExceptionAutorNoExisteix e) {
                                    errorAutorNoExisteix();
                                    return false;
                                } catch (ExceptionObraNoExisteix e) {
                                    errorObraNoExisteix();
                                    return false;
                                }

                                missatgeGuardat();

                                autorGuardat = nouAutor.getText();
                                titolGuardat = nouTitol.getText();
                                contingutGuardat = nouContingut.getText();

                                guardarButton.setDisable(true);

                                return true;
                            }
                        } catch (ExceptionAutorNoExisteix ex) {
                            errorAutorNoExisteix();
                            return false;
                        } catch (ExceptionObraNoExisteix ex) {
                            errorObraNoExisteix();
                            return false;
                        }

                        missatgeGuardat();
                        autorGuardat = nouAutor.getText();
                        titolGuardat = nouTitol.getText();
                        contingutGuardat = nouContingut.getText();

                        guardarButton.setDisable(true);

                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Elimina un document demanant confirmacio i
     * comprovant errors
     */
    public void eliminaDocument() {

        if (titolGuardat.equals("") && autorGuardat.equals("")) {

            nouContingut.setText("");
            nouTitol.setText("");
            nouAutor.setText("");
            contingutGuardat = "";

            controladorPresentacio.tornaEnrere();

            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Elimiar Document");
        alert.setHeaderText("Segur que vols eliminar el document?");

        ButtonType elimina = new ButtonType("Elimina");
        ButtonType buttonTypeCancel = new ButtonType("Cancel·la", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(elimina, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == elimina) {
            try {
                controladorPresentacio.eliminaDocument(titolGuardat, autorGuardat);
            } catch (ExceptionAutorNoExisteix | ExceptionObraNoExisteix | IOException ignored) {

            }

            nouAutor.setText("");
            nouTitol.setText("");
            nouContingut.setText("");

            autorGuardat = "";
            titolGuardat = "";
            contingutGuardat = "";

            controladorPresentacio.tornaEnrere();

        }
    }

    /**
     * Esborra tots els camps del document demanant confirmacio
     */
    public void esborraTot() {

        if (!docGuardat()) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Esborrar");
            alert.setHeaderText("Segur que vols esborrar tot?");

            ButtonType desar = new ButtonType("Esborra");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(desar, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == desar) {

                nouAutor.setText("");
                nouTitol.setText("");
                nouContingut.setText("");

            }
        } else {

            nouAutor.setText("");
            nouTitol.setText("");
            nouContingut.setText("");

        }
    }

    /**
     * Obre document
     */
    private void obreDocu() {

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Kitty files (*.txt, *.xml, *.kitty)", "*.txt", "*.xml", "*.kitty");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.setTitle("Open Resource File");
        File selectedFile = fileChooser.showOpenDialog(stage);
        String path = selectedFile.getAbsolutePath();

        ArrayList<String> res = null;

        try {
            res = controladorPresentacio.carregarDocument(path);
        } catch (ExceptionAPersitencia e) {
            errorPersistencia();
        }
        if (res != null) {

            String titol = res.get(0);
            String autor = res.get(1);
            String contingut = res.get(2);

            try {

                controladorPresentacio.creaDocument(titol, autor, contingut);
                controladorPresentacio.actualitzarConsultesBooleanes();

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
                        controladorPresentacio.actualitzarConsultesBooleanes();

                    } catch (ExceptionObraJaExisteix ex) {
                        errorObraJaExisteix();
                    } catch (ExceptionObraNoExisteix ex) {
                        errorObraNoExisteix();
                    } catch (ExceptionAutorNoExisteix ex) {
                        errorAutorNoExisteix();
                    }
                }
            } catch (ExceptionAutorBuit | ExceptionTitolBuit e) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Introdueix un titol i un autor");
                alert.setTitle("Error titol o autor buit");
                alert.showAndWait();
                return;
            }

            stage = (Stage) myMenuBar.getScene().getWindow();
            controladorPresentacio.carregaVistaModifDoc(titol, autor, stage);
        }
    }

    /**
     * Obre document
     */
    public void obreDoc() {

        if (!docGuardat()) {
            if (comprovaGuardat()) obreDocu();
        } else obreDocu();

    }

    /**
     * Carrega la vista principal
     */
    public void carregaMainMenu() {

        if (!docGuardat()) {
            if (comprovaGuardat()) {

                stage = (Stage) myMenuBar.getScene().getWindow();

                controladorPresentacio.carregaVistaPagPrincipal(stage);
            }
        } else {
            stage = (Stage) myMenuBar.getScene().getWindow();

            controladorPresentacio.carregaVistaPagPrincipal(stage);
        }
    }

    /**
     * Carrega la vista de modificar document a la part de nou document
     */
    public void carregaNouDocument() {

        stage = (Stage) myMenuBar.getScene().getWindow();

        if (!docGuardat()) {
            if (comprovaGuardat()) controladorPresentacio.carregaNouDocument(stage);
        } else controladorPresentacio.carregaNouDocument(stage);

    }

    /**
     * Torna a la vista anterior
     */
    public void tornaEnrere() {
        if (!docGuardat()) {
            if (comprovaGuardat()) controladorPresentacio.tornaEnrere();
        } else controladorPresentacio.tornaEnrere();

    }

    /**
     * Carrega el link al GitLab
     *
     * @throws URISyntaxException Exepcio de Java
     * @throws IOException        Exepcio de Java
     */
    public void carregaGit() throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://repo.fib.upc.es/grau-prop/subgrup-prop31.2"));
        }
    }

    //-----------Persistencia------------//

    /**
     * Descarrega un document en format txt
     */
    public void baixaDocumentTxt() {

        if (guardaDoc()) {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File directori = directoryChooser.showDialog(stage);

            if (directori != null) {
                String path = directori.getAbsolutePath();

                try {
                    controladorPresentacio.baixaDocumentTxt(titolGuardat, autorGuardat, path);
                } catch (ExceptionAPersitencia e) {
                    errorPersistencia();
                }
            }
        }

    }

    /**
     * Descarrega un document en format xml
     */
    public void baixaDocumentXML() {

        if (guardaDoc()) {

            DirectoryChooser directoryChooser = new DirectoryChooser();
            File directori = directoryChooser.showDialog(stage);

            if (directori != null) {
                String path = directori.getAbsolutePath();

                try {
                    controladorPresentacio.baixaDocumentXML(titolGuardat, autorGuardat, path);
                } catch (ExceptionAPersitencia e) {
                    errorPersistencia();
                }
            }

        }

    }

    /**
     * Descarrega un document en format kitty
     */
    public void baixaDocumentKitty() {

        if (guardaDoc()) {

            DirectoryChooser directoryChooser = new DirectoryChooser();
            File directori = directoryChooser.showDialog(stage);

            if (directori != null) {
                String path = directori.getAbsolutePath();
                try {
                    controladorPresentacio.baixaDocumentKitty(titolGuardat, autorGuardat, path);
                } catch (ExceptionAPersitencia e) {
                    errorPersistencia();
                }
            }
        }

    }

    //---------Alertes--------------//

    /**
     * Detecta si l'autor ja te una obra amb aquest titol
     */
    public void errorObraJaExisteix() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Aquest autor ja te una obra amb aquest titol");
        alert.setTitle("Error obra ja existeix");
        alert.showAndWait();
    }

    /**
     * Detecta si l'autor no existeix
     */
    public void errorAutorNoExisteix() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("No existeix aquest autor");
        alert.setTitle("Error autor no existeix");
        alert.showAndWait();
    }

    /**
     * Detecta si l'obra no existeix
     */
    public void errorObraNoExisteix() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("No existeix aquesta obra");
        alert.setTitle("Error obra no existeix");
        alert.showAndWait();
    }


    /**
     * Notifica si s'ha produit un error a persistencia
     */
    public void errorPersistencia() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Hi ha hagut un error al trobar el document, consulta amb els desenvolupadors per arreglar-lo");
        alert.setTitle("Error");
        alert.showAndWait();
    }
}

