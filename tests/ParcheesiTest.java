import com.sun.tools.javac.comp.Enter;
import org.junit.*;

import java.util.LinkedList;

public class ParcheesiTest {
    @BeforeClass
    public static void beforeClass() {
    }

    @AfterClass
    public static void afterClass() {
    }

    @Before
    public void beforeTest() {
    }

    @After
    public void afterTest() {
    }

    @Test void processEnterPieceTest() {
        Parcheesi game = new Parcheesi();

        Pawn p1 = new Pawn(0, "blue");
        Pawn p2 = new Pawn(0, "yellow");
        Pawn p3 = new Pawn(0, "green");
        Pawn p4 = new Pawn(0, "red");

        EnterPiece m1 = new EnterPiece(p1);
        EnterPiece m2 = new EnterPiece(p2);
        EnterPiece m3 = new EnterPiece(p3);
        EnterPiece m4 = new EnterPiece(p4);

        game.processMoves(game.board, m1);
        game.processMoves(game.board, m2);
        game.processMoves(game.board, m3);
        game.processMoves(game.board, m4);

        Assert.assertTrue(game.board.ring[5].first.equals(p1));
        Assert.assertTrue(game.board.ring[22].first.equals(p2));
        Assert.assertTrue(game.board.ring[39].first.equals(p3));
        Assert.assertTrue(game.board.ring[56].first.equals(p4));
    }

    @Test void processMainMoveTest() {
        Parcheesi game = new Parcheesi();

        Pawn p1 = new Pawn(0, "red");
        game.board.ring[0].first = p1;
        MoveMain m1 = new MoveMain(p1, 0, 3);
        game.processMoves(game.board, m1);
        Assert.assertTrue("Pawn should move 3 spaces in ring", game.board.ring[3].first.equals(p1));
        Assert.assertFalse("Pawn should not be in original space in ring", game.board.ring[0].first.equals(p1));
    }

    @Test void processMoveHomeTest() {
        Parcheesi game = new Parcheesi();

        Pawn p1 = new Pawn(0, "red");
        game.board.runways.get("red").runway[3].first = p1;
        MoveHome m1 = new MoveHome(p1, 3, 2);
        game.processMoves(game.board, m1);
        Assert.assertTrue("Pawn should move 3 spaces in runway", game.board.runways.get("red").runway[5].first.equals(p1));
        Assert.assertFalse("Pawn should not be in original space in runway", game.board.runways.get("red").runway[3].first.equals(p1));
    }

    @Test
    public void canEnterPawnTest() {
        Parcheesi game = new Parcheesi();
        int[] d1 = {1, 4, 0, 0};
        int[] d2 = {2, 3, 0, 0};
        int[] d3 = {5, 5, 0, 0};
        int[] d4 = {6, 2, 0, 0};

        Assert.assertTrue("Can enter pawn with 1, 4", game.canEnter(d1));
        Assert.assertTrue("Can enter pawn with 2, 3", game.canEnter(d2));
        Assert.assertTrue("Can enter pawn with 5, 5", game.canEnter(d3));
        Assert.assertFalse("Cannot enter pawn with 6, 2", game.canEnter(d4));
    }
}