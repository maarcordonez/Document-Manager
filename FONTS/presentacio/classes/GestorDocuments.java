package presentacio.classes;

import domini.excepcions.ExceptionAutorNoExisteix;
import domini.excepcions.ExceptionObraNoExisteix;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import persistencia.excepcions.ExceptionAPersitencia;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.Stack;

/**
 * Classe GestorDocuments. Vista dedicada a la gestio de documents
 *
 * @author laura.hui.perez
 */
public class GestorDocuments {

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
     * Identificador camp de text per l'input
     */
    @FXML
    Label inputLabel;
    /**
     * Identificador camp de text per l'autor
     */
    @FXML
    Label autorLabel;
    /**
     * Identificador camp de text pel titol
     */
    @FXML
    Label titolLabel;
    /**
     * Indentificador camp de text d'autor
     */
    @FXML
    TextField autor;
    /**
     * Indentificador camp de text de titol
     */
    @FXML
    TextField titol;
    /**
     * Indentificador camp d'input
     */
    @FXML
    TextField input;
    /**
     * Indentificador boto per kDocumentsSemblants
     */
    @FXML
    Button kDocSemblantButton;
    /**
     * Indentificador boto per kDocumentsRellevants
     */
    @FXML
    Button kDocRellevantsButton;
    /**
     * Indentificador boto per llistar titols
     */
    @FXML
    Button llistarTitolsButton;
    /**
     * Indentificador boto per llistar autors
     */
    @FXML
    Button llistarAutorsButton;
    /**
     * Indentificador tabla de documents
     */
    @FXML
    TableView taulaDocuments;
    /**
     * Indentificador kdocuments
     */
    @FXML
    TextField kNum;
    /**
     * Indentificador kdocuments
     */
    @FXML
    Label kLabel;
    /**
     * Indentificador opcio bool del radioButton
     */
    @FXML
    RadioButton boolRadioButton;
    /**
     * Indentificador opcio tfidf del radioButton
     */
    @FXML
    RadioButton tfRadioButton;
    /**
     * Indentificador boto mostra contingut
     */
    @FXML
    Button mostraContingutButton;
    /**
     * Indentificador boto obrir document
     */
    @FXML
    Button obreDocButton;
    /**
     * Indentificador menu d'etiquetes
     */
    @FXML
    MenuButton etiquetesMenu;
    /**
     * Indentificador llista d'elements
     */
    @FXML
    ListView llistaElements;
    /**
     * Indentificador stack1
     */
    @FXML
    GridPane stack1;
    /**
     * Indentificador stack2
     */
    @FXML
    GridPane stack2;
    /**
     * Indentificador boto baixa
     */
    @FXML
    MenuButton baixaButton;

    /**
     * Contenidor de la vista
     */
    private Stage stage;

    /**
     * Boolea autor_titol
     */
    Boolean autor_titol = true;
    /**
     * Boolea llistarTitols
     */
    Boolean llistarTitols = false;
    /**
     * Boolea prefix
     */
    Boolean prefix = false;
    /**
     * Boolea kDocS
     */
    Boolean kDocS = false;
    /**
     * Boolea kDocR
     */
    Boolean kDocR = false;

    /**
     * Llista de titol, autor i data
     */
    ArrayList<ArrayList<String>> resultatData;

    /**
     * Autor seleccionat
     */
    String autorSeleccionat;
    /**
     * Titol seleccionat
     */
    String titolSeleccionat;

    /**
     * Listener d'autor titol
     */
    ChangeListener autorTitolListener;
    /**
     * Listener de titol
     */
    ChangeListener titolListener;
    /**
     * Listener d'autor
     */
    ChangeListener autorListener;
    /**
     * Listener per l'input
     */
    ChangeListener inputListener;

    /**
     * Event pel dobleclic
     */
    EventHandler<MouseEvent> dobleClick;

    /**
     * Opcio autor titol
     */
    Callback<TableColumn, Cell> opcionsAutorTitol;
    /**
     * Opcio titol
     */
    Callback<ListView, ListCell> opcionsTitol;
    /**
     * Opcio autor
     */
    Callback<ListView, ListCell> opcionsAutor;

    /**
     * Llista d'etiquetes
     */
    ArrayList<String> llistaEtiquetes;

    /**
     * Taula columna de titol
     */
    TableColumn columnaTitol;
    /**
     * Taula columna de d'autor
     */
    TableColumn columnaAutor;
    /**
     * Taula columna de data
     */
    TableColumn columnaData;
    /**
     * Stack que desa historial d'opcions
     */
    Stack<String> pilaEnrere;

