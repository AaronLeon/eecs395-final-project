package parser;

import org.junit.*;
import org.w3c.dom.Document;
import parcheesi.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class DiceParserTest {
    private static DiceParser parser;
    private static DocumentBuilder db;

    @BeforeClass
    public static void beforeClass() {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        parser = new DiceParser(db);
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
    public void constructXmlFromDiceTest() {
        String buffer = "<dice><die>0</die><die>1</die><die>2</die><die>3</die></dice>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document expected = null;
        Document doc = null;
        int[] dice = {0, 1, 2, 3};

        try {
            expected = db.parse(is);
            doc = parser.toXml(dice);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Assert.assertTrue("Parsing dice should return correct dice XML", expected.isEqualNode(doc));
    }

    @Test
    public void constructDiceFromXmlTest() {
        String buffer = "<dice><die>0</die><die>1</die><die>2</die><die>3</die></dice>";
        InputStream is = new ByteArrayInputStream(buffer.getBytes());
        Document doc;
        int[] dice = null;
        try {
            doc = db.parse(is);
            dice = parser.fromXml(doc);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        int[] expected = {0, 1, 2, 3};
        Assert.assertTrue("Parsing dice XML should return correct dice", Arrays.equals(expected, dice));
    }

    @Test
    public void invalidXmlThrowsExceptionTest() {

    }

}