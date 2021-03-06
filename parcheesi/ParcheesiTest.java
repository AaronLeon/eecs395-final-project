package parcheesi;

import org.junit.*;

public class ParcheesiTest {
    Parcheesi game;

    @BeforeClass
    public static void beforeClass() {
    }

    @AfterClass
    public static void afterClass() {
    }

    @Before
    public void beforeTest() {
        game = new Parcheesi();
        for (String color : Board.COLORS) {
            SPlayer player = new MPlayer(color, null);
            game.register(player);
        }
    }

    @After
    public void afterTest() {
    }

    /*
     * Basic tests
     */

    @Test
    public void processEnterPieceTest() {
        for (String color : Board.COLORS) {
            int nestLocation = Board.NEST_LOCATIONS.get(color);
            Pawn pawn = game.board.pawns.get(color)[0];
            EnterPiece m = new EnterPiece(pawn);
            game.board = (game.processMoves(game.board, m)).first;

            Assert.assertTrue(((Pawn) game.board.ring[nestLocation]).equals(pawn));
        }
    }

    @Test
    public void processMoveMainTest() {
        // Set up pawn and have it enter
        String color = Board.COLORS[0];
        Pawn pawn1 = game.board.pawns.get(color)[0];
        EnterPiece e1 = new EnterPiece(pawn1);
        game.board = (game.processMoves(game.board, e1)).first;

        // parcheesi.Move the pawn again
        MoveMain m1 = new MoveMain(pawn1, 4);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertTrue("parcheesi.Pawn should move 3 spaces in ring", result.first.ring[8] != null);
        Assert.assertTrue("pawn updated in pawns hash", result.first.pawns.get(Board.COLORS[0])[0].location.index == 8);
        Assert.assertTrue("parcheesi.Pawn should not be in original space in ring", result.first.ring[5] == null);
    }

    @Test
    public void processMoveHomeTest() {
        String color = Board.COLORS[0];
        Pawn pawn = game.board.pawns.get(color)[0];
        pawn.location = new Location(Board.BoardComponent.HOMEROW, 2);
        game.board.homeRows.get(color)[pawn.location.index] = pawn;
        MoveHome m1 = new MoveHome(pawn, 2);

        Pair<Board, Integer> result = game.processMoves(game.board, m1);
        Board board = result.first;

        Assert.assertEquals("parcheesi.Pawn should move 4 spaces in runway", pawn, board.homeRows.get(color)[4]);
        Assert.assertNull("parcheesi.Pawn should not be in original space in runway", board.homeRows.get(color)[2]);

        MoveHome m2 = new MoveHome(pawn, 3);
        result = game.processMoves(game.board, m2);
        board = result.first;


        Assert.assertEquals("parcheesi.Pawn should move 2 spaces in runway and enter home", pawn, board.homes.get(color)[pawn.id]);
    }

    /*
     * Entering
     */
    //TODO: parcheesi.Move this to RuleEngineTest
    @Test
    public void canEnterPawnTest() {
        Parcheesi game = new Parcheesi();
        int[] d1 = {1, 4, 0, 0};
        int[] d2 = {2, 3, 0, 0};
        int[] d3 = {5, 5, 0, 0};
        int[] d4 = {6, 2, 0, 0};

        Assert.assertTrue("Can enter pawn with 1, 4", RuleEngine.canEnter(d1));
        Assert.assertTrue("Can enter pawn with 2, 3", RuleEngine.canEnter(d2));
        Assert.assertTrue("Can enter pawn with 5, 5", RuleEngine.canEnter(d3));
        Assert.assertFalse("Cannot enter pawn with 6, 2", RuleEngine.canEnter(d4));
    }

