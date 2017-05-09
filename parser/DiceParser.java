package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.parsers.DocumentBuilder;

import parcheesi.Pawn;

public class DiceParser extends AbstractParser<int[]> {
    public DiceParser(DocumentBuilder db) {
        super(db);
    }

    public int[] fromXml(Document doc) throws Exception {
        int[] result = new int[4];

        Node dice = doc.getFirstChild();
        if (!dice.getNodeName().equals("dice")) {
            throw new Exception("Tried to parse XML Document that is not <dice></dice>");
        }

        int i = 0;
        Node die = dice.getFirstChild();
        while (die != null) {
            if (!die.getNodeName().equals("die") || i >= 4) {
                throw new Exception("Invalid <dice> element");
            }

            int val = Integer.parseInt(die.getTextContent());
            result[i] = val;
            ++i;
            die = die.getNextSibling();
        }

        return result;
    }

    public Document toXml(int[] dice) {
        Document doc = db.newDocument();

        for (int d : dice) {
            Element die = doc.createElement("die");
            die.appendChild(doc.createTextNode(String.valueOf(d)));
            doc.appendChild(die);
        }

        return doc;
    }
}