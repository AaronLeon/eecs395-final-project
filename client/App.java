package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import parcheesi.Board;

public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Board b = new Board();
        b.initPawns();
        UIBoard board = new UIBoard(b);
        System.out.println(board.getChildren());

        Scene scene = new Scene(board, 500, 500);
        stage.setScene(scene);
        stage.show();
    }

}