    /*
     * Bopping
     */
    @Test
    public void boppingGivesBonusTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];

        // Setup bopping pawn
        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.location = new Location(Board.BoardComponent.RING, 19);
        game.board.ring[p1.location.index] = p1;

        // Setup bopped pawn
        Pawn p2 = game.board.pawns.get(color2)[0];
        p2.location = new Location(Board.BoardComponent.RING, 20);
        game.board.ring[p2.location.index] = p2;

        MoveMain m = new MoveMain(p1, 1);
        Pair<Board, Integer> result = game.processMoves(game.board, m);
        int bonus = result.second;

        Assert.assertEquals("Bopping earns 20 bonus", 20, bonus);
    }

    @Test
    public void enterPieceCanBopTest() {
        // Set up entering/bopping pawn
        String color1 = "blue";
        Pawn pawn1 = game.board.pawns.get(color1)[0];

        // Set up bopped pawn
        String color2 = "yellow";
        Pawn pawn2 = game.board.pawns.get(color2)[0];

        int nestLocation = Board.NEST_LOCATIONS.get(color1);
        pawn2.location = new Location(Board.BoardComponent.RING, nestLocation);
        game.board.ring[nestLocation] = pawn2;

        // Enter pawn
        EnterPiece m = new EnterPiece(pawn1);
        Pair<Board, Integer> result = game.processMoves(game.board, m);

        Board board = result.first;
        int bonus = result.second;

        Assert.assertEquals("Entering piece should replace bopped piece", pawn1, board.ring[nestLocation]);
        Assert.assertEquals("Bopped piece should be returned to nest", pawn2, board.nests.get(color2)[pawn2.id]);
        Assert.assertEquals("Bonus is earned", 20, bonus);

    }

    @Test
    public void cannotBopOnSafetyTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];

        // Setup bopping pawn
        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.location = new Location(Board.BoardComponent.RING, 20);
        game.board.ring[p1.location.index] = p1;

        // Setup bopped pawn
        Pawn p2 = game.board.pawns.get(color2)[0];
        p2.location = new Location(Board.BoardComponent.RING, 21);
        game.board.ring[p2.location.index] = p2;

        MoveMain m = new MoveMain(p1, 1);
        Pair<Board, Integer> result = game.processMoves(game.board, m);

//        Assert.assertTrue("Piece should not be removed on safety", result.first.ring[21] != null);
//        Assert.assertTrue("cheater removed from game", result.first.ring[20].first == null);
        Assert.assertNull("Bopping safety should return null (e.g. cheat)", result);
    }

    @Test
    public void boppingTwoPiecesGivesTwoBonusesTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];

        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.location = new Location(Board.BoardComponent.RING, 0);
        game.board.ring[p1.location.index] = p1;

        Pawn p2 = game.board.pawns.get(color1)[1];
        p2.location = new Location(Board.BoardComponent.RING, 1);
        game.board.ring[p2.location.index] = p2;


        Pawn p3 = game.board.pawns.get(color2)[0];
        p3.location = new Location(Board.BoardComponent.RING, 3);
        game.board.ring[p3.location.index] = p3;

        Pawn p4 = game.board.pawns.get(color2)[1];
        p4.location = new Location(Board.BoardComponent.RING, 7);
        game.board.ring[p4.location.index] = p4;

        MoveMain m1 = new MoveMain(p1, 3);
        MoveMain m2 = new MoveMain(p2, 6);
        Pair<Board, Integer> result1 = game.processMoves(game.board, m1);
        Pair<Board, Integer> result2 = game.processMoves(game.board, m2);

        Assert.assertEquals("First bop earns 20 bonus", result1.second, 20);
        Assert.assertEquals("Second bop earns 20 bonus", result2.second, 20);
    }


    @Test
    public void blockadeCannotMoveTogetherTest() {
        String color = Board.COLORS[0];

        Pawn p1 = game.board.pawns.get(color)[0];
        Pawn p2 = game.board.pawns.get(color)[1];
        p1.location = new Location(Board.BoardComponent.RING, 10);
        p2.location = new Location(Board.BoardComponent.RING, 10);
        game.board.ring[p2.location.index] = new Blockade(p1, p2);


        MoveMain m1 = new MoveMain(p1, 3);
        MoveMain m2 = new MoveMain(p2, 3);

        Board startState = game.board;

        Move[] singleMove = {m1};
        Move[] moves = {m1, m2};

        Pair<Board, Integer> result1 = game.processMoves(game.board, m1);
        Assert.assertFalse("player did not cheat by moving blockade", RuleEngine.movedBlockadeTogether(startState, result1.first, singleMove, color));
        Pair<Board, Integer> result2 = game.processMoves(result1.first, m2);
        Assert.assertTrue("player cheated by moving blockade", RuleEngine.movedBlockadeTogether(startState, result2.first, moves, color));

    }

    //
