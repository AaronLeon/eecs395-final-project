package parser;

import org.junit.*;
import org.w3c.dom.Document;
import parcheesi.*;
import parcheesi.Pawn;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MoveParserTest {
    private static MoveParser parser;
    private static DocumentBuilder db;

    @BeforeClass
    public static void beforeClass() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        parser = new MoveParser(db);
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

    @Test(expected = Exception.class)
    public void parsingEmptyXmlThrowsExceptionTest() throws Exception {
        Document doc = db.newDocument();
        parser.fromXml(doc);

    }

    @Test
    public void constructXmlFromEnterPieceTest() {
        String buffer = "<enter-piece><pawn><color>red</color><id>3</id></pawn></enter-piece>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document doc = null;
        Pawn pawn = new Pawn(3, "red");
        EnterPiece move = new EnterPiece(pawn);
        try {
            expected = db.parse(is);
            doc = parser.toXml(move);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue("Parsing pawn should return correct pawn XML", expected.isEqualNode(doc));

    }

    @Test
    public void constructEnterPieceFromXmlTest() {
        String buffer = "<enter-piece><pawn><color>red</color><id>3</id></pawn></enter-piece>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document doc;
        EnterPiece move = null;
        try {
            doc = db.parse(is);
            move = (EnterPiece) parser.fromXml(doc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        EnterPiece expected = new EnterPiece(new Pawn (3, "red"));
        Assert.assertTrue("Parsing pawn XML should return correct pawn", expected.equals(move));
    }


    @Test
    public void constructXmlFromMoveMainTest() {
        String buffer = "<move-piece-main><pawn><color>green</color><id>0</id></pawn><start>5</start><distance>3</distance></move-piece-main>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document doc = null;
        Pawn pawn = new Pawn(0, "green");
        pawn.location=5;
        pawn.bc=Board.BoardComponent.RING;
        MoveMain move = new MoveMain(pawn,3);
        try {
            expected = db.parse(is);
            doc = parser.toXml(move);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue("Parsing pawn should return correct pawn XML", expected.isEqualNode(doc));

    }

    @Test
    public void constructMoveMainFromXmlTest() {
        String buffer = "<move-piece-main><pawn><color>green</color><id>0</id></pawn><start>5</start><distance>3</distance></move-piece-main>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document doc;
        Pawn pawn = new Pawn(0, "green");
        pawn.location=5;
        pawn.bc=Board.BoardComponent.RING;
        MoveMain move = null;
        try {
            doc = db.parse(is);
            move = (MoveMain) parser.fromXml(doc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        MoveMain expected = new MoveMain(pawn,5);
        Assert.assertTrue("Parsing pawn XML should return correct pawn", expected.equals(move));
    }








    @Test
    public void constructXmlFromMoveHomeTest() {
        String buffer = "<move-piece-home><pawn><color>green</color><id>0</id></pawn><start>5</start><distance>1</distance></move-piece-home>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document doc = null;
        Pawn pawn = new Pawn(0, "green");
        pawn.location=5;
        pawn.bc=Board.BoardComponent.HOMEROW;
        MoveHome move = new MoveHome(pawn,1);
        try {
            expected = db.parse(is);
            doc = parser.toXml(move);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue("Parsing pawn should return correct pawn XML", expected.isEqualNode(doc));

    }


    @Test
    public void constructMoveHomeFromXmlTest() {
        String buffer = "<move-piece-home><pawn><color>green</color><id>0</id></pawn><start>1</start><distance>2</distance></move-piece-home>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document doc;
        Pawn pawn = new Pawn(0, "green");
        pawn.location=1;
        pawn.bc=Board.BoardComponent.HOMEROW;
        MoveHome move = null;
        try {
            doc = db.parse(is);
            move = (MoveHome) parser.fromXml(doc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        MoveHome expected = new MoveHome(pawn,2);
        Assert.assertTrue("Parsing pawn XML should return correct pawn", expected.equals(move));
    }
}