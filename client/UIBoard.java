package client;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import parcheesi.Board;
import parcheesi.Pair;

import java.util.HashMap;

public class UIBoard extends GridPane {
    final int COLS = 20;
    final int ROWS = 20;
    final int CELL_HEIGHT = 30;
    final int CELL_WIDTH = 30;
    Board board;

    public UIBoard(Board board) {
        this.board = board;
        this.setHeight(600);
        this.setWidth(600);
        this.setGridLinesVisible(true);

        RowConstraints rc = new RowConstraints(CELL_HEIGHT);
        rc.setFillHeight(true);
        ColumnConstraints cc = new ColumnConstraints(CELL_WIDTH);
        cc.setFillWidth(true);
        this.getRowConstraints().add(rc);
        this.getColumnConstraints().add(cc);

        Pair[] ringToGridIndex = fillRingToGridIndex();
        HashMap<String, Pair[]> homeRowToGridIndex = new HashMap<>();
        {
            {
                Pair[] blueRunway = new Pair[Board.HOMEROW_SIZE];
                for (int i = 1; i < 8; i++) {
                    blueRunway[i] = new Pair(9, i);
                }
                homeRowToGridIndex.put("blue", blueRunway);

                Pair[] greenRunway = new Pair[Board.HOMEROW_SIZE];
                for (int i = 17; i > 10; i--) {
                    greenRunway[i] = new Pair(9, i);
                }
                homeRowToGridIndex.put("green", greenRunway);


                Pair[] yellowRunway = new Pair[Board.HOMEROW_SIZE];
                for (int i = 1; i < 8; i++) {
                    yellowRunway[i] = new Pair(i, 9);
                }

                homeRowToGridIndex.put("yellow", yellowRunway);
                Pair[] redRunway = new Pair[Board.HOMEROW_SIZE];
                for (int i = 17; i > 10; i--) {
                    redRunway[i] = new Pair(i, 9);
                }
                homeRowToGridIndex.put("red", redRunway);
            }
        }

        HashMap<String, Pair> homeToGridIndex = new HashMap<>();
        {
            {
                homeToGridIndex.put("blue", new Pair(9, 8));
                homeToGridIndex.put("green", new Pair(9, 10));
                homeToGridIndex.put("yellow", new Pair(8, 9));
                homeToGridIndex.put("red", new Pair(10, 9));
            }
        }

        HashMap<String, Pair> nestToGridIndex = new HashMap<>();
        {
            {
                homeToGridIndex.put("blue", new Pair(7, 4));
                homeToGridIndex.put("green", new Pair(11, 14));
                homeToGridIndex.put("yellow", new Pair(4, 11));
                homeToGridIndex.put("red", new Pair(14, 7));
            }
        }

        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                this.add(new UISquare(30), col, row);
            }
        }
    }


    private Pair[] fillRingToGridIndex() {
        Pair[] indexing = new Pair[Board.RING_SIZE];
        indexing[0] = new Pair(9, 0);
        for (int i = 1; i <= 8; i++) {
            indexing[i] = new Pair(8, i - 1);
        }
        for (int i = 9; i <= 16; i++) {
            indexing[i] = new Pair(16 - i, 8);
        }
        indexing[17] = new Pair(0, 9);
        for (int i = 18; i <= 25; i++) {
            indexing[i] = new Pair(i - 18, 10);
        }
        for (int i = 26; i <= 33; i++) {
            indexing[i] = new Pair(8, i - 15);
        }
        indexing[34] = new Pair(9, 18);
        for (int i = 35; i <= 42; i++) {
            indexing[i] = new Pair(10, 35 + 18 - i);
        }
        for (int i = 43; i <= 50; i++) {
            indexing[i] = new Pair(i - 32, 10);
        }
        indexing[51] = new Pair(18, 9);
        for (int i = 52; i <= 59; i++) {
            indexing[i] = new Pair(52 + 18 - i, 8);
        }
        for (int i = 60; i <= 67; i++) {
            indexing[i] = new Pair(10, 67 - i);
        }
        return indexing;
    }
}
