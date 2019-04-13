package client.gui;

import client.gui.WAM;
import client.gui.WAMNetworkClient;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;

/**
 * JavaFX GUI for a connected game of Whack-A-Mole
 *
 * @author Sam Chilaka @<soc5881@rit.edu>
 * @author Dhaval Shrimshal @<drs4554@rit.edu>
 */
public class WAMGUI extends Application implements Observer<WAM> {

    private WAM wam;

    private WAMNetworkClient client;

    private Label labels = new Label();

    /**
     * The init method initializes the whack-a-mole GUI display and other
     * attributes of it
     */
    @Override
    public void init() {
        try {
            List<String> param = getParameters().getRaw();
            String host = param.get(0);
            int port = Integer.parseInt(param.get(1));
            this.wam = new WAM(wam.getrows(), wam.getcols(), client.player);
            this.wam.addObserver(this);
            this.client = new WAMNetworkClient(host, port, this.wam);
        }
        catch (NumberFormatException e) {
            System.err.println(e);
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    private void buttonPressed() {
        return;
    }

    private GridPane makeHoles(int col, int row){
        GridPane g = new GridPane();
        Image hole = new Image(getClass().getResourceAsStream("hole.png"));
        for (int c = 0; c < col; c++) {
            for (int r = 0; r < row; r++) {

                Button b = new Button();
                b.setGraphic(new ImageView(hole));
                b.setOnAction(event -> buttonPressed());

            }
        }
        return g;
    }


    public void start(Stage stage) throws Exception {

        GridPane g = makeHoles(wam.getcols(), wam.getrows());
        VBox vb = new VBox(this.labels, g);
        Scene scene = new Scene(vb);
        stage.setScene(scene);

    }

    /**
     * This method handles the GUI updates
     */
    public void refresh() {}

    /**
     * Called by the model, client.WAM, whenever there is a state change
     * that needs to be updated by the GUI.
     *
     * @param
     */
    @Override
    public void update(WAM wam) {
        if ( Platform.isFxApplicationThread() ) {
            this.refresh();
        }
        else {
            Platform.runLater( () -> this.refresh() );
        }
    }

    /**
     * The main method takes in the host and port of the server
     * @param args cmd line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java WhackAmole host port");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }


}
