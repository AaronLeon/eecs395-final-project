import org.junit.*;

public class    ParcheesiTest {
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

    /*
     * Basic tests
     */
    @Test
    public void processEnterPieceTest() {
        Parcheesi game = new Parcheesi();
        SPlayer player1 = new SPlayer("blue");
        SPlayer player2 = new SPlayer("yellow");
        SPlayer player3 = new SPlayer("red");
        SPlayer player4 = new SPlayer("green");

        game.register(player1);
        game.register(player2);
        game.register(player3);
        game.register(player4);

        EnterPiece m1 = new EnterPiece(player1.getPawns()[0]);
        EnterPiece m2 = new EnterPiece(player2.getPawns()[0]);
        EnterPiece m3 = new EnterPiece(player3.getPawns()[0]);
        EnterPiece m4 = new EnterPiece(player4.getPawns()[0]);

        game.board = (game.processMoves(game.board, m1)).first;
        game.turn++;
        game.board = (game.processMoves(game.board, m2)).first;
        game.turn++;
        game.board = (game.processMoves(game.board, m3)).first;
        game.turn++;
        game.board = (game.processMoves(game.board, m4)).first;
        game.turn++;


        Assert.assertTrue(game.board.ring[5].first.equals(player1.getPawns()[0]));
        Assert.assertTrue(game.board.ring[22].first.equals(player2.getPawns()[0]));
        Assert.assertTrue(game.board.ring[39].first.equals(player3.getPawns()[0]));
        Assert.assertTrue(game.board.ring[56].first.equals(player4.getPawns()[0]));
    }

    @Test
    public void processMainMoveTest() {
        Parcheesi game = new Parcheesi();
        Pawn p1 = new Pawn(0, "red");
        game.board.ring[0].first = p1;
        MoveMain m1 = new MoveMain(p1, 0);

        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertTrue("Pawn should move 3 spaces in ring", result.first.ring[3].first.equals(p1));
        Assert.assertFalse("Pawn should not be in original space in ring", result.first.ring[0].first.equals(p1));
    }

    @Test
    public void processMoveHomeTest() {
        Parcheesi game = new Parcheesi();
        Pawn p1 = new Pawn(0, "red");
        game.board.runways.get("red").runway[3].first = p1;
        MoveHome m1 = new MoveHome(p1, 3, 2);
        MoveHome m2 = new MoveHome(p1, 5, 2);

        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertTrue("Pawn should move 3 spaces in runway", result.first.runways.get("red").runway[5].first.equals(p1));
        Assert.assertFalse("Pawn should not be in original space in runway", result.first.runways.get("red").runway[3].first.equals(p1));

        result = game.processMoves(game.board, m2);

        Assert.assertTrue("Pawn should move 2 spaces in runway", result.first.runways.get("red").runway[6].first.equals(p1));
    }

    /*
     * Entering
     */
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

    /*
     * Bopping
     */
    @Test
    public void boppingGivesBonusTest() {
        Parcheesi game = new Parcheesi();

        Pawn p1 = new Pawn(0, "red");
        Pawn p2 = new Pawn(0, "green");

        game.board.ring[0].first = p1;
        game.board.ring[3].first = p2;

        MoveMain m1 = new MoveMain(p1, 3);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertTrue("Bopping earns 20 bonus", result.second == 20);
    }

    @Test
    public void enterPieceCanBopTest() {
        Parcheesi game = new Parcheesi();

        Pawn p1 = new Pawn(0, "red");
        Pawn p2 = new Pawn(0, "green");

        game.board.ring[39].first = p1;

        EnterPiece m1 = new EnterPiece(p2);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertFalse("Bopped piece should be removed", result.first.ring[3].first.equals(p1));
        Assert.assertTrue("Moving piece should replace bopped piece", result.first.ring[3].first.equals(p2));
    }

    @Test
    public void cannotBopOnSafetyTest() {
        Parcheesi game = new Parcheesi();

        Pawn p1 = new Pawn(0, "red");
        Pawn p2 = new Pawn(0, "green");

        game.board.ring[3].first = p1;

        EnterPiece m1 = new EnterPiece(p2);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertFalse("Piece should not be removed on safety", result.first.ring[3].first.equals(p1));
        Assert.assertTrue("Moving piece should not replace piece at destination", result.first.ring[3].first.equals(p2));
        // Check if cheated
        Assert.assertNull("Bopping safety should return null (e.g. cheat)", result);
    }

    @Test
    public void boppingTwoPiecesGivesTwoBonusesTest() {
        Parcheesi game = new Parcheesi();

        Pawn p1 = new Pawn(0, "red");
        Pawn p2 = new Pawn(1, "red");
        Pawn p3 = new Pawn(0, "green");
        Pawn p4 = new Pawn(1, "green");

        game.board.ring[0].first = p1;
        game.board.ring[1].first = p2;
        game.board.ring[3].first = p3;
        game.board.ring[7].first = p4;

        MoveMain m1 = new MoveMain(p1, 3);
        MoveMain m2 = new MoveMain(p2, 6);
        Pair<Board, Integer> result1 = game.processMoves(game.board, m1);
        Pair<Board, Integer> result2 = game.processMoves(game.board, m2);

        Assert.assertTrue("First bop earns 20 bonus", result1.second == 20);
        Assert.assertTrue("Second bop earns 20 bonus", result2.second == 20);
    }




    /*
     * Blockades
     */

    /*
     * Exit row
     */

    /*
     * Complete Move
     */


    /*
     * Doubles Penalty
     */

}