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

    private Label label;

    private Button[][] holes;


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

            this.client = new WAMNetworkClient(host, port);
            this.wam = this.client.wam;
            this.wam.addObserver(this);
            this.holes = new Button[this.client.cols][this.client.rows];
        }
        catch (NumberFormatException e) {
            System.err.println(e);
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * returns the scores for each player
     * @return
     */
    private String getScores(){
        String[] score = this.wam.points.split(" ");
        String fin = "";
        int i = 0;
        for (String s : score) {
            fin += "P" + i + ": " + s;
            i++;
        }
        return fin;
    }

    /**
     * method handles the status of moles whacked
     */
    private void buttonPressed(Button b) {
        client.sendMove(Integer.parseInt(b.getId()), wam.getplayer());
    }


    /**
     * makeHoles method creates the base playing field for the whack a mole
     * game
     * @param col
     * @param row
     * @return g
     */
    private GridPane makeHoles(int col, int row){
        GridPane g = new GridPane();
        Image hole = new Image(getClass().getResourceAsStream("hole.jpg"));
        int i = 0;
        for (int c = 0; c < col; c++) {
            for (int r = 0; r < row; r++) {

                Button b = new Button();
                b.setGraphic(new ImageView(hole));
                b.setOnAction(event -> buttonPressed(b));
                b.setId(Integer.toString(i));
                g.add(b, c, r);
                this.holes[c][r] = b;
                i++;
            }
        }
        return g;
    }


    /**
     * start method constructs the GUI layout for the game
     * @param stage
     * @throws Exception
     */
    public void start(Stage stage) throws Exception {

        GridPane g = makeHoles(wam.getcols(), wam.getrows());
        stage.setTitle("Whack*A*Mole");
        this.label = new Label(this.getScores());
        Label pnum = new Label("YOU ARE PLAYER NUMBER " + this.wam.getplayer() + "  ::  " + "THE GAME IS ON");
        pnum.setAlignment(Pos.CENTER);
        VBox vb = new VBox(this.label, g, pnum);
        Scene scene = new Scene(vb);
        stage.setScene(scene);
        stage.show();

        this.client.startListener();

    }


    /**
     * handles the GUI updates as the game progresses
     */
    public void refresh() {

        this.label.setText(this.getScores());

        for (int c = 0; c < wam.getcols(); c++) {
            for (int r = 0; r < wam.getrows(); r++) {
                Button b = this.holes[c][r];
                if (wam.holes[Integer.parseInt(b.getId())] == WAM.status.DOWN) {
                    b.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("hole.jpg"))));
                }
                else {
                    b.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("mole.jpg"))));

                }

            }
        }

    }


    /**
     * Called by the model, client.ConnectFourBoard, whenever there is a state change
     * that needs to be updated by the GUI.
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
     * The main method accepts the host and port
     * @param args
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java WhackAMole host port");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }


}
