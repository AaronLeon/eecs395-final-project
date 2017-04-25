import org.junit.*;

public class    ParcheesiTest {
    Parcheesi game;
    SPlayer player1;
    SPlayer player2;
    SPlayer player3;
    SPlayer player4;

    @BeforeClass
    public static void beforeClass() {
    }

    @AfterClass
    public static void afterClass() {
    }

    @Before
    public void beforeTest() {
        game = new Parcheesi();
        player1 = new SPlayer("blue");
        player2 = new SPlayer("yellow");
        player3 = new SPlayer("red");
        player4 = new SPlayer("green");

        game.register(player1);
        game.register(player2);
        game.register(player3);
        game.register(player4);
    }

    @After
    public void afterTest() {
    }

    /*
     * Basic tests
     */
    @Test
    public void processEnterPieceTest() {


        EnterPiece m1 = new EnterPiece(player1.getPawns()[0]);
        EnterPiece m2 = new EnterPiece(player2.getPawns()[0]);
        EnterPiece m3 = new EnterPiece(player3.getPawns()[0]);
        EnterPiece m4 = new EnterPiece(player4.getPawns()[0]);

        game.board = (game.processMoves(m1)).first;
        game.turn++;
        game.board = (game.processMoves(m2)).first;
        game.turn++;
        game.board = (game.processMoves(m3)).first;
        game.turn++;
        game.board = (game.processMoves(m4)).first;
        game.turn++;


        Assert.assertTrue(game.board.ring[5].first.equals(player1.getPawns()[0]));
        Assert.assertTrue(game.board.ring[22].first.equals(player2.getPawns()[0]));
        Assert.assertTrue(game.board.ring[39].first.equals(player3.getPawns()[0]));
        Assert.assertTrue(game.board.ring[56].first.equals(player4.getPawns()[0]));
    }

    @Test
    public void processMainMoveTest() {

        EnterPiece e1 = new EnterPiece(player1.getPawns()[0]);
        game.board = (game.processMoves(e1)).first;
        //make another move
        MoveMain m1 = new MoveMain(player1.getPawns()[0], 3);

        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertTrue("Pawn should move 3 spaces in ring", result.first.ring[8].first!=null);
        Assert.assertTrue("Pawn should not be in original space in ring", result.first.ring[5].first==null);
    }

    @Test
    public void processMoveHomeTest() {
        Pawn p1 = game.players[0].getPawns()[0];
        p1.runway=true;
        p1.home=false;
        p1.location=2;
        game.board.runways.get("blue").runway[2].first = p1;
        game.players[0].setPawn(0,p1);
        MoveHome m1 = new MoveHome(p1, 2, 2);

        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertTrue("Pawn should move 4 spaces in runway", result.first.runways.get("blue").runway[4].first!=null);
        Assert.assertFalse("Pawn should not be in original space in runway", result.first.runways.get("blue").runway[2]==null);

        MoveHome m2 = new MoveHome(game.players[0].getPawns()[0], 4, 2);
        result = game.processMoves(m2);

        Assert.assertTrue("Pawn should move 2 spaces in runway", result.first.runways.get("blue").endZone[1]!=null);
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

        //blue
        Pawn p1 = game.players[0].getPawns()[0];
        p1.home=false;
        p1.location=20;
        game.players[0].setPawn(0,p1);
        //yellow
        Pawn p2 = game.players[1].getPawns()[0];
        p2.home=false;
        p2.location=21;
        game.players[0].setPawn(1,p2);

        //need to simulate game progressing

        game.board.ring[20].first = p1;
        game.board.ring[21].first = p2;

        MoveMain m1 = new MoveMain(p1, 1);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertTrue("Bopping earns 20 bonus", result.second == 20);
    }

    @Test
    public void enterPieceCanBopTest() {
        Parcheesi game = new Parcheesi();
        Pawn p1 = new Pawn(0, "red");
        Pawn p2 = new Pawn(0, "green");

        game.board.ring[39].first = p1;

        EnterPiece m1 = new EnterPiece(p2);
        Pair<Board, Integer> result = game.processMoves(m1);

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
        Pair<Board, Integer> result = game.processMoves(m1);

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
        Pair<Board, Integer> result1 = game.processMoves(m1);
        Pair<Board, Integer> result2 = game.processMoves(m2);

        Assert.assertEquals("First bop earns 20 bonus", result1.second, 20);
        Assert.assertEquals("Second bop earns 20 bonus", result2.second, 20);
    }

