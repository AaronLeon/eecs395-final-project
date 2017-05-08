package parser;

import org.w3c.dom.Document;
    import org.w3c.dom.Element;
    import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;

import parcheesi.Pawn;

public class PawnParser extends AbstractParser<Pawn> {
    public PawnParser(DocumentBuilder db) {
        super(db);
    }

    public Pawn fromXml(Document doc) throws Exception {
        Node pawn = doc.getFirstChild();
        if (!pawn.getNodeName().equals("pawn")) {
            throw new Exception("Tried to parse XML Document that is not <pawn></pawn>");
        }

        Node color = pawn.getFirstChild();
        Node id = color.getNextSibling();

        if (color == null || id == null
                || !color.getNodeName().equals("color")
                || !id.getNodeName().equals("id")) {
            throw new Exception("Invalid Pawn XML Document");
        }

        String _color = color.getTextContent();
        int _id = Integer.parseInt(id.getTextContent());

        return new Pawn(_id, _color);
    }

    public Document toXml(Pawn p) {
        Document doc = db.newDocument();

        Element pawn = doc.createElement("pawn");

        Element color = doc.createElement("color");
        color.appendChild(doc.createTextNode(p.color));

        Element id = doc.createElement("id");
        id.appendChild(doc.createTextNode(Integer.toString(p.id)));

        pawn.appendChild(color);
        pawn.appendChild(id);
        doc.appendChild(pawn);

        return doc;
    }
}