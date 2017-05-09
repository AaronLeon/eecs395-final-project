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
import java.util.Arrays;

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
    public void constructBoardFromXmlTest() {
        String buffer = "<board><start><pawn><color>blue</color><id>0</id></pawn><pawn><color>blue</color><id>1</id></pawn><pawn><color>blue</color><id>2</id></pawn><pawn><color>blue</color><id>3</id></pawn><pawn><color>yellow</color><id>0</id></pawn><pawn><color>yellow</color><id>1</id></pawn><pawn><color>yellow</color><id>2</id></pawn><pawn><color>yellow</color><id>3</id></pawn><pawn><color>green</color><id>0</id></pawn><pawn><color>green</color><id>1</id></pawn><pawn><color>green</color><id>2</id></pawn><pawn><color>green</color><id>3</id></pawn><pawn><color>red</color><id>0</id></pawn><pawn><color>red</color><id>1</id></pawn><pawn><color>red</color><id>2</id></pawn><pawn><color>red</color><id>3</id></pawn></start><main></main><home-rows></home-rows><home></home></board>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document doc;
        Board board = null;

        // Setup Expected board is default board
        Board expected = new Board();
        expected.initPawns();

        try {
            doc = db.parse(is);
            board = parser.fromXml(doc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals("Parsing board XML should return correct board", expected, board);
    }

    @Test
    public void constructXmlFromBoardTest() {
        String buffer = "<board><start><pawn><color>blue</color><id>0</id></pawn><pawn><color>blue</color><id>1</id></pawn><pawn><color>blue</color><id>2</id></pawn><pawn><color>blue</color><id>3</id></pawn><pawn><color>yellow</color><id>0</id></pawn><pawn><color>yellow</color><id>1</id></pawn><pawn><color>yellow</color><id>2</id></pawn><pawn><color>yellow</color><id>3</id></pawn><pawn><color>green</color><id>0</id></pawn><pawn><color>green</color><id>1</id></pawn><pawn><color>green</color><id>2</id></pawn><pawn><color>green</color><id>3</id></pawn><pawn><color>red</color><id>0</id></pawn><pawn><color>red</color><id>1</id></pawn><pawn><color>red</color><id>2</id></pawn><pawn><color>red</color><id>3</id></pawn></start><main></main><home-rows></home-rows><home></home></board>";
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
        Assert.assertTrue("Parsing board should return correct board XML", expected.isEqualNode(doc));

    }

    @Test
    public void invalidXmlThrowsExceptionTest() {

    }

}