    /**
     * Funcio gestora dels elements de la interficie i la comunicacio amb les accions a realitzar
     */
    public GestorDocuments() {

        taulaDocuments = new TableView<>();
        autorTitolListener = (ChangeListener<RowDocument>) (observableValue, rowDocument, t1) -> {

            RowDocument doc = (RowDocument) taulaDocuments.getSelectionModel().getSelectedItem();

            if (doc != null) {

                obreDocButton.setDisable(false);
                baixaButton.setDisable(false);

                autorSeleccionat = doc.getAutor();
                titolSeleccionat = doc.getTitol();

                autor.setText(autorSeleccionat);
                titol.setText(titolSeleccionat);
            } else {
                obreDocButton.setDisable(true);
                baixaButton.setDisable(true);
            }
        };

        titolListener = (ChangeListener<String>) (arg0, arg1, arg2) -> {

            titolSeleccionat = (String) llistaElements.getSelectionModel().getSelectedItem();
            obreDocButton.setDisable(titolSeleccionat == null);

        };

        inputListener = (ChangeListener<String>) (arg0, arg1, arg2) -> {

            if (prefix) {
                llistarAutors();
            }

        };
        autorListener = (ChangeListener<String>) (arg0, arg1, arg2) -> {

            autorSeleccionat = (String) llistaElements.getSelectionModel().getSelectedItem();

            obreDocButton.setDisable(true);

        };


        dobleClick = event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                if(prefix){
                    input.setText(autorSeleccionat);
                    taulaDocuments.getItems().clear();
                    mostraTitolsAutor();
                    llistarTitols();
                }
                else {
                    if (titolSeleccionat != null && autorSeleccionat != null) {
                        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

                        controladorPresentacio.carregaVistaModifDoc(titolSeleccionat, autorSeleccionat, stage);
                    }
                }

            }
        };

        opcionsAutorTitol = tableColumn -> {
            TableCell<RowDocument, String> cell = new TableCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem mostraContingut = new MenuItem();
            mostraContingut.textProperty().bind(Bindings.format("Mostra contingut"));
            mostraContingut.setOnAction(event -> {
                String t = titolSeleccionat;
                String a = autorSeleccionat;
                mostraAutorTitolCerca();
                autor.setText(a);
                titol.setText(t);
                mostraContingut();

            });
            MenuItem kDocs = new MenuItem();
            kDocs.textProperty().bind(Bindings.format("K Documents"));
            kDocs.setOnAction(event -> {
                mostraKDocS();
                autor.setText(autorSeleccionat);
                titol.setText(titolSeleccionat);

            });
            MenuItem eliminaDocument = new MenuItem();
            eliminaDocument.textProperty().bind(Bindings.format("Elimina Document"));
            eliminaDocument.setOnAction(event -> {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Elimiar Document");
                alert.setHeaderText("Segur que vols eliminar el document?");

                ButtonType elimina = new ButtonType("Elimina");
                ButtonType buttonTypeCancel = new ButtonType("Cancel·la", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(elimina, buttonTypeCancel);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == elimina) {
                    try {
                        controladorPresentacio.eliminaDocument(titolSeleccionat, autorSeleccionat);
                        taulaDocuments.getItems().remove(taulaDocuments.getSelectionModel().getSelectedItem());
                    } catch (ExceptionAutorNoExisteix e) {
                        alertaAutorNoExisteix();
                    } catch (ExceptionObraNoExisteix e) {
                        alertaObraNoExisteix();
                    } catch (IOException ignored) {
                    }

                }


            });
            MenuItem afegeixEtiqueta = new MenuItem();
            afegeixEtiqueta.textProperty().bind(Bindings.format("Afegeix Etiqueta"));
            afegeixEtiqueta.setOnAction(event -> {

                ArrayList<String> etiqsAfeg = controladorPresentacio.etiquetesAfegibles(titolSeleccionat, autorSeleccionat);

                if (etiqsAfeg.size() > 0) {
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(etiqsAfeg.get(0), etiqsAfeg);
                    dialog.setTitle("Etiquetes");
                    dialog.setContentText("Escull etiqueta:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        controladorPresentacio.afegeixEtiquetaObra(titolSeleccionat, autorSeleccionat, result.get());
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Aquest document no pot tenir més etiquetes");
                    alert.setTitle("Etiquetes");
                    alert.showAndWait();
                }

            });

            MenuItem eliminaEtiqueta = new MenuItem();
            eliminaEtiqueta.textProperty().bind(Bindings.format("Elimina Etiqueta"));
            eliminaEtiqueta.setOnAction(event -> {

                ArrayList<String> etiquetesObra = controladorPresentacio.getEtiquetesDocument(titolSeleccionat, autorSeleccionat);

                if (etiquetesObra.size() > 0) {
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(etiquetesObra.get(0), etiquetesObra);
                    dialog.setTitle("Etiquetes");
                    dialog.setContentText("Escull etiqueta:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        controladorPresentacio.eliminarEtiquetaObra(result.get(), titolSeleccionat, autorSeleccionat);
                        filtraDocumentsEtiq();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Aquest document no té etiquetes");
                    alert.setTitle("Etiquetes");
                    alert.showAndWait();
                }

            });

            MenuItem mostraEtiquetes = new MenuItem();
            mostraEtiquetes.textProperty().bind(Bindings.format("Mostra Etiquetes"));
            mostraEtiquetes.setOnAction(event -> {

                ArrayList<String> etiquetesObra = controladorPresentacio.getEtiquetesDocument(titolSeleccionat, autorSeleccionat);

                if (etiquetesObra.size() > 0) {
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Etiquetes");
                    dialog.setResizable(true);

                    ListView<String> etiqs = new ListView<>();
                    etiqs.getItems().addAll(etiquetesObra);

                    dialog.getDialogPane().setContent(etiqs);

                    ButtonType type = new ButtonType("Surt", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().add(type);
                    dialog.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Aquest document no té etiquetes");
                    alert.setTitle("Etiquetes");
                    alert.showAndWait();
                }

            });
            contextMenu.getItems().addAll(mostraContingut, kDocs, eliminaDocument, afegeixEtiqueta, eliminaEtiqueta, mostraEtiquetes);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;

        };

        opcionsAutor = lv -> {

            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem mostraTitols = new MenuItem();
            mostraTitols.textProperty().bind(Bindings.format("Mostra títols"));
            mostraTitols.setOnAction(event -> {
                input.setText(autorSeleccionat);
                taulaDocuments.getItems().clear();
                mostraTitolsAutor();
                llistarTitols();

            });

            contextMenu.getItems().addAll(mostraTitols);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        };

        opcionsTitol = lv -> {

            ListCell<String> cell = new ListCell<>();

            ContextMenu contextMenu = new ContextMenu();

            MenuItem mostraContingut = new MenuItem();
            mostraContingut.textProperty().bind(Bindings.format("Mostra contingut"));
            mostraContingut.setOnAction(event -> {
                autor.setText(autorSeleccionat);
                titol.setText(titolSeleccionat);
                mostraAutorTitolCerca();
                mostraContingut();

            });
            MenuItem kDocs = new MenuItem();
            kDocs.textProperty().bind(Bindings.format("K Documents"));
            kDocs.setOnAction(event -> {
                autor.setText(autorSeleccionat);
                titol.setText(titolSeleccionat);
                mostraKDocS();

            });
            MenuItem eliminaDocument = new MenuItem();
            eliminaDocument.textProperty().bind(Bindings.format("Elimina Document"));
            eliminaDocument.setOnAction(event -> {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Elimiar Document");
                alert.setHeaderText("Segur que vols eliminar el document?");

                ButtonType elimina = new ButtonType("Elimina");
                ButtonType buttonTypeCancel = new ButtonType("Cancel·la", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(elimina, buttonTypeCancel);

                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == elimina) {
                    try {
                        controladorPresentacio.eliminaDocument(titolSeleccionat, autorSeleccionat);
                        taulaDocuments.getItems().remove(taulaDocuments.getSelectionModel().getSelectedItem());

                    } catch (ExceptionAutorNoExisteix e) {
                        alertaAutorNoExisteix();
                    } catch (ExceptionObraNoExisteix e) {
                        alertaObraNoExisteix();
                    } catch (IOException ignored) {

                    }

                }


            });
            MenuItem afegeixEtiqueta = new MenuItem();
            afegeixEtiqueta.textProperty().bind(Bindings.format("Afegeix Etiqueta"));
            afegeixEtiqueta.setOnAction(event -> {

                ArrayList<String> etiqsAfeg = controladorPresentacio.etiquetesAfegibles(titolSeleccionat, autorSeleccionat);

                if (etiqsAfeg.size() > 0) {
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(etiqsAfeg.get(0), etiqsAfeg);
                    dialog.setTitle("Etiquetes");
                    dialog.setContentText("Escull etiqueta:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        controladorPresentacio.afegeixEtiquetaObra(titolSeleccionat, autorSeleccionat, result.get());
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Aquest document no pot tenir més etiquetes");
                    alert.setTitle("Etiquetes");
                    alert.showAndWait();
                }

            });

            MenuItem eliminaEtiqueta = new MenuItem();
            eliminaEtiqueta.textProperty().bind(Bindings.format("Elimina Etiqueta"));
            eliminaEtiqueta.setOnAction(event -> {

                ArrayList<String> etiquetesObra = controladorPresentacio.getEtiquetesDocument(titolSeleccionat, autorSeleccionat);

                if (etiquetesObra.size() > 0) {
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(etiquetesObra.get(0), etiquetesObra);
                    dialog.setTitle("Etiquetes");
                    dialog.setContentText("Escull etiqueta:");

                    Optional<String> result = dialog.showAndWait();
                    if (result.isPresent()) {
                        controladorPresentacio.eliminarEtiquetaObra(result.get(), titolSeleccionat, autorSeleccionat);
                        filtraDocumentsEtiq();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Aquest document no té etiquetes");
                    alert.setTitle("Etiquetes");
                    alert.showAndWait();
                }

            });

            MenuItem mostraEtiquetes = new MenuItem();
            mostraEtiquetes.textProperty().bind(Bindings.format("Mostra Etiquetes"));
            mostraEtiquetes.setOnAction(event -> {

                ArrayList<String> etiquetesObra = controladorPresentacio.getEtiquetesDocument(titolSeleccionat, autorSeleccionat);

                if (etiquetesObra.size() > 0) {
                    Dialog<String> dialog = new Dialog<>();
                    dialog.setTitle("Etiquetes");
                    dialog.setResizable(true);

                    ListView<String> etiqs = new ListView<>();
                    etiqs.getItems().addAll(etiquetesObra);

                    dialog.getDialogPane().setContent(etiqs);

                    ButtonType type = new ButtonType("Surt", ButtonBar.ButtonData.OK_DONE);
                    dialog.getDialogPane().getButtonTypes().add(type);
                    dialog.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Aquest document no té etiquetes");
                    alert.setTitle("Etiquetes");
                    alert.showAndWait();
                }

            });


            contextMenu.getItems().addAll(mostraContingut, kDocs, eliminaDocument, afegeixEtiqueta, eliminaEtiqueta, mostraEtiquetes);

            cell.textProperty().bind(cell.itemProperty());

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    cell.setContextMenu(contextMenu);
                }
            });
            return cell;
        };

        pilaEnrere = new Stack<>();

    }

    /**
     * Carrega els camps necesaris de gestor documents
     */
    public void carregaGestorDocuments() {

        columnaTitol = new TableColumn("Titol");
        columnaTitol.setCellValueFactory(new PropertyValueFactory<>("titol"));
        columnaTitol.setPrefWidth(136);

        columnaAutor = new TableColumn("Autor");
        columnaAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        columnaAutor.setPrefWidth(136);

        columnaData = new TableColumn("Data");
        columnaData.setCellValueFactory(new PropertyValueFactory<>("data"));
        columnaData.setPrefWidth(137);

        taulaDocuments.getColumns().addAll(columnaTitol, columnaAutor, columnaData);

        resultatData = controladorPresentacio.getAllDocumentsData();

        insertaDocumentsData();

        taulaDocuments.setPlaceholder(new Label(""));
        taulaDocuments.getSelectionModel().selectedItemProperty().addListener(autorTitolListener);
        input.textProperty().addListener((inputListener));
        columnaTitol.setCellFactory(opcionsAutorTitol);
        columnaAutor.setCellFactory(opcionsAutorTitol);
        columnaData.setCellFactory(opcionsAutorTitol);
        taulaDocuments.setOnMouseClicked(dobleClick);
        llistaElements.setOnMouseClicked(dobleClick);

        llistaEtiquetes = controladorPresentacio.llistarEtiquetes();
        actualitzaEtiquetes();

        stack2.toFront();

        pilaEnrere.add("menu");

    }


    //---------Metodes auxiliars---------//

    /**
     * Inserta la data a un document
     */
    private void insertaDocumentsData() {

        for (ArrayList<String> doc : resultatData) {
            RowDocument docRow = new RowDocument(doc.get(0), doc.get(1), doc.get(2));
            taulaDocuments.getItems().add(docRow);
        }

    }

    /**
     * Carrega la vista de pagina principal
     */
    public void carregaMain() {

        stage = (Stage) myMenuBar.getScene().getWindow();
        controladorPresentacio.carregaVistaPagPrincipal(stage);

    }

    /**
     * Actualitza la visibilitat dels elements de la interficie
     */
    private void actualitzaVisibilitat(boolean enrere) {

        if (autor_titol) {
            autor_titol = false;
            if(!enrere && !pilaEnrere.peek().equals("autorTitol")) pilaEnrere.add("autorTitol");
        } else if (llistarTitols) {
            llistarTitols = false;
            if(!enrere && !pilaEnrere.peek().equals("titols")) pilaEnrere.add("titols");
        } else if (prefix) {
            prefix = false;
            if(!enrere && !pilaEnrere.peek().equals("prefix")) pilaEnrere.add("prefix");
        } else if (kDocS) {
            kDocS = false;
            if(!enrere && !pilaEnrere.peek().equals("kDocS")) pilaEnrere.add("kDocS");
        } else if (kDocR) {
            kDocR = false;
            if(!enrere && !pilaEnrere.peek().equals("kDocR")) pilaEnrere.add("kDocR");
        }

    }

    /**
     * Actualitza els elements interactius de la vista
     */
    private void actualitzaVista() {

        taulaDocuments.getItems().clear();
        baixaButton.setDisable(true);
        obreDocButton.setDisable(true);

        if (autor_titol) {
            autorLabel.setVisible(true);
            titolLabel.setVisible(true);

            autor.setVisible(true);
            titol.setVisible(true);

            llistarTitolsButton.setVisible(false);
            llistarAutorsButton.setVisible(false);
            kDocSemblantButton.setVisible(false);
            kDocRellevantsButton.setVisible(false);

            input.setVisible(false);
            inputLabel.setVisible(false);

            kNum.setVisible(false);
            kLabel.setVisible(false);
            boolRadioButton.setVisible(false);
            tfRadioButton.setVisible(false);

            mostraContingutButton.setVisible(true);
            etiquetesMenu.setDisable(false);

            taulaDocuments.setVisible(true);
            llistaElements.setVisible(false);

            titolLabel.setText("Títol");

            stack2.toFront();

            etiquetesMenu.setDisable(false);
        } else if (llistarTitols) {

            llistaElements.getItems().clear();

            autorLabel.setVisible(false);
            titolLabel.setVisible(true);

            autor.setVisible(false);
            titol.setVisible(false);

            llistarTitolsButton.setVisible(true);
            llistarAutorsButton.setVisible(false);
            kDocSemblantButton.setVisible(false);
            kDocRellevantsButton.setVisible(false);

            input.setVisible(true);
            inputLabel.setVisible(false);

            kNum.setVisible(false);
            kLabel.setVisible(false);
            boolRadioButton.setVisible(false);
            tfRadioButton.setVisible(false);

            mostraContingutButton.setVisible(false);

            taulaDocuments.setVisible(false);
            llistaElements.setVisible(true);

            titolLabel.setText("Autor");

            stack1.toFront();

            etiquetesMenu.setDisable(true);
        } else if (prefix) {

            autorLabel.setVisible(false);
            titolLabel.setVisible(false);
            autor.setVisible(false);
            titol.setVisible(false);

            llistarTitolsButton.setVisible(false);
            llistarAutorsButton.setVisible(false);
            kDocSemblantButton.setVisible(false);
            kDocRellevantsButton.setVisible(false);

            input.setVisible(true);
            inputLabel.setVisible(true);
            inputLabel.setText("Prefix");

            kNum.setVisible(false);
            kLabel.setVisible(false);
            boolRadioButton.setVisible(false);
            tfRadioButton.setVisible(false);

            mostraContingutButton.setVisible(false);

            taulaDocuments.setVisible(false);
            llistaElements.setVisible(true);

            llistaElements.getItems().clear();
            llistaElements.getItems().addAll(controladorPresentacio.llistarAutors(""));

            titolLabel.setText("Autor");

            stack1.toFront();

            etiquetesMenu.setDisable(true);


        } else if (kDocS) {

            autorLabel.setVisible(true);
            titolLabel.setVisible(true);
            autor.setVisible(true);
            titol.setVisible(true);

            llistarTitolsButton.setVisible(false);
            llistarAutorsButton.setVisible(false);
            kDocSemblantButton.setVisible(true);
            kDocRellevantsButton.setVisible(false);

            input.setVisible(false);
            inputLabel.setVisible(false);

            kNum.setVisible(true);
            kLabel.setVisible(true);
            boolRadioButton.setVisible(true);
            tfRadioButton.setVisible(true);

            mostraContingutButton.setVisible(false);

            taulaDocuments.setVisible(true);
            llistaElements.setVisible(false);

            titolLabel.setText("Títol");

            stack2.toFront();

            etiquetesMenu.setDisable(true);

        } else if (kDocR) {

            autorLabel.setVisible(false);
            titolLabel.setVisible(false);
            autor.setVisible(false);
            titol.setVisible(false);

            llistarTitolsButton.setVisible(false);
            llistarAutorsButton.setVisible(false);
            kDocSemblantButton.setVisible(false);
            kDocRellevantsButton.setVisible(true);

            input.setVisible(true);
            inputLabel.setVisible(true);
            inputLabel.setText("Paraules: p1,p2,..,pn");

            kNum.setVisible(true);
            kLabel.setVisible(true);
            boolRadioButton.setVisible(true);
            tfRadioButton.setVisible(true);

            mostraContingutButton.setVisible(false);

            taulaDocuments.setVisible(true);
            llistaElements.setVisible(false);

            stack1.toFront();

            etiquetesMenu.setDisable(true);
        }

    }

    /**
     * Buida la taula i fa una nova cerca
     */
    private void novaCerca() {
        taulaDocuments.getItems().clear();
        llistaElements.getItems().clear();
        desactivaListeners();
    }

    /**
     * Desactiva els listeners
     */
    private void desactivaListeners() {
        llistaElements.getSelectionModel().selectedItemProperty().removeListener(titolListener);
        llistaElements.getSelectionModel().selectedItemProperty().removeListener(autorListener);

    }


    //--------Accions Vistes--------------//

    /**
     * Mostra el contingut del document
     */
    public void mostraContingut() {
        String a = autor.getText();
        String t = titol.getText();

        try {
            String c = controladorPresentacio.mostraContingut(t, a);

            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Contingut");
            dialog.setResizable(true);

            TextArea contingutArea = new TextArea();
            contingutArea.setWrapText(true);
            contingutArea.setText(c);
            contingutArea.setEditable(false);

            dialog.getDialogPane().setContent(contingutArea);

            ButtonType type = new ButtonType("Surt", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().add(type);
            dialog.showAndWait();

        } catch (ExceptionAutorNoExisteix e) {
            alertaAutorNoExisteix();
        } catch (ExceptionObraNoExisteix e) {
            alertaObraNoExisteix();
        } catch (ExceptionAPersitencia e) {
            errorPersistencia();
        }

    }

    /**
     * Mostra l'autor i el titol d'una cerca
     */
    public void mostraAutorTitolCerca() {

        actualitzaVisibilitat(false);
        autor_titol = true;
        actualitzaVista();

        resultatData = controladorPresentacio.getAllDocumentsData();
        insertaDocumentsData();

    }

    /**
     * Mostra els titols i autor
     */
    public void mostraTitolsAutor() {

        actualitzaVisibilitat(false);
        llistarTitols = true;
        actualitzaVista();

    }

    /**
     * Mostra autors per prefix
     */
    public void mostraAutorPref() {

        actualitzaVisibilitat(false);
        prefix = true;
        actualitzaVista();

        llistaElements.getSelectionModel().selectedItemProperty().addListener(autorListener);
        llistaElements.setCellFactory(opcionsAutor);

    }

    /**
     * Mostra k documents similars
     */
    public void mostraKDocS() {

        actualitzaVisibilitat(false);
        kDocS = true;
        actualitzaVista();

    }

    /**
     * Mostra k documents rellevants
     */
    public void mostraKdocR() {

        actualitzaVisibilitat(false);
        kDocR = true;
        actualitzaVista();

    }

    /**
     * Llista els titols resultat d'una cerca
     */
    public void llistarTitols() {

        novaCerca();

        autorSeleccionat = input.getText();

        try {
            llistaElements.getItems().addAll(controladorPresentacio.llistarTitols(autorSeleccionat));

        } catch (ExceptionAutorNoExisteix e) {
            alertaAutorNoExisteix();
            return;
        }

        llistaElements.getSelectionModel().selectedItemProperty().addListener(titolListener);
        llistaElements.setCellFactory(opcionsTitol);

    }

    /**
     * Llista els autors resultat d'una cerca
     */
    public void llistarAutors() {

        novaCerca();

        String a = input.getText();
        autorSeleccionat = a;

        llistaElements.getItems().addAll(controladorPresentacio.llistarAutors(a));

        llistaElements.getSelectionModel().selectedItemProperty().addListener(autorListener);
        llistaElements.setCellFactory(opcionsAutor);

    }

    /**
     * Realitza una consulta de k documents semblants
     */
    public void consultaKDocSemblants() {

        novaCerca();

        String tipus = "Bool";
        if (tfRadioButton.isSelected()) tipus = "tf-idf";

        int k = 0;

        try {
            k = Integer.parseInt(kNum.getText());
            if (k < 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("K ha de ser positiu");
                alert.setTitle("Informació introduïda incorrecta");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("K ha de ser un enter");
            alert.setTitle("Informació introduïda incorrecta");
            alert.showAndWait();
            return;
        }

        try {
            resultatData = controladorPresentacio.kDocumentsSemblants(tipus, k, titol.getText(), autor.getText());
        } catch (ExceptionAPersitencia e) {
            errorPersistencia();
            return;
        }

        insertaDocumentsData();

    }

    /**
     * Realitza una consulta de k documents rellevants
     */
    public void consultaKDocRellevants() {

        novaCerca();

        String tipus = "Bool";
        if (tfRadioButton.isSelected()) tipus = "tf-idf";

        int k = 0;

        try {
            k = Integer.parseInt(kNum.getText());
            if (k < 0) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("K ha de ser positiu");
                alert.setTitle("Informació introduïda incorrecta");
                alert.showAndWait();
                return;
            }
        } catch (NumberFormatException ex) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("K ha de ser un enter");
            alert.setTitle("Informació introduïda incorrecta");
            alert.showAndWait();
            return;
        }


        ArrayList<String> paraules = new ArrayList<>(Arrays.asList(input.getText().toLowerCase().replaceAll("^\\s+", "").split("[!,? ._'@]+")));

        resultatData = controladorPresentacio.kDocumentsRellevants(tipus, k, paraules);

        insertaDocumentsData();


    }

    /**
     * Mostra tots els documents registrats
     */
    public void mostraTots() {

        llistaElements.setVisible(false);
        taulaDocuments.setVisible(true);

        taulaDocuments.getItems().clear();
        resultatData = controladorPresentacio.getAllDocumentsData();

        for (int i = 0; i < llistaEtiquetes.size(); ++i) {

            CustomMenuItem capsa = (CustomMenuItem) etiquetesMenu.getItems().get(i);
            CheckBox aux = (CheckBox) capsa.getContent();

            aux.setSelected(false);

        }

        insertaDocumentsData();
    }



    //---------------Canvi de vistes-------------------//

    /**
     * Carrega la vista de consultes booleanes
     */
    public void carregaConsultesBooleanes() {

        stage = (Stage) myMenuBar.getScene().getWindow();
        controladorPresentacio.carregaVistaBool(stage);
    }

    /**
     * Obre document
     *
     * @param actionEvent Action event per detectar l'accio
     */
    public void obreDoc(ActionEvent actionEvent) {

        if (titolSeleccionat.equals("") || autorSeleccionat.equals("")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Introdueix Títol i Autor");
            alert.setTitle("Error");
            alert.showAndWait();
        } else {
            stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            controladorPresentacio.carregaVistaModifDoc(titolSeleccionat, autorSeleccionat, stage);
        }
    }

    /**
     * Permet crear un nou document
     *
     * @param actionEvent Action event per detectar l'accio
     */
    public void nouDoc(ActionEvent actionEvent) {
        stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        controladorPresentacio.carregaNouDocument(stage);
    }

    /**
     * Carrega el link al GitLab del projecte
     * @throws URISyntaxException Exepcio de Java
     * @throws IOException Exepcio de Java
     */
    public void carregaGit() throws URISyntaxException, IOException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://repo.fib.upc.es/grau-prop/subgrup-prop31.2"));
        }
    }

    /**
     * Torna a la vista anterior
     */
    public void tornaEnrere() {

        if((autor_titol && pilaEnrere.peek().equals("autorTitol")) ||
                (llistarTitols && pilaEnrere.peek().equals("titols")) ||
                (prefix && pilaEnrere.peek().equals("prefix")) ||
                (kDocS && pilaEnrere.peek().equals("kDocS")) ||
                (kDocR) && pilaEnrere.peek().equals("kDocR")) pilaEnrere.pop();

        String aux = pilaEnrere.pop();

        switch (aux) {
            case "autorTitol":
                actualitzaVisibilitat(true);
                autor_titol = true;
                actualitzaVista();

                resultatData = controladorPresentacio.getAllDocumentsData();
                insertaDocumentsData();
                break;
            case "titols":
                actualitzaVisibilitat(true);
                llistarTitols = true;
                actualitzaVista();
                break;
            case "prefix":
                actualitzaVisibilitat(true);
                prefix = true;
                actualitzaVista();
                break;
            case "kDocS":
                actualitzaVisibilitat(true);
                kDocS = true;
                actualitzaVista();
                break;
            case "kDocR":
                actualitzaVisibilitat(true);
                kDocR = true;
                actualitzaVista();
                break;
            default:
                controladorPresentacio.tornaEnrere();
                break;
        }
    }

    //---------------Etiquetes----------------//

    /**
     * Filtra els documents per etiqueta
     */
    private void filtraDocumentsEtiq() {

        novaCerca();

        boolean mesUnaEtiqueta = false;
        ArrayList<String> etiquetesSeleccionades = new ArrayList<>();

        for (int i = 0; i < llistaEtiquetes.size(); ++i) {

            CustomMenuItem capsa = (CustomMenuItem) etiquetesMenu.getItems().get(i);
            CheckBox aux = (CheckBox) capsa.getContent();

            if (aux.isSelected()) {

                etiquetesSeleccionades.add(aux.getText());
                mesUnaEtiqueta = true;
            }

        }

        if (mesUnaEtiqueta) resultatData = controladorPresentacio.llistarObresEtiquetes(etiquetesSeleccionades);
        else resultatData = controladorPresentacio.getAllDocumentsData();

        insertaDocumentsData();
    }

    /**
     * Actualitza les etiquetes
     */
    private void actualitzaEtiquetes() {
        etiquetesMenu.getItems().clear();

        for (String et : llistaEtiquetes) {
            CheckBox aux = new CheckBox(et);
            CustomMenuItem capsa = new CustomMenuItem(aux);
            capsa.setHideOnClick(false);
            etiquetesMenu.getItems().add(capsa);
        }

        etiquetesMenu.setOnHiding(e -> {

            filtraDocumentsEtiq();
            taulaDocuments.getSelectionModel().selectedItemProperty().addListener(autorTitolListener);

        });

        MenuItem afegeixEtiqueta = new MenuItem("Crear Etiqueta");
        afegeixEtiqueta.setOnAction(event -> {
            TextInputDialog td = new TextInputDialog("Nom etiqueta");
            td.setHeaderText("Quin nom li vols posar a l'etiqueta?");

            Optional<String> result = td.showAndWait();
            if (result.isPresent()) {
                String nomEtiqueta = td.getEditor().getText();

                controladorPresentacio.afegirEtiqueta(nomEtiqueta);

                llistaEtiquetes = controladorPresentacio.llistarEtiquetes();
                actualitzaEtiquetes();
            }

        });

        MenuItem eliminaEtiqueta = new MenuItem("Eliminar Etiqueta");
        eliminaEtiqueta.setOnAction(event -> {

            if (llistaEtiquetes.size() > 1) {

                ChoiceDialog<String> dialog = new ChoiceDialog<>(llistaEtiquetes.get(1), llistaEtiquetes.subList(1, llistaEtiquetes.size()));
                dialog.setTitle("Etiquetes");
                dialog.setContentText("Escull etiqueta:");

                Optional<String> result = dialog.showAndWait();

                if (result.isPresent()) {
                    String a = result.get();
                    controladorPresentacio.eliminaEtiqueta(a);
                    llistaEtiquetes = controladorPresentacio.llistarEtiquetes();

                    actualitzaEtiquetes();

                }
            } else {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("No pots eliminar més etiquetes");
                alert.setTitle("Etiquetes");
                alert.showAndWait();

            }

        });

        etiquetesMenu.getItems().add(afegeixEtiqueta);
        etiquetesMenu.getItems().add(eliminaEtiqueta);

    }

    //----------Persistencia----------------//

    /**
     * Descarrega en format txt
     */
    public void baixaTxt() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directori = directoryChooser.showDialog(stage);

        if (directori != null) {

            String path = directori.getAbsolutePath();

            try {
                controladorPresentacio.baixaDocumentTxt(titolSeleccionat, autorSeleccionat, path);
            } catch (ExceptionAPersitencia e) {
                errorPersistencia();
            }
        }

    }

    /**
     * Descarrega en format xml
     */
    public void baixaXML() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directori = directoryChooser.showDialog(stage);

        if (directori != null) {
            String path = directori.getAbsolutePath();

            try {
                controladorPresentacio.baixaDocumentXML(titolSeleccionat, autorSeleccionat, path);
            } catch (ExceptionAPersitencia e) {
                errorPersistencia();
            }
        }

    }

    /**
     * Descarrega en format kitty
     */
    public void baixaKitty() {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File directori = directoryChooser.showDialog(stage);

        if (directori != null) {
            String path = directori.getAbsolutePath();

            try {
                controladorPresentacio.baixaDocumentKitty(titolSeleccionat, autorSeleccionat, path);
            } catch (ExceptionAPersitencia e) {
                errorPersistencia();
            }
        }

    }

    /**
     * Desa la sessio actual
     */
    public void desaSessio() {

        try {
            controladorPresentacio.tancarAplicacio();
        } catch (ExceptionAPersitencia e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("No s'ha pogut desar correctament la sessió");
            alert.setTitle("Error");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("S'ha desat correctament la sessió");
        alert.setTitle("Informació");
        alert.showAndWait();
    }

    //---------------Alertes----------------//

    /**
     * Alerta d'autor no existeix
     */
    private void alertaAutorNoExisteix() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Autor no existeix");
        alert.setTitle("Autor no existeix");
        alert.showAndWait();
    }

    /**
     * Alerta d'obra no existeix
     */
    private void alertaObraNoExisteix() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Obra no existeix");
        alert.setTitle("Obra no existeix");
        alert.showAndWait();
    }

    /**
     * Abisa si es produeix un error a persistencia
     */
    public void errorPersistencia() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Hi ha hagut un error al trobar el document, consulta amb els desenvolupadors per arreglar-lo");
        alert.setTitle("Error");
        alert.showAndWait();

    }
}