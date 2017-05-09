package parser;

import org.junit.*;
import org.w3c.dom.Document;

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

//  TODO: Find way to test documentToString... current method wraps XML in <xml> tags
//    @Test
//    public void documentToStringTest() {
//        String originalXml = "<board><start><pawn><color>blue</color><id>0</id></pawn><pawn><color>blue</color><id>1</id></pawn><pawn><color>blue</color><id>2</id></pawn><pawn><color>blue</color><id>3</id></pawn><pawn><color>yellow</color><id>0</id></pawn><pawn><color>yellow</color><id>1</id></pawn><pawn><color>yellow</color><id>2</id></pawn><pawn><color>yellow</color><id>3</id></pawn><pawn><color>green</color><id>0</id></pawn><pawn><color>green</color><id>1</id></pawn><pawn><color>green</color><id>2</id></pawn><pawn><color>green</color><id>3</id></pawn><pawn><color>red</color><id>0</id></pawn><pawn><color>red</color><id>1</id></pawn><pawn><color>red</color><id>2</id></pawn><pawn><color>red</color><id>3</id></pawn></start><main></main><home-rows></home-rows><home></home></board>";
//        InputStream is = new ByteArrayInputStream(originalXml.getBytes());
//        Document doc = null;
//        String result = null;
//        try {
//            doc = db.parse(is);
//            result = Parser.documentToString(doc);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        Assert.assertEquals("Parsed Document to String should equal original XML String", originalXml, result);
//    }

}