    @Test
    public void blockadeCannotMoveTogetherWithBopBonusTest() {
        Parcheesi game = new Parcheesi();
        Pawn p1 = new Pawn(0, "red");
        Pawn p2 = new Pawn(1, "red");
        Pawn blockade1 = new Pawn(2, "red");
        Pawn blockade2 = new Pawn(3, "red");
        Pawn p3 = new Pawn(0, "green");
        Pawn p4 = new Pawn(1, "green");

        game.board.ring[0].first = p1;
        game.board.ring[1].first = p2;
        game.board.ring[3].first = p3;
        game.board.ring[7].first = p4;

        MoveMain m1 = new MoveMain(p1, 3);
        MoveMain m2 = new MoveMain(p2, 6);
        Pair<Board, Integer> result1 = game.processMoves(m1);
        Pair<Board, Integer> result2 = game.processMoves(m2);

        //TODO: Check that bonus moves cant move blockade together
    }

    /*
     * Blockades
     */
    @Test
    public void cannotEnterPieceOnBlockadedEntryTest() {
        Parcheesi game = new Parcheesi();

        Pawn blockade1 = new Pawn(0, "red");
        Pawn blockade2 = new Pawn(1, "red");
        Pawn p1 = new Pawn(2, "red");

        game.board.ring[39].first = blockade1;
        game.board.ring[39].second = blockade2;

        EnterPiece m1 = new EnterPiece(p1);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertNull("Entering piece on blockaded entry is flagged as cheating", result);
    }

    @Test
    public void cannotMoveToOwnBlockadeTest() {
        Parcheesi game = new Parcheesi();
        Pawn blockade1 = new Pawn(0, "red");
        Pawn blockade2 = new Pawn(1, "red");
        Pawn p1 = new Pawn(0, "red");

        game.board.ring[3].first = blockade1;
        game.board.ring[3].second = blockade2;
        game.board.ring[0].first = p1;

        MoveMain m1 = new MoveMain(p1, 3);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertNull("Moving to your own blockade is flagged as cheating", result);
    }

    @Test
    public void cannotMoveThroughOwnBlockadeTest() {
        Parcheesi game = new Parcheesi();

        Pawn blockade1 = new Pawn(0, "red");
        Pawn blockade2 = new Pawn(1, "red");
        Pawn p1 = new Pawn(0, "red");

        game.board.ring[3].first = blockade1;
        game.board.ring[3].second = blockade2;
        game.board.ring[0].first = p1;

        MoveMain m1 = new MoveMain(p1, 5);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertNull("Moving through your own blockade is flagged as cheating", result);
    }

    @Test
    public void cannotMoveToOpponentBlockadeTest() {
        Parcheesi game = new Parcheesi();
        Pawn blockade1 = new Pawn(0, "green");
        Pawn blockade2 = new Pawn(1, "green");
        Pawn p1 = new Pawn(0, "red");

        game.board.ring[3].first = blockade1;
        game.board.ring[3].second = blockade2;
        game.board.ring[0].first = p1;

        MoveMain m1 = new MoveMain(p1, 3);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertNull("Moving to an opponent's blockade is flagged as cheating", result);
    }

    @Test
    public void cannotMoveThroughOpponentBlockadeTest() {
        Parcheesi game = new Parcheesi();
        Pawn blockade1 = new Pawn(0, "green");
        Pawn blockade2 = new Pawn(1, "green");
        Pawn p1 = new Pawn(0, "red");

        game.board.ring[3].first = blockade1;
        game.board.ring[3].second = blockade2;
        game.board.ring[0].first = p1;

        MoveMain m1 = new MoveMain(p1, 5);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertNull("Moving through an opponent's blockade is flagged as cheating", result);
    }

