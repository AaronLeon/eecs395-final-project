package client;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import parcheesi.Board;
import parcheesi.Pair;
import parcheesi.Pawn;

import java.util.HashMap;

public class UIBoard extends GridPane {
    final int COLS = 20;
    final int ROWS = 20;

    final int CELL_HEIGHT = 30;
    final int CELL_WIDTH = 30;

    final Pair<Integer, Integer>[] ringToGridIndex = fillRingToGridIndex();

    final HashMap<String, Pair<Integer, Integer>[]> homeRowToGridIndex = new HashMap<>();
    {{
        Pair[] blueRunway = new Pair[Board.HOMEROW_SIZE];
        for (int i = 0; i < Board.HOMEROW_SIZE; ++i) {
            blueRunway[i] = new Pair<>(9, 1 + i);
        }
        homeRowToGridIndex.put("blue", blueRunway);

        Pair[] greenRunway = new Pair[Board.HOMEROW_SIZE];
        for (int i = 0; i < Board.HOMEROW_SIZE; ++i) {
            greenRunway[i] = new Pair<>(9, 11 + i);
        }
        homeRowToGridIndex.put("green", greenRunway);


        Pair[] yellowRunway = new Pair[Board.HOMEROW_SIZE];
        for (int i = 0; i < Board.HOMEROW_SIZE; ++i) {
            yellowRunway[i] = new Pair<>(1 + i, 9);
        }

        homeRowToGridIndex.put("yellow", yellowRunway);
        Pair[] redRunway = new Pair[Board.HOMEROW_SIZE];
        for (int i = 0; i < Board.HOMEROW_SIZE; ++i) {
            redRunway[i] = new Pair<>(11 + i, 9);
        }
        homeRowToGridIndex.put("red", redRunway);
    }}

    final HashMap<String, Pair<Integer, Integer>> homeToGridIndex = new HashMap<>();
    {{
        homeToGridIndex.put("blue", new Pair<>(9, 8));
        homeToGridIndex.put("green", new Pair<>(9, 10));
        homeToGridIndex.put("yellow", new Pair<>(8, 9));
        homeToGridIndex.put("red", new Pair<>(10, 9));
    }}

    final HashMap<String, Pair<Integer, Integer>> nestToGridIndex = new HashMap<>();
    {{
        nestToGridIndex.put("blue", new Pair<>(7, 4));
        nestToGridIndex.put("green", new Pair<>(11, 14));
        nestToGridIndex.put("yellow", new Pair<>(4, 11));
        nestToGridIndex.put("red", new Pair<>(14, 7));
    }}

    Board board;

    public UIBoard(Board board) {
        this.board = board;
        this.setHeight(600);
        this.setWidth(600);
        this.setGridLinesVisible(true);

        RowConstraints rc = new RowConstraints(CELL_HEIGHT);
        rc.setFillHeight(true);
        rc.setValignment(VPos.CENTER);
        ColumnConstraints cc = new ColumnConstraints(CELL_WIDTH);
        cc.setFillWidth(true);
        cc.setHalignment(HPos.CENTER);
        this.getRowConstraints().add(rc);
        this.getColumnConstraints().add(cc);


        for (int pos = 0; pos < Board.RING_SIZE; ++pos) {
            Pair<Integer, Integer> cellLocation = ringToGridIndex[pos];
            Color color = Board.isSafe(pos)
                    ? Color.MEDIUMPURPLE
                    : Color.BEIGE;
            this.add(new UISquare(30, color, board.ring[pos]), cellLocation.first, cellLocation.second);
        }

        for (String c: Board.COLORS) {
            Color color = stringToColor(c);

            this.add(new UISquare(30, color, board.nests.get(c)), nestToGridIndex.get(c).first, nestToGridIndex.get(c).second);
            for (int pos = 0; pos < Board.HOMEROW_SIZE; ++pos) {
                Pair<Integer, Integer> location = homeRowToGridIndex.get(c)[pos];
                this.add(new UISquare(30, color, board.homeRows.get(c)), location.first, location.second);
            }
            this.add(new UISquare(30, color, board.homes.get(c)), homeToGridIndex.get(c).first, homeToGridIndex.get(c).second);
        }

//        drawPawns();
    }

    // TODO: Is there a better way to do this?!?!?!
    private Pair<Integer, Integer>[] fillRingToGridIndex() {
        Pair[] indexing = new Pair[Board.RING_SIZE];
        for (int i = 0; i <= 7; i++) {
            indexing[i] = new Pair<>(8, i);
        }
        for (int i = 8; i <= 15; i++) {
            indexing[i] = new Pair<>(15 - i, 8);
        }
        indexing[16] = new Pair<>(0, 9);
        for (int i = 17; i <= 27; i++) {
            indexing[i] = new Pair<>(i - 17, 10);
        }
        for (int i = 25; i <= 32; i++) {
            indexing[i] = new Pair<>(8, i - 14);
        }
        indexing[33] = new Pair<>(9, 18);
        for (int i = 34; i <= 41; i++) {
            indexing[i] = new Pair<>(10, 34 + 18 - i);
        }
        for (int i = 42; i <= 49; i++) {
            indexing[i] = new Pair<>(i - 31, 10);
        }
        indexing[50] = new Pair<>(18, 9);
        for (int i = 51; i <= 58; i++) {
            indexing[i] = new Pair<>(51 + 18 - i, 8);
        }
        for (int i = 59; i <= 66; i++) {
            indexing[i] = new Pair<>(10, 66 - i);
        }
        indexing[67] = new Pair<>(9, 0);
        return indexing;
    }

    private void drawPawns() {
        for (String c: Board.COLORS) {
            Color color = stringToColor(c);
            Pair<Integer, Integer> nestLocation = nestToGridIndex.get(c);
            Pair<Integer, Integer> homeLocation = homeToGridIndex.get(c);

            for (Object pawn: board.nests.get(c)) {
                if (pawn != null && pawn instanceof Pawn) {
                    this.add(new UIPawn(color), nestLocation.first, nestLocation.second);
                }
            }

            for (Object pawn: board.ring) {
                if (pawn != null && pawn instanceof Pawn) {
                    Pair<Integer, Integer> location = ringToGridIndex[((Pawn) pawn).location];
                    this.add(new UIPawn(color), location.first, location.second);
                }
            }

            for (Object pawn: board.homeRows.get(c)) {
                if (pawn != null && pawn instanceof Pawn) {
                    Pair<Integer, Integer> location = homeRowToGridIndex.get(c)[((Pawn) pawn).location];
                    this.add(new UIPawn(color), location.first, location.second);
                }
            }


            for (Object pawn: board.homes.get(c)) {
                if (pawn != null && pawn instanceof Pawn) {
                    this.add(new UIPawn(color), homeLocation.first, homeLocation.second);
                }
            }
        }
    }

    static Color stringToColor(String c) {
        switch (c) {
            case "blue":
                return Color.BLUE;
            case "green":
                return Color.GREEN;
            case "yellow":
                return Color.YELLOW;
            case "red":
                return Color.RED;
            default:
                return null;
        }
    }
}
