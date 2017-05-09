package parser;

import org.junit.*;
import org.w3c.dom.Document;
import parcheesi.EnterPiece;
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
            System.out.println(expected.getFirstChild().getNodeName());
            doc = parser.toXml(move);
            System.out.println(expected.getNodeName());
            System.out.println(expected.getFirstChild().getNodeName());
            System.out.println(expected.getFirstChild().getFirstChild().getNodeName());

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
/*
    @Test
    public void pawnFromXmlShouldReturnOriginalXml() {
        String buffer = "<pawn><color>red</color><id>3</id></pawn>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document doc = null;
        Pawn pawn;
        try {
            expected = db.parse(is);
            pawn = parser.fromXml(expected);
            doc = parser.toXml(pawn);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue("Pawn from XML should return original XML", expected.isEqualNode(doc));
    }
    @Test
    public void invalidXmlThrowsExceptionTest() {

    }
*/

}