    @Test
    public void cannotPassBlockadeInHomeRowTest() {
        Parcheesi game = new Parcheesi();
        Pawn blockade1 = new Pawn(0, "red");
        Pawn blockade2 = new Pawn(1, "red");
        Pawn p1 = new Pawn(0, "red");

        game.board.runways.get("red").runway[2].first = blockade1;
        game.board.runways.get("red").runway[2].second = blockade2;
        game.board.runways.get("red").runway[0].first = p1;

        MoveMain m1 = new MoveMain(p1, 4);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertNull("Passing to your own blockade in the home row is flagged as cheating", result);
    }

    @Test
    public void breakBlockadeTest() {
        Parcheesi game = new Parcheesi();
        Pawn blockade1 = new Pawn(0, "green");
        Pawn blockade2 = new Pawn(1, "green");
        Pawn p1 = new Pawn(0, "red");

        game.board.ring[3].first = blockade1;
        game.board.ring[3].second = blockade2;

        MoveMain m1 = new MoveMain(blockade1, 4);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertEquals("Blockading pawn can move out of blockade", result.first.ring[7].first, blockade1);
        Assert.assertEquals("Remaining blockading pawn stays in original location", result.first.ring[7].second, blockade2);
    }

    @Test
    public void cannotMoveBlockadeTogetherTest() {
        Parcheesi game = new Parcheesi();
        Pawn blockade1 = new Pawn(0, "green");
        Pawn blockade2 = new Pawn(1, "green");

        game.board.ring[3].first = blockade1;
        game.board.ring[3].second = blockade2;

        MoveMain m1 = new MoveMain(blockade1, 4);
        MoveMain m2 = new MoveMain(blockade2, 4);
        Pair<Board, Integer> result1 = game.processMoves(m1);
        Pair<Board, Integer> result2 = game.processMoves(m2);

        Assert.assertNull("Moving blockade together is flagged as cheating", result2);
    }

    @Test
    public void cannotMoveBlockadeTogetherInMultipleMovesTest() {
        Parcheesi game = new Parcheesi();
        Pawn blockade1 = new Pawn(0, "green");
        Pawn blockade2 = new Pawn(1, "green");

        game.board.ring[3].first = blockade1;
        game.board.ring[3].second = blockade2;

        MoveMain m1 = new MoveMain(blockade1, 2);
        MoveMain m2 = new MoveMain(blockade1, 5);
        MoveMain m3 = new MoveMain(blockade2, 2);
        MoveMain m4 = new MoveMain(blockade2, 5);

        Pair<Board, Integer> result1 = game.processMoves(m1);
        Pair<Board, Integer> result2 = game.processMoves(m2);
        Pair<Board, Integer> result3 = game.processMoves(m3);
        Pair<Board, Integer> result4 = game.processMoves(m4);

        Assert.assertNull("Moving blockade together in multiple moves with doubles bonus is flagged as cheating", result4);
    }

    @Test
    public void formNewBlockadeTest() {

    }

    public void cannotEnterHomeRowOnBlockadeTest() {
        Parcheesi game = new Parcheesi();
        Pawn blockade1 = new Pawn(0, "green");
        Pawn blockade2 = new Pawn(1, "green");
        Pawn p1 = new Pawn(0, "red");

        game.board.ring[34].first = blockade1;
        game.board.ring[34].second = blockade2;
        game.board.ring[32].second = p1;

        MoveMain m1 = new MoveMain(p1, 5);

        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertNull("Moving blockade past blockade on home row is flagged as cheating", result);
    }

    /*
     * Exit row
     */

    @Test
    public void canMoveFromRingToRunwayTest() {
        Parcheesi game = new Parcheesi();
        Pawn p1 = new Pawn(0, "green");

        game.board.ring[32].second = p1;

        MoveMain m1 = new MoveMain(p1, 5);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertEquals("Pawn can move from ring into home row", result.first.runways.get("green").runway[2], p1);
    }

    @Test
    public void canMoveFromRingToHome() {
        Parcheesi game = new Parcheesi();
        Pawn p1 = new Pawn(0, "green");

        game.board.ring[33].second = p1;

        MoveMain m1 = new MoveMain(p1, 6);
        Pair<Board, Integer> result = game.processMoves(m1);

        // TODO: Check pawn is in endzone
//        Assert.assertEquals("Pawn can move from ring into home row", result.first.runways.get("green").runway[2], p1);
    }

    /*
     * Complete Move
     */

