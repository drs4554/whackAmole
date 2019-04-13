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

/**
 * JavaFX GUI for a connected game of Whack-A-Mole
 *
 * @author Sam Chilaka @<soc5881@rit.edu>
 * @author Dhaval Shrimshal @<drs4554@rit.edu>
 */
public class WAMGUI {

    private WAM wam;

    private WAMNetworkClient client;

    private void buttonPressed() {

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

        VBox vb = new VBox();

    }


    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java WhackAmole host port");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }
}
