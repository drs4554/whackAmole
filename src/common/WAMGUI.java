package common;

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


public class WAMGUI {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java WhackAmole host port");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }
}