    @Test
    public void cannotIgnoreDieRoll() {

    }

    @Test
    public void noMovesLeftDueToBlockadeTest() {
        Parcheesi game = new Parcheesi();
        Pawn blockade1 = new Pawn(0, "green");
        Pawn blockade2 = new Pawn(1, "green");
        Pawn p1 = new Pawn(0, "red");

        game.board.ring[3].first = blockade1;
        game.board.ring[3].second = blockade2;
        game.board.ring[0].first = p1;

        int[] dice = {4, 6};
        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);

        Assert.assertFalse("No moves left due to blockade", movesPossible);
    }

    @Test
    public void canOnlyUseFirstDiceDueToBlockadeTest() {
        Parcheesi game = new Parcheesi();

        Pawn p1 = new Pawn(0, "red");
        Pawn blockade1 = new Pawn(1, "green");
        Pawn blockade2 = new Pawn(2, "green");

        game.board.ring[0].first = p1;
        game.board.ring[4].first = blockade1;
        game.board.ring[4].second = blockade2;

        MoveMain m1 = new MoveMain(p1, 3);
        Pair<Board, Integer> result = game.processMoves(m1);
        int[] dice = {2, 5, 0, 0};

        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);

        Assert.assertTrue("One move possible with due to blockade", movesPossible);
    }

    @Test
    public void canOnlyUseSecondDiceDueToBlockadeTest() {
        Parcheesi game = new Parcheesi();

        Pawn p1 = new Pawn(0, "red");
        Pawn blockade1 = new Pawn(1, "green");
        Pawn blockade2 = new Pawn(2, "green");

        game.board.ring[0].first = p1;
        game.board.ring[4].first = blockade1;
        game.board.ring[4].second = blockade2;

        MoveMain m1 = new MoveMain(p1, 3);
        Pair<Board, Integer> result = game.processMoves(m1);
        int[] dice = {5, 2, 0, 0};

        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);

        Assert.assertTrue("One move possible with due to blockade", movesPossible);
    }

    @Test
    public void cannotUseBopBonusTest() {
        Parcheesi game = new Parcheesi();

        Pawn p1 = new Pawn(0, "red");
        Pawn p2 = new Pawn(0, "green");
        Pawn blockade1 = new Pawn(1, "green");
        Pawn blockade2 = new Pawn(2, "green");

        game.board.ring[0].first = p1;
        game.board.ring[3].first = p2;
        game.board.ring[5].first = blockade1;
        game.board.ring[5].second = blockade2;

        MoveMain m1 = new MoveMain(p1, 3);
        Pair<Board, Integer> result = game.processMoves(m1);
        int[] dice = {0, 0, 0, result.second};

        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);

        Assert.assertFalse("No moves possible with bop bonus due to blockade", movesPossible);
    }

    @Test
    public void cannotUseHomeBonusTest() {
        Parcheesi game = new Parcheesi();

        Pawn p1 = new Pawn(0, "red");
        Pawn p2 = new Pawn(1, "red");
        Pawn blockade1 = new Pawn(0, "green");
        Pawn blockade2 = new Pawn(1, "green");



        game.board.runways.get("red").runway[0].first = p1;
        game.board.ring[3].first = p2;
        game.board.ring[5].first = blockade1;
        game.board.ring[5].second = blockade2;

        MoveHome m1 = new MoveHome(p1, 3, 3);
        Pair<Board, Integer> result = game.processMoves(m1);
        int[] dice = {0, 0, 0, result.second};

        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);

        Assert.assertFalse("No moves possible with bop bonus due to blockade", movesPossible);
    }

    @Test
    public void canOnlyUseOneDiceDueToMovingBlockadeTogetherTest() {
        Parcheesi game = new Parcheesi();

        Pawn blockade1 = new Pawn(0, "green");
        Pawn blockade2 = new Pawn(1, "green");

        game.board.ring[4].first = blockade1;
        game.board.ring[4].second = blockade2;

        MoveMain m1 = new MoveMain(blockade1, 3);
        Pair<Board, Integer> result = game.processMoves(m1);
        int[] dice = {0, 3, 0, 0};

        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);

        Assert.assertFalse("One move possible with due to blockade", movesPossible);
    }


}