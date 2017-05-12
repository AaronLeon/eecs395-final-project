package parser;

import org.junit.*;
import org.w3c.dom.Document;
import parcheesi.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class ParserTest {
    private static DocumentBuilder db;

    @BeforeClass
    public static void beforeClass() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void afterClass() {}

    @Before
    public void beforeTest() {}

    @After
    public void afterTest() {}

    /*
     * Basic tests
     */

    @Test
    public void generateStartGameXmlTest() {
        String color = "red";
        String buffer = "<start-game>" + color + "</start-game>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document result = null;
        try {
            expected = db.parse(is);
            result = Parser.generateStartGameXml(db, color);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue("Generating start game XML returns correct XML", expected.isEqualNode(result));
    }

    @Test
    public void generateDoMoveXmlTest() {
        String buffer = "" +
                "<do-move>" +
                    "<board>" +
                        "<start>" +
                            "<pawn><color>blue</color><id>0</id></pawn>" +
                            "<pawn><color>blue</color><id>1</id></pawn>" +
                            "<pawn><color>blue</color><id>2</id></pawn>" +
                            "<pawn><color>blue</color><id>3</id></pawn>" +
                            "<pawn><color>yellow</color><id>0</id></pawn>" +
                            "<pawn><color>yellow</color><id>1</id></pawn>" +
                            "<pawn><color>yellow</color><id>2</id></pawn>" +
                            "<pawn><color>yellow</color><id>3</id></pawn>" +
                            "<pawn><color>green</color><id>0</id></pawn>" +
                            "<pawn><color>green</color><id>1</id></pawn>" +
                            "<pawn><color>green</color><id>2</id></pawn>" +
                            "<pawn><color>green</color><id>3</id></pawn>" +
                            "<pawn><color>red</color><id>0</id></pawn>" +
                            "<pawn><color>red</color><id>1</id></pawn>" +
                            "<pawn><color>red</color><id>2</id></pawn>" +
                            "<pawn><color>red</color><id>3</id></pawn>" +
                        "</start>" +
                        "<main>" +
                        "</main>" +
                        "<home-rows>" +
                        "</home-rows>" +
                        "<home>" +
                        "</home>" +
                    "</board>" +
                    "<dice>" +
                        "<die>2</die>" +
                        "<die>3</die>" +
                        "<die>0</die>" +
                        "<die>0</die>" +
                    "</dice>" +
                "</do-move>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document result = null;
        try {
            expected = db.parse(is);

            Board board = new Board();
            board.initPawns();
            int[] dice = {2, 3, 0, 0};
            result = Parser.generateDoMoveXml(db, board, dice);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue("Generating start game XML returns correct XML", expected.isEqualNode(result));
    }

    @Test
    public void generateMovesXmlTest() {
        Pawn p0 = new Pawn(0, "red", Board.BoardComponent.NEST, 0);
        Pawn p1 = new Pawn(1, "red", Board.BoardComponent.RING, 7);
        Pawn p2 = new Pawn(2, "red", Board.BoardComponent.HOMEROW, 3);
        Pawn p3 = new Pawn(3, "red", Board.BoardComponent.RING, 26);
        Move[] moves = {
                new EnterPiece(p0),
                new MoveMain(p1, 3),
                new MoveHome(p2, 2),
                new MoveMain(p3, 4)
        };
        String buffer = "" +
                "<moves>" +
                    "<enter-piece>" +
                        "<pawn><color>red</color><id>0</id></pawn>" +
                    "</enter-piece>" +
                    "<move-piece-main>" +
                        "<pawn><color>red</color><id>1</id></pawn><start>7</start><distance>3</distance>" +
                    "</move-piece-main>" +
                    "<move-piece-home>" +
                        "<pawn><color>red</color><id>2</id></pawn><start>3</start><distance>2</distance>" +
                    "</move-piece-home>" +
                    "<move-piece-main>" +
                        "<pawn><color>red</color><id>3</id></pawn><start>26</start><distance>4</distance>" +
                    "</move-piece-main>" +
                "</moves>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document result = null;
        try {
            expected = db.parse(is);
            result = Parser.generateMovesXml(db, moves);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue("Generating start game XML returns correct XML", expected.isEqualNode(result));
    }

    @Test
    public void generateDoublesPenaltyXmlTest() {
        String buffer = "<doubles-penalty></doubles-penalty>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document result = null;
        try {
            expected = db.parse(is);
            result = Parser.generateDoublesPenaltyXml(db);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue("Generating doubles penalty XML returns correct XML", expected.isEqualNode(result));
    }

    @Test
    public void generateVoidXmlTest() {
        String buffer = "<void></void>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document result = null;
        try {
            expected = db.parse(is);
            result = Parser.generateVoidXml(db);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertTrue("Generating void XML returns correct XML", expected.isEqualNode(result));
    }

    @Test
    public void documentToStringTest() {
        String originalXml = "" +
                "<board>" +
                    "<start>" +
                        "<pawn><color>blue</color><id>0</id></pawn>" +
                        "<pawn><color>blue</color><id>1</id></pawn>" +
                        "<pawn><color>blue</color><id>2</id></pawn>" +
                        "<pawn><color>blue</color><id>3</id></pawn>" +
                        "<pawn><color>yellow</color><id>0</id></pawn>" +
                        "<pawn><color>yellow</color><id>1</id></pawn>" +
                        "<pawn><color>yellow</color><id>2</id></pawn>" +
                        "<pawn><color>yellow</color><id>3</id></pawn>" +
                        "<pawn><color>green</color><id>0</id></pawn>" +
                        "<pawn><color>green</color><id>1</id></pawn>" +
                        "<pawn><color>green</color><id>2</id></pawn>" +
                        "<pawn><color>green</color><id>3</id></pawn>" +
                        "<pawn><color>red</color><id>0</id></pawn>" +
                        "<pawn><color>red</color><id>1</id></pawn>" +
                        "<pawn><color>red</color><id>2</id></pawn>" +
                        "<pawn><color>red</color><id>3</id></pawn>" +
                    "</start>" +
                    "<main>" +
                    "</main>" +
                    "<home-rows>" +
                    "</home-rows>" +
                    "<home>" +
                    "</home>" +
                "</board>";
        InputStream is = new ByteArrayInputStream(originalXml.getBytes());
        Document doc;
        String result = null;
        try {
            doc = db.parse(is);
            result = Parser.documentToString(doc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals("Parsed Document to String should equal original XML String", originalXml, result);
    }

}