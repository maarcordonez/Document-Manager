package presentacio.classes;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import persistencia.excepcions.ExceptionAPersitencia;

import java.io.IOException;

/**Classe MainApp. Clase encarregada d'executar el programa
 * @author laura.hui.perez
 */
public class MainApp extends Application {

    /**Obte la instancia singleton de controlador presentacio
     */
    ControladorPresentacio controladorPresentacio = ControladorPresentacio.getSingletonInstance();

    /** Creadora buida
     */
    public MainApp() {
    }

    /**Definex els parametres basics de la interficie inicial
     * @param stage Contenidor de la vista principal
     */
    @Override
    public void start(Stage stage){

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
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        stage.setTitle("DocKitty");
        stage.setScene(scene);
        stage.setMinWidth(600.0);
        stage.setMinHeight(437.0);
        stage.getIcons().add(new Image("file:../../icones2/3468377.png"));

        stage.show();

        stage.setOnCloseRequest(event -> {

            event.consume();
            try {
                controladorPresentacio.tancarAplicacio();
            } catch (ExceptionAPersitencia e) {
                throw new RuntimeException(e);
            }
            stage.close();

        });


    }

    /** Metode per iniciar l'aplicacio
     * @param args Parametre necessari
     */
    public static void main(String[] args) {
        launch(args);
    }

}