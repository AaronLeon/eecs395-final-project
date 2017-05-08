package parser;

import org.junit.*;
import org.w3c.dom.Document;
import parcheesi.Board;
import parcheesi.Pawn;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BoardParserTest {
    private static BoardParser parser;
    private static DocumentBuilder db;

    @BeforeClass
    public static void beforeClass() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        parser = new BoardParser(db);
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
    public void constructNewBoardFromXmlTest() {
        String buffer = "<board><start><pawn><color>yellow</color><id>3</id></pawn><pawn><color>yellow</color><id>2</id></pawn><pawn><color>yellow</color><id>1</id></pawn><pawn><color>yellow</color><id>0</id></pawn><pawn><color>red</color><id>3</id></pawn><pawn><color>red</color><id>2</id></pawn><pawn><color>red</color><id>1</id></pawn><pawn><color>red</color><id>0</id></pawn><pawn><color>green</color><id>3</id></pawn><pawn><color>green</color><id>2</id></pawn><pawn><color>green</color><id>1</id></pawn><pawn><color>green</color><id>0</id></pawn><pawn><color>blue</color><id>3</id></pawn><pawn><color>blue</color><id>2</id></pawn><pawn><color>blue</color><id>1</id></pawn><pawn><color>blue</color><id>0</id></pawn></start><main></main><home-rows></home-rows><home></home></board>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document doc;
        Board board = new Board();
        try {
            doc = db.parse(is);
            System.out.println(doc.getNodeName());
            board = parser.fromXml(doc);
            //builds from Xml
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Pawn expected = new Pawn(3, "red");
        Assert.assertEquals("Parsing pawn XML should return correct pawn", expected, board);
    }

    @Test
    public void constructXmlFromNewBoardTest() {
        String buffer = "<board><start><pawn><color>yellow</color><id>3</id></pawn><pawn><color>yellow</color><id>2</id></pawn><pawn><color>yellow</color><id>1</id></pawn><pawn><color>yellow</color><id>0</id></pawn><pawn><color>red</color><id>3</id></pawn><pawn><color>red</color><id>2</id></pawn><pawn><color>red</color><id>1</id></pawn><pawn><color>red</color><id>0</id></pawn><pawn><color>green</color><id>3</id></pawn><pawn><color>green</color><id>2</id></pawn><pawn><color>green</color><id>1</id></pawn><pawn><color>green</color><id>0</id></pawn><pawn><color>blue</color><id>3</id></pawn><pawn><color>blue</color><id>2</id></pawn><pawn><color>blue</color><id>1</id></pawn><pawn><color>blue</color><id>0</id></pawn></start><main></main><home-rows></home-rows><home></home></board>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document doc = null;
        Board board = new Board();
        try {
            expected = db.parse(is);
            doc = parser.toXml(board);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue("Parsing pawn should return correct pawn XML", expected.isEqualNode(doc));

    }

    @Test
    public void modifiedBoardFromXmlShouldReturnOriginalXml() {
        String buffer = "<board><start><pawn><color>yellow</color><id>3</id></pawn><pawn><color>red</color><id>2</id></pawn><pawn><color>green</color><id>1</id></pawn><pawn><color>blue</color><id>0</id></pawn></start><main><piece-loc><pawn><color>yellow</color><id>2</id></pawn><loc>56</loc></piece-loc><piece-loc><pawn><color>blue</color><id>3</id></pawn><loc>39</loc></piece-loc><piece-loc><pawn><color>red</color><id>1</id></pawn><loc>22</loc></piece-loc><piece-loc><pawn><color>green</color><id>0</id></pawn><loc>5</loc></piece-loc></main><home-rows><piece-loc><pawn><color>green</color><id>2</id></pawn><loc>0</loc></piece-loc><piece-loc><pawn><color>red</color><id>3</id></pawn><loc>1</loc></piece-loc><piece-loc><pawn><color>blue</color><id>1</id></pawn><loc>2</loc></piece-loc><piece-loc><pawn><color>yellow</color><id>0</id></pawn><loc>3</loc></piece-loc></home-rows><home><pawn><color>yellow</color><id>1</id></pawn><pawn><color>red</color><id>0</id></pawn><pawn><color>green</color><id>3</id></pawn><pawn><color>blue</color><id>2</id></pawn></home></board>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document doc = null;
        Board board;
        try {
            expected = db.parse(is);
            board = parser.fromXml(expected);
            doc = parser.toXml(board);
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