//    @Test
//    public void blockadeCannotMoveTogetherWithBopBonusTest() {
//        parcheesi.Parcheesi game = new parcheesi.Parcheesi();
//        parcheesi.Pawn p1 = new parcheesi.Pawn(0, "red");
//        parcheesi.Pawn p2 = new parcheesi.Pawn(1, "red");
//        parcheesi.Pawn blockade1 = new parcheesi.Pawn(2, "red");
//        parcheesi.Pawn blockade2 = new parcheesi.Pawn(3, "red");
//        parcheesi.Pawn p3 = new parcheesi.Pawn(0, "green");
//        parcheesi.Pawn p4 = new parcheesi.Pawn(1, "green");
//
//        game.board.ring[0] = p1;
//        game.board.ring[1] = p2;
//        game.board.ring[3] = p3;
//        game.board.ring[7] = p4;
//
//        parcheesi.MoveMain m1 = new parcheesi.MoveMain(p1, 3);
//        parcheesi.MoveMain m2 = new parcheesi.MoveMain(p2, 6);
//        parcheesi.Pair<parcheesi.Board, Integer> result1 = game.processMoves(game.board, m1);
//        parcheesi.Pair<parcheesi.Board, Integer> result2 = game.processMoves(game.board, m2);
//
//        //TODO: Check that bonus moves cant move blockade together
//    }
//
    /*
     * Blockades
     */
    @Test
    public void cannotEnterPieceOnBlockadedEntryTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];
        int nestLocation = Board.NEST_LOCATIONS.get(color1);

        // Setup entering pawn
        Pawn p1 = game.board.pawns.get(color1)[0];

        // Setup blockade
        Pawn b1 = game.board.pawns.get(color2)[0];
        Pawn b2 = game.board.pawns.get(color2)[1];
        b1.location = b2.location = new Location(Board.BoardComponent.RING, nestLocation);
        game.board.ring[nestLocation] = new Blockade(b1, b2);

        EnterPiece m1 = new EnterPiece(p1);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertNull("Entering piece on blockaded entry is flagged as cheating", result);
    }

    @Test
    public void cannotMoveToOwnBlockadeTest() {
        String color1 = Board.COLORS[0];

        // Setup moving pawn
        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.location = new Location(Board.BoardComponent.RING, 1);
        game.board.ring[p1.location.index] = p1;

        // Setup blockade
        Pawn b1 = game.board.pawns.get(color1)[1];
        Pawn b2 = game.board.pawns.get(color1)[2];
        b1.location = b2.location = new Location(Board.BoardComponent.RING,6);
        game.board.ring[b1.location.index] = new Blockade(b1, b2);

        MoveMain m1 = new MoveMain(p1, 5);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertNull("Moving to your own blockade is flagged as cheating", result);
    }

    @Test
    public void cannotMoveThroughOwnBlockadeTest() {
        String color1 = Board.COLORS[0];

        // Setup moving pawn
        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.location = new Location(Board.BoardComponent.RING, 1);
        game.board.ring[p1.location.index] = p1;

        // Setup blockade
        Pawn b1 = game.board.pawns.get(color1)[1];
        Pawn b2 = game.board.pawns.get(color1)[2];
        b1.location = b2.location = new Location(Board.BoardComponent.RING, 3);
        game.board.ring[b1.location.index] = new Blockade(b1, b2);

        MoveMain m1 = new MoveMain(p1, 5);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertNull("Moving to your own blockade is flagged as cheating", result);
    }

    @Test
    public void cannotMoveToOpponentBlockadeTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];

        // Setup moving pawn
        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.location = new Location(Board.BoardComponent.RING, 1);
        game.board.ring[p1.location.index] = p1;

        // Setup blockade
        Pawn b1 = game.board.pawns.get(color2)[0];
        Pawn b2 = game.board.pawns.get(color2)[1];
        b1.location = b2.location = new Location(Board.BoardComponent.RING, 6);
        game.board.ring[b1.location.index] = new Blockade(b1, b2);

        MoveMain m1 = new MoveMain(p1, 5);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertNull("Moving to your own blockade is flagged as cheating", result);
    }

    @Test
    public void cannotMoveThroughOpponentBlockadeTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];

        // Setup moving pawn
        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.location = new Location(Board.BoardComponent.RING, 1);
        game.board.ring[p1.location.index] = p1;

        // Setup blockade
        Pawn b1 = game.board.pawns.get(color2)[0];
        Pawn b2 = game.board.pawns.get(color2)[1];
        b1.location = b2.location = new Location(Board.BoardComponent.RING, 3);
        game.board.ring[b1.location.index] = new Blockade(b1, b2);

        MoveMain m1 = new MoveMain(p1, 5);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertNull("Moving to your own blockade is flagged as cheating", result);
    }

    @Test
    public void cannotPassBlockadeInHomeRowTest() {
        String color = Board.COLORS[0];
        Pawn[] pawns = game.board.pawns.get(color);

        Pawn b1 = pawns[0];
        Pawn b2 = pawns[1];
        b1.location = b2.location = new Location(Board.BoardComponent.HOMEROW, 2);
        Blockade blockade = new Blockade(b1, b2);
        game.board.homeRows.get(color)[2] = blockade;

        Pawn p1 = pawns[2];
        p1.location = new Location(Board.BoardComponent.HOMEROW, 0);
        game.board.homeRows.get(color)[0] = p1;

        MoveHome m1 = new MoveHome(p1, 4);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertNull("Passing your own blockade in the home row is flagged as cheating", result);
    }

    @Test
    public void breakBlockadeTest() {
        String color = Board.COLORS[0];
        Pawn[] pawns = game.board.pawns.get(color);

        Pawn b1 = pawns[0];
        Pawn b2 = pawns[1];
        b1.location = b2.location = new Location (Board.BoardComponent.RING, 7);
        Blockade blockade = new Blockade(b1, b2);
        game.board.ring[7] = blockade;

        MoveMain m1 = new MoveMain(b1, 4);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertEquals("Blockading pawn can move out of blockade", b1, result.first.ring[11]);
        Assert.assertEquals("Remaining blockading pawn stays in original location", b2, result.first.ring[7]);
    }

    //
