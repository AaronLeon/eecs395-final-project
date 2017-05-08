package parser;

import org.junit.*;
import org.w3c.dom.Document;
import parcheesi.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class PawnParserTest {
    private static PawnParser parser;
    private static DocumentBuilder db;

    @BeforeClass
    public static void beforeClass() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        parser = new PawnParser(db);
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
    public void constructXmlFromPawnTest() {
        String buffer = "<pawn><color>red</color><id>3</id></pawn>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document doc = null;
        Pawn pawn = new Pawn(3, "red");
        try {
            expected = db.parse(is);
            doc = parser.toXml(pawn);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue("Parsing pawn should return correct pawn XML", expected.isEqualNode(doc));

    }

    @Test
    public void constructPawnFromXmlTest() {
        String buffer = "<pawn><color>red</color><id>3</id></pawn>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document doc;
        Pawn pawn = null;
        try {
            doc = db.parse(is);
            System.out.println(doc.getNodeName());
            pawn = parser.fromXml(doc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Pawn expected = new Pawn(3, "red");
        Assert.assertEquals("Parsing pawn XML should return correct pawn", expected, pawn);
    }

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

}