//    @Test
//    public void cannotMoveBlockadeTogetherInMultipleMovesTest() {
//        parcheesi.Parcheesi game = new parcheesi.Parcheesi();
//        parcheesi.Pawn blockade1 = new parcheesi.Pawn(0, "green");
//        parcheesi.Pawn blockade2 = new parcheesi.Pawn(1, "green");
//
//        game.board.ring[3].first = blockade1;
//        game.board.ring[3].second = blockade2;
//
//        parcheesi.MoveMain m1 = new parcheesi.MoveMain(blockade1, 2);
//        parcheesi.MoveMain m2 = new parcheesi.MoveMain(blockade1, 5);
//        parcheesi.MoveMain m3 = new parcheesi.MoveMain(blockade2, 2);
//        parcheesi.MoveMain m4 = new parcheesi.MoveMain(blockade2, 5);
//
//        parcheesi.Pair<parcheesi.Board, Integer> result1 = game.processMoves(m1);
//        parcheesi.Pair<parcheesi.Board, Integer> result2 = game.processMoves(m2);
//        parcheesi.Pair<parcheesi.Board, Integer> result3 = game.processMoves(m3);
//        parcheesi.Pair<parcheesi.Board, Integer> result4 = game.processMoves(m4);
//
//        Assert.assertNull("Moving blockade together in multiple moves with doubles bonus is flagged as cheating", result4);
//    }
//
    @Test
    public void formNewBlockadeTest() {
        String color = Board.COLORS[0];
        Pawn[] pawns = game.board.pawns.get(color);

        Pawn b1 = pawns[0];
        b1.location = new Location(Board.BoardComponent.RING, 3);
        game.board.ring[3] = b1;

        Pawn b2 = pawns[1];
        b2.location = new Location(Board.BoardComponent.RING, 7);
        game.board.ring[7] = b2;


        MoveMain m1 = new MoveMain(b1, 4);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertEquals("parcheesi.Blockade is formed at new location", new Blockade(b1, b2), result.first.ring[7]);
    }

    @Test
    public void cannotEnterHomeRowOnBlockadeTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];

        Pawn[] pawns1 = game.board.pawns.get(color1);
        Pawn[] pawns2 = game.board.pawns.get(color2);

        // Set up moving pawn
        int homeRowLocation = Board.HOMEROW_LOCATIONS.get(color1);
        int startLocation = homeRowLocation - 3;
        Pawn p1 = pawns1[0];
        p1.location = new Location(Board.BoardComponent.RING, startLocation);
        game.board.ring[startLocation] = p1;

        // Set up blockade
        Pawn b1 = pawns2[0];
        Pawn b2 = pawns2[1];
        b1.location = b2.location = new Location(Board.BoardComponent.RING, homeRowLocation);
        Blockade blockade = new Blockade(b1, b2);
        game.board.ring[homeRowLocation] = blockade;

        MoveMain m1 = new MoveMain(p1, 5);

        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertNull("Moving blockade past blockade on home row is flagged as cheating", result);
    }

    /*
     * Exit row
     */

    @Test
    public void canMoveFromRingToHomeRowTest() {
        String color = Board.COLORS[0];

        Pawn[] pawns = game.board.pawns.get(color);

        // Set up moving pawn
        int homeRowLocation = Board.HOMEROW_LOCATIONS.get(color);
        int startLocation = homeRowLocation - 3;
        Pawn p = pawns[0];
        p.location = new Location(Board.BoardComponent.RING, startLocation);
        game.board.ring[startLocation] = p;
        Parcheesi game = new Parcheesi();

        MoveMain m1 = new MoveMain(p, 5);
        Pair<Board, Integer> result = game.processMoves(game.board, m1);

        Assert.assertEquals("parcheesi.Pawn can move from ring into home row", p, result.first.homeRows.get(color)[1]);
    }

    /*
     * Complete parcheesi
     */

    @Test
    public void noMovesLeftDueToBlockadeTest() {
        Pawn[] pawns1 = game.board.pawns.get("green");
        Pawn b1 = pawns1[0];
        Pawn b2 = pawns1[1];
        b1.location = new Location(Board.BoardComponent.RING, 3);
        b2.location = new Location(Board.BoardComponent.RING, 3);

        Pawn p1 = game.board.pawns.get("red")[0];
        p1.location = new Location(Board.BoardComponent.RING, 0);

        Blockade blockade = new Blockade(b1, b2);
        game.board.ring[3] = blockade;
        game.board.ring[0] = p1;

        int[] dice = {4, 6, 0, 0};
        boolean result = RuleEngine.canMove(game.players.get("red"), dice, game.board);

        Assert.assertFalse("No moves left due to blockade", result);
    }

    @Test
    public void canOneDiceDueToBlockadeTest() {
        Pawn[] pawns1 = game.board.pawns.get("green");
        Pawn b1 = pawns1[0];
        Pawn b2 = pawns1[1];
        b1.location = new Location(Board.BoardComponent.RING, 5);
        b2.location = new Location(Board.BoardComponent.RING, 5);

        Pawn p1 = game.board.pawns.get("red")[0];
        p1.location = new Location(Board.BoardComponent.RING, 1);

        Blockade blockade = new Blockade(b1, b2);
        game.board.ring[5] = blockade;
        game.board.ring[1] = p1;

        int[] dice = {2, 5, 0, 0};
        boolean result = RuleEngine.canMove(game.players.get("red"), dice, game.board);

        Assert.assertTrue("Only one move possible due to blockade", result);
    }

    @Test
    public void canOnlyUseSecondDiceDueToBlockadeTest() {
        Pawn[] pawns1 = game.board.pawns.get("green");
        Pawn b1 = pawns1[0];
        Pawn b2 = pawns1[1];
        b1.location = new Location(Board.BoardComponent.RING, 4);
        b2.location = new Location(Board.BoardComponent.RING, 4);

        Pawn p1 = game.board.pawns.get("red")[0];
        p1.location = new Location(Board.BoardComponent.RING, 0);

        Blockade blockade = new Blockade(b1, b2);
        game.board.ring[4] = blockade;
        game.board.ring[0] = p1;

        int[] dice = {5, 2, 0, 0};
        boolean result = RuleEngine.canMove(game.players.get("red"), dice, game.board);

        Assert.assertTrue("Only one move possible due to blockade", result);
    }

//    @Test
//    public void cannotUseBopBonusTest() {
//        parcheesi.Parcheesi game = new parcheesi.Parcheesi();
//
//        parcheesi.Pawn p1 = new parcheesi.Pawn(0, "red");
//        parcheesi.Pawn p2 = new parcheesi.Pawn(0, "green");
//        parcheesi.Pawn blockade1 = new parcheesi.Pawn(1, "green");
//        parcheesi.Pawn blockade2 = new parcheesi.Pawn(2, "green");
//
//        game.board.ring[0].first = p1;
//        game.board.ring[3].first = p2;
//        game.board.ring[5].first = blockade1;
//        game.board.ring[5].second = blockade2;
//
//        parcheesi.MoveMain m1 = new parcheesi.MoveMain(p1, 3);
//        parcheesi.Pair<parcheesi.Board, Integer> result = game.processMoves(m1);
//        int[] dice = {0, 0, 0, result.second};
//
//        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);
//
//        Assert.assertFalse("No moves possible with bop bonus due to blockade", movesPossible);
//    }
//
//    @Test
//    public void cannotUseHomeBonusTest() {
//        parcheesi.Parcheesi game = new parcheesi.Parcheesi();
//
//        parcheesi.Pawn p1 = new parcheesi.Pawn(0, "red");
//        parcheesi.Pawn p2 = new parcheesi.Pawn(1, "red");
//        parcheesi.Pawn blockade1 = new parcheesi.Pawn(0, "green");
//        parcheesi.Pawn blockade2 = new parcheesi.Pawn(1, "green");
//
//
//        game.board.homerows.get("red").runway[0].first = p1;
//        game.board.ring[3].first = p2;
//        game.board.ring[5].first = blockade1;
//        game.board.ring[5].second = blockade2;
//
//        parcheesi.MoveHome m1 = new parcheesi.MoveHome(p1, 3, 3);
//        parcheesi.Pair<parcheesi.Board, Integer> result = game.processMoves(m1);
//        int[] dice = {0, 0, 0, result.second};
//
//        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);
//
//        Assert.assertFalse("No moves possible with bop bonus due to blockade", movesPossible);
//    }
//
//    @Test
//    public void canOnlyUseOneDiceDueToMovingBlockadeTogetherTest() {
//        parcheesi.Parcheesi game = new parcheesi.Parcheesi();
//
//        parcheesi.Pawn blockade1 = new parcheesi.Pawn(0, "green");
//        parcheesi.Pawn blockade2 = new parcheesi.Pawn(1, "green");
//
//        game.board.ring[4].first = blockade1;
//        game.board.ring[4].second = blockade2;
//
//        parcheesi.MoveMain m1 = new parcheesi.MoveMain(blockade1, 3);
//        parcheesi.Pair<parcheesi.Board, Integer> result = game.processMoves(m1);
//        int[] dice = {0, 3, 0, 0};
//
//        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);
//
//        Assert.assertFalse("One move possible with due to blockade", movesPossible);
//    